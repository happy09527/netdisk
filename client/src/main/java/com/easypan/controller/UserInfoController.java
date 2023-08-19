package com.easypan.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import com.easypan.annotation.GlobalInterceptor;
import com.easypan.annotation.VerifyParam;
import com.easypan.component.RedisComponent;
import com.easypan.config.APPConfig;
import com.easypan.entity.constants.Constants;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.pojo.UserInfo;
import com.easypan.entity.vo.ResponseVo;
import com.easypan.entity.enums.VerifyRegexEnum;
import com.easypan.exception.BusinessException;
import com.easypan.service.EmailCodeService;
import com.easypan.service.UserInfoService;
import com.easypan.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Author hapZhang
 * @Description 用户信息表
 * 存储用户登录信息、云盘空间大小Controller
 * @Date 2023/07/21 14:45:18
 */

@RestController("userInfoController")
public class UserInfoController extends ABaseController {
    private static final Logger logger = LoggerFactory.getLogger(UserInfoController.class);
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_TYPE_VALUE = "application/json;charset=UTF-8";
    @Resource
    private EmailCodeService emailCodeService;
    @Resource
    private UserInfoService userInfoService;
    @Resource
    private APPConfig appConfig;

    @Resource
    private RedisComponent redisComponent;

    // 生成验证码
    @RequestMapping("/checkCode")
    public void checkCode(HttpServletResponse response, HttpSession session, Integer type) throws IOException {
        //定义图形验证码的长、宽、验证码字符数、干扰线宽度
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(150, 40, 5, 4);
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        String verifyCode = captcha.getCode();
        if (type == null || type == 0) {
            session.setAttribute(Constants.CHECK_CODE_KEY, verifyCode);
        } else {
            session.setAttribute(Constants.CHECK_CODE_KEY_EMAIL, verifyCode);
        }
        //图形验证码写出，可以写出到文件，也可以写出到流
        captcha.write(response.getOutputStream());
    }

    // 发送邮箱验证码前的校验
    @RequestMapping("/sendEmailCode")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVo sendEmailCode(HttpSession session,
                                    @VerifyParam(required = true) String email,
                                    @VerifyParam(required = true) String checkCode,
                                    @VerifyParam(required = true) Integer type) {
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY_EMAIL))) {
                throw new BusinessException("图片验证码不正确");
            }
            emailCodeService.sendEmailCode(email, type);
            return getSuccessResponseVo(null);
        } finally {
            session.getAttribute(Constants.CHECK_CODE_KEY_EMAIL);
        }
    }

    /**
     * @date: 2023/7/23 8:39
     * 注册
     **/
    @RequestMapping("/register")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVo register(HttpSession session,
                               @VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
                               @VerifyParam(required = true, max = 20) String nickName,
                               @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD, min = 8, max = 18) String password,
                               @VerifyParam(required = true) String checkCode,
                               @VerifyParam(required = true) String emailCode) {
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
                throw new BusinessException("图片验证码不正确");
            }
            userInfoService.register(email, nickName, password, emailCode);
            return getSuccessResponseVo(null);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }

    /**
     * @date: 2023/7/23 8:33
     * 登录功能
     **/
    @RequestMapping("/login")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVo login(HttpSession session,
                            @VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
                            @VerifyParam(required = true) String password,
                            @VerifyParam(required = true) String checkCode) {
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
                throw new BusinessException("图片验证码不正确");
            }
            SessionWebUserDto sessionWebUserDto = userInfoService.login(email, password);
            session.setAttribute(Constants.SESSION_KEY, sessionWebUserDto);
            return getSuccessResponseVo(sessionWebUserDto);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }

    /**
     * @date: 2023/7/23 8:34
     * 重置密码
     **/
    @RequestMapping("/resetPwd")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVo resetPwd(HttpSession session,
                               @VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
                               @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD, min = 8, max = 18) String password,
                               @VerifyParam(required = true) String checkCode,
                               @VerifyParam(required = true) String emailCode) {
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
                throw new BusinessException("图片验证码不正确");
            }
            userInfoService.resetPwd(email, password, emailCode);
            return getSuccessResponseVo(null);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }

    /**
     * @date: 2023/7/23 23:18
     * 修改密码
     **/
    @RequestMapping("/updatePwd")
    @GlobalInterceptor(checkParams = true)
    public ResponseVo updatePwd(HttpSession session,
                                @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD, min = 8, max = 18) String password) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        String userId = webUserDto.getUserId();
        UserInfo userInfo = new UserInfo();
        userInfo.setPassword(StringUtils.encodeByMD5(password));
        userInfoService.updateUserInfoByUserId(userInfo, userId);
        return getSuccessResponseVo(null);
    }

    /**
     * @date: 2023/7/23 10:27
     * 获取用户头像
     **/
    @RequestMapping("/getAvatar/{userId}")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public void getAvatar(HttpServletResponse response, HttpSession session,
                          @VerifyParam(required = true) @PathVariable(name = "userId") String userId) {
        String avatarFolderName = Constants.FILE_FOLDER_FILE + Constants.FILE_FOLDER_AVATAR_NAME;
        File folder = new File(appConfig.getProjectFolder() + avatarFolderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // 用户个人头像路径
        String avatarPath = appConfig.getProjectFolder() + avatarFolderName + "/" + userId + Constants.AVATAR_SUFFIX;
        File file = new File(avatarPath);
        if (!file.exists()) {
            // 默认头像路径
            avatarPath = appConfig.getProjectFolder() + avatarFolderName + "/" + Constants.AVATAR_DEFUALT;
            // 获取系统默认头像
            if (!new File(avatarPath).exists()) {
                // 获取默认头像失败
                printNoDefaultImage(response);
                return;
            }
        }
        response.setContentType("image/jpg");
        readFile(response, avatarPath);
    }

    private void printNoDefaultImage(HttpServletResponse response) {
        response.setHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE);
        response.setStatus(HttpStatus.OK.value());
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.print("请在头像目录下放置默认头像default_avatar.jpg");
            writer.close();
        } catch (Exception e) {
            logger.error("输出无默认图片失败", e);
        } finally {
            writer.close();
        }
    }

    /**
     * @date: 2023/7/23 22:56
     * 获取用户信息
     **/
    @RequestMapping("/getUserInfo")
    @GlobalInterceptor(checkParams = true)
    public ResponseVo getUserInfo(HttpSession session) {
        SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
        return getSuccessResponseVo(sessionWebUserDto);
    }

    /**
     * @date: 2023/7/23 22:56
     * 获取用户内存信息
     **/
    @RequestMapping("/getUseSpace")
    @GlobalInterceptor(checkParams = true)
    public ResponseVo getUseSpace(HttpSession session) {
        SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
        return getSuccessResponseVo(redisComponent.getUserSpaceUse(sessionWebUserDto.getUserId()));
    }

    /**
     * @date: 2023/7/23 22:57
     * 退出登录
     **/
    @RequestMapping("/logout")
    public ResponseVo logout(HttpSession session) {
        session.invalidate();
        return getSuccessResponseVo(null);
    }

    /**
     * @date: 2023/7/23 23:02
     * 更新用户头像。 拿到用户信息，在相应路径下进行文件存储
     **/
    @RequestMapping("/updateUserAvatar")
    @GlobalInterceptor(checkParams = true)
    public ResponseVo updateUserAvatar(HttpSession session, MultipartFile avatar) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        String baserFolder = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE;
        File targetFileFolder = new File(baserFolder + Constants.FILE_FOLDER_AVATAR_NAME);
        if (!targetFileFolder.exists()) {
            targetFileFolder.mkdirs();
        }
        File targetFile = new File(targetFileFolder.getPath() + "/" + webUserDto.getUserId() + Constants.AVATAR_SUFFIX);
        try {
            avatar.transferTo(targetFile);
        } catch (Exception e) {
            logger.error("上传头像失败", e);
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setQqAvatar("");
        userInfoService.updateUserInfoByUserId(userInfo, webUserDto.getUserId());
        webUserDto.setAvatar(null);
        session.setAttribute(Constants.SESSION_KEY, webUserDto);
        return getSuccessResponseVo(null);
    }
}

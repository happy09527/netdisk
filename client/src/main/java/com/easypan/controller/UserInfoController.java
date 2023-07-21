package com.easypan.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import com.easypan.entity.constants.Constants;
import com.easypan.entity.vo.ResponseVo;
import com.easypan.entity.pojo.UserInfo;
import com.easypan.entity.query.UserInfoQuery;
import com.easypan.exception.BusinessException;
import com.easypan.service.UserInfoService;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * @Author hapZhang
 * @Description 用户信息表
 * 存储用户登录信息、云盘空间大小Controller
 * @Date 2023/07/21 14:45:18
 */

@RestController("userInfoController")
public class UserInfoController extends ABaseController {

    @Resource
    private UserInfoService userInfoService;

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

    // 邮箱验证码校验
    @RequestMapping("/sendEmailCode")
    public ResponseVo sendEmailCode(HttpSession session, String email, String checkCode, Integer type) {
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY_EMAIL))) {
                throw new BusinessException("图片验证码不正确");
            }
            return getSuccessResponseVo(null);
        } finally {
            session.getAttribute(Constants.CHECK_CODE_KEY_EMAIL);
        }

    }
}

package com.easypan.service.impl;

import com.easypan.component.RedisComponent;
import com.easypan.config.APPConfig;
import com.easypan.entity.constants.Constants;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.dto.SysSettingsDto;
import com.easypan.entity.dto.UserSpaceDto;
import com.easypan.entity.enums.UserStatusEnum;
import com.easypan.entity.pojo.EmailCode;
import com.easypan.entity.query.SimplePage;
import com.easypan.enums.PageSize;
import com.easypan.entity.vo.PaginationResultVo;
import com.easypan.entity.pojo.UserInfo;
import com.easypan.entity.query.UserInfoQuery;
import com.easypan.exception.BusinessException;
import com.easypan.mappers.UserInfoMapper;
import com.easypan.service.EmailCodeService;
import com.easypan.service.UserInfoService;
import com.easypan.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Author hapZhang
 * @Description 用户信息表
 * 存储用户登录信息、云盘空间大小Service
 * @Date 2023/07/21 14:42:24
 */
@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {

    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

    @Resource
    private EmailCodeService emailCodeService;

    //    @Resource
//    private FileInfoMapper fileInfoMapper;
    @Resource
    private RedisComponent redisComponent;

    @Resource
    private APPConfig appConfig;

    /**
     * 根据条件查询列表
     */
    public List<UserInfo> findListByParam(UserInfoQuery query) {
        return this.userInfoMapper.selectList(query);
    }

    /**
     * 根据条件查询数量
     */
    public Integer findCountByParam(UserInfoQuery query) {
        return this.userInfoMapper.selectCount(query);
    }

    /**
     * 分页查询
     */
    public PaginationResultVo<UserInfo> findListByPage(UserInfoQuery query) {
        Integer count = this.findCountByParam(query);
        Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
        SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
        query.setSimplePage(page);
        List<UserInfo> list = this.findListByParam(query);
        PaginationResultVo<UserInfo> result = new PaginationResultVo(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * 新增
     */
    public Integer add(UserInfo bean) {
        return this.userInfoMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    public Integer addBatch(List<UserInfo> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.userInfoMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或修改
     */
    public Integer addOrUpdateBatch(List<UserInfo> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.userInfoMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 根据UserId查询
     */
    public UserInfo getUserInfoByUserId(Integer userId) {
        return this.userInfoMapper.selectByUserId(userId);
    }

    /**
     * 根据UserId更新
     */
    public Integer updateUserInfoByUserId(UserInfo bean, String userId) {
        return this.userInfoMapper.updateByUserId(bean, userId);
    }

    /**
     * 根据UserId删除
     */
    public Integer deleteUserInfoByUserId(Integer userId) {
        return this.userInfoMapper.deleteByUserId(userId);
    }

    /**
     * 根据Email查询
     */
    public UserInfo getUserInfoByEmail(String email) {
        return this.userInfoMapper.selectByEmail(email);
    }

    /**
     * 根据Email更新
     */
    public Integer updateUserInfoByEmail(UserInfo bean, String email) {
        return this.userInfoMapper.updateByEmail(bean, email);
    }

    /**
     * 根据Email删除
     */
    public Integer deleteUserInfoByEmail(String email) {
        return this.userInfoMapper.deleteByEmail(email);
    }

    /**
     * 根据QqOpenId查询
     */
    public UserInfo getUserInfoByQqOpenId(String qqOpenId) {
        return this.userInfoMapper.selectByQqOpenId(qqOpenId);
    }

    /**
     * 根据QqOpenId更新
     */
    public Integer updateUserInfoByQqOpenId(UserInfo bean, String qqOpenId) {
        return this.userInfoMapper.updateByQqOpenId(bean, qqOpenId);
    }

    /**
     * 根据QqOpenId删除
     */
    public Integer deleteUserInfoByQqOpenId(String qqOpenId) {
        return this.userInfoMapper.deleteByQqOpenId(qqOpenId);
    }

    /**
     * 根据Nickname查询
     */
    public UserInfo getUserInfoByNickname(String nickname) {
        return this.userInfoMapper.selectByNickname(nickname);
    }

    /**
     * 根据Nickname更新
     */
    public Integer updateUserInfoByNickname(UserInfo bean, String nickname) {
        return this.userInfoMapper.updateByNickname(bean, nickname);
    }

    /**
     * 根据Nickname删除
     */
    public Integer deleteUserInfoByNickname(String nickname) {
        return this.userInfoMapper.deleteByNickname(nickname);
    }
    /**
     * 登录
     **/
    @Override
    public SessionWebUserDto login(String email, String password) {
        UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
        if (userInfo == null) {
            throw new BusinessException("该用户不存在");
        }
        String userPassword = userInfo.getPassword();
        if (password.equals(userPassword)) {
            if (userInfo.getStatus() == 0) {
                throw new BusinessException("账户被禁用，无法登录");
            }
            UserInfo updateUserInfo = new UserInfo();
            updateUserInfo.setLastLoginTime(new Date());
            this.userInfoMapper.updateByUserId(updateUserInfo, userInfo.getUserId());
            SessionWebUserDto userDto = new SessionWebUserDto();
            BeanUtils.copyProperties(userInfo, userDto);
            if (ArrayUtils.contains(appConfig.getAdminEmails().split(","), userInfo.getEmail())) {
                userDto.setIsAdmin(true);
            } else {
                userDto.setIsAdmin(false);
            }
            userDto.setAvatar(userInfo.getQqAvatar());
            UserSpaceDto userSpaceDto = new UserSpaceDto();
            userSpaceDto.setUseSpace(userInfo.getUseSpace());
            userSpaceDto.setTotalSpace(userInfo.getTotalSpace());
            redisComponent.saveUserSpaceUse(userInfo.getUserId(), userSpaceDto);
            return userDto;
        } else {
            throw new BusinessException("密码错误");
        }

    }

    /**
     * @date: 2023/7/22 22:41
     * 用户注册。检验验证码是否正确，之后插入用户信息
     **/
    @Override
    public void register(String email, String nickName, String password, String emailCode) {
        UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
        if (userInfo != null) {
            throw new BusinessException("用户邮箱已被注册");
        }
        UserInfo nickNameUser = this.userInfoMapper.selectByNickname(nickName);
        if (null != nickNameUser) {
            throw new BusinessException("昵称已经存在");
        }
        emailCodeService.checkCode(email, emailCode);
        // 获取随机Id
        String userId = StringUtils.getRandomNumber(Constants.LENGTH_10);
        userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setNickname(nickName);
        userInfo.setEmail(email);
        userInfo.setJoinTime(new Date());
        userInfo.setUseSpace(0L);
        userInfo.setStatus(UserStatusEnum.ENABLE.getStatus());
        userInfo.setPassword(StringUtils.encodeByMD5(password));
        SysSettingsDto sysSettingDto = redisComponent.getSysSettingDto();
        userInfo.setTotalSpace(sysSettingDto.getUserInitUseSpace() * Constants.MB);
        this.userInfoMapper.insert(userInfo);
    }

    /**
     * @date: 2023/7/23 20:18
     * 重置密码
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPwd(String email, String password, String code) {
        UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
        if (userInfo == null) {
            throw new BusinessException("用户邮箱不存在");
        }
        this.emailCodeService.checkCode(email, code);
        UserInfo updateInfo = new UserInfo();
        updateInfo.setPassword(StringUtils.encodeByMD5(password));
        this.userInfoMapper.updateByEmail(updateInfo, email);
    }
}
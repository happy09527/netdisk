package com.easypan.component;

import com.easypan.utils.RedisUtils;
import com.easypan.entity.constants.Constants;
import com.easypan.entity.dto.SysSettingsDto;
import com.easypan.entity.dto.UserSpaceDto;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author: ZhangX
 * @createDate: 2023/7/22
 * @description:
 */
@Component("redisComponent")
public class RedisComponent {
    @Resource
    private RedisUtils redisUtils;

    /**
     * @date: 2023/7/23 21:28
     * 获取用户系统信息。包括邮件验证码，初始化内存大小
     **/
    public SysSettingsDto getSysSettingDto() {
        SysSettingsDto sysSettingsDto = (SysSettingsDto) redisUtils.get(Constants.REDIS_KEY_SYS_SETTING);
        if (sysSettingsDto == null) {
            sysSettingsDto = new SysSettingsDto();
            redisUtils.set(Constants.REDIS_KEY_SYS_SETTING, sysSettingsDto);
        }
        return sysSettingsDto;
    }

    /**
     * @date: 2023/7/23 21:30
     * 存储用户内存信息，key为Constants.REDIS_KEY_USER_SPACE_USE + userId
     **/
    public void saveUserSpaceUse(String userId, UserSpaceDto userSpaceDto) {
        redisUtils.setExpires(Constants.REDIS_KEY_USER_SPACE_USE + userId,
                userSpaceDto, Constants.REDIS_KEY_EXPIRES_DAY);
    }

    /**
     * @date: 2023/7/23 21:29
     * 获取用户内存信息
     **/
    public UserSpaceDto getUserSpaceUse(String userId) {
        UserSpaceDto userSpaceDto = (UserSpaceDto) redisUtils.get(Constants.REDIS_KEY_USER_SPACE_USE + userId);
        if (userSpaceDto == null) {
            userSpaceDto = new UserSpaceDto();
            // TODO 查询当前用户内存信息
            userSpaceDto.setUseSpace(0L);
            userSpaceDto.setTotalSpace(getSysSettingDto().getUserInitUseSpace() * Constants.MB);
            saveUserSpaceUse(userId, userSpaceDto);
        }
        return userSpaceDto;
    }
}

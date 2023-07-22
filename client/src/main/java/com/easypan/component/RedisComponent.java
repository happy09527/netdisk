package com.easypan.component;

import com.easypan.config.RedisConfig;
import com.easypan.entity.constants.Constants;
import com.easypan.entity.dto.SysSettingsDto;
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
    private RedisConfig redisConfig;

    public SysSettingsDto getSysSettingDto() {
        SysSettingsDto sysSettingsDto = (SysSettingsDto) redisConfig.get(Constants.REDIS_KEY_SYS_SETTING);
        if (sysSettingsDto == null) {
            sysSettingsDto = new SysSettingsDto();
            redisConfig.set(Constants.REDIS_KEY_SYS_SETTING,sysSettingsDto);
        }
        return sysSettingsDto;
    }
}
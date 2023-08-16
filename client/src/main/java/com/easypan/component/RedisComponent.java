package com.easypan.component;

import com.easypan.entity.pojo.FileInfo;
import com.easypan.entity.query.FileInfoQuery;
import com.easypan.mappers.FileInfoMapper;
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
    @Resource
    private FileInfoMapper<FileInfo, FileInfoQuery> fileInfoMapper;

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
            userSpaceDto.setUseSpace(fileInfoMapper.selectUseSpace(userId));
            userSpaceDto.setTotalSpace(getSysSettingDto().getUserInitUseSpace() * Constants.MB);
            saveUserSpaceUse(userId, userSpaceDto);
        }
        return userSpaceDto;
    }

    /**
     * @date: 2023/7/25 11:39
     * 存储临时文件所占空间大小
     **/
    public void saveFileTempSize(String userId, String fileId, Long fileSize) {
        long currentSize = getFileTempSize(userId, fileId);
        redisUtils.setExpires(Constants.REDIS_KEY_USER_FILE_TEMP_SIZE + userId + fileId, currentSize + fileSize, Constants.REDIS_KEY_EXPIRES_ONE_HOUR);
    }

    /**
     * @date: 2023/7/25 11:34
     * 获取上传文件临时存储空间大小
     **/
    public Long getFileTempSize(String userId, String fileId) {
        Long currentSize = getFileSizeFromRedis(Constants.REDIS_KEY_USER_FILE_TEMP_SIZE + userId + fileId);
        return currentSize;
    }

    /**
     * @date: 2023/7/25 11:35
     * 从redis获取信息。
     **/
    private Long getFileSizeFromRedis(String key) {
        Object sizeObject = redisUtils.get(key);
        if (sizeObject == null) {
            return 0L;
        }
        if (sizeObject instanceof Integer) {
            return ((Integer) sizeObject).longValue();
        } else if (sizeObject instanceof Long) {
            return (Long) sizeObject;
        }
        return 0L;
    }

    public void removeFileTempSize(String userId, String fileId) {
        redisUtils.delete(Constants.REDIS_KEY_USER_FILE_TEMP_SIZE + userId + fileId);
    }
}

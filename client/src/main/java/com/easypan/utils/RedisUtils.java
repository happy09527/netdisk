package com.easypan.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * @author: ZhangX
 * @createDate: 2023/7/22
 * @description:
 */
@Component("redisUtils")
public class RedisUtils<V> {
    private static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);
    @Resource
    private RedisTemplate<String, V> redisTemplate;

    public V get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    public boolean set(String key, V value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            logger.error("设置redis key{}，value{}失败", key, value);
            return false;
        }
    }
    /**
     * @date: 2023/7/23 21:19
     * 存储有过期时间的键值对
     **/
    public boolean setExpires(String key, V value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            logger.error("设置redis Key：{}，value：{}失败", key, value);
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    public void delete(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete((Collection<String>) CollectionUtils.arrayToList(key));
            }
        }
    }
}

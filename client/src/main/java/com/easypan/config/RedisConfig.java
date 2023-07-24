package com.easypan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author: ZhangX
 * @createDate: 2023/7/24
 * @description: redis配置类。key的序列化配置
 */
@Configuration
public class RedisConfig<V> {
    @Bean
    public RedisTemplate<String, V> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, V> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        // 设置key的序列化方式
        template.setKeySerializer(RedisSerializer.string());
        // 设置value序列化方式
        template.setValueSerializer(RedisSerializer.json());
        // 设置hash key序列化方式
        template.setHashKeySerializer(RedisSerializer.string());
        // 设置hash value序列化方式
        template.setHashValueSerializer(RedisSerializer.json());
        template.afterPropertiesSet();
        return template;
    }
}

package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author : Real
 * @date : 2021/12/8 14:13
 * @description : redis 配置类
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);

        // 设置 key 的序列化方式
        redisTemplate.setKeySerializer(RedisSerializer.string());
        // 设置 value 的序列化方式
        redisTemplate.setValueSerializer(RedisSerializer.json());
        // 设置 hash 的 key 序列化方式
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        // 设置 hash 的 value 序列化方式
        redisTemplate.setHashValueSerializer(RedisSerializer.json());

        // 触发参数生效
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

}

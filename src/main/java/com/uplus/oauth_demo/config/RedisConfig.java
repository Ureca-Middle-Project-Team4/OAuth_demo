// src/main/java/com/example/demo/config/RedisConfig.java
package com.uplus.oauth_demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;


@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // localhost:6379 기본값 사용
        return new LettuceConnectionFactory();
    }
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory cf) {
        RedisTemplate<String, Object> rt = new RedisTemplate<>();
        rt.setConnectionFactory(cf);

        // 키는 문자열로
        rt.setKeySerializer(new StringRedisSerializer());
        rt.setHashKeySerializer(new StringRedisSerializer());

        // 값은 Java 직렬화 방식으로
        JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer();
        rt.setValueSerializer(jdkSerializer);
        rt.setHashValueSerializer(jdkSerializer);

        rt.afterPropertiesSet();
        return rt;
    }
}

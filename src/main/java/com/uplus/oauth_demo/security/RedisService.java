package com.uplus.oauth_demo.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
public class RedisService {
    private final ValueOperations<String, Object> ops;
    private final Duration refreshTtl;

    public RedisService(org.springframework.data.redis.core.RedisTemplate<String,Object> redisTemplate,
                        @Value("${app.auth.refreshTokenExpirationMsec}") long refreshMs) {
        this.ops = redisTemplate.opsForValue();
        this.refreshTtl = Duration.ofMillis(refreshMs);
    }

    public void storeRefreshToken(String userId, String refreshToken) {
        ops.set("refresh_token:" + userId, refreshToken, refreshTtl);
    }

    public String getRefreshToken(String userId) {
        return (String) ops.get("refresh_token:" + userId);
    }
}
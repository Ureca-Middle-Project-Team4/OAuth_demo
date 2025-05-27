package com.uplus.oauth_demo.controller;

import com.uplus.oauth_demo.security.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class TokenController {
    private final JwtTokenProvider jwtProvider;
    private final RedisService redisService;

    public TokenController(JwtTokenProvider jwtProvider,
                           RedisService redisService) {
        this.jwtProvider = jwtProvider;
        this.redisService = redisService;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestParam String userId,
                                     @RequestParam String refreshToken) {
        String saved = redisService.getRefreshToken(userId);
        if (saved == null || !saved.equals(refreshToken) || !jwtProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(401).body("Invalid refresh token");
        }
        String newAccess = jwtProvider.createAccessToken(userId);
        return ResponseEntity.ok("{\"accessToken\":\"" + newAccess + "\"}");
    }
}
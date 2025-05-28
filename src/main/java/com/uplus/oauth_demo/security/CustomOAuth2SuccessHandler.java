package com.uplus.oauth_demo.security;

import jakarta.servlet.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider jwtProvider;
    private final RedisService redisService;

    public CustomOAuth2SuccessHandler(JwtTokenProvider jwtProvider,
                                      RedisService redisService) {
        this.jwtProvider = jwtProvider;
        this.redisService = redisService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req,
                                        HttpServletResponse res,
                                        Authentication auth) throws IOException {
        OAuth2User user = (OAuth2User) auth.getPrincipal();
        String userId = user.getAttribute("id"); // 구글의 경우 'sub', 카카오는 'id' 등
        // TODO: users 테이블에 userId, email, name 저장/업데이트

        String accessToken = jwtProvider.createAccessToken(userId);
        String refreshToken = jwtProvider.createRefreshToken(userId);
        // Redis에 저장
        System.out.println("[DEBUG] storeRefreshToken 호출: userId=" + userId + ", refreshToken=" + refreshToken);
        redisService.storeRefreshToken(userId, refreshToken);

        // 클라이언트에 토큰 전달 (예: JSON 응답)
        res.setContentType("application/json");
        res.getWriter().write(
                "{\"accessToken\":\"" + accessToken + "\",\"refreshToken\":\"" + refreshToken + "\"}"
        );
    }
}
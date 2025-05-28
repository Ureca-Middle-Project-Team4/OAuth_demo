package com.uplus.oauth_demo.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
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
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User user = oauthToken.getPrincipal();
        String regId = oauthToken.getAuthorizedClientRegistrationId();

        String userId = "google".equals(regId)
                ? user.getAttribute("sub")
                : user.getAttribute("id");
        if (userId == null) userId = authentication.getName();

        String accessToken  = jwtProvider.createAccessToken(userId);
        String refreshToken = jwtProvider.createRefreshToken(userId);
        redisService.storeRefreshToken(userId, refreshToken);

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(
                "{\"accessToken\":\""  + accessToken  + "\"," +
                        "\"refreshToken\":\"" + refreshToken + "\"}"
        );
        response.getWriter().flush();      // ★
        response.flushBuffer();            // ★
        return;                            // ★ 핸들러 종료
    }
}
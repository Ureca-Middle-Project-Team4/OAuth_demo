// src/main/java/com/example/demo/config/SecurityConfig.java
package com.uplus.oauth_demo.config;

import com.uplus.oauth_demo.security.CustomOAuth2SuccessHandler;
import com.uplus.oauth_demo.security.CustomOAuth2UserService;
import com.uplus.oauth_demo.security.RedisAuthorizationRequestRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CustomOAuth2UserService customUserService;
    private final CustomOAuth2SuccessHandler successHandler;

    public SecurityConfig(RedisTemplate<String, Object> redisTemplate,
                          CustomOAuth2UserService customuserService,
                          CustomOAuth2SuccessHandler successHandler) {
        this.redisTemplate = redisTemplate;
        this.customUserService = customuserService;
        this.successHandler = successHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Redis에 state 저장/조회용 리포지토리
        var authorizationRepo = new RedisAuthorizationRequestRepository(redisTemplate);

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 로그인·콜백·리프레시 토큰 엔드포인트는 공개
                        .requestMatchers(
                                "/",
                                "/login**",
                                "/oauth2/**",
                                "/css/**",
                                "/auth/refresh"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        // Redis 저장소 적용
                        .authorizationEndpoint(endpoint ->
                                endpoint.authorizationRequestRepository(authorizationRepo)
                        )
                        .userInfoEndpoint(u -> u
                                .userService(customUserService)
                        )
                        // 로그인 성공 시 JWT 발급 핸들러 적용
                        .successHandler(successHandler)
                );

        return http.build();
    }
}
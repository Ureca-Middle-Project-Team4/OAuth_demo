// src/main/java/com/example/demo/config/SecurityConfig.java
package com.uplus.oauth_demo.config;

import com.uplus.oauth_demo.security.CustomOAuth2SuccessHandler;
import com.uplus.oauth_demo.security.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRepo;
    private final CustomOAuth2UserService customUserService;
    private final CustomOAuth2SuccessHandler successHandler;

    // ↓ RedisTemplate 대신 AuthorizationRequestRepository 를 주입받도록 변경
    public SecurityConfig(
            AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRepo,
            CustomOAuth2UserService customUserService,
            CustomOAuth2SuccessHandler successHandler
    ) {
        this.authorizationRepo = authorizationRepo;
        this.customUserService = customUserService;
        this.successHandler    = successHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login**", "/oauth2/**", "/css/**", "/auth/refresh")
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(endpoint ->
                                // ↓ 새로 만든 빈을 여기서 바로 사용
                                endpoint.authorizationRequestRepository(authorizationRepo)
                        )
                        .userInfoEndpoint(u -> u
                                .userService(customUserService)
                        )
                        .successHandler(successHandler)
                );

        return http.build();
    }
}
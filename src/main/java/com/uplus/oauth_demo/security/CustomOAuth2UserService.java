package com.uplus.oauth_demo.security;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class CustomOAuth2UserService
        implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = delegate.loadUser(userRequest);
        String regId = userRequest.getClientRegistration().getRegistrationId();
        Map<String,Object> attrs = user.getAttributes();

        String userId;
        String email;
        String name;

        switch (regId) {
            case "google":
                userId = (String) attrs.get("sub");
                email  = (String) attrs.get("email");
                name   = (String) attrs.get("name");
                break;
            case "kakao":
                userId = String.valueOf(attrs.get("id"));
                Map<String,Object> kakaoAccount = (Map) attrs.get("kakao_account");
                email  = (String) kakaoAccount.get("email");
                Map<String,Object> profile = (Map) kakaoAccount.get("profile");
                name   = (String) profile.get("nickname");
                break;
            case "naver":
                Map<String,Object> response = (Map) attrs.get("response");
                userId = (String) response.get("id");
                email  = (String) response.get("email");
                name   = (String) response.get("name");
                break;
            default:
                throw new OAuth2AuthenticationException("Unknown provider: " + regId);
        }

        // 권한은 기본 ROLE_USER
        return new DefaultOAuth2User(
                user.getAuthorities(),
                Map.of(
                        "id", userId,
                        "email", email,
                        "name", name
                ),
                "id"
        );
    }
}
package com.tunduh.timemanagement.config;

import com.tunduh.timemanagement.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.tunduh.timemanagement.repository.UserRepository;
import com.tunduh.timemanagement.security.JwtTokenProvider;
import com.tunduh.timemanagement.security.OAuth2AuthenticationSuccessHandler;
import com.tunduh.timemanagement.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class OAuth2Config {

    @Bean
    public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler(
            JwtTokenProvider jwtTokenProvider,
            HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository) {
        return new OAuth2AuthenticationSuccessHandler(jwtTokenProvider, httpCookieOAuth2AuthorizationRequestRepository);
    }

    @Bean
    public CustomOAuth2UserService customOAuth2UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return new CustomOAuth2UserService(userRepository, passwordEncoder);
    }

    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }
}
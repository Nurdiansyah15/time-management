package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.entity.Role;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.repository.UserRepository;
import com.tunduh.timemanagement.security.CustomOAuth2User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomOAuth2UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oauth2User);
        } catch (Exception ex) {
            logger.error("Error processing OAuth2 user", ex);
            OAuth2Error oauth2Error = new OAuth2Error("processing_error", "Error processing OAuth2 user", null);
            throw new OAuth2AuthenticationException(oauth2Error, ex.getMessage(), ex);
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String email = getEmail(oauth2User);
        String name = getName(oauth2User);

        if (email == null || name == null) {
            logger.error("Email or name not provided by OAuth2 provider");
            throw new OAuth2AuthenticationException("Email or name not provided by OAuth2 provider");
        }

        logger.info("Processing OAuth2 user: email={}, name={}", email, name);

        UserEntity user = userRepository.findByEmail(email)
                .map(existingUser -> updateExistingUser(existingUser, oauth2User))
                .orElseGet(() -> registerNewUser(userRequest, oauth2User));

        return new CustomOAuth2User(user, oauth2User.getAttributes());
    }

    private UserEntity updateExistingUser(UserEntity existingUser, OAuth2User oauth2User) {
        logger.info("Updating existing user: {}", existingUser.getEmail());
        existingUser.setUsername(getName(oauth2User));
        return userRepository.save(existingUser);
    }

    private UserEntity registerNewUser(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        logger.info("Registering new user: {}", getEmail(oauth2User));
        UserEntity user = new UserEntity();
        user.setEmail(getEmail(oauth2User));
        user.setUsername(getName(oauth2User));
        user.setPassword(passwordEncoder.encode(generateRandomPassword()));
        user.setRole(Role.ROLE_USER);
        return userRepository.save(user);
    }

    private String getEmail(OAuth2User oauth2User) {
        return (String) oauth2User.getAttributes().get("email");
    }

    private String getName(OAuth2User oauth2User) {
        return (String) oauth2User.getAttributes().get("name");
    }

    private String generateRandomPassword() {
        return UUID.randomUUID().toString();
    }
}
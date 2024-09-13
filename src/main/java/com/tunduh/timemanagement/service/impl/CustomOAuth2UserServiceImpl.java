package com.tunduh.timemanagement.service.impl;

import com.tunduh.timemanagement.entity.Role;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.repository.UserRepository;
import com.tunduh.timemanagement.security.CustomOAuth2User;
import com.tunduh.timemanagement.service.CustomOAuth2UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserServiceImpl extends DefaultOAuth2UserService implements CustomOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oauth2User);
        } catch (Exception ex) {
            logger.error("Error processing OAuth2 user", ex);
            throw new OAuth2AuthenticationException(ex.getMessage());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String email = getEmail(oauth2User);
        String name = getName(oauth2User);

        UserEntity user = userRepository.findByEmail(email)
                .map(existingUser -> updateExistingUser(existingUser, oauth2User))
                .orElseGet(() -> registerNewUser(userRequest, oauth2User));

        return new CustomOAuth2User(user, oauth2User.getAttributes());
    }

    private UserEntity updateExistingUser(UserEntity existingUser, OAuth2User oauth2User) {
        existingUser.setUsername(getName(oauth2User));
        return userRepository.save(existingUser);
    }

    private UserEntity registerNewUser(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        UserEntity user = new UserEntity();
        user.setEmail(getEmail(oauth2User));
        user.setUsername(getName(oauth2User));
        user.setRole(Role.ROLE_USER);
        return userRepository.save(user);
    }

    private String getEmail(OAuth2User oauth2User) {
        return (String) oauth2User.getAttributes().get("email");
    }

    private String getName(OAuth2User oauth2User) {
        return (String) oauth2User.getAttributes().get("name");
    }
}

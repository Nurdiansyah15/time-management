package com.tunduh.timemanagement.security;

import enigma.travelwise.model.UserEntity;
import enigma.travelwise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSecurity {
    private final UserRepository userRepository;

    public boolean hasUserId(Authentication authentication, Long userId) {
        UserEntity user = userRepository.findByUsername(authentication.getName()).orElse(null);
        Long currentUserId = user.getId();
        System.out.println("bbbbb");
        System.out.println(currentUserId);
        System.out.println(userId);
        return currentUserId.equals(userId);
    }

    public boolean isUser(Authentication authentication, int userId) {
//      jika user yang login null atau bukan authenticated, kembalikan false
        System.out.println("cek " + authentication.isAuthenticated());
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
//      mengambil principal/user dari authentication
        Object principal = authentication.getPrincipal();
        System.out.println(principal instanceof UserEntity);
//      jika principal bukan instance dari class User, kembalikan false

        if (!(principal instanceof User)) {
            return false;
        }
        UserEntity user = (UserEntity) principal;
//      jika user id yang login sesuai dengan user id yang ingin diakses, kembalikan true
        return user.getId() == userId;
    }
}

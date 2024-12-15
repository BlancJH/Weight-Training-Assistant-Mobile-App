package com.blancJH.weight_assistant_mobile_app_backend.service;

import com.blancJH.weight_assistant_mobile_app_backend.UserRepository;
import com.blancJH.weight_assistant_mobile_app_backend.MemoryUserRepository;

public class UserService {

    private final UserRepository userRepository = new MemoryUserRepository();

    /**
     * User Register method
     */
    public long join(User user) {
        // Does not allow duplicated email
        validateDuplicateUser(user); // Check email duplication
        userRepository.save(user);
        return user.getId();
    }

    private void validateDuplicateUser(User user) {
        userRepository.findByEmail(user.getEmail())
            .ifPresent(m -> {
            throw new IllegalStateException("The email is already registered.");
            });
    }
}


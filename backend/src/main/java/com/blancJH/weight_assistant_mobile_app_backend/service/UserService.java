package com.blancJH.weight_assistant_mobile_app_backend.service;

import com.blancJH.weight_assistant_mobile_app_backend.model.User;
import com.blancJH.weight_assistant_mobile_app_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String registerUser(User user) {
        // Validate duplicate email
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalStateException("Email is already taken.");
        }

        // Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        return "User registered successfully!";
    }

    public String loginUser(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return "Invalid email or password!";
        }

        if (passwordEncoder.matches(password, user.get().getPassword())) {
            return "Login successful!";
        } else {
            return "Invalid email or password!";
        }
    }
}

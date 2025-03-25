package com.blancJH.weight_assistant_mobile_app_backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.blancJH.weight_assistant_mobile_app_backend.model.User;
import com.blancJH.weight_assistant_mobile_app_backend.repository.UserRepository;
import com.blancJH.weight_assistant_mobile_app_backend.util.JwtUtil;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public String registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalStateException("Email is already taken.");
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalStateException("Username is already taken.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "User registered successfully!";
    }

    public String loginUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
                logger.error("Invalid Email", email);


        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.error("Invalid Password for User", user);
            throw new IllegalArgumentException("Invalid email or password");
        }

            // Check if the user's account is active
        if (!user.isActive()) {
            logger.error("The user account id deactivated", user);
            throw new IllegalArgumentException("Invalid email or password");
        }
        

        // Generate JWT Token
        return jwtUtil.generateToken(user.getId(), user.getUsername(), user.getProfileUrl());
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> {
                logger.error("User not found with email: {}", email);
                return new IllegalArgumentException("User not found");
            });
    }

    public User updateUser(Long userId, String newUsername, String profileUrl) {
        // Find the user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Update username if provided
        if (newUsername != null && !newUsername.isBlank()) {
            if (userRepository.findByUsername(newUsername).isPresent()) {
                throw new IllegalStateException("Username is already taken.");
            }
            user.setUsername(newUsername);
        }

        // Update profile picture URL if provided
        if (profileUrl != null) {
            user.setProfileUrl(profileUrl);
        }

        // Save and return the updated user
        return userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User not found with id: {}", id);
                    return new IllegalArgumentException("User not found");
                });
            }

    /**
     * Disables the user account by setting the active flag to false.
     *
     * @param userId the ID of the user to disable
     * @return the updated User entity
     * @throws IllegalArgumentException if the user is not found
     */
    public User disableUserAccount(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                logger.error("User not found with id: {}", userId);
                return new IllegalArgumentException("User not found");
            });
        user.setActive(false);
        return userRepository.save(user);
    }
}

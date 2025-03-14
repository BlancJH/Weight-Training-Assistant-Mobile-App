package com.blancJH.weight_assistant_mobile_app_backend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blancJH.weight_assistant_mobile_app_backend.model.User;
import com.blancJH.weight_assistant_mobile_app_backend.service.UserService;
import com.blancJH.weight_assistant_mobile_app_backend.service.UserSphereService;
import com.blancJH.weight_assistant_mobile_app_backend.util.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserSphereService userSphereService;

    @Autowired
    public AuthController(UserService userService, UserSphereService userSphereService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.userSphereService = userSphereService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String email = payload.get("email");
        String password = payload.get("password");

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);

        String result = userService.registerUser(user);
        if (result.equals("User registered successfully!")) {
            // Retrieve the created user
            User createdUser = userService.getUserByEmail(email);
            // Give default sphere "Rocky" to the user.
            userSphereService.giveDefaultSphereToUser(createdUser);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");

        // Authenticate user and get the token
        String token = userService.loginUser(email, password);

        // Fetch user details from the database
        User user = userService.getUserByEmail(email);

        // Return token and additional user info
        Map<String, Object> response = Map.of(
                "token", token,
                "username", user.getUsername(),
                "profileUrl", user.getProfileUrl() != null ? user.getProfileUrl() : "Default" // Send "Default" as string might need to be fixed later.
        );

        return ResponseEntity.ok(response);
    }
    
}
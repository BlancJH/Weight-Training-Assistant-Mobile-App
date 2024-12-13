package com.blancJH.weight_assistant_mobile_app_backend.controller;

import com.blancJH.weight_assistant_mobile_app_backend.model.User;
import com.blancJH.weight_assistant_mobile_app_backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String email = payload.get("email");
        String password = payload.get("password");

        String result = authService.registerUser(username, email, password);
        if (result.equals("User registered successfully!")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
}

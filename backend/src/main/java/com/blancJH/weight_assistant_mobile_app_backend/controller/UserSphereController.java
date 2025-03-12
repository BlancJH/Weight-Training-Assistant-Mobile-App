package com.blancJH.weight_assistant_mobile_app_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blancJH.weight_assistant_mobile_app_backend.model.UserSphere;
import com.blancJH.weight_assistant_mobile_app_backend.service.UserSphereService;
import com.blancJH.weight_assistant_mobile_app_backend.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user-spheres")
public class UserSphereController {

    private final UserSphereService userSphereService;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserSphereController(UserSphereService userSphereService, JwtUtil jwtUtil) {
        this.userSphereService = userSphereService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Retrieves spheres owned by the authenticated user.
     */
    @GetMapping
    public ResponseEntity<?> getUserSpheres(HttpServletRequest request) {
        try {
            String token = jwtUtil.extractTokenFromRequest(request);
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Invalid or expired token.");
            }

            Long userId = jwtUtil.extractUserId(token);
            List<UserSphere> userSpheres = userSphereService.getUserSpheres(userId);
            return ResponseEntity.ok(userSpheres);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("An error occurred: " + e.getMessage());
        }
    }

    /**
     * Levels up a sphere for the authenticated user if they possess at least 5 copies.
     */
    @PostMapping("/level-up/{sphereId}")
    public ResponseEntity<?> levelUpSphere(@PathVariable Long sphereId, HttpServletRequest request) {
        try {
            String token = jwtUtil.extractTokenFromRequest(request);
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Invalid or expired token.");
            }

            Long userId = jwtUtil.extractUserId(token);
            boolean success = userSphereService.upgradeSphere(userId, sphereId);
            if (success) {
                return ResponseEntity.ok("Sphere leveled up successfully!");
            } else {
                return ResponseEntity.badRequest().body("Insufficient spheres to level up.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body("An error occurred: " + e.getMessage());
        }
    }
}

package com.blancJH.weight_assistant_mobile_app_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blancJH.weight_assistant_mobile_app_backend.model.Sphere;
import com.blancJH.weight_assistant_mobile_app_backend.model.SphereCardPackRank;
import com.blancJH.weight_assistant_mobile_app_backend.model.User;
import com.blancJH.weight_assistant_mobile_app_backend.service.SpherePackService;
import com.blancJH.weight_assistant_mobile_app_backend.service.UserService;
import com.blancJH.weight_assistant_mobile_app_backend.service.UserSphereService;
import com.blancJH.weight_assistant_mobile_app_backend.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/sphere-packs")
public class SpherePackController {

    private final SpherePackService spherePackService;
    private final UserSphereService userSphereService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public SpherePackController(SpherePackService spherePackService, UserSphereService userSphereService, UserService userService, JwtUtil jwtUtil) {
        this.spherePackService = spherePackService;
        this.userSphereService = userSphereService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Generate a sphere pack and save it to the user's collection.
     */
    @PostMapping("/generate-and-save")
    public ResponseEntity<String> generateAndSaveSpherePack(
            @RequestParam("packType") String packTypeStr,
            @RequestParam(value = "packSize", defaultValue = "5") int packSize,
            HttpServletRequest request) {

        try {
            // Extract token from request header
            String token = jwtUtil.extractTokenFromRequest(request);

            // Validate the token
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Invalid or expired token.");
            }

            // Extract user ID from token
            Long userId = jwtUtil.extractUserId(token);
            User user = userService.findById(userId);

            if (user == null) {
                return ResponseEntity.badRequest().body("User not found.");
            }

            // Convert the pack type to enum
            SphereCardPackRank packType = SphereCardPackRank.valueOf(packTypeStr.toUpperCase());

            // Generate spheres
            List<Sphere> generatedPack = spherePackService.generateSpherePack(packType, packSize);

            // Save spheres to user's collection
            for (Sphere sphere : generatedPack) {
                userSphereService.addSphereToUser(user, sphere);
            }

            return ResponseEntity.ok("Sphere pack generated and saved successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid pack type.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred: " + e.getMessage());
        }
    }
}

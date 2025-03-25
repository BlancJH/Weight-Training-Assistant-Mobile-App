package com.blancJH.weight_assistant_mobile_app_backend.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blancJH.weight_assistant_mobile_app_backend.dto.UserSphereDTO;
import com.blancJH.weight_assistant_mobile_app_backend.model.UserSphere;
import com.blancJH.weight_assistant_mobile_app_backend.repository.UserSphereRepository;
import com.blancJH.weight_assistant_mobile_app_backend.service.UserSphereService;
import com.blancJH.weight_assistant_mobile_app_backend.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user-spheres")
public class UserSphereController {

    private static final Logger logger = LoggerFactory.getLogger(UserSphereController.class);

    private final UserSphereService userSphereService;
    private final UserSphereRepository userSphereRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserSphereController(UserSphereService userSphereService, JwtUtil jwtUtil, UserSphereRepository userSphereRepository) {
        this.userSphereService = userSphereService;
        this.jwtUtil = jwtUtil;
        this.userSphereRepository = userSphereRepository;
    }

    /**
     * Retrieves spheres owned by the authenticated user.
     */
    @GetMapping("/get-all")
    public ResponseEntity<?> getUserSphereDTOs(HttpServletRequest request) {
        try {
            String token = jwtUtil.extractTokenFromRequest(request);
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Invalid or expired token.");
            }
            Long userId = jwtUtil.extractUserId(token);
            List<UserSphereDTO> dtos = userSphereService.getUserSpheres(userId);
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("An error occurred: " + e.getMessage());
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
            logger.error("Error upgrading sphere: " + e.getMessage());
            return ResponseEntity.status(400).body("Error upgrading sphere");
        }
    }

    /**
     * Endpoint to update a sphere as a representator, ensuring only one can be true.
     */
    @PutMapping("/representator/set")
    public ResponseEntity<String> markAsRepresentator(HttpServletRequest request, @RequestParam Long sphereId) {
        // Extract the token from the request header.
        String token = jwtUtil.extractTokenFromRequest(request);
        
        // Optionally validate the token.
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token.");
        }
        
        // Extract the user id from the token.
        Long userId = jwtUtil.extractUserId(token);
        
        // Find the UserSphere record for the user and sphere.
        UserSphere userSphere = userSphereRepository.findByUserIdAndSphereId(userId, sphereId)
                .orElseThrow(() -> new IllegalArgumentException("UserSphere not found for given user and sphere"));
        
        // Update representator status.
        userSphereService.markAsRepresentator(userSphere.getId());
        return ResponseEntity.ok("UserSphere marked as representator successfully.");
    }

    /**
     * Endpoint to fetch the representator sphere for the authenticated user.
     * The JWT token is decoded to extract the user id.
     *
     * Example request: GET /api/user-spheres/representator/get with header "Authorization: Bearer <token>"
     */
    @GetMapping("/representator/get")
    public ResponseEntity<?> getRepresentator(HttpServletRequest request) {
        try {
            // Extract the JWT token from the Authorization header.
            String token = jwtUtil.extractTokenFromRequest(request);
            // Extract user id from the token.
            Long userId = jwtUtil.extractUserId(token);
            
            Optional<UserSphereDTO> representator = userSphereService.getRepresentator(userId);
            if (representator.isPresent()) {
                return ResponseEntity.ok(representator.get());
            } else {
                logger.warn("No representator found for user id: {}", userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Representator not found");
            }
        } catch (Exception e) {
            logger.error("Error fetching representator for user", e);
            // Return a generic error message to the client
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred. Please try again later.");
        }
    }
}

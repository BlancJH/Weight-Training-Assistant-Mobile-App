package com.blancJH.weight_assistant_mobile_app_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blancJH.weight_assistant_mobile_app_backend.dto.ExercisePreferenceRequest;
import com.blancJH.weight_assistant_mobile_app_backend.model.UserExercisePreference;
import com.blancJH.weight_assistant_mobile_app_backend.service.UserExercisePreferenceService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/preferences")
public class UserExercisePreferenceController {

    private final UserExercisePreferenceService preferenceService;

    public UserExercisePreferenceController(UserExercisePreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }

    @PostMapping("/update")
    public ResponseEntity<?> updatePreference(
            @RequestBody ExercisePreferenceRequest request,
            HttpServletRequest httpRequest
    ) {
        try {
            // Extract JWT token from Authorization Header
            String jwtToken = httpRequest.getHeader("Authorization").replace("Bearer ", "");

            // Update the preference
            UserExercisePreference updatedPreference = preferenceService.updateExercisePreference(
                    jwtToken,
                    request.getExerciseId(),
                    request.isFavorite(),
                    request.isDislike(),
                    request.getDislikeReason()
            );

            return ResponseEntity.ok(updatedPreference);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating preference: " + e.getMessage());
        }
    }
}

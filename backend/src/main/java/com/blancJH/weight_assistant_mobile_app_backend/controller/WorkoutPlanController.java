package com.blancJH.weight_assistant_mobile_app_backend.controller;

import com.blancJH.weight_assistant_mobile_app_backend.model.User;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPlan;
import com.blancJH.weight_assistant_mobile_app_backend.service.WorkoutPlanService;
import com.blancJH.weight_assistant_mobile_app_backend.service.ChatGptService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/workout-plans")
public class WorkoutPlanController {

    private final WorkoutPlanService workoutPlanService;
    private final ChatGptService chatGptService;

    public WorkoutPlanController(WorkoutPlanService workoutPlanService, ChatGptService chatGptService) {
        this.workoutPlanService = workoutPlanService;
        this.chatGptService = chatGptService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateAndSaveWorkoutPlan(@RequestBody Map<String, Object> userDetails) {
        try {
            // Call ChatGPT API
            String chatGptResponse = chatGptService.sendUserDetailsToChatGpt(userDetails);

            // Assume user is fetched or passed in the request
            User user = fetchAuthenticatedUser();

            // Save workout plan
            List<WorkoutPlan> workoutPlans = workoutPlanService.saveWorkoutPlanFromChatGptResponse(chatGptResponse, user);

            return ResponseEntity.ok(workoutPlans);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error generating or saving workout plan: " + e.getMessage());
        }
    }

    // Clear it later
    private User fetchAuthenticatedUser() {
        // Mocked or implemented logic for getting the authenticated user
        User user = new User();
        user.setId(1L); // Example ID
        return user;
    }

    // Complete workout
    @PostMapping("/{planId}/complete")
    public ResponseEntity<?> markWorkoutAsDone(@PathVariable Long planId) {
        try {
            workoutPlanService.markPlanAsDone(planId);
            return ResponseEntity.ok("Workout marked as done and saved to history");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error marking workout as done: " + e.getMessage());
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetWorkoutPlans(@RequestParam Long userId) {
        try {
            List<WorkoutPlan> updatedPlans = workoutPlanService.resetAndRescheduleWorkoutPlans(userId);
            return ResponseEntity.ok(updatedPlans);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}

package com.blancJH.weight_assistant_mobile_app_backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blancJH.weight_assistant_mobile_app_backend.model.User;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPlan;
import com.blancJH.weight_assistant_mobile_app_backend.service.ChatGptService;
import com.blancJH.weight_assistant_mobile_app_backend.service.UserService;
import com.blancJH.weight_assistant_mobile_app_backend.service.WorkoutPlanService;
import com.blancJH.weight_assistant_mobile_app_backend.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/api/v1/workout-plans")
public class WorkoutPlanController {

    private final WorkoutPlanService workoutPlanService;
    private final ChatGptService chatGptService;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public WorkoutPlanController(WorkoutPlanService workoutPlanService, ChatGptService chatGptService, JwtUtil jwtUtil, UserService userService) {
        this.workoutPlanService = workoutPlanService;
        this.chatGptService = chatGptService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateAndSaveWorkoutPlan(@RequestBody Map<String, Object> userDetails, 
                                                        HttpServletRequest request) {
        try {
            // Extract JWT token from request
            String token = jwtUtil.extractTokenFromRequest(request);

            // Extract userId from the token
            Long userId = jwtUtil.extractUserId(token);

            // Fetch user
            User user = userService.findById(userId);

            // Call ChatGPT API to generate workout plan
            String chatGptResponse = chatGptService.sendUserDetailsToChatGpt(userDetails);

            // Save workout plan
            List<WorkoutPlan> workoutPlans = workoutPlanService.saveWorkoutPlanFromChatGptResponse(chatGptResponse, user);

            return ResponseEntity.ok(workoutPlans);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error generating or saving workout plan: " + e.getMessage());
        }
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

    @PutMapping("/{planId}/edit")
    public ResponseEntity<?> editWorkoutPlan(
            @PathVariable Long planId,
            @RequestBody List<Map<String, Object>> updatedExercises) {
        try {
            WorkoutPlan updatedPlan = workoutPlanService.editWorkoutPlan(planId, updatedExercises);
            return ResponseEntity.ok(updatedPlan);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error editing workout plan: " + e.getMessage());
        }
    }

    @GetMapping("/get")
    public ResponseEntity<?> getWorkoutPlans(HttpServletRequest request) {
        try {
            // Extract JWT token from request
            String token = jwtUtil.extractTokenFromRequest(request);

            // Extract userId from the token
            Long userId = jwtUtil.extractUserId(token);

            // Fetch user's workout plans
            List<WorkoutPlan> workoutPlans = workoutPlanService.getWorkoutPlansByUserId(userId);

            return ResponseEntity.ok(workoutPlans);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error fetching workout plans: " + e.getMessage());
        }
    }
}

package com.blancJH.weight_assistant_mobile_app_backend.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blancJH.weight_assistant_mobile_app_backend.dto.WorkoutPlanDTO;
import com.blancJH.weight_assistant_mobile_app_backend.dto.WorkoutPlanExerciseDTO;
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

            // Check and delete any incomplete workout plans for the user
            workoutPlanService.deleteIncompleteWorkoutPlans(user);

            // Call ChatGPT API to generate workout plan
            String chatGptResponse = chatGptService.sendUserDetailsToChatGpt(userDetails);

            // Save workout plan
            List<WorkoutPlan> workoutPlans = workoutPlanService.saveChatgptWorkoutPlan(chatGptResponse, user);

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
            return ResponseEntity.ok("Workout marked as done");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error marking workout as done: " + e.getMessage());
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
    public ResponseEntity<List<WorkoutPlanDTO>> getWorkoutPlansForUser(HttpServletRequest request) {
        try {
            // Extract JWT token from the request header
            String token = jwtUtil.extractTokenFromRequest(request);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Extract userId from the token
            Long userId = jwtUtil.extractUserId(token);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Fetch workout plans using the extracted userId
            List<WorkoutPlan> workoutPlans = workoutPlanService.getWorkoutPlansByUserId(userId);

            // Map your WorkoutPlan entities to WorkoutPlanDTOs
            List<WorkoutPlanDTO> response = workoutPlans.stream().map(wp -> new WorkoutPlanDTO(
                    wp.getId(),
                    wp.getPlannedDate(),
                    wp.isStatus(),
                    wp.getSplitName(),
                    wp.getExercises().stream().map(ex -> new WorkoutPlanExerciseDTO(
                            ex.getExercise().getExerciseId(),
                            ex.getExercise().getExerciseName(),
                            ex.getExercise().getExerciseCategory() != null 
                                ? ex.getExercise().getExerciseCategory().toString() 
                                : null,
                            ex.getExercise().getPrimaryMuscle(),
                            ex.getExercise().getSecondaryMuscle(),
                            ex.getExercise().getExerciseGifUrl(),
                            ex.getSets(),
                            ex.getReps()
                    )).collect(Collectors.toList())
            )).collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(null);
        }
    }
}

package com.blancJH.weight_assistant_mobile_app_backend.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
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

import com.blancJH.weight_assistant_mobile_app_backend.dto.WorkoutPlanDTO;
import com.blancJH.weight_assistant_mobile_app_backend.dto.WorkoutPlanExerciseDTO;
import com.blancJH.weight_assistant_mobile_app_backend.model.User;
import com.blancJH.weight_assistant_mobile_app_backend.model.UserDetails;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPlan;
import com.blancJH.weight_assistant_mobile_app_backend.service.UserDetailsJsonService;
import com.blancJH.weight_assistant_mobile_app_backend.service.UserDetailsService;
import com.blancJH.weight_assistant_mobile_app_backend.service.UserService;
import com.blancJH.weight_assistant_mobile_app_backend.service.WorkoutPlanService;
import com.blancJH.weight_assistant_mobile_app_backend.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/api/v1/workout-plans")
public class WorkoutPlanController {

    private final WorkoutPlanService workoutPlanService;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final UserDetailsService userDetailsService; // Service to fetch UserDetails from DB
    private static final Logger logger = LoggerFactory.getLogger(WorkoutPlanService.class);

    public WorkoutPlanController(WorkoutPlanService workoutPlanService,
                                 JwtUtil jwtUtil,
                                 UserService userService,
                                 UserDetailsService userDetailsService,
                                 UserDetailsJsonService userDetailsJsonService) {
        this.workoutPlanService = workoutPlanService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateAndSaveWorkoutPlan(HttpServletRequest request) {
        try {
            // 1. Extract JWT token from request header.
            String token = jwtUtil.extractTokenFromRequest(request);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                     .body("JWT token is missing or invalid.");
            }

            // 2. Extract userId from the token.
            Long userId = jwtUtil.extractUserId(token);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                     .body("Unable to extract user ID from token.");
            }

            // 3. Fetch the user from the database.
            User user = userService.findById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                     .body("User not found.");
            }
            
            // 4. Delete any incomplete workout plans for the user.
            workoutPlanService.deleteIncompleteWorkoutPlans(user);

            // 5. Fetch user details from the database.
            Optional<UserDetails> userDetailsOpt = userDetailsService.findByUserId(userId);
            if (!userDetailsOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                     .body("User details not found.");
            }

            // 6. Create the workout plan using the retrieved user details.
            // This service call creates workout plans (one per split) with their corresponding exercises.
            List<WorkoutPlan> workoutPlans = workoutPlanService.createWorkoutPlans(userDetailsOpt.get());

            // 7. Return the generated workout plan.
            return ResponseEntity.ok(workoutPlans);
        } catch (Exception e) {
            logger.error("Error generating or saving workout plan", e);
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
    public ResponseEntity<List<WorkoutPlanDTO>> getWorkoutPlansForUser(
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            HttpServletRequest request) {
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

            // If a date is provided, filter the plans by plannedDate
            if (date != null) {
                workoutPlans = workoutPlans.stream()
                        .filter(wp -> wp.getPlannedDate() != null && wp.getPlannedDate().isEqual(date))
                        .collect(Collectors.toList());
            }

            // Map WorkoutPlan entities to WorkoutPlanDTOs
            List<WorkoutPlanDTO> response = workoutPlans.stream().map(wp -> new WorkoutPlanDTO(
                    wp.getId(),
                    wp.getPlannedDate(),
                    wp.isStatus(),
                    wp.getWorkoutSplitCategory() != null ? wp.getWorkoutSplitCategory().toString() : null,
                    wp.getExercises().stream().map(ex -> new WorkoutPlanExerciseDTO(
                            ex.getExercise().getId(),
                            ex.getExercise().getExerciseName(),
                            ex.getExercise().getExerciseCategory() != null
                                    ? ex.getExercise().getExerciseCategory().toString()
                                    : null,
                            ex.getExercise().getPrimaryMuscle(),
                            ex.getExercise().getSecondaryMuscle(),
                            ex.getExercise().getExerciseGifUrl(),
                            ex.getSets(),
                            ex.getReps(),
                            ex.getDuration()
                    )).collect(Collectors.toList())
            )).collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching workout plans", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}

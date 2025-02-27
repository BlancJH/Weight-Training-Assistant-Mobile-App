package com.blancJH.weight_assistant_mobile_app_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blancJH.weight_assistant_mobile_app_backend.model.Exercise;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPlanExercise;
import com.blancJH.weight_assistant_mobile_app_backend.service.ExerciseService;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;

    @Autowired
    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<Exercise>> searchExercises(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String primaryMuscle) {

        if (name == null && primaryMuscle == null) {
            // Return a bad request response or an empty list
            return ResponseEntity.badRequest().build();
            // Alternatively: return ResponseEntity.ok(Collections.emptyList());
        }

        List<Exercise> results;
        if (name != null && primaryMuscle != null) {
            results = exerciseService.searchExercises(name, primaryMuscle);
        } else if (name != null) {
            results = exerciseService.searchExercisesByName(name);
        } else {
            results = exerciseService.searchExercisesByPrimaryMuscle(primaryMuscle);
        }

        return ResponseEntity.ok(results);
    }

    @GetMapping("/by-workout-plan/{workoutPlanId}")
    public ResponseEntity<?> getExercisesByWorkoutPlanId(@PathVariable Long workoutPlanId) {
        List<WorkoutPlanExercise> exercises = exerciseService.getExercisesByWorkoutPlanId(workoutPlanId);
        return ResponseEntity.ok(exercises);
    }
}

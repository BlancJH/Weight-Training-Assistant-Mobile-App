package com.blancJH.weight_assistant_mobile_app_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blancJH.weight_assistant_mobile_app_backend.model.Exercise;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutSplitCategory;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    Optional<Exercise> findByExerciseCategory(String exerciseCategory);
    List<Exercise> findByPrimaryMuscle(String primaryMuscle);
    List<Exercise> findByWorkoutSplitCategory(WorkoutSplitCategory workoutSplitCategory);
    List<Exercise> findByExerciseNameContainingIgnoreCase(String searchTerm);
    Optional<Exercise> findByExerciseName(String exerciseName);

    // Search by exercise name and primary muscle
    List<Exercise> findByExerciseNameContainingIgnoreCaseAndPrimaryMuscle(String searchTerm, String primaryMuscle);

}

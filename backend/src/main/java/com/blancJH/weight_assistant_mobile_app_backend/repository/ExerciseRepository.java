package com.blancJH.weight_assistant_mobile_app_backend.repository;

import com.blancJH.weight_assistant_mobile_app_backend.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    Optional<Exercise> findByExerciseName(String exerciseName);
    Optional<Exercise> findByExerciseCategory(String exerciseCategory);
    Optional<Exercise> findByMuscles(String muscles);
}

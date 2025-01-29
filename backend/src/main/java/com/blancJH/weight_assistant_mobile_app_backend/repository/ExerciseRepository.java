package com.blancJH.weight_assistant_mobile_app_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blancJH.weight_assistant_mobile_app_backend.model.Exercise;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    Optional<Exercise> findByExerciseName(String exerciseName);
    Optional<Exercise> findByExerciseCategory(String exerciseCategory);
    Optional<Exercise> findByMuscles(String muscles);

}

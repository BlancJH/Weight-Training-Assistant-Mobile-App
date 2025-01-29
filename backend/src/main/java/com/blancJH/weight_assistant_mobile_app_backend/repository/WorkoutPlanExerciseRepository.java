package com.blancJH.weight_assistant_mobile_app_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPlanExercise;

public interface WorkoutPlanExerciseRepository extends JpaRepository<WorkoutPlanExercise, Long> {
    
    List<WorkoutPlanExercise> findByWorkoutPlan_Id(Long workoutPlanId);
}

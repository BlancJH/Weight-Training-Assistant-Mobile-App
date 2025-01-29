package com.blancJH.weight_assistant_mobile_app_backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPlan;

@Repository
public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Long> {

    // Find all workout plans for a specific user
    List<WorkoutPlan> findByUserId(Long userId);

    // Find all workout plans for a specific user and status
    List<WorkoutPlan> findByUserIdAndStatus(Long userId, boolean status);

    // Find a workout plan by its ID and user ID (to ensure it belongs to the user)
    Optional<WorkoutPlan> findByIdAndUserId(Long workoutPlanId, Long userId);

    // Find all workout plans scheduled for a specific date
    List<WorkoutPlan> findByPlannedDate(LocalDate plannedDate);

    // Find all workout plans with status false
    List<WorkoutPlan> findByUserIdAndStatusFalse(Long userId);
}

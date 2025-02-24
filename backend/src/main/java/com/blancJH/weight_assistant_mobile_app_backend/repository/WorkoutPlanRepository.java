package com.blancJH.weight_assistant_mobile_app_backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPlan;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPlanStatus;

@Repository
public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Long> {

    // Find all workout plans for a specific user
    @Query("SELECT wp FROM WorkoutPlan wp JOIN FETCH wp.exercises WHERE wp.user.id = :userId")
    List<WorkoutPlan> findByUserId(Long userId);

    // Find all workout plans for a specific user and status
    @Query("SELECT wp FROM WorkoutPlan wp WHERE wp.user.id = :userId AND wp.plannedDate = :date AND wp.status = :status")
    Optional<WorkoutPlan> findByUserIdAndPlannedDateAndStatus(@Param("userId") Long userId, @Param("date") LocalDate date, @Param("status") WorkoutPlanStatus status);
    
    // Find all workout plans for a specific user and Status by date order
    @Query("SELECT wp FROM WorkoutPlan wp WHERE wp.user.id = :userId AND wp.status = :status ORDER BY wp.plannedDate")
    List<WorkoutPlan> findByUserIdAndStatusAndPlannedDateGreaterThanEqual(
    Long userId, WorkoutPlanStatus status, LocalDate plannedDate);

    // Find a workout plan by its ID and user ID (to ensure it belongs to the user)
    Optional<WorkoutPlan> findByIdAndUserId(Long workoutPlanId, Long userId);

    // Find all workout plans scheduled for a specific date
    List<WorkoutPlan> findByPlannedDate(LocalDate plannedDate);

    // Find all workout plans with status
    List<WorkoutPlan> findByUserIdAndStatus(Long userId, WorkoutPlanStatus status);
    
    // Finds the most recent workout plan for a user.
    Optional<WorkoutPlan> findTopByUserIdOrderByPlannedDateDesc(Long userId);

    // Fetches the latest 'n' workout plans for a user.
    @Query("SELECT wp FROM WorkoutPlan wp WHERE wp.user.id = :userId ORDER BY wp.plannedDate DESC")
    List<WorkoutPlan> findTopNByUserIdOrderByPlannedDateDesc(@Param("userId") Long userId, int numberOfSplit);



}

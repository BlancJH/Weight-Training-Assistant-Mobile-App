package com.blancJH.weight_assistant_mobile_app_backend.repository;

import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutHistoryRepository extends JpaRepository<WorkoutHistory, Long> {
}

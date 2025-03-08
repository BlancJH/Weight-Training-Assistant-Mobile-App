package com.blancJH.weight_assistant_mobile_app_backend.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blancJH.weight_assistant_mobile_app_backend.dto.WorkoutPlanDTO;
import com.blancJH.weight_assistant_mobile_app_backend.dto.WorkoutPlanExerciseDTO;
import com.blancJH.weight_assistant_mobile_app_backend.model.UserExercisePreference;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPlan;
import com.blancJH.weight_assistant_mobile_app_backend.repository.UserExercisePreferenceRepository;

@Service
public class WorkoutPlanMappingService {
    private final WorkoutPlanService workoutPlanService;
    private final UserExercisePreferenceRepository userExercisePreferenceRepository;

    @Autowired
    public WorkoutPlanMappingService(WorkoutPlanService workoutPlanService,
                                    UserExercisePreferenceRepository userExercisePreferenceRepository) {
        this.workoutPlanService = workoutPlanService;
        this.userExercisePreferenceRepository = userExercisePreferenceRepository;
    }

    public List<WorkoutPlanDTO> getWorkoutPlanDTOsForUser(Long userId, LocalDate date) {
        // Fetch workout plans for the user
        List<WorkoutPlan> workoutPlans = workoutPlanService.getWorkoutPlansByUserId(userId);

        // Filter by date if provided
        if (date != null) {
            workoutPlans = workoutPlans.stream()
                    .filter(wp -> wp.getPlannedDate() != null && wp.getPlannedDate().isEqual(date))
                    .collect(Collectors.toList());
        }

        // Fetch all user exercise preferences for the user in bulk
        List<UserExercisePreference> preferences = userExercisePreferenceRepository.findAllByUserId(userId);

        // Create a map of exercise ID to the latest preference (by createdAt)
        Map<Long, UserExercisePreference> latestPreferenceMap = preferences.stream()
            .collect(Collectors.groupingBy(
                pref -> pref.getExercise().getId(),
                Collectors.collectingAndThen(
                    Collectors.maxBy(Comparator.comparing(UserExercisePreference::getCreatedAt)),
                    Optional::get
                )
            ));

        // Map WorkoutPlan entities to DTOs, including the user preference if available.
        return workoutPlans.stream().map(wp -> new WorkoutPlanDTO(
                wp.getId(),
                wp.getPlannedDate(),
                wp.getStatus().toString(),
                wp.getWorkoutSplitCategory() != null ? wp.getWorkoutSplitCategory().toString() : null,
                wp.getExercises().stream().map(ex -> {
                    // Look up the latest preference for this exercise, if available.
                    UserExercisePreference pref = latestPreferenceMap.get(ex.getExercise().getId());
                    String userPreference = null;
                    if (pref != null) {
                        userPreference = pref.isFavorite() ? "FAVORITE" : (pref.isDislike() ? "DISLIKE" : null);
                    }
                    return new WorkoutPlanExerciseDTO(
                            ex.getId(),
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
                            ex.getDuration(),
                            userPreference
                    );
                }).collect(Collectors.toList())
        )).collect(Collectors.toList());
    }
}
package com.blancJH.weight_assistant_mobile_app_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutPlanExerciseDTO {
    private Long exerciseId;
    private String exerciseName;
    private String exerciseCategory;
    private String primaryMuscle;
    private String secondaryMuscle;
    private String exerciseGifUrl;
    private Integer sets;
    private Integer reps;
    private String duration;
}

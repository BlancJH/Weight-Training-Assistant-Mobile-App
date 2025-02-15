package com.blancJH.weight_assistant_mobile_app_backend.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WorkoutPlanDTO {
    private Long id;
    private LocalDate plannedDate;
    private boolean status;
    private String WorkoutSplitCategory;
    private List<WorkoutPlanExerciseDTO> exercises;
}

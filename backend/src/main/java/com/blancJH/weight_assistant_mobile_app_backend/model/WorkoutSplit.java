package com.blancJH.weight_assistant_mobile_app_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutSplit {
    private String categoryPath; // e.g., "Upper Body", "Push", etc.
}

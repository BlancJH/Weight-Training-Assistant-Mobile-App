package com.blancJH.weight_assistant_mobile_app_backend.dto;

import com.blancJH.weight_assistant_mobile_app_backend.model.DislikeReason;
import lombok.Data;

@Data
public class ExercisePreferenceRequest {
    private Long userId;
    private Long exerciseId;
    private boolean favorite;
    private boolean dislike;
    private DislikeReason dislikeReason;
}

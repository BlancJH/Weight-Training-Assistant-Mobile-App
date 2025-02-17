package com.blancJH.weight_assistant_mobile_app_backend.algorithm.algorithm_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blancJH.weight_assistant_mobile_app_backend.model.Exercise;
import com.blancJH.weight_assistant_mobile_app_backend.repository.UserExercisePreferenceRepository;


@Service
public class ExerciseStatisticsService {

    @Autowired
    private UserExercisePreferenceRepository preferenceRepository;

    public int getLatestFavoriteCount(Exercise exercise) {
        return preferenceRepository.countLatestFavoritesByExercise(exercise);
    }

    public int getLatestDislikeCount(Exercise exercise) {
        return preferenceRepository.countLatestDislikesByExercise(exercise);
    }
}



package com.blancJH.weight_assistant_mobile_app_backend.algorithm.algorithm_service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blancJH.weight_assistant_mobile_app_backend.algorithm.split_exercise_algorithms.ExerciseScoringAlgorithm;
import com.blancJH.weight_assistant_mobile_app_backend.model.Exercise;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutSplitCategory;

@Service
public class ExerciseRankingService {

    @Autowired
    private ExerciseScoringAlgorithm exerciseScorer;

    /**
     * Selects the top n exercises from the list based on their composite score for the given target split tag,
     * and returns a list of exercise names.
     *
     * @param exercises The list of exercises.
     * @param splitName The desired split tag (display name) to score against.
     * @param n         The number of top exercises to pick.
     * @return A list of the top n exercise names sorted by descending composite score.
     */
    public List<String> pickTopNExerciseNames(List<Exercise> exercises, String splitName, int n) {
        // Convert the string splitName to its enum representation.
        WorkoutSplitCategory targetCategory = exerciseScorer.fromDisplayName(splitName);
        if (targetCategory == null) {
            return List.of();
        }
        return exercises.stream()
                // Sort exercises in descending order using compositeScore (base score + bonus).
                .sorted(Comparator.comparingInt(
                        (Exercise ex) -> exerciseScorer.compositeScore(ex, targetCategory)).reversed())
                .limit(n)
                // Map each Exercise to its exercise name.
                .map(Exercise::getExerciseName)
                .collect(Collectors.toList());
    }
}

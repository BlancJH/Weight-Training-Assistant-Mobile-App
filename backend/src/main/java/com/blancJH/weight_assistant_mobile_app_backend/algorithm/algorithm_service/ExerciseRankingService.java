package com.blancJH.weight_assistant_mobile_app_backend.algorithm.algorithm_service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blancJH.weight_assistant_mobile_app_backend.algorithm.exercise_allocation_algorithms.ExerciseScoringAlgorithm;
import com.blancJH.weight_assistant_mobile_app_backend.model.Exercise;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutSplitCategory;
import com.blancJH.weight_assistant_mobile_app_backend.repository.ExerciseRepository;

@Service
public class ExerciseRankingService {

    @Autowired
    private ExerciseScoringAlgorithm exerciseScorer;
    
    @Autowired
    private ExerciseDistributionService distributionService;
    
    @Autowired
    private ExerciseRepository exerciseRepository;

    /**
     * Selects the top n exercises from the list based on their composite score for the given target split tag,
     * and returns a list of exercise names.
     *
     * @param exercises      The list of exercises.
     * @param splitName      The desired split tag (display name) to score against.
     * @param n              The number of top exercises to pick.
     * @param totalUserCount The total number of users (for popularity scoring).
     * @return A list of the top n exercise names sorted by descending composite score.
     */
    public List<String> pickTopNExerciseNames(String splitName, int n, int totalUserCount) {
        // Convert the string splitName to its enum representation.
        WorkoutSplitCategory targetCategory = exerciseScorer.fromDisplayName(splitName);
        if (targetCategory == null) {
            return List.of();
        }
        // Query repository for exercises that match the target category.
        List<Exercise> exercises = exerciseRepository.findByWorkoutSplitCategory(targetCategory);
        
        return exercises.stream()
                .sorted(Comparator.comparingInt(
                        (Exercise ex) -> exerciseScorer.compositeScore(ex, targetCategory, totalUserCount, null)
                ).reversed())
                .limit(n)
                .map(Exercise::getExerciseName)
                .collect(Collectors.toList());
    }

    /**
     * Picks top exercises for a target split based on a ratio distribution.
     * First, the distribution is calculated for the target split and total number of exercises.
     * Then, for each sub-category in the distribution, the top exercises are picked based on the composite score.
     *
     * @param targetSplit    The target split category (e.g., ARMS, UPPER_BODY).
     * @param totalCount     The total number of exercises to pick for the target split.
     * @param totalUserCount The total number of users (for popularity scoring).
     * @return A list of exercise names distributed according to the ratio configuration.
     */
    public List<String> pickTopExercisesByDistribution(WorkoutSplitCategory targetSplit, int totalCount, int totalUserCount) {
        // Retrieve the distribution map for the target split.
        Map<String, Integer> distribution = distributionService.getExerciseDistribution(targetSplit, totalCount);
        List<String> result = new ArrayList<>();
        
        // For each sub-category, pick the top exercises based on the allocated count.
        for (Map.Entry<String, Integer> entry : distribution.entrySet()) {
            String subCategory = entry.getKey();
            int count = entry.getValue();
            // Use the ranking method for each sub-category.
            List<String> subCategoryExercises = pickTopNExerciseNames(subCategory, count, totalUserCount);
            result.addAll(subCategoryExercises);
        }
        return result;
    }
}

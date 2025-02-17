package com.blancJH.weight_assistant_mobile_app_backend.algorithm.exercise_allocation_algorithms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blancJH.weight_assistant_mobile_app_backend.algorithm.algorithm_service.UserStatisticsService;
import com.blancJH.weight_assistant_mobile_app_backend.model.Exercise;
import com.blancJH.weight_assistant_mobile_app_backend.model.UserExercisePreference;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutSplitCategory;

@Service
public class ExerciseScoringAlgorithm {

    @Autowired
    private UserStatisticsService userStatisticsService;

    /**
     * Returns the enum constant for the given display name.
     *
     * @param displayName The display name of the split category.
     * @return The corresponding WorkoutSplitCategory or null if not found.
     */
    public WorkoutSplitCategory fromDisplayName(String displayName) {
        for (WorkoutSplitCategory cat : WorkoutSplitCategory.values()) {
            if (cat.getDisplayName().equalsIgnoreCase(displayName)) {
                return cat;
            }
        }
        return null;
    }

    /**
     * Adds bonus points if the exercise has high efficiency (advantage is true).
     * Advantage is recorded on the exercise table.
     *
     * @param exercise The exercise to check.
     * @return 3 points if advantage is true, 0 otherwise.
     */
    public int additionalAdvantageScore(Exercise exercise) {
        return exercise.isAdvantage() ? 3 : 0;
    }

    public double popularityScore(Exercise exercise) {
        int totalUserCount = userStatisticsService.getTotalUserCount();
        if (totalUserCount <= 0) {
            return 0;
        }
            double favRatio = ((double) exercise.getLatestFavoriteCount()) / totalUserCount;
            double dislikeRatio = ((double) exercise.getLatestDislikeCount()) / totalUserCount;
            return favRatio - dislikeRatio;
        }

    /**
     * Computes a composite score for an exercise by combining:
     * - The base hierarchical score,
     * - If a user preference exists: +1 if favorite, -1 if disliked.
     * - Otherwise: the advantage bonus and the popularity score.
     *
     * @param exercise       The exercise to score.
     * @param targetCategory The target split category.
     * @param totalUserCount The total number of users (for popularity score).
     * @param userPreference The user's preference for this exercise (can be null if not marked).
     * @return The composite score as an integer.
     */
    public int compositeScore(Exercise exercise, WorkoutSplitCategory targetCategory, int totalUserCount, UserExercisePreference userPreference) {
        int baseScore = 0;
        int bonus = 0;
        if (userPreference != null) {
            // If a preference is recorded for this exercise, use it.
            if (userPreference.isFavorite()) {
                bonus += 1;
            } else if (userPreference.isDislike()) {
                bonus -= 1;
            }
        } else {
            // Otherwise, use the advantage bonus and global popularity.
            bonus += additionalAdvantageScore(exercise);
            bonus += (int) Math.round(popularityScore(exercise));
        }
    return baseScore + bonus;
    }
}

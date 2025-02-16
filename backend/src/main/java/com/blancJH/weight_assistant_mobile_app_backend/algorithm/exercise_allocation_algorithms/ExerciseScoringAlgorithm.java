package com.blancJH.weight_assistant_mobile_app_backend.algorithm.exercise_allocation_algorithms;

import org.springframework.stereotype.Service;

import com.blancJH.weight_assistant_mobile_app_backend.model.Exercise;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutSplitCategory;

@Service
public class ExerciseScoringAlgorithm {

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

    /**
     * Computes a composite score for an exercise by combining the base hierarchical score and the advantage bonus.
     *
     * @param exercise       The exercise to score.
     * @param targetCategory The target split category.
     * @return The composite score.
     */
    public int compositeScore(Exercise exercise, WorkoutSplitCategory targetCategory) {
        int baseScore = 0;
        int bonus = additionalAdvantageScore(exercise);
        return baseScore + bonus;
    }

}

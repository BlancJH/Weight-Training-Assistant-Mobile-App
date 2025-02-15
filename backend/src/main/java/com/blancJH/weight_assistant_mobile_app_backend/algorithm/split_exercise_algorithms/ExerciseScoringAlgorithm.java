package com.blancJH.weight_assistant_mobile_app_backend.algorithm.split_exercise_algorithms;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
     * Recursively computes the hierarchical distance between an exercise's category and a target category.
     *
     * @param exerciseCategory The category of the exercise.
     * @param targetCategory   The target category to compare against.
     * @return The distance (0 for exact match, 1 for direct child, 2 for grandchild, etc.) or -1 if not related.
     */
    public int hierarchicalDistance(WorkoutSplitCategory exerciseCategory, WorkoutSplitCategory targetCategory) {
        if (exerciseCategory.equals(targetCategory)) {
            return 0;
        }
        for (WorkoutSplitCategory parent : exerciseCategory.getParents()) {
            if (parent.equals(targetCategory)) {
                return 1;
            }
            int distance = hierarchicalDistance(parent, targetCategory);
            if (distance != -1) {
                return distance + 1;
            }
        }
        return -1;
    }

    /**
     * Calculates a score for an individual exercise based on the hierarchical relationship 
     * between its split tag and the target split category.
     *
     * @param exercise       The exercise to score.
     * @param targetCategory The target split category.
     * @return The score for the exercise.
     */
    public int scoreExerciseBySplit(Exercise exercise, WorkoutSplitCategory targetCategory) {
        // Assume the Exercise entity has a method getSplitTag() that returns a string.
        WorkoutSplitCategory exerciseCategory = exercise.getWorkoutSplitCategory();
        if (exerciseCategory == null) {
            return 0;
        }
        int distance = hierarchicalDistance(exerciseCategory, targetCategory);
        if (distance < 0) {
            return 0;
        }

        switch (distance) {
            case 0:
                return 10;
            case 1:
                return 8;
            case 2:
                return 6;
            default:
                return 0;
        }
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
        int baseScore = scoreExerciseBySplit(exercise, targetCategory);
        int bonus = additionalAdvantageScore(exercise);
        return baseScore + bonus;
    }

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
        WorkoutSplitCategory targetCategory = fromDisplayName(splitName);
        if (targetCategory == null) {
            return List.of();
        }
        return exercises.stream()
                // Sort exercises in descending order using compositeScore (base score + bonus).
                .sorted(Comparator.comparingInt((Exercise ex) -> compositeScore(ex, targetCategory)).reversed())
                .limit(n)
                // Map each Exercise to its exercise name.
                .map(Exercise::getExerciseName)
                .collect(Collectors.toList());
    }
}

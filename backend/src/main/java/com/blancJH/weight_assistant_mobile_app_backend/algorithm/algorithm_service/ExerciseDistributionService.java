package com.blancJH.weight_assistant_mobile_app_backend.algorithm.algorithm_service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blancJH.weight_assistant_mobile_app_backend.config.RatioConfig;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutSplitCategory;

@Service
public class ExerciseDistributionService {

    @Autowired
    private RatioConfig ratioConfig;

    /**
     * Retrieves the ratio map for the given target split.
     * If a direct mapping is not found for the target split,
     * this method will try to combine the ratio maps of its children.
     * If neither is available, it returns a default map that assigns 100% weight to the target split.
     *
     * @param targetSplit the target split category.
     * @return a map where keys are sub-category names and values are ratios.
     */
    private Map<String, Double> getRatioMapForTargetSplit(WorkoutSplitCategory targetSplit) {
        // Get all ratio mappings from configuration
        Map<String, Map<String, Double>> allRatios = ratioConfig.getRatios();
        // Try to get a direct mapping
        Map<String, Double> directMap = allRatios.get(targetSplit.getDisplayName());
        if (directMap != null && !directMap.isEmpty()) {
            return directMap;
        }

        // If no direct mapping exists, try to merge the mappings of all descendant categories.
        Map<String, Double> combined = new HashMap<>();
        for (WorkoutSplitCategory cat : WorkoutSplitCategory.values()) {
            if (!cat.equals(targetSplit) && cat.isDescendantOf(targetSplit)) {
                Map<String, Double> childMap = allRatios.get(cat.getDisplayName());
                if (childMap != null && !childMap.isEmpty()) {
                    childMap.forEach((key, value) -> combined.merge(key, value, Double::sum));
                }
            }
        }
        if (!combined.isEmpty()) {
            // Normalize the combined map so that the sum equals 1.
            double total = combined.values().stream().mapToDouble(Double::doubleValue).sum();
            return combined.entrySet().stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue() / total
                    ));
        } else {
            // Fallback: if no mapping is found, return a default map with 100% assigned to the target split itself.
            Map<String, Double> defaultMap = new HashMap<>();
            defaultMap.put(targetSplit.getDisplayName(), 1.0);
            return defaultMap;
        }
    }

    /**
     * Calculates the distribution of exercises among subcategories for the target split.
     * It calculates the number of exercises to pick from each sub-category based on the provided ratios,
     * ensuring that the sum of counts equals the total number provided.
     *
     * @param targetSplit    The target split category (e.g. ARMS).
     * @param totalExercises The total number of exercises to pick.
     * @return A map with sub-category names as keys and the number of exercises to pick as values.
     */
    public Map<String, Integer> getExerciseDistribution(WorkoutSplitCategory targetSplit, int totalExercises) {
        Map<String, Double> ratioMap = getRatioMapForTargetSplit(targetSplit);
        Map<String, Integer> distribution = new HashMap<>();
        int allocated = 0;
        Map<String, Double> fractionalParts = new HashMap<>();

        // Compute counts using floor of (ratio * totalExercises).
        for (Map.Entry<String, Double> entry : ratioMap.entrySet()) {
            double exactCount = entry.getValue() * totalExercises;
            int count = (int) Math.floor(exactCount);
            distribution.put(entry.getKey(), count);
            allocated += count;
            fractionalParts.put(entry.getKey(), exactCount - count);
        }

        // Calculate remaining exercises to allocate.
        int remaining = totalExercises - allocated;

        // Distribute the remaining count based on the highest fractional parts.
        List<String> sortedKeys = fractionalParts.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        int i = 0;
        while (remaining > 0) {
            String key = sortedKeys.get(i % sortedKeys.size());
            distribution.put(key, distribution.get(key) + 1);
            remaining--;
            i++;
        }
        return distribution;
    }
}

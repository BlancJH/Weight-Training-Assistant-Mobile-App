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
     *
     * @param targetSplit the target split category.
     * @return a map where keys are sub-category names and values are ratios.
     */
    private Map<String, Double> getRatioMapForTargetSplit(WorkoutSplitCategory targetSplit) {
        Map<String, Map<String, Double>> allRatios = ratioConfig.getRatios();
        // Attempt to get a direct mapping.
        Map<String, Double> directMap = allRatios.get(targetSplit.getDisplayName());
        if (directMap != null && !directMap.isEmpty()) {
            return directMap;
        }

        // If no direct mapping is defined, attempt to combine the mappings from child categories.
        Map<String, Double> combined = new HashMap<>();
        // Iterate over all possible categories.
        for (WorkoutSplitCategory cat : WorkoutSplitCategory.values()) {
            // If cat is a descendant of targetSplit (but not equal to targetSplit)
            if (!cat.equals(targetSplit) && cat.isDescendantOf(targetSplit)) {
                Map<String, Double> childMap = allRatios.get(cat.getDisplayName());
                if (childMap != null && !childMap.isEmpty()) {
                    // Merge the child mapping into the combined map.
                    childMap.forEach((key, value) -> 
                        combined.merge(key, value, Double::sum));
                }
            }
        }
        if (!combined.isEmpty()) {
            // Normalize the combined map so that the sum of ratios equals 1.
            double total = combined.values().stream().mapToDouble(Double::doubleValue).sum();
            return combined.entrySet().stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue() / total
                    ));
        }
        throw new IllegalArgumentException("Ratio distribution not defined for target split: " + targetSplit);
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

package com.blancJH.weight_assistant_mobile_app_backend.algorithm.algorithm_service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blancJH.weight_assistant_mobile_app_backend.algorithm.workout_split_algorithms.GainMuscleSplitAllocationAlgorithm;
import com.blancJH.weight_assistant_mobile_app_backend.algorithm.workout_split_algorithms.LossWeightSplitAllocationAlgorithm;
import com.blancJH.weight_assistant_mobile_app_backend.algorithm.workout_split_algorithms.WorkoutSplitAllocationAlgorithm;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPurpose;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutSplit;

@Service
public class WorkoutSplitAllocationService {

    @Autowired
    private GainMuscleSplitAllocationAlgorithm gainMuscleStrategy;

    @Autowired
    private LossWeightSplitAllocationAlgorithm lossWeightStrategy;

    // You can add other strategy implementations here.

    /**
     * Allocates splits based on the workout purpose and the number of splits.
     *
     * @param purpose        The workout purpose (e.g., GAIN_MUSCLE, LOSS_WEIGHT, etc.).
     * @param numberOfSplits The number of splits desired.
     * @return A list of allocated WorkoutSplits.
     */
    public List<WorkoutSplit> allocateSplits(WorkoutPurpose purpose, int numberOfSplits) {
        WorkoutSplitAllocationAlgorithm strategy = null;
        switch (purpose) {
            case GAIN_MUSCLE:
                strategy = gainMuscleStrategy;
                break;
            case LOSS_WEIGHT:
                strategy = lossWeightStrategy;
                break;
            // Add additional cases for other purposes.
            default:
                // throw an exception
                throw new IllegalArgumentException("Unsupported workout purpose: " + purpose);
        }
        return strategy.allocateSplits(numberOfSplits);
    }
}

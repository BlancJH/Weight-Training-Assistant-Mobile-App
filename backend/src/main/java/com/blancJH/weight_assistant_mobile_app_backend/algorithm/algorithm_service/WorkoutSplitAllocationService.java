package com.blancJH.weight_assistant_mobile_app_backend.algorithm.algorithm_service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blancJH.weight_assistant_mobile_app_backend.algorithm.split_allocation_algorithm.CardicEnduranceSplitAllocationAlgorithm;
import com.blancJH.weight_assistant_mobile_app_backend.algorithm.split_allocation_algorithm.GainMuscleSplitAllocationAlgorithm;
import com.blancJH.weight_assistant_mobile_app_backend.algorithm.split_allocation_algorithm.LossWeightSplitAllocationAlgorithm;
import com.blancJH.weight_assistant_mobile_app_backend.algorithm.split_allocation_algorithm.WorkoutSplitAllocationAlgorithm;
import com.blancJH.weight_assistant_mobile_app_backend.model.UserDetails;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPurpose;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutSplit;

@Service
public class WorkoutSplitAllocationService {

    @Autowired
    private GainMuscleSplitAllocationAlgorithm gainMuscleStrategy;

    @Autowired
    private LossWeightSplitAllocationAlgorithm lossWeightStrategy;

    @Autowired
    private CardicEnduranceSplitAllocationAlgorithm cardicEnduranceStrategy;

    // Add other strategy implementations here.

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
            case IMPROVE_CARDIC_ENDURANCE:
                strategy = cardicEnduranceStrategy;
                break;
            // Add additional cases for other purposes.
            default:
                // throw an exception
                throw new IllegalArgumentException("Unsupported workout purpose: " + purpose);
        }
        return strategy.allocateSplits(numberOfSplits);
    }

    /**
     * Calculates the number of exercises to pick based on workout duration.
     * It divides the workout duration by 15 and rounds up or down.
     *
     * @param workoutDuration The workout duration in minutes.
     * @return The number of exercises to pick.
     */
    public int calculateExerciseCount(int workoutDuration) {
        return (int) Math.floor((double) workoutDuration / 15); // Pick 1 exercise for every 15 min.
    }

    /**
     * Allocates workout splits for a user based on their details.
     *
     * @param userDetails The user's details containing the workout purpose and number of splits.
     * @return A list of allocated WorkoutSplits.
     */
    public List<WorkoutSplit> allocateSplitsForUser(UserDetails userDetails) {
        if (userDetails == null) {
            throw new IllegalArgumentException("User details must not be null");
        }
        // Extract the workout purpose and number of splits from the user details.
        WorkoutPurpose purpose = userDetails.getWorkoutPurpose();
        int numberOfSplits = userDetails.getNumberOfSplit();
        return allocateSplits(purpose, numberOfSplits);
    }
}

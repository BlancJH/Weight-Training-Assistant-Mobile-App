package com.blancJH.weight_assistant_mobile_app_backend.algorithm.workout_split_algorithms;

import java.util.List;

import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutSplit;

public interface WorkoutSplitAllocationAlgorithm {
    List<WorkoutSplit> allocateSplits(int numberOfSplits);
}

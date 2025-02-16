package com.blancJH.weight_assistant_mobile_app_backend.algorithm.split_allocation_algorithm;

import java.util.List;

import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutSplit;

public interface WorkoutSplitAllocationAlgorithm {
    List<WorkoutSplit> allocateSplits(int numberOfSplits);
}

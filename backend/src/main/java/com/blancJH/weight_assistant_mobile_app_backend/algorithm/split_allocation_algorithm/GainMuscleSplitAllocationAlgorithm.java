package com.blancJH.weight_assistant_mobile_app_backend.algorithm.split_allocation_algorithm;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutSplit;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutSplitCategory;

@Service
public class GainMuscleSplitAllocationAlgorithm implements WorkoutSplitAllocationAlgorithm {

    @Override
    public List<WorkoutSplit> allocateSplits(int numberOfSplits) {
        List<WorkoutSplit> splits = new ArrayList<>();
        switch (numberOfSplits) {
            case 1 -> splits.add(new WorkoutSplit(WorkoutSplitCategory.STRENGTH.getDisplayName()));
            case 2 -> {
                splits.add(new WorkoutSplit(WorkoutSplitCategory.UPPER_BODY.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.LOWER_BODY.getDisplayName()));
            }
            case 3 -> {
                splits.add(new WorkoutSplit(WorkoutSplitCategory.PUSH.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.PULL.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.LOWER_BODY.getDisplayName()));
            }
            case 4 -> {
                splits.add(new WorkoutSplit(WorkoutSplitCategory.CHEST.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.BACK.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.ARMS.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.LOWER_BODY.getDisplayName()));
            }
            case 5 -> {
                splits.add(new WorkoutSplit(WorkoutSplitCategory.CHEST.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.BACK.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.SHOULDERS.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.LOWER_BODY.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.ARMS.getDisplayName()));
            }
            case 6 -> {
                splits.add(new WorkoutSplit(WorkoutSplitCategory.CHEST.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.BACK.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.SHOULDERS.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.LOWER_BODY.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.ARMS.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.CORE.getDisplayName()));
            }
            case 7 -> {
                splits.add(new WorkoutSplit(WorkoutSplitCategory.CHEST.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.BACK.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.SHOULDERS.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.LOWER_BODY.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.ARMS.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.CORE.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.RECOVERY.getDisplayName()));
            }
            default -> { }
        }
        return splits;
    }
}

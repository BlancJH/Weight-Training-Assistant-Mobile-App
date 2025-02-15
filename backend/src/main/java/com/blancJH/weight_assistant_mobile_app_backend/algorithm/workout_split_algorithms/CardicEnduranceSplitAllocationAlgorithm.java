package com.blancJH.weight_assistant_mobile_app_backend.algorithm.workout_split_algorithms;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutSplit;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutSplitCategory;

@Service
public class CardicEnduranceSplitAllocationAlgorithm implements WorkoutSplitAllocationAlgorithm {

    @Override
    public List<WorkoutSplit> allocateSplits(int numberOfSplits) {
        List<WorkoutSplit> splits = new ArrayList<>();
        switch (numberOfSplits) {
            case 1 -> splits.add(new WorkoutSplit(WorkoutSplitCategory.CARDIO.getDisplayName()));
            case 2 -> {
                splits.add(new WorkoutSplit(WorkoutSplitCategory.STEADY_STATE.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.HIIT.getDisplayName()));
            }
            case 3 -> {
                splits.add(new WorkoutSplit(WorkoutSplitCategory.ENDURANCE.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.HIIT.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.LOW_IMPACT.getDisplayName()));
            }
            case 4 -> {
                splits.add(new WorkoutSplit(WorkoutSplitCategory.STEADY_STATE.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.HIIT.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.STEADY_STATE.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.LOW_IMPACT.getDisplayName()));
            }
            case 5 -> {
                splits.add(new WorkoutSplit(WorkoutSplitCategory.ENDURANCE.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.HIIT.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.HIIT.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.STEADY_STATE.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.LOW_IMPACT.getDisplayName()));
            }
            case 6 -> {
                splits.add(new WorkoutSplit(WorkoutSplitCategory.STEADY_STATE.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.HIIT.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.LOWER_BODY_CIRCUIT.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.LOW_IMPACT.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.STEADY_STATE.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.HIIT.getDisplayName()));
            }
            case 7 -> {
                splits.add(new WorkoutSplit(WorkoutSplitCategory.STEADY_STATE.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.HIIT.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.LOWER_BODY_CIRCUIT.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.LOW_IMPACT.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.STEADY_STATE.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.HIIT.getDisplayName()));
                splits.add(new WorkoutSplit(WorkoutSplitCategory.RECOVERY.getDisplayName()));
            }
            default -> { }
        }
        return splits;
    }
}

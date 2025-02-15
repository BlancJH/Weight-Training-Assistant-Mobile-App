package com.blancJH.weight_assistant_mobile_app_backend.algorithm.algorithm_service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPurpose;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutSplit;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutSplitCategory;

@SpringBootTest
public class WorkoutSplitAllocationServiceTest {

    @Autowired
    private WorkoutSplitAllocationService workoutSplitAllocationService;

    @Test
    public void testGainMuscleOneSplit() {
        List<WorkoutSplit> splits = workoutSplitAllocationService.allocateSplits(WorkoutPurpose.GAIN_MUSCLE, 1);
        // According to GainMuscleSplitAllocationAlgorithm for 1 split, we return STRENGTH.
        assertEquals(1, splits.size(), "Expected 1 split for one-split plan.");
        assertEquals(WorkoutSplitCategory.STRENGTH.getDisplayName(), splits.get(0).getCategoryPath(),
                     "Expected split to be STRENGTH.");
    }
    
    @Test
    public void testGainMuscleTwoSplits() {
        List<WorkoutSplit> splits = workoutSplitAllocationService.allocateSplits(WorkoutPurpose.GAIN_MUSCLE, 2);
        // For 2 splits, we expect UPPER_BODY and LOWER_BODY.
        assertEquals(2, splits.size(), "Expected 2 splits for two-split plan.");
        assertEquals(WorkoutSplitCategory.UPPER_BODY.getDisplayName(), splits.get(0).getCategoryPath(),
                     "First split should be UPPER_BODY.");
        assertEquals(WorkoutSplitCategory.LOWER_BODY.getDisplayName(), splits.get(1).getCategoryPath(),
                     "Second split should be LOWER_BODY.");
    }

    @Test
    public void testGainMuscleThreeSplits() {
        List<WorkoutSplit> splits = workoutSplitAllocationService.allocateSplits(WorkoutPurpose.GAIN_MUSCLE, 3);
        // For 3 splits, the algorithm returns PUSH, PULL, and LOWER_BODY.
        assertEquals(3, splits.size(), "Expected 3 splits for three-split plan.");
        assertEquals(WorkoutSplitCategory.PUSH.getDisplayName(), splits.get(0).getCategoryPath(),
                     "First split should be PUSH.");
        assertEquals(WorkoutSplitCategory.PULL.getDisplayName(), splits.get(1).getCategoryPath(),
                     "Second split should be PULL.");
        assertEquals(WorkoutSplitCategory.LOWER_BODY.getDisplayName(), splits.get(2).getCategoryPath(),
                     "Third split should be LOWER_BODY.");
    }

    @Test
    public void testGainMuscleFourSplits() {
        List<WorkoutSplit> splits = workoutSplitAllocationService.allocateSplits(WorkoutPurpose.GAIN_MUSCLE, 4);
        // For 4 splits, the expected order is CHEST, BACK, ARMS, LOWER_BODY.
        assertEquals(4, splits.size(), "Expected 4 splits for four-split plan.");
        assertEquals(WorkoutSplitCategory.CHEST.getDisplayName(), splits.get(0).getCategoryPath(),
                     "First split should be CHEST.");
        assertEquals(WorkoutSplitCategory.BACK.getDisplayName(), splits.get(1).getCategoryPath(),
                     "Second split should be BACK.");
        assertEquals(WorkoutSplitCategory.ARMS.getDisplayName(), splits.get(2).getCategoryPath(),
                     "Third split should be ARMS.");
        assertEquals(WorkoutSplitCategory.LOWER_BODY.getDisplayName(), splits.get(3).getCategoryPath(),
                     "Fourth split should be LOWER_BODY.");
    }
    
    // You can add tests for cases 5, 6, 7 in a similar fashion.
}

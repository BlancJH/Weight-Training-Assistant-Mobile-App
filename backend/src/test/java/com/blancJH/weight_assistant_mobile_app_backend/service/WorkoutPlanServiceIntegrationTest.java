package com.blancJH.weight_assistant_mobile_app_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.blancJH.weight_assistant_mobile_app_backend.model.UserDetails;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPlan;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPurpose;

@SpringBootTest
public class WorkoutPlanServiceIntegrationTest {

    @Autowired
    private WorkoutPlanService workoutPlanService;

    @Test
    public void testCreateWorkoutPlan() {
        // Prepare a dummy UserDetails object.
        UserDetails userDetails = new UserDetails();
        // For this test, we assume the strategy for workoutPurpose GAIN_MUSCLE returns 4 splits in the following order:
        // 1. CHEST, 2. BACK, 3. ARMS, 4. LOWER_BODY.
        // (Adjust the purpose if your test expects a different one.)
        userDetails.setWorkoutPurpose(WorkoutPurpose.GAIN_MUSCLE);
        userDetails.setNumberOfSplit(4);
        userDetails.setWorkoutDuration(90); // 90 minutes -> floor(90/15) = 6 exercises per split.

        // Call the service method to create the workout plan.
        Map<String, List<String>> planMap = workoutPlanService.createWorkoutPlan(userDetails);
        
        // Assert that the plan contains exactly 4 splits.
        assertEquals(4, planMap.size(), "Workout plan should contain 4 splits");
        
        // Verify that the expected splits are present.
        // (These keys are the display names as returned by WorkoutSplit::getCategoryPath.)
        assertTrue(planMap.containsKey("CHEST"), "Plan should contain split 'CHEST'");
        assertTrue(planMap.containsKey("BACK"), "Plan should contain split 'BACK'");
        assertTrue(planMap.containsKey("ARMS"), "Plan should contain split 'ARMS'");
        assertTrue(planMap.containsKey("LOWER_BODY"), "Plan should contain split 'LOWER_BODY'");
        
        // Optionally, if the allocation service preserves the order, you can check the order.
        // For example, if you use a LinkedHashMap in your service, then:
        List<String> orderedSplits = planMap.keySet().stream().collect(Collectors.toList());
        assertEquals("CHEST", orderedSplits.get(0), "First split should be 'CHEST'");
        assertEquals("BACK", orderedSplits.get(1), "Second split should be 'BACK'");
        assertEquals("ARMS", orderedSplits.get(2), "Third split should be 'ARMS'");
        assertEquals("LOWER_BODY", orderedSplits.get(3), "Fourth split should be 'LOWER_BODY'");
    }
}

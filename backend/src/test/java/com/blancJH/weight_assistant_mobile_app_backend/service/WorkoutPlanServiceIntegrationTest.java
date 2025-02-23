package com.blancJH.weight_assistant_mobile_app_backend.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        // For this test, we assume that for workoutPurpose GAIN_MUSCLE, the allocation returns 4 splits:
        // 1. CHEST, 2. BACK, 3. ARMS, 4. LOWER_BODY.
        userDetails.setWorkoutPurpose(WorkoutPurpose.GAIN_MUSCLE);
        userDetails.setNumberOfSplit(4);
        userDetails.setWorkoutDuration(90); // 90 minutes -> floor(90/15) = 6 exercises per split.

        // Call the service method to create the workout plans.
        List<WorkoutPlan> workoutPlans = workoutPlanService.createWorkoutPlans(userDetails);

        // Assert that we have exactly 4 workout plans.
        assertEquals(4, workoutPlans.size(), "Workout plan should contain 4 splits");

        // For easier verification, create a map from split name to WorkoutPlan.
        Map<String, WorkoutPlan> planMap = workoutPlans.stream()
                .collect(Collectors.toMap(plan -> plan.getWorkoutSplitCategory().toString(), plan -> plan));

        // Verify that the expected splits are present.
        assertTrue(planMap.containsKey("CHEST"), "Plan should contain split 'CHEST'");
        assertTrue(planMap.containsKey("BACK"), "Plan should contain split 'BACK'");
        assertTrue(planMap.containsKey("ARMS"), "Plan should contain split 'ARMS'");
        assertTrue(planMap.containsKey("LOWER_BODY"), "Plan should contain split 'LOWER_BODY'");

        // Optionally, verify the order if your allocation guarantees it.
        assertEquals("CHEST", workoutPlans.get(0).getWorkoutSplitCategory().toString(), "First split should be 'CHEST'");
        assertEquals("BACK", workoutPlans.get(1).getWorkoutSplitCategory().toString(), "Second split should be 'BACK'");
        assertEquals("ARMS", workoutPlans.get(2).getWorkoutSplitCategory().toString(), "Third split should be 'ARMS'");
        assertEquals("LOWER_BODY", workoutPlans.get(3).getWorkoutSplitCategory().toString(), "Fourth split should be 'LOWER_BODY'");
    }
}
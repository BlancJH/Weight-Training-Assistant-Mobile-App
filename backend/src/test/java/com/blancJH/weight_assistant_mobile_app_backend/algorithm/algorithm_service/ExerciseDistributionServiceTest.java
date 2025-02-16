package com.blancJH.weight_assistant_mobile_app_backend.algorithm.algorithm_service;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutSplitCategory;

@SpringBootTest
public class ExerciseDistributionServiceTest {

    @Autowired
    private ExerciseDistributionService distributionService;

    @Test
    public void testUpperBodyDistributionForSixExercises() {
        int totalUpperBodyExercises = 6;
        // Get the distribution for UPPER_BODY
        Map<String, Integer> upperBodyDistribution = distributionService.getExerciseDistribution(
            WorkoutSplitCategory.UPPER_BODY, totalUpperBodyExercises);
        
        // Expected floor counts:
        // ARMS: floor(6 * 0.245) = 1
        // CHEST: floor(6 * 0.245) = 1
        // BACK: floor(6 * 0.245) = 1
        // SHOULDERS: floor(6 * 0.245) = 1
        // UPPER_BODY: floor(6 * 0.02) = 0
        // Total floor = 4, remainder = 2.
        // Assuming insertion order (ARMS, CHEST, BACK, SHOULDERS, UPPER_BODY) gets extras:
        // ARMS becomes 2 and CHEST becomes 2.
        
        assertEquals(2, upperBodyDistribution.get("ARMS"), "Expected ARMS to receive 2 exercises.");
        assertEquals(2, upperBodyDistribution.get("CHEST"), "Expected CHEST to receive 2 exercises.");
        assertEquals(1, upperBodyDistribution.get("BACK"), "Expected BACK to receive 1 exercise.");
        assertEquals(1, upperBodyDistribution.get("SHOULDERS"), "Expected SHOULDERS to receive 1 exercise.");
        assertEquals(0, upperBodyDistribution.get("UPPER_BODY"), "Expected UPPER_BODY to receive 0 exercises.");
        
        // Sum of distribution must equal 6.
        int sum = upperBodyDistribution.values().stream().mapToInt(Integer::intValue).sum();
        assertEquals(6, sum, "The sum of UPPER_BODY distribution should equal 6.");
        
        // Now, for the ARMS branch, assume 2 exercises are allocated from UPPER_BODY distribution.
        Map<String, Integer> armsDistribution = distributionService.getExerciseDistribution(
            WorkoutSplitCategory.ARMS, upperBodyDistribution.get("ARMS"));
        
        // For ARMS:
        // Biceps: floor(2 * 0.40) = floor(0.8) = 0, remainder = 0.8
        // Triceps: floor(2 * 0.45) = floor(0.9) = 0, remainder = 0.9
        // Arms: floor(2 * 0.1) = floor(0.2) = 0, remainder = 0.2
        // Total floor = 0, remainder = 2.
        // Extra distribution: first extra goes to Triceps (0.9), second to Biceps (0.8)
        // Expected distribution: Triceps = 1, Biceps = 1, Arms = 0.
        assertEquals(1, armsDistribution.get("Triceps"), "Expected TRICEPS to receive 1 exercise.");
        assertEquals(1, armsDistribution.get("Biceps"), "Expected BICEPS to receive 1 exercise.");
        assertEquals(0, armsDistribution.get("Arms"), "Expected ARMS (generic) to receive 0 exercises.");
        
        int armsSum = armsDistribution.values().stream().mapToInt(Integer::intValue).sum();
        assertEquals(upperBodyDistribution.get("ARMS"), armsSum, "Sum of ARMS subcategory distribution should equal allocated ARMS exercises.");
    }
}

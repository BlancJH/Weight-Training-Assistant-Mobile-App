package com.blancJH.weight_assistant_mobile_app_backend.algorithm.algorithm_service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.blancJH.weight_assistant_mobile_app_backend.model.Exercise;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutSplitCategory;

@SpringBootTest
public class ExerciseRankingServiceTest {

    @Autowired
    private ExerciseRankingService rankingService;

    @Test
    public void testPickTopNExerciseNamesForArms() {
        // Create sample exercises.
        Exercise ex1 = new Exercise();
        ex1.setExerciseName("Hammer Curl");
        // Assume BICEPS is a child of ARMS.
        ex1.setWorkoutSplitCategory(WorkoutSplitCategory.BICEPS);
        ex1.setAdvantage(true); // composite score = 8 + 3 = 11

        Exercise ex2 = new Exercise();
        ex2.setExerciseName("Dumbbell Curl");
        ex2.setWorkoutSplitCategory(WorkoutSplitCategory.BICEPS);
        ex2.setAdvantage(false); // composite score = 8

        Exercise ex3 = new Exercise();
        ex3.setExerciseName("Cable Push Down");
        ex3.setWorkoutSplitCategory(WorkoutSplitCategory.TRICEPS);
        ex3.setAdvantage(false); // composite score = 8

        Exercise ex4 = new Exercise();
        ex4.setExerciseName("Arm Extension");
        ex4.setWorkoutSplitCategory(WorkoutSplitCategory.ARMS);
        ex4.setAdvantage(false); // composite score = 10 (exact match)

        // Combine exercises into a list.
        List<Exercise> exercises = Arrays.asList(ex1, ex2, ex3, ex4);

        // Target split is "Arms". We request the top 3 exercises.
        List<String> topNames = rankingService.pickTopNExerciseNames(exercises, "Arms", 3);

        // Verify that we have 3 exercise names.
        assertEquals(3, topNames.size(), "Expected 3 top exercises for the Arms split.");

        // Expect "Hammer Curl" (score 11) to be ranked first.
        assertEquals("Hammer Curl", topNames.get(0), "Hammer Curl should be ranked first.");

        // "Arm Extension" (score 10) should be second.
        assertEquals("Arm Extension", topNames.get(1), "Arm Extension should be ranked second.");

        // The third one should be either "Dumbbell Curl" or "Cable Push Down" (both score 8).
        assertTrue(topNames.contains("Dumbbell Curl") || topNames.contains("Cable Push Down"),
                   "Either Dumbbell Curl or Cable Push Down should be included in the top 3.");
    }

    @Test
    public void testPickTopExercisesForUpperBody() {
        // Create dummy exercises for UPPER_BODY subcategories.
        List<Exercise> exercises = new ArrayList<>();

        // ARMS exercises
        Exercise armsEx1 = new Exercise();
        armsEx1.setExerciseName("Dumbbell Curl");
        armsEx1.setWorkoutSplitCategory(WorkoutSplitCategory.ARMS);
        armsEx1.setAdvantage(false);
        
        Exercise armsEx2 = new Exercise();
        armsEx2.setExerciseName("Hammer Curl");
        armsEx2.setWorkoutSplitCategory(WorkoutSplitCategory.ARMS);
        armsEx2.setAdvantage(true);
        
        // CHEST exercises
        Exercise chestEx1 = new Exercise();
        chestEx1.setExerciseName("Incline Dumbbell Press");
        chestEx1.setWorkoutSplitCategory(WorkoutSplitCategory.CHEST);
        chestEx1.setAdvantage(false);
        
        Exercise chestEx2 = new Exercise();
        chestEx2.setExerciseName("Flat Bench Press");
        chestEx2.setWorkoutSplitCategory(WorkoutSplitCategory.CHEST);
        chestEx2.setAdvantage(false);
        
        // BACK exercises
        Exercise backEx1 = new Exercise();
        backEx1.setExerciseName("Pull-Up");
        backEx1.setWorkoutSplitCategory(WorkoutSplitCategory.BACK);
        backEx1.setAdvantage(false);
        
        // SHOULDERS exercises
        Exercise shouldersEx1 = new Exercise();
        shouldersEx1.setExerciseName("Overhead Press");
        shouldersEx1.setWorkoutSplitCategory(WorkoutSplitCategory.SHOULDERS);
        shouldersEx1.setAdvantage(false);
        
        // Add all exercises to candidate list
        exercises.add(armsEx1);
        exercises.add(armsEx2);
        exercises.add(chestEx1);
        exercises.add(chestEx2);
        exercises.add(backEx1);
        exercises.add(shouldersEx1);
        
        // Assume we want to pick a total of 6 exercises for UPPER_BODY.
        // The integrated method uses the external properties for UPPER_BODY to derive the distribution.
        List<String> result = rankingService.pickTopExercisesByDistribution(exercises, WorkoutSplitCategory.UPPER_BODY, 6);
        
        // Verify that the result list has 6 exercise names.
        assertEquals(6, result.size(), "Should pick 6 exercises for UPPER_BODY split.");
        
        // Check that at least one exercise from each of the expected subcategories is present.
        // Expected distribution (based on our ratio and floor logic):
        // ARMS: 2 exercises, CHEST: 2 exercises, BACK: 1, SHOULDERS: 1.
        // We don't strictly control which exercise is selected from each group, but we can assert that:
        boolean containsArms = result.stream().anyMatch(name -> name.contains("Curl") || name.contains("Dumbbell Curl") || name.contains("Hammer Curl"));
        boolean containsChest = result.stream().anyMatch(name -> name.contains("Press"));
        boolean containsBack = result.stream().anyMatch(name -> name.contains("Pull-Up"));
        boolean containsShoulders = result.stream().anyMatch(name -> name.contains("Overhead Press"));
        
        assertTrue(containsArms, "Result should contain at least one ARMS exercise.");
        assertTrue(containsChest, "Result should contain at least one CHEST exercise.");
        assertTrue(containsBack, "Result should contain at least one BACK exercise.");
        assertTrue(containsShoulders, "Result should contain at least one SHOULDERS exercise.");
        
        // Optionally, print the result for manual inspection.
        result.forEach(System.out::println);
    }
}

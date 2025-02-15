package com.blancJH.weight_assistant_mobile_app_backend.algorithm.algorithm_service;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    public void testPickTopNExercisesForArms_ExcludesLegPress() {
        // Create exercises that belong to the "Arms" hierarchy.
        Exercise ex1 = new Exercise();
        ex1.setExerciseName("Dumbbell Curl");
        // Assume Biceps is a child of ARMS.
        ex1.setWorkoutSplitCategory(WorkoutSplitCategory.BICEPS);
        ex1.setAdvantage(false);
        
        Exercise ex2 = new Exercise();
        ex2.setExerciseName("Hammer Curl");
        ex2.setWorkoutSplitCategory(WorkoutSplitCategory.BICEPS);
        ex2.setAdvantage(false);
        
        Exercise ex3 = new Exercise();
        ex3.setExerciseName("Cable Push Down");
        // Assume Triceps is a child of ARMS.
        ex3.setWorkoutSplitCategory(WorkoutSplitCategory.TRICEPS);
        ex3.setAdvantage(false);
        
        // Create an exercise that does NOT belong to the "Arms" hierarchy.
        Exercise ex4 = new Exercise();
        ex4.setExerciseName("Leg Press");
        // Leg Press is typically in the LOWER_BODY category.
        ex4.setWorkoutSplitCategory(WorkoutSplitCategory.LOWER_BODY);
        ex4.setAdvantage(true); // Even with advantage bonus, it shouldn't count for ARMS.

        List<Exercise> exercises = Arrays.asList(ex1, ex2, ex3, ex4);

        // When targeting "Arms", the composite score is calculated based on the relationship.
        // Exercises with Biceps or Triceps split tags (children of ARMS) should score,
        // but Leg Press (with LOWER_BODY split tag) should score 0.
        List<String> topNames = rankingService.pickTopNExerciseNames(exercises, "Arms", 3);

        // Assert that the returned list has 3 names.
        assertEquals(3, topNames.size(), "Expected 3 top exercises for the Arms split.");
        // Assert that "Leg Press" is not in the list.
        assertFalse(topNames.contains("Leg Press"), "Leg Press should not be included in the top exercises for the Arms split.");
    }
}

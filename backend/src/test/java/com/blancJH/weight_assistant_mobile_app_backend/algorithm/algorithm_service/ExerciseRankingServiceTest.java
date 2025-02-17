package com.blancJH.weight_assistant_mobile_app_backend.algorithm.algorithm_service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.blancJH.weight_assistant_mobile_app_backend.algorithm.exercise_allocation_algorithms.ExerciseScoringAlgorithm;
import com.blancJH.weight_assistant_mobile_app_backend.model.Exercise;
import com.blancJH.weight_assistant_mobile_app_backend.model.UserExercisePreference;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutSplitCategory;

@SpringBootTest
public class ExerciseRankingServiceTest {

    @Autowired
    private ExerciseRankingService rankingService;

    @Autowired
    private ExerciseScoringAlgorithm exerciseScorer;

    /**
     * Test picking top N exercises for a given subcategory (here, "Arms") using the composite score
     * that includes hierarchical score, advantage bonus, and popularity score.
     */
    @Test
    public void testPickTopNExerciseNamesForArms() {
        // Create sample exercises.
        Exercise ex1 = new Exercise();
        ex1.setExerciseName("Hammer Curl");
        // Assume BICEPS is a child of ARMS.
        ex1.setWorkoutSplitCategory(WorkoutSplitCategory.BICEPS);
        ex1.setAdvantage(true);  // Expected composite score higher due to advantage bonus

        Exercise ex2 = new Exercise();
        ex2.setExerciseName("Dumbbell Curl");
        ex2.setWorkoutSplitCategory(WorkoutSplitCategory.BICEPS);
        ex2.setAdvantage(false); // Base score only

        Exercise ex3 = new Exercise();
        ex3.setExerciseName("Cable Push Down");
        // Assume TRICEPS is a child of ARMS.
        ex3.setWorkoutSplitCategory(WorkoutSplitCategory.TRICEPS);
        ex3.setAdvantage(false);

        Exercise ex4 = new Exercise();
        ex4.setExerciseName("Arm Extension");
        // Directly labeled as ARMS; exact match should yield a higher base score.
        ex4.setWorkoutSplitCategory(WorkoutSplitCategory.ARMS);
        ex4.setAdvantage(false);

        // Combine exercises into a list.
        List<Exercise> exercises = Arrays.asList(ex1, ex2, ex3, ex4);

        // Assume a total user count of 100 for popularity scoring.
        int totalUserCount = 100;

        // Pick top 3 exercises for the target split "Arms".
        List<String> topNames = rankingService.pickTopNExerciseNames("Arms", 3, totalUserCount);

        // Verify that we have 3 exercise names.
        assertEquals(3, topNames.size(), "Expected 3 top exercises for the Arms split.");

        // Assuming composite score computation, expect "Hammer Curl" (advantage true) to rank highest,
        // then "Arm Extension" (exact match to ARMS, base score 10) to be second,
        // and either "Dumbbell Curl" or "Cable Push Down" to be third.
        assertEquals("Hammer Curl", topNames.get(0), "Hammer Curl should be ranked first.");
        assertEquals("Arm Extension", topNames.get(1), "Arm Extension should be ranked second.");
        assertTrue(topNames.contains("Dumbbell Curl") || topNames.contains("Cable Push Down"),
                   "Either Dumbbell Curl or Cable Push Down should be included in the top 3.");
    }

    /**
     * Test picking top exercises for a target split using ratio distribution.
     * Here, the target split is UPPER_BODY. Based on your external properties,
     * UPPER_BODY has ratios defined for ARMS, CHEST, BACK, SHOULDERS, and UPPER_BODY.
     * This test creates dummy exercises from these subcategories and then checks that
     * the total number of selected exercises equals the desired total.
     */
    @Test
    public void testPickTopExercisesByDistributionForUpperBody() {
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

        // Add all exercises to the candidate list.
        exercises.addAll(Arrays.asList(armsEx1, armsEx2, chestEx1, chestEx2, backEx1, shouldersEx1));

        // Assume we want to pick a total of 6 exercises for UPPER_BODY.
        int totalUpperBodyExercises = 6;
        int totalUserCount = 100; // for popularity calculations

        // Call the distribution-based selection method.
        List<String> result = rankingService.pickTopExercisesByDistribution(WorkoutSplitCategory.UPPER_BODY, totalUpperBodyExercises, totalUserCount);

        // Verify that the result list has 6 exercise names.
        assertEquals(6, result.size(), "Should pick 6 exercises for UPPER_BODY split.");

        // Check that at least one exercise from each key subcategory is present.
        // Expected (based on your properties):
        // For UPPER_BODY, you might have distribution similar to:
        // ARMS: ~2, CHEST: ~2, BACK: ~1, SHOULDERS: ~1, UPPER_BODY: 0.
        boolean containsArms = result.stream().anyMatch(name -> name.contains("Curl"));
        boolean containsChest = result.stream().anyMatch(name -> name.contains("Press"));
        boolean containsBack = result.stream().anyMatch(name -> name.contains("Pull-Up"));
        boolean containsShoulders = result.stream().anyMatch(name -> name.contains("Overhead Press"));

        assertTrue(containsArms, "Result should contain at least one ARMS exercise.");
        assertTrue(containsChest, "Result should contain at least one CHEST exercise.");
        assertTrue(containsBack, "Result should contain at least one BACK exercise.");
        assertTrue(containsShoulders, "Result should contain at least one SHOULDERS exercise.");
        
        // Optionally, print the result for debugging.
        result.forEach(System.out::println);
    }

    @Test
    public void testCompositeScoreFavoriteBonus() {
        // Create a dummy exercise.
        Exercise exercise = new Exercise();
        exercise.setExerciseName("Hammer Curl");
        // Assume this exercise is categorized under Biceps (which is a child of ARMS).
        exercise.setWorkoutSplitCategory(WorkoutSplitCategory.BICEPS);
        // For this test, we'll set advantage to false so that any bonus comes only from the user preference.
        exercise.setAdvantage(false);

        // Assume a total user count (for popularity calculation) of 100.
        int totalUserCount = 100;

        // First, compute composite score without a user preference.
        int scoreWithoutPreference = exerciseScorer.compositeScore(exercise, WorkoutSplitCategory.ARMS, totalUserCount, null);

        // Now, simulate that the user has marked this exercise as favorite.
        UserExercisePreference userPref = new UserExercisePreference();
        // Set favorite to true.
        userPref.setFavorite(true);
        userPref.setDislike(false);

        // Compute composite score with user preference.
        int scoreWithPreference = exerciseScorer.compositeScore(exercise, WorkoutSplitCategory.ARMS, totalUserCount, userPref);

        // The expected difference should be +1 (because the method adds +1 if the user preference is favorite).
        assertEquals(scoreWithoutPreference + 1, scoreWithPreference, "Favorite bonus should add 1 to the composite score.");
    }
}

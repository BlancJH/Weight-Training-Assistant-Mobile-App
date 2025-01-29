package com.blancJH.weight_assistant_mobile_app_backend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.blancJH.weight_assistant_mobile_app_backend.model.Exercise;
import com.blancJH.weight_assistant_mobile_app_backend.model.User;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPlan;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPlanExercise;
import com.blancJH.weight_assistant_mobile_app_backend.repository.ExerciseRepository;
import com.blancJH.weight_assistant_mobile_app_backend.repository.WorkoutPlanRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WorkoutPlanServiceTest {

    @Mock
    private WorkoutPlanRepository workoutPlanRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private WorkoutPlanService workoutPlanService;

    private User testUser;
    private String mockChatGptResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("password");
        testUser.setUsername("testuser");

        mockChatGptResponse = """
        {
            "workout_plan": [
                {
                    "day": 1,
                    "split": "Chest",
                    "exercises": [
                        {
                            "exerciseName": "Bench Press",
                            "sets": 3,
                            "reps": 10
                        },
                        {
                            "exerciseName": "Incline Bench Press",
                            "sets": 4,
                            "reps": 12
                        }
                    ]
                },
                {
                    "day": 2,
                    "split": "Back",
                    "exercises": [
                        {
                            "exerciseName": "Deadlift",
                            "sets": 3,
                            "reps": 8
                        },
                        {
                            "exerciseName": "Pull-up",
                            "sets": 4,
                            "reps": 10
                        }
                    ]
                }
            ]
        }
        """;
    }

    @Test
    public void testSaveChatgptWorkoutPlan() throws Exception {
        // Mock objectMapper behavior to return parsed JSON
        when(objectMapper.readValue(anyString(), eq(Map.class)))
                .thenReturn(Map.of("workout_plan", List.of(
                        Map.of(
                                "day", 1,
                                "split", "Chest",
                                "exercises", List.of(
                                        Map.of("exerciseName", "Bench Press", "sets", 3, "reps", 10),
                                        Map.of("exerciseName", "Incline Bench Press", "sets", 4, "reps", 12)
                                )
                        ),
                        Map.of(
                                "day", 2,
                                "split", "Back",
                                "exercises", List.of(
                                        Map.of("exerciseName", "Deadlift", "sets", 3, "reps", 8),
                                        Map.of("exerciseName", "Pull-up", "sets", 4, "reps", 10)
                                )
                        )
                )));

        // Mock exercise repository to return empty (forcing new creation)
        when(exerciseRepository.findByExerciseName(anyString())).thenReturn(Optional.empty());
        when(exerciseRepository.save(any(Exercise.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Mock workoutPlanRepository save behavior
        when(workoutPlanRepository.save(any(WorkoutPlan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Call the method
        List<WorkoutPlan> savedPlans = workoutPlanService.saveChatgptWorkoutPlan(mockChatGptResponse, testUser);

        // Assertions
        assertNotNull(savedPlans);
        assertEquals(2, savedPlans.size()); // Two splits: Chest and Back

        // Check first workout plan (Chest)
        WorkoutPlan chestPlan = savedPlans.get(0);
        assertEquals("Chest", chestPlan.getSplitName());
        assertEquals(LocalDate.now(), chestPlan.getPlannedDate());
        assertFalse(chestPlan.isStatus());
        assertEquals(2, chestPlan.getExercises().size());

        // Check second workout plan (Back)
        WorkoutPlan backPlan = savedPlans.get(1);
        assertEquals("Back", backPlan.getSplitName());
        assertEquals(LocalDate.now().plusDays(1), backPlan.getPlannedDate());
        assertFalse(backPlan.isStatus());
        assertEquals(2, backPlan.getExercises().size());

        // Check exercises for first plan
        WorkoutPlanExercise firstExercise = chestPlan.getExercises().get(0);
        assertEquals("Bench Press", firstExercise.getExercise().getExerciseName());
        assertEquals(3, firstExercise.getSets());
        assertEquals(10, firstExercise.getReps());

        WorkoutPlanExercise secondExercise = chestPlan.getExercises().get(1);
        assertEquals("Incline Bench Press", secondExercise.getExercise().getExerciseName());
        assertEquals(4, secondExercise.getSets());
        assertEquals(12, secondExercise.getReps());

        // Verify interactions
        verify(workoutPlanRepository, times(2)).save(any(WorkoutPlan.class));
        verify(exerciseRepository, times(4)).findByExerciseName(anyString());
        verify(exerciseRepository, times(4)).save(any(Exercise.class));
    }
}

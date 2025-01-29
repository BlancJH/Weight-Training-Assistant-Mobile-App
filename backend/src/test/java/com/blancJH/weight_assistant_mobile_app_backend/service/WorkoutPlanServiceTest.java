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
        
        // Mock User
        testUser = new User();
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("password");
        testUser.setUsername("testuser");

        // Mock ChatGPT Response
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

    // Test for Saving Workout Plan from ChatGPT Response
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
        assertEquals(2, savedPlans.size());

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

        // Verify interactions
        verify(workoutPlanRepository, times(2)).save(any(WorkoutPlan.class));
        verify(exerciseRepository, times(4)).findByExerciseName(anyString());
        verify(exerciseRepository, times(4)).save(any(Exercise.class));
    }

    // Test for Getting Exercises by WorkoutPlan ID
    @Test
    public void testGetExercisesByWorkoutPlanId() {
        // Given: Mock WorkoutPlan
        Long workoutPlanId = 1L;
        WorkoutPlan mockWorkoutPlan = new WorkoutPlan();
        mockWorkoutPlan.setUser(testUser);
        mockWorkoutPlan.setSplitName("Chest");
        mockWorkoutPlan.setPlannedDate(LocalDate.now());
        mockWorkoutPlan.setStatus(false);

        // Given: Mock Exercises
        Exercise benchPress = new Exercise();
        benchPress.setExerciseName("Bench Press");

        Exercise inclinePress = new Exercise();
        inclinePress.setExerciseName("Incline Bench Press");

        // Given: Mock WorkoutPlanExercises
        WorkoutPlanExercise exercise1 = new WorkoutPlanExercise();
        exercise1.setWorkoutPlan(mockWorkoutPlan);
        exercise1.setExercise(benchPress);
        exercise1.setSets(3);
        exercise1.setReps(10);

        WorkoutPlanExercise exercise2 = new WorkoutPlanExercise();
        exercise2.setWorkoutPlan(mockWorkoutPlan);
        exercise2.setExercise(inclinePress);
        exercise2.setSets(4);
        exercise2.setReps(12);

        List<WorkoutPlanExercise> mockExercises = List.of(exercise1, exercise2);

        // When: Mock repository behavior
        when(workoutPlanRepository.findById(workoutPlanId)).thenReturn(Optional.of(mockWorkoutPlan));
        when(workoutPlanService.getExercisesByWorkoutPlanId(workoutPlanId)).thenReturn(mockExercises);

        // Then: Call the service method
        List<WorkoutPlanExercise> retrievedExercises = workoutPlanService.getExercisesByWorkoutPlanId(workoutPlanId);

        // Assertions: Verify the response
        assertNotNull(retrievedExercises);
        assertEquals(2, retrievedExercises.size());

        assertEquals("Bench Press", retrievedExercises.get(0).getExercise().getExerciseName());
        assertEquals(3, retrievedExercises.get(0).getSets());
        assertEquals(10, retrievedExercises.get(0).getReps());

        assertEquals("Incline Bench Press", retrievedExercises.get(1).getExercise().getExerciseName());
        assertEquals(4, retrievedExercises.get(1).getSets());
        assertEquals(12, retrievedExercises.get(1).getReps());

        // Verify that the repository method was called once
        verify(workoutPlanRepository, times(1)).findById(workoutPlanId);
    }
}

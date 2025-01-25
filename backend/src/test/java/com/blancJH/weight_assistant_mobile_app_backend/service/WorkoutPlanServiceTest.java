package com.blancJH.weight_assistant_mobile_app_backend.service;

import com.blancJH.weight_assistant_mobile_app_backend.model.User;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPlan;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutHistory;
import com.blancJH.weight_assistant_mobile_app_backend.repository.WorkoutPlanRepository;
import com.blancJH.weight_assistant_mobile_app_backend.repository.UserRepository;
import com.blancJH.weight_assistant_mobile_app_backend.repository.WorkoutHistoryRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WorkoutPlanServiceTest {

    @Autowired
    private WorkoutPlanService workoutPlanService;

    @Autowired
    private WorkoutPlanRepository workoutPlanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkoutHistoryRepository workoutHistoryRepository;

    @Test
    public void testWorkoutPlanAllocationAndDateAdjustment() {
        // Set up user
        User user = new User();
        user.setEmail("testuser@example.com");
        user.setPassword("password");
        user.setUsername("testuser");
        user = userRepository.save(user);

        // Generate mock workout plan JSON from ChatGPT
        String mockResponse = """
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
                        }
                    ]
                },
                {
                    "day": 3,
                    "split": "Legs",
                    "exercises": [
                        {
                            "exerciseName": "Squat",
                            "sets": 3,
                            "reps": 12
                        }
                    ]
                }
            ]
        }
        """;

        // Save workout plans
        List<WorkoutPlan> savedPlans = workoutPlanService.saveWorkoutPlanFromChatGptResponse(mockResponse, user);

        // Assertions on allocation
        assertNotNull(savedPlans);
        assertEquals(3, savedPlans.size());
        assertEquals("Chest", savedPlans.get(0).getSplit());
        assertEquals(LocalDate.now(), savedPlans.get(0).getPlannedDate());
        assertEquals(LocalDate.now().plusDays(1), savedPlans.get(1).getPlannedDate());
        assertEquals(LocalDate.now().plusDays(2), savedPlans.get(2).getPlannedDate());

        // Update status of the first plan to `false`
        WorkoutPlan firstPlan = savedPlans.get(0);
        firstPlan.setStatus(false);
        workoutPlanRepository.save(firstPlan);

        // Adjust subsequent plans
        workoutPlanService.adjustDatesForSkippedPlans(user);

        // Fetch updated plans
        List<WorkoutPlan> updatedPlans = workoutPlanRepository.findByUserId(user.getId());

        // Assertions on date adjustment
        assertEquals(LocalDate.now(), updatedPlans.get(0).getPlannedDate());
        assertEquals(LocalDate.now().plusDays(2), updatedPlans.get(1).getPlannedDate());
        assertEquals(LocalDate.now().plusDays(3), updatedPlans.get(2).getPlannedDate());
    }

    @Test
    public void testDay1DoneDay2NotDoneDateAdjustment() {
        // Set up user
        User user = new User();
        user.setEmail("testuser2@example.com");
        user.setPassword("password");
        user.setUsername("testuser2");
        user = userRepository.save(user);

        // Mock ChatGPT Response
        String mockResponse = """
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
                        }
                    ]
                },
                {
                    "day": 3,
                    "split": "Legs",
                    "exercises": [
                        {
                            "exerciseName": "Squat",
                            "sets": 3,
                            "reps": 12
                        }
                    ]
                }
            ]
        }
        """;

        // Save workout plans
        List<WorkoutPlan> savedPlans = workoutPlanService.saveWorkoutPlanFromChatGptResponse(mockResponse, user);

        // Assertions on initial allocation
        assertNotNull(savedPlans);
        assertEquals(3, savedPlans.size());
        assertEquals(LocalDate.now(), savedPlans.get(0).getPlannedDate());
        assertEquals(LocalDate.now().plusDays(1), savedPlans.get(1).getPlannedDate());
        assertEquals(LocalDate.now().plusDays(2), savedPlans.get(2).getPlannedDate());

        // Mark Day 1 as done
        WorkoutPlan day1Plan = savedPlans.get(0);
        day1Plan.setStatus(true);
        workoutPlanRepository.save(day1Plan);

        // Mark Day 2 as not done
        WorkoutPlan day2Plan = savedPlans.get(1);
        day2Plan.setStatus(false);
        workoutPlanRepository.save(day2Plan);

        // Adjust dates for skipped plans
        workoutPlanService.adjustDatesForSkippedPlans(user);

        // Fetch updated plans
        List<WorkoutPlan> updatedPlans = workoutPlanRepository.findByUserId(user.getId());

        // Assertions on updated plans
        assertEquals(LocalDate.now(), updatedPlans.get(0).getPlannedDate()); // Day 1 unchanged
        assertEquals(LocalDate.now().plusDays(2), updatedPlans.get(1).getPlannedDate()); // Day 2 shifted
        assertEquals(LocalDate.now().plusDays(3), updatedPlans.get(2).getPlannedDate()); // Day 3 shifted
    }

    @Test
    public void testWorkoutPlanGoesToHistoryWhenMarkedDone() {
        // Set up user
        User user = new User();
        user.setEmail("historytestuser@example.com");
        user.setPassword("password");
        user.setUsername("historytestuser");
        user = userRepository.save(user);

        // Create workout plans
        String mockResponse = """
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
                        }
                    ]
                }
            ]
        }
        """;

        List<WorkoutPlan> savedPlans = workoutPlanService.saveWorkoutPlanFromChatGptResponse(mockResponse, user);

        // Assertions on allocation
        assertNotNull(savedPlans);
        assertEquals(2, savedPlans.size());

        // Mark Day 1 as done
        WorkoutPlan day1Plan = savedPlans.get(0);
        day1Plan.setStatus(true);
        workoutPlanRepository.save(day1Plan);

        // Check workout history after marking as done
        workoutPlanService.markPlanAsDone(day1Plan.getId());

        // Verify history record
        List<WorkoutHistory> historyEntries = workoutHistoryRepository.findAll();
        assertEquals(1, historyEntries.size());

        WorkoutHistory history = historyEntries.get(0);
        assertNotNull(history);
        assertEquals(user.getId(), history.getUser().getId());
        assertEquals(day1Plan.getPlannedDate(), history.getCompletedDate());
        assertEquals(day1Plan.getSplit(), history.getSplit());
        assertEquals(day1Plan.getExercises(), history.getExercises());

        // Verify the status in the workout plan
        WorkoutPlan updatedPlan = workoutPlanRepository.findById(day1Plan.getId()).orElseThrow();
        assertTrue(updatedPlan.isStatus());
    }

    @Test
    public void testResetAndRescheduleWorkoutPlans() {
        // Create a mock user
        User user = new User();
        user.setEmail("testuser@example.com");
        user.setPassword("password");
        user.setUsername("testuser");
        user = userRepository.save(user);

        // Create mock workout plans
        WorkoutPlan plan1 = new WorkoutPlan(null, user, LocalDate.now(), "Chest", "{}", true);
        WorkoutPlan plan2 = new WorkoutPlan(null, user, LocalDate.now().plusDays(1), "Back", "{}", true);
        WorkoutPlan plan3 = new WorkoutPlan(null, user, LocalDate.now().plusDays(2), "Legs", "{}", true);

        workoutPlanRepository.saveAll(List.of(plan1, plan2, plan3));

        // Reset and reschedule
        List<WorkoutPlan> updatedPlans = workoutPlanService.resetAndRescheduleWorkoutPlans(user.getId());

        // Validate
        assertNotNull(updatedPlans);
        assertEquals(3, updatedPlans.size());
        assertFalse(updatedPlans.get(0).isStatus());
        assertEquals(LocalDate.now().plusDays(3), updatedPlans.get(0).getPlannedDate());
        assertEquals(LocalDate.now().plusDays(4), updatedPlans.get(1).getPlannedDate());
        assertEquals(LocalDate.now().plusDays(5), updatedPlans.get(2).getPlannedDate());
    }

    @Test
    public void testAutoRepeatWorkoutPlans() {
        // Arrange
        User user = new User();
        user.setEmail("testuser@example.com");
        user.setPassword("password");
        user.setUsername("testuser");
        user = userRepository.save(user);

        List<WorkoutPlan> plans = List.of(
            new WorkoutPlan(user, LocalDate.now(), "Chest", "{}", true),
            new WorkoutPlan(user, LocalDate.now().plusDays(1), "Back", "{}", true),
            new WorkoutPlan(user, LocalDate.now().plusDays(2), "Legs", "{}", true)
        );
        workoutPlanRepository.saveAll(plans);

        // Act
        workoutPlanService.markPlanAsDone(plans.get(2).getId());

        // Assert
        List<WorkoutPlan> updatedPlans = workoutPlanRepository.findByUserId(user.getId());
        assertEquals(3, updatedPlans.size());
        assertFalse(updatedPlans.get(0).isStatus());
        assertEquals(LocalDate.now().plusDays(3), updatedPlans.get(0).getPlannedDate());
    }

    @Test
    public void testModifyWorkoutPlans() {
        // Create a user
        User user = new User();
        user.setEmail("testuser@example.com");
        user.setPassword("password");
        user.setUsername("testuser");
        user = userRepository.save(user);

        // Create original workout plans
        String mockResponse = """
        {
            "workout_plan": [
                {
                    "day": 1,
                    "split": "Push",
                    "exercises": [
                        {
                            "exerciseName": "Push-up",
                            "sets": 3,
                            "reps": 10
                        },
                        {
                            "exerciseName": "Overhead Press",
                            "sets": 3,
                            "reps": 8
                        }
                    ]
                },
                {
                    "day": 2,
                    "split": "Pull",
                    "exercises": [
                        {
                            "exerciseName": "Pull-up",
                            "sets": 3,
                            "reps": 8
                        },
                        {
                            "exerciseName": "Bent-over Rows",
                            "sets": 3,
                            "reps": 12
                        }
                    ]
                }
            ]
        }
        """;

        // Save the plans
        List<WorkoutPlan> savedPlans = workoutPlanService.saveWorkoutPlanFromChatGptResponse(mockResponse, user);

        // Verify original plans
        assertEquals(2, savedPlans.size());

        WorkoutPlan firstPlan = savedPlans.get(0);
        WorkoutPlan secondPlan = savedPlans.get(1);

        assertEquals("Push", firstPlan.getSplit());
        assertEquals("Pull", secondPlan.getSplit());

        // Original second plan's first exercise
        String originalSecondPlanExercises = secondPlan.getExercises();
        assertTrue(originalSecondPlanExercises.contains("Pull-up"));

        // Modify the second split's first exercise
        List<Map<String, Object>> updatedExercises = List.of(
            Map.of("exerciseName", "Barbell Rows", "sets", 4, "reps", 10),
            Map.of("exerciseName", "Bent-over Rows", "sets", 3, "reps", 12)
        );

        WorkoutPlan updatedPlan = workoutPlanService.editWorkoutPlan(secondPlan.getId(), updatedExercises);

        // Verify the update
        String updatedSecondPlanExercises = updatedPlan.getExercises();
        assertTrue(updatedSecondPlanExercises.contains("Barbell Rows"));
        assertFalse(updatedSecondPlanExercises.contains("Pull-up"));

        // Clean up
        workoutPlanRepository.deleteAll();
        userRepository.delete(user);
    }
}

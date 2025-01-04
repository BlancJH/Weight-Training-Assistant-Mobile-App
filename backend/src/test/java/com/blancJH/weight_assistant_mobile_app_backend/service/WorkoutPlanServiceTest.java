package com.blancJH.weight_assistant_mobile_app_backend.service;

import com.blancJH.weight_assistant_mobile_app_backend.model.User;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPlan;
import com.blancJH.weight_assistant_mobile_app_backend.repository.WorkoutPlanRepository;
import com.blancJH.weight_assistant_mobile_app_backend.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WorkoutPlanServiceTest {

    @Autowired
    private WorkoutPlanService workoutPlanService;

    @Autowired
    private WorkoutPlanRepository workoutPlanRepository;

    @Autowired
    private UserRepository userRepository;

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

}

package com.blancJH.weight_assistant_mobile_app_backend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.blancJH.weight_assistant_mobile_app_backend.model.User;
import com.blancJH.weight_assistant_mobile_app_backend.model.UserDetails;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPlan;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPlanStatus;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPurpose;
import com.blancJH.weight_assistant_mobile_app_backend.repository.WorkoutPlanRepository;

@SpringBootTest
@Transactional
public class WorkoutPlanServiceTest {

    @Autowired
    private WorkoutPlanService workoutPlanService;

    @Autowired
    private WorkoutPlanRepository workoutPlanRepository;

    private User testUser;
    private Long testUserId;

    @BeforeEach
    public void setup() {
        // Create and persist a test user.
        testUser = new User();
        // Depending on your implementation, you might need to persist the user.
        // For this test, we assume the user already exists or is managed.
        testUser.setId(1L);
        // Set additional fields if necessary.
        testUserId = testUser.getId();
    }

    @Test
    public void testRescheduleUserIncompletedWorkoutPlans_updatesDates() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // Create a workout plan with plannedDate = yesterday and status SCHEDULED.
        WorkoutPlan plan1 = new WorkoutPlan();
        plan1.setUser(testUser);
        plan1.setPlannedDate(yesterday);
        plan1.setStatus(WorkoutPlanStatus.SCHEDULED);
        workoutPlanRepository.save(plan1);

        // Create another workout plan with plannedDate = yesterday.plusDays(2) (>= yesterday)
        WorkoutPlan plan2 = new WorkoutPlan();
        plan2.setUser(testUser);
        plan2.setPlannedDate(yesterday.plusDays(2));
        plan2.setStatus(WorkoutPlanStatus.SCHEDULED);
        workoutPlanRepository.save(plan2);

        // Call the reschedule method.
        workoutPlanService.rescheduleUserIncompletedWorkoutPlans(testUserId);

        // Fetch updated workout plans with plannedDate >= yesterday.
        List<WorkoutPlan> updatedPlans = workoutPlanRepository.findByUserIdAndStatusAndPlannedDateGreaterThanEqual(
                testUserId, WorkoutPlanStatus.SCHEDULED, yesterday);

        // Check plan1: yesterday -> yesterday.plusDays(1)
        Optional<WorkoutPlan> updatedPlan1Opt = updatedPlans.stream()
                .filter(p -> p.getId().equals(plan1.getId()))
                .findFirst();
        // Check plan2: yesterday.plusDays(2) -> yesterday.plusDays(3)
        Optional<WorkoutPlan> updatedPlan2Opt = updatedPlans.stream()
                .filter(p -> p.getId().equals(plan2.getId()))
                .findFirst();

        assertTrue(updatedPlan1Opt.isPresent(), "Plan1 should be present after rescheduling");
        assertTrue(updatedPlan2Opt.isPresent(), "Plan2 should be present after rescheduling");

        assertEquals(yesterday.plusDays(1), updatedPlan1Opt.get().getPlannedDate(),
                "Plan1 planned date should be updated by 1 day");
        assertEquals(yesterday.plusDays(3), updatedPlan2Opt.get().getPlannedDate(),
                "Plan2 planned date should be updated by 1 day");
    }

    @Test
    public void testRescheduleMultiplePlansOnSameDay_updatesDates() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // Create two workout plans on the same day: yesterday.
        WorkoutPlan plan1 = new WorkoutPlan();
        plan1.setUser(testUser);
        plan1.setPlannedDate(yesterday);
        plan1.setStatus(WorkoutPlanStatus.SCHEDULED);
        workoutPlanRepository.save(plan1);

        WorkoutPlan plan2 = new WorkoutPlan();
        plan2.setUser(testUser);
        plan2.setPlannedDate(yesterday);
        plan2.setStatus(WorkoutPlanStatus.SCHEDULED);
        workoutPlanRepository.save(plan2);

        // Call the reschedule method.
        workoutPlanService.rescheduleUserIncompletedWorkoutPlans(testUserId);

        // Fetch updated workout plans with plannedDate >= yesterday.
        List<WorkoutPlan> updatedPlans = workoutPlanRepository.findByUserIdAndStatusAndPlannedDateGreaterThanEqual(
                testUserId, WorkoutPlanStatus.SCHEDULED, yesterday);

        // Both plans should now have their planned date updated to yesterday.plusDays(1)
        Optional<WorkoutPlan> updatedPlan1Opt = updatedPlans.stream()
                .filter(p -> p.getId().equals(plan1.getId()))
                .findFirst();
        Optional<WorkoutPlan> updatedPlan2Opt = updatedPlans.stream()
                .filter(p -> p.getId().equals(plan2.getId()))
                .findFirst();

        assertTrue(updatedPlan1Opt.isPresent(), "Plan1 should be present after rescheduling");
        assertTrue(updatedPlan2Opt.isPresent(), "Plan2 should be present after rescheduling");

        // Both plans should now be scheduled for the same day: yesterday.plusDays(1)
        assertEquals(yesterday.plusDays(1), updatedPlan1Opt.get().getPlannedDate(),
                "Plan1 planned date should be updated by 1 day");
        assertEquals(yesterday.plusDays(1), updatedPlan2Opt.get().getPlannedDate(),
                "Plan2 planned date should be updated by 1 day");
    }

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
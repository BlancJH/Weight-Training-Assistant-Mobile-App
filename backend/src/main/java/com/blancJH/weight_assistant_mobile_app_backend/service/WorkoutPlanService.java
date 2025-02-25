package com.blancJH.weight_assistant_mobile_app_backend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.blancJH.weight_assistant_mobile_app_backend.algorithm.algorithm_service.ExerciseRankingService;
import com.blancJH.weight_assistant_mobile_app_backend.algorithm.algorithm_service.UserStatisticsService;
import com.blancJH.weight_assistant_mobile_app_backend.algorithm.algorithm_service.WorkoutSplitAllocationService;
import com.blancJH.weight_assistant_mobile_app_backend.model.Exercise;
import com.blancJH.weight_assistant_mobile_app_backend.model.User;
import com.blancJH.weight_assistant_mobile_app_backend.model.UserDetails;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPlan;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPlanExercise;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPlanStatus;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutSplit;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutSplitCategory;
import com.blancJH.weight_assistant_mobile_app_backend.repository.ExerciseRepository;
import com.blancJH.weight_assistant_mobile_app_backend.repository.UserDetailsRepository;
import com.blancJH.weight_assistant_mobile_app_backend.repository.UserRepository;
import com.blancJH.weight_assistant_mobile_app_backend.repository.WorkoutPlanRepository;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class WorkoutPlanService {

    private final WorkoutPlanRepository workoutPlanRepository;
    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final ExerciseRepository exerciseRepository;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(WorkoutPlanService.class);

    public WorkoutPlanService(
        WorkoutPlanRepository workoutPlanRepository,
        UserRepository userRepository,
        UserDetailsRepository userDetailsRepository,
        ExerciseRepository exerciseRepository,
        ObjectMapper objectMapper
    ) {
        this.workoutPlanRepository = workoutPlanRepository;
        this.userRepository = userRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.exerciseRepository = exerciseRepository;
        this.objectMapper = objectMapper;
    }

    @Autowired
    private WorkoutSplitAllocationService splitAllocationService;

    @Autowired
    private ExerciseRankingService rankingService;
    
    @Autowired
    private UserStatisticsService userStatisticsService;

    /**
     * Creates workout plans for a user by:
     * <ol>
     *   <li>Allocating splits based on the user's workout purpose and number of splits.</li>
     *   <li>Calculating the number of exercises per split from the workout duration.</li>
     *   <li>For each split, retrieving the top exercises using the ratio distribution, then creating a WorkoutPlan
     *       and its associated WorkoutPlanExercise entities (which link via foreign keys).</li>
     *   <li>Each WorkoutPlan gets a planned date starting from today, its status is set to false, and the split category is saved.</li>
     * </ol>
     *
     * @param userDetails The user's workout details.
     * @return A list of saved WorkoutPlan entities.
     */
    public List<WorkoutPlan> createWorkoutPlans(UserDetails userDetails) {
        if (userDetails == null) {
            throw new IllegalArgumentException("User details must not be null");
        }
        
        // 1. Allocate splits based on the user's workout purpose and number of splits.
        List<WorkoutSplit> splits = splitAllocationService.allocateSplitsForUser(userDetails);
        
        // 2. Calculate the number of exercises to pick per split based on workout duration.
        int exerciseCountPerSplit = splitAllocationService.calculateExerciseCount(userDetails.getWorkoutDuration());
        
        // 3. Get total user count for popularity scoring.
        int totalUserCount = userStatisticsService.getTotalUserCount();
        
        List<WorkoutPlan> plans = new ArrayList<>();
        LocalDate startDate = LocalDate.now();
        
        // 4. For each allocated split, create a WorkoutPlan and associated WorkoutPlanExercise entities.
        for (int i = 0; i < splits.size(); i++) {
            WorkoutSplit split = splits.get(i);
            // Convert the split's display name to its corresponding enum.
            WorkoutSplitCategory targetCategory = WorkoutSplitCategory.fromDisplayName(split.getCategoryPath());
            if (targetCategory == null) {
                continue;
            }
            
            // Retrieve top exercise names for this split using the ranking service.
            List<String> exerciseNames = rankingService.pickTopExercisesByDistribution(targetCategory, exerciseCountPerSplit, totalUserCount);
            
            // Create a new WorkoutPlan.
            WorkoutPlan plan = new WorkoutPlan();
            plan.setUser(userDetails.getUser());
            plan.setPlannedDate(startDate.plusDays(i)); // Set planned date starting today.
            plan.setStatus(WorkoutPlanStatus.SCHEDULED);
            plan.setWorkoutSplitCategory(targetCategory);
            
            // Create WorkoutPlanExercise entities for each selected exercise.
            List<WorkoutPlanExercise> planExercises = new ArrayList<>();
            for (String exerciseName : exerciseNames) {
                // Look up the Exercise entity by name.
                Exercise exercise = exerciseRepository.findByExerciseName(exerciseName).orElse(null);
                if (exercise != null) {
                    WorkoutPlanExercise wpe = new WorkoutPlanExercise();
                    // Set foreign keys by setting the associated entities.
                    wpe.setWorkoutPlan(plan);  // This sets the workoutPlanId foreign key.
                    wpe.setExercise(exercise); // This sets the exerciseId foreign key.
                    // Optionally, you can set default values for sets, reps, or duration.
                    planExercises.add(wpe);
                }
            }
            plan.setExercises(planExercises);
            
            // Persist the workout plan (which cascades the WorkoutPlanExercise entities if mapped accordingly).
            plans.add(workoutPlanRepository.save(plan));
        }
        return plans;
    }


    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void rescheduleUserIncompletedWorkoutPlans(Long userId) {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // 1. Check if the user's workout plan from yesterday is still SCHEDULED.
        Optional<WorkoutPlan> yesterdayPlanOpt = workoutPlanRepository.findByUserIdAndPlannedDateAndStatus(
                userId, yesterday, WorkoutPlanStatus.SCHEDULED);

        if (yesterdayPlanOpt.isPresent()) {
            // 2. Fetch all scheduled workout plans for the user with plannedDate >= yesterday.
            List<WorkoutPlan> scheduledPlans = workoutPlanRepository
                .findByUserIdAndStatusAndPlannedDateGreaterThanEqual(
                    userId, WorkoutPlanStatus.SCHEDULED, yesterday);

            if (!scheduledPlans.isEmpty()) {
                // 3. Postpone each scheduled workout plan by +1 day.
                for (WorkoutPlan plan : scheduledPlans) {
                    plan.setPlannedDate(plan.getPlannedDate().plusDays(1));
                }
                workoutPlanRepository.saveAll(scheduledPlans);
            }
        }
    }

    public void markPlanAsDone(Long planId) {
        // Retrieve the workout plan or throw an exception if not found
        WorkoutPlan plan = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Workout Plan not found"));

        // Mark the plan as done
        plan.setStatus(WorkoutPlanStatus.COMPLETED);
        workoutPlanRepository.save(plan);
    }

    @Transactional
    public void repeatCompletedWorkoutPlans(Long userId) {
        // 1. Retrieve user details (assuming a service or repository exists for this)
        UserDetails userDetails = userDetailsRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User details not found"));
        int numberOfSplit = userDetails.getNumberOfSplit();

        // 2. Retrieve the most recent workout plan for the user (sorted by planned date descending)
        WorkoutPlan lastWorkoutPlan = workoutPlanRepository.findTopByUserIdOrderByPlannedDateDesc(userId)
                .orElseThrow(() -> new RuntimeException("No workout plans found for user"));

        // Only proceed if the most recent workout plan is COMPLETED.
        if (lastWorkoutPlan.getStatus() != WorkoutPlanStatus.COMPLETED) {
            return; // User hasn't finished all workout plans.
        }
        
        LocalDate lastCompletedDate = lastWorkoutPlan.getPlannedDate();

        // 3. Fetch the latest 'numberOfSplit' workout plans to use as templates.
        // Here we assume a repository method that returns the top N plans, sorted descending.
        List<WorkoutPlan> templatePlans = workoutPlanRepository.findTopNByUserIdOrderByPlannedDateDesc(userId, numberOfSplit);
        
        // Reverse the list so that the oldest among the latest is first.
        Collections.reverse(templatePlans);

        // 4. Duplicate each template plan and set new planned dates.
        for (int i = 0; i < templatePlans.size(); i++) {
            WorkoutPlan originalPlan = templatePlans.get(i);
            WorkoutPlan newPlan = duplicateWorkoutPlan(originalPlan);
            
            // Set the new planned date as one day after the lastCompletedDate plus the index offset.
            newPlan.setPlannedDate(lastCompletedDate.plusDays(i + 1));
            newPlan.setStatus(WorkoutPlanStatus.SCHEDULED);
            
            workoutPlanRepository.save(newPlan);
        }
    }

    @Transactional
    public void updateWorkoutPlanExercises(Long workoutPlanId, List<Map<String, Object>> updatedExercises) {
        // 1. Fetch the workout plan by ID.
        WorkoutPlan plan = workoutPlanRepository.findById(workoutPlanId)
                .orElseThrow(() -> new RuntimeException("Workout plan not found"));

        // 2. Iterate over each update request.
        for (Map<String, Object> exerciseMap : updatedExercises) {
            // Extract the workoutPlanExerciseId and newExerciseId from the map.
            Long workoutPlanExerciseId = ((Number) exerciseMap.get("workoutPlanExerciseId")).longValue();
            Long newExerciseId = ((Number) exerciseMap.get("newExerciseId")).longValue();

            // Find the corresponding WorkoutPlanExercise from the plan.
            Optional<WorkoutPlanExercise> optWpe = plan.getExercises().stream()
                    .filter(wpe -> wpe.getId().equals(workoutPlanExerciseId))
                    .findFirst();

            if (!optWpe.isPresent()) {
                // Optionally, log or handle missing workoutPlanExercise.
                continue;
            }
            WorkoutPlanExercise wpe = optWpe.get();

            // 3. Retrieve the new Exercise entity by its ID.
            Exercise newExercise = exerciseRepository.findById(newExerciseId)
                    .orElseThrow(() -> new RuntimeException("Exercise not found for id: " + newExerciseId));

            // 4. Update the exercise reference.
            wpe.setExercise(newExercise);
        }
        // 5. Save the workout plan to persist changes in its exercises.
        workoutPlanRepository.save(plan);
    }

    public List<WorkoutPlanExercise> getExercisesByWorkoutPlanId(Long workoutPlanId) {
        WorkoutPlan workoutPlan = workoutPlanRepository.findById(workoutPlanId)
                .orElseThrow(() -> new RuntimeException("Workout Plan not found"));
        return workoutPlan.getExercises();
    }

    public void deleteIncompleteWorkoutPlans(User user) {

        // Retrieve incomplete workout plans for the user
        List<WorkoutPlan> incompletePlans = workoutPlanRepository.findByUserIdAndStatus(user.getId(), WorkoutPlanStatus.SCHEDULED);

        // Check if there are any incomplete plans to delete
        if (incompletePlans == null || incompletePlans.isEmpty()) {
            // No incomplete workout plans found; skip deletion
            return;
        }
        
        // Delete all found workout plans
        workoutPlanRepository.deleteAll(incompletePlans);
    }

    private WorkoutPlan duplicateWorkoutPlan(WorkoutPlan originalPlan) {
        WorkoutPlan newPlan = new WorkoutPlan();
        newPlan.setUser(originalPlan.getUser());
        newPlan.setWorkoutSplitCategory(originalPlan.getWorkoutSplitCategory());
        // Duplicate other necessary fields if any.

        // Duplicate the associated workoutPlanExercises.
        List<WorkoutPlanExercise> newExercises = new ArrayList<>();
        for (WorkoutPlanExercise originalExercise : originalPlan.getExercises()) {
            WorkoutPlanExercise newExercise = new WorkoutPlanExercise();
            newExercise.setSets(originalExercise.getSets());
            newExercise.setReps(originalExercise.getReps());
            newExercise.setDuration(originalExercise.getDuration());
            newExercise.setExercise(originalExercise.getExercise()); // Reuse the same Exercise
            newExercise.setWorkoutPlan(newPlan); // Link the exercise to the new plan
            newExercises.add(newExercise);
        }
        newPlan.setExercises(newExercises);
        return newPlan;
    }

    public List<WorkoutPlan> getWorkoutPlansByUserId(Long userId) {
        return workoutPlanRepository.findByUserId(userId);
    }
}

package com.blancJH.weight_assistant_mobile_app_backend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blancJH.weight_assistant_mobile_app_backend.algorithm.algorithm_service.ExerciseRankingService;
import com.blancJH.weight_assistant_mobile_app_backend.algorithm.algorithm_service.UserStatisticsService;
import com.blancJH.weight_assistant_mobile_app_backend.algorithm.algorithm_service.WorkoutSplitAllocationService;
import com.blancJH.weight_assistant_mobile_app_backend.model.Exercise;
import com.blancJH.weight_assistant_mobile_app_backend.model.User;
import com.blancJH.weight_assistant_mobile_app_backend.model.UserDetails;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPlan;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPlanExercise;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutSplit;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutSplitCategory;
import com.blancJH.weight_assistant_mobile_app_backend.repository.ExerciseRepository;
import com.blancJH.weight_assistant_mobile_app_backend.repository.UserRepository;
import com.blancJH.weight_assistant_mobile_app_backend.repository.WorkoutPlanRepository;
import com.blancJH.weight_assistant_mobile_app_backend.util.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class WorkoutPlanService {

    private final WorkoutPlanRepository workoutPlanRepository;
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(WorkoutPlanService.class);

    public WorkoutPlanService(
        WorkoutPlanRepository workoutPlanRepository,
        UserRepository userRepository,
        ExerciseRepository exerciseRepository,
        ObjectMapper objectMapper
    ) {
        this.workoutPlanRepository = workoutPlanRepository;
        this.userRepository = userRepository;
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
            plan.setStatus(false);
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


    public void adjustDatesForSkippedPlans(User user) {
        List<WorkoutPlan> plans = workoutPlanRepository.findByUserIdAndStatusFalse(user.getId());

        plans.sort(Comparator.comparing(WorkoutPlan::getPlannedDate));

        LocalDate today = LocalDate.now();
        for (WorkoutPlan plan : plans) {
            if (plan.getPlannedDate().isBefore(today)) {
                plan.setPlannedDate(today);
                today = today.plusDays(1);
            }
        }

        workoutPlanRepository.saveAll(plans);
    }

    public void markPlanAsDone(Long planId) {
        // Retrieve the workout plan or throw an exception if not found
        WorkoutPlan plan = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Workout Plan not found"));

        // Mark the plan as done
        plan.setStatus(true);
        workoutPlanRepository.save(plan);

        // Check if all plans for the user are done
        Long userId = plan.getUser().getId();
        boolean allPlansDone = workoutPlanRepository.findByUserId(userId)
                .stream()
                .allMatch(WorkoutPlan::isStatus);

        if (allPlansDone) {
            // Duplicate workout plans and reset
            resetAndRescheduleWorkoutPlans(userId);
        }
    }

    public void resetAndRescheduleWorkoutPlans(Long userId) {
        // Fetch all existing workout plans for the user
        List<WorkoutPlan> existingPlans = workoutPlanRepository.findByUserId(userId);

        if (existingPlans.isEmpty()) {
            throw new RuntimeException("No workout plans found for the user.");
        }

        // Get the user from the existing plans
        User user = existingPlans.get(0).getUser();

        // Determine the starting date for the new plans
        LocalDate startDate = LocalDate.now();

        // Duplicate workout plans
        for (int i = 0; i < existingPlans.size(); i++) {
            WorkoutPlan originalPlan = existingPlans.get(i);

            // Create a new WorkoutPlan
            WorkoutPlan newPlan = new WorkoutPlan();
            newPlan.setUser(user);
            newPlan.setPlannedDate(startDate.plusDays(i)); // Set new dates sequentially
            newPlan.setStatus(false); // Reset status to not done
            newPlan.setWorkoutSplitCategory(originalPlan.getWorkoutSplitCategory()); // Copy split name

            // Duplicate exercises
            List<WorkoutPlanExercise> newPlanExercises = new ArrayList<>();
            for (WorkoutPlanExercise originalExercises : originalPlan.getExercises()) {
                WorkoutPlanExercise newExercise = new WorkoutPlanExercise();
                newExercise.setSets(originalExercises.getSets());
                newExercise.setReps(originalExercises.getReps());
                newExercise.setExercise(originalExercises.getExercise()); // Reuse the same Exercise
                newExercise.setWorkoutPlan(newPlan); // Set relationship to the new plan

                newPlanExercises.add(newExercise);
            }

            newPlan.setExercises(newPlanExercises);

            // Save the new workout plan
            workoutPlanRepository.save(newPlan);
        }
    }

    public WorkoutPlan editWorkoutPlan(Long workoutPlanId, List<Map<String, Object>> updatedExercises) {
        // Fetch the workout plan by ID
        WorkoutPlan plan = workoutPlanRepository.findById(workoutPlanId)
                .orElseThrow(() -> new RuntimeException("Workout Plan not found"));

        // Clear existing exercises for this workout plan
        plan.getExercises().clear();

        // Parse and update exercises from the updatedExercises list
        List<WorkoutPlanExercise> updatedPlanExercises = new ArrayList<>();

        for (Map<String, Object> exerciseMap : updatedExercises) {
            // Normalize the exercise name
            String rawExerciseName = (String) exerciseMap.get("exerciseName");
            String normalizedExerciseName = StringUtil.normaliseExerciseName(rawExerciseName);

            // Find or create the Exercise entity
            Exercise exercise = exerciseRepository.findByExerciseName(normalizedExerciseName)
                    .orElseGet(() -> {
                        Exercise newExercise = new Exercise();
                        newExercise.setExerciseName(normalizedExerciseName);
                        newExercise.setExerciseCategory(null); // Set default or null
                        newExercise.setPrimaryMuscle(null); // Set muscles to null
                        newExercise.setSecondaryMuscle(null); // Set muscles to null
                        newExercise.setExerciseGifUrl(null); // Optional: set GIF URL to null
                        return exerciseRepository.save(newExercise);
                    });

            // Create a new WorkoutPlanExercises entity
            WorkoutPlanExercise workoutPlanExercise = new WorkoutPlanExercise();
            workoutPlanExercise.setExercise(exercise);
            workoutPlanExercise.setSets((Integer) exerciseMap.get("sets"));
            workoutPlanExercise.setReps((Integer) exerciseMap.get("reps"));
            workoutPlanExercise.setWorkoutPlan(plan); // Link it to the current workout plan

            updatedPlanExercises.add(workoutPlanExercise);
        }

        // Update the workout plan with new exercises
        plan.setExercises(updatedPlanExercises);

        // Save the updated workout plan and return it
        return workoutPlanRepository.save(plan);
    }

    public List<WorkoutPlan> getWorkoutPlansByUserId(Long userId) {
        return workoutPlanRepository.findByUserId(userId);
    }

    public List<WorkoutPlanExercise> getExercisesByWorkoutPlanId(Long workoutPlanId) {
        WorkoutPlan workoutPlan = workoutPlanRepository.findById(workoutPlanId)
                .orElseThrow(() -> new RuntimeException("Workout Plan not found"));
        return workoutPlan.getExercises();
    }

    public void deleteIncompleteWorkoutPlans(User user) {

        // Retrieve incomplete workout plans for the user
        List<WorkoutPlan> incompletePlans = workoutPlanRepository.findByUserIdAndStatusFalse(user.getId());

        // Check if there are any incomplete plans to delete
        if (incompletePlans == null || incompletePlans.isEmpty()) {
            // No incomplete workout plans found; skip deletion
            return;
        }
        
        // Delete all found workout plans
        workoutPlanRepository.deleteAll(incompletePlans);
    }

}

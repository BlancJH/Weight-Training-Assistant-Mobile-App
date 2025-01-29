package com.blancJH.weight_assistant_mobile_app_backend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.blancJH.weight_assistant_mobile_app_backend.model.Exercise;
import com.blancJH.weight_assistant_mobile_app_backend.model.User;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPlan;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPlanExercise;
import com.blancJH.weight_assistant_mobile_app_backend.repository.ExerciseRepository;
import com.blancJH.weight_assistant_mobile_app_backend.repository.UserRepository;
import com.blancJH.weight_assistant_mobile_app_backend.repository.WorkoutHistoryRepository;
import com.blancJH.weight_assistant_mobile_app_backend.repository.WorkoutPlanRepository;
import com.blancJH.weight_assistant_mobile_app_backend.util.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WorkoutPlanService {

    private final WorkoutPlanRepository workoutPlanRepository;
    private final WorkoutHistoryRepository workoutHistoryRepository;
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final ObjectMapper objectMapper;

    public WorkoutPlanService(
        WorkoutPlanRepository workoutPlanRepository,
        WorkoutHistoryRepository workoutHistoryRepository,
        UserRepository userRepository,
        ExerciseRepository exerciseRepository,
        ObjectMapper objectMapper
    ) {
        this.workoutPlanRepository = workoutPlanRepository;
        this.workoutHistoryRepository = workoutHistoryRepository;
        this.userRepository = userRepository;
        this.exerciseRepository = exerciseRepository;
        this.objectMapper = objectMapper;
    }

    public List<WorkoutPlan> saveChatgptWorkoutPlan(String chatgptResponse, User user) {
        try {
            // Parse the ChatGPT response into a Map
            Map<String, Object> responseMap = objectMapper.readValue(chatgptResponse, Map.class);
            List<Map<String, Object>> workoutDays = (List<Map<String, Object>>) responseMap.get("workout_plan");

            if (workoutDays == null || workoutDays.isEmpty()) {
                throw new RuntimeException("Invalid workout plan format in ChatGPT response.");
            }

            LocalDate currentDate = LocalDate.now();
            List<WorkoutPlan> workoutPlans = new ArrayList<>();

            for (Map<String, Object> dayPlan : workoutDays) {
                // Create a new WorkoutPlan
                WorkoutPlan workoutPlan = new WorkoutPlan();
                workoutPlan.setUser(user);
                workoutPlan.setPlannedDate(currentDate.plusDays((int) dayPlan.get("day") - 1));
                workoutPlan.setSplitName((String) dayPlan.get("split"));
                workoutPlan.setStatus(false); // Default: not done

                // Handle exercises for this workout plan
                List<Map<String, Object>> exercises = (List<Map<String, Object>>) dayPlan.get("exercises");
                List<WorkoutPlanExercise> workoutPlanExercisesList = new ArrayList<>();

                for (Map<String, Object> exerciseMap : exercises) {
                    // Normalize the exercise name
                    String rawExerciseName = (String) exerciseMap.get("exerciseName");
                    String normalizedExerciseName = StringUtil.normaliseExerciseName(rawExerciseName);

                    // Find existing exercise or create a new one
                    Exercise exercise = exerciseRepository.findByExerciseName(normalizedExerciseName)
                            .orElseGet(() -> {
                                Exercise newExercise = new Exercise();
                                newExercise.setExerciseName(normalizedExerciseName);
                                newExercise.setExerciseCategory(null); // Set category to null
                                newExercise.setMuscles(null); // Set muscles to null
                                newExercise.setExerciseGifUrl(null); // Set GIF URL to null
                                return exerciseRepository.save(newExercise);
                            });

                    // Create a new WorkoutPlanExercises
                    WorkoutPlanExercise workoutPlanExercise = new WorkoutPlanExercise();
                    workoutPlanExercise.setExercise(exercise);
                    workoutPlanExercise.setSets((Integer) exerciseMap.get("sets"));
                    workoutPlanExercise.setReps((Integer) exerciseMap.get("reps"));
                    workoutPlanExercise.setWorkoutPlan(workoutPlan); // Associate with the workout plan

                    workoutPlanExercisesList.add(workoutPlanExercise);
                }

                workoutPlan.setExercises(workoutPlanExercisesList);

                // Save the workout plan
                workoutPlanRepository.save(workoutPlan);
                workoutPlans.add(workoutPlan);
            }

            return workoutPlans;

        } catch (Exception e) {
            throw new RuntimeException("Error processing ChatGPT workout plan response", e);
        }
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
            newPlan.setSplitName(originalPlan.getSplitName()); // Copy split name

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
                        newExercise.setMuscles(null); // Optional: set muscles to null
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
}

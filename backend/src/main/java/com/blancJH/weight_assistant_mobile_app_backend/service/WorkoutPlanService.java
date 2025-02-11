package com.blancJH.weight_assistant_mobile_app_backend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.blancJH.weight_assistant_mobile_app_backend.model.Exercise;
import com.blancJH.weight_assistant_mobile_app_backend.model.User;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPlan;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPlanExercise;
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

    public List<WorkoutPlan> saveChatgptWorkoutPlan(String chatgptResponse, User user) {
        logger.debug("Initiating saveChatgptWorkoutPlan for user: {}", user.getUsername());
        try {
            // Parse the ChatGPT response into a Map
            Map<String, Object> responseMap = objectMapper.readValue(chatgptResponse, Map.class);
            logger.debug("Parsed ChatGPT response successfully: {}", responseMap);
            
            List<Map<String, Object>> workoutDays = (List<Map<String, Object>>) responseMap.get("workout_plan");
            if (workoutDays == null || workoutDays.isEmpty()) {
                String errorMsg = "Invalid workout plan format in ChatGPT response: 'workout_plan' is missing or empty.";
                logger.error(errorMsg + " Raw response: {}", chatgptResponse);
                throw new RuntimeException(errorMsg + " Raw response: " + chatgptResponse);
            }
            
            LocalDate currentDate = LocalDate.now();
            List<WorkoutPlan> workoutPlans = new ArrayList<>();
            
            for (Map<String, Object> dayPlan : workoutDays) {
                logger.debug("Processing day plan: {}", dayPlan);
                
                WorkoutPlan workoutPlan = new WorkoutPlan();
                workoutPlan.setUser(user);
                
                // Safely parse and set the planned date
                try {
                    int dayNumber = (int) dayPlan.get("day");
                    workoutPlan.setPlannedDate(currentDate.plusDays(dayNumber - 1));
                } catch (Exception e) {
                    String errorMsg = "Error parsing 'day' field in day plan: " + dayPlan;
                    logger.error(errorMsg + " Raw response: {}", chatgptResponse, e);
                    throw new RuntimeException(errorMsg + " Raw response: " + chatgptResponse, e);
                }
                
                workoutPlan.setSplitName((String) dayPlan.get("split"));
                workoutPlan.setStatus(false); // Default: not done
                
                // Process exercises for this day
                List<Map<String, Object>> exercises = (List<Map<String, Object>>) dayPlan.get("exercises");
                if (exercises == null || exercises.isEmpty()) {
                    String warnMsg = "No exercises found for day plan: " + dayPlan;
                    logger.warn(warnMsg);
                }
                
                List<WorkoutPlanExercise> workoutPlanExercisesList = new ArrayList<>();
                
                for (Map<String, Object> exerciseMap : exercises) {
                    logger.debug("Processing exercise map: {}", exerciseMap);
                    
                    // Normalize the exercise name
                    String rawExerciseName = (String) exerciseMap.get("exerciseName");
                    String normalizedExerciseName = StringUtil.normaliseExerciseName(rawExerciseName);
                    logger.debug("Normalized exercise name: '{}' -> '{}'", rawExerciseName, normalizedExerciseName);
                    
                    // Find existing exercise or create a new one
                    Exercise exercise = exerciseRepository.findByExerciseName(normalizedExerciseName)
                        .orElseGet(() -> {
                            logger.debug("Exercise '{}' not found in repository; creating new exercise.", normalizedExerciseName);
                            Exercise newExercise = new Exercise();
                            newExercise.setExerciseName(normalizedExerciseName);
                            newExercise.setExerciseCategory(null); // Set category to null
                            newExercise.setPrimaryMuscle(null); // Set primary muscle to null
                            newExercise.setSecondaryMuscle(null); // Set secondary muscle to null
                            newExercise.setExerciseGifUrl(null); // Set GIF URL to null
                            Exercise savedExercise = exerciseRepository.save(newExercise);
                            logger.debug("Saved new exercise: {}", savedExercise);
                            return savedExercise;
                        });
                    
                    // Create a new WorkoutPlanExercise and set details
                    WorkoutPlanExercise workoutPlanExercise = new WorkoutPlanExercise();
                    workoutPlanExercise.setExercise(exercise);
                    try {
                        workoutPlanExercise.setSets((Integer) exerciseMap.get("sets"));
                        workoutPlanExercise.setReps((Integer) exerciseMap.get("reps"));
                    } catch (Exception e) {
                        String errorMsg = "Error parsing 'sets' or 'reps' for exercise: " + exerciseMap;
                        logger.error(errorMsg + " Raw response: {}", chatgptResponse, e);
                        throw new RuntimeException(errorMsg + " Raw response: " + chatgptResponse, e);
                    }
                    workoutPlanExercise.setWorkoutPlan(workoutPlan); // Associate with the workout plan
                    
                    workoutPlanExercisesList.add(workoutPlanExercise);
                }
                
                workoutPlan.setExercises(workoutPlanExercisesList);
                
                // Save the workout plan
                workoutPlanRepository.save(workoutPlan);
                logger.debug("Saved workout plan for date: {}", workoutPlan.getPlannedDate());
                workoutPlans.add(workoutPlan);
            }
            
            logger.debug("Successfully processed all workout plans. Total plans saved: {}", workoutPlans.size());
            return workoutPlans;
            
        } catch (Exception e) {
            logger.error("Error processing ChatGPT workout plan response. Raw response: {}", chatgptResponse, e);
            throw new RuntimeException("Error processing ChatGPT workout plan response. Raw response: " + chatgptResponse, e);
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
        // Delete all found workout plans
        workoutPlanRepository.deleteAll(incompletePlans);
    }

}

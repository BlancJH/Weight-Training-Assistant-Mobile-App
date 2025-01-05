package com.blancJH.weight_assistant_mobile_app_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPlan;
import com.blancJH.weight_assistant_mobile_app_backend.model.User;
import com.blancJH.weight_assistant_mobile_app_backend.repository.WorkoutPlanRepository;
import org.springframework.stereotype.Service;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutHistory;
import com.blancJH.weight_assistant_mobile_app_backend.repository.WorkoutHistoryRepository;
import com.blancJH.weight_assistant_mobile_app_backend.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Comparator;

@Service
public class WorkoutPlanService {

    private final WorkoutPlanRepository workoutPlanRepository;
    private final WorkoutHistoryRepository workoutHistoryRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public WorkoutPlanService(WorkoutPlanRepository workoutPlanRepository, ObjectMapper objectMapper, WorkoutHistoryRepository workoutHistoryRepository, UserRepository userRepository) {
        this.workoutPlanRepository = workoutPlanRepository;
        this.workoutHistoryRepository = workoutHistoryRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    public List<WorkoutPlan> saveWorkoutPlanFromChatGptResponse(String chatGptResponse, User user) {
        try {
            // Parse the JSON response into a Map
            Map<String, Object> responseMap = objectMapper.readValue(chatGptResponse, Map.class);
            List<Map<String, Object>> workoutDays = (List<Map<String, Object>>) responseMap.get("workout_plan");

            if (workoutDays == null || workoutDays.isEmpty()) {
                throw new RuntimeException("Invalid workout plan format in ChatGPT response.");
            }

            // Initialize date for Day 1
            LocalDate currentDate = LocalDate.now();
            List<WorkoutPlan> workoutPlans = new ArrayList<>();

            // Iterate through the workout plan and create WorkoutPlan entities
            for (Map<String, Object> dayPlan : workoutDays) {
                WorkoutPlan workoutPlan = new WorkoutPlan();
                workoutPlan.setUser(user);

                // Set date based on the day
                int dayIndex = (int) dayPlan.get("day");
                workoutPlan.setPlannedDate(currentDate.plusDays(dayIndex - 1));

                // Set other attributes
                workoutPlan.setSplit((String) dayPlan.get("split"));
                workoutPlan.setStatus(false); // Default to not done
                workoutPlan.setExercises(objectMapper.writeValueAsString(dayPlan.get("exercises"))); // Store exercises as JSON

                // Save and collect the plan
                workoutPlanRepository.save(workoutPlan);
                workoutPlans.add(workoutPlan);
            }

            return workoutPlans;

        } catch (Exception e) {
            throw new RuntimeException("Error saving workout plan from ChatGPT response.", e);
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
        WorkoutPlan plan = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Workout Plan not found"));

        // Save to workout history
        WorkoutHistory history = new WorkoutHistory();
        history.setUser(plan.getUser());
        history.setCompletedDate(plan.getPlannedDate());
        history.setSplit(plan.getSplit());
        history.setExercises(plan.getExercises());

        workoutHistoryRepository.save(history);

        // Mark the plan as done
        plan.setStatus(true);
        workoutPlanRepository.save(plan);
    }

    public List<WorkoutPlan> resetAndRescheduleWorkoutPlans(Long userId) {
        // Fetch the user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch all workout plans for the user
        List<WorkoutPlan> userPlans = workoutPlanRepository.findByUserId(userId);

        if (userPlans.isEmpty()) {
            throw new RuntimeException("No workout plans found for this user.");
        }

        // Find the last completed workout plan
        LocalDate lastCompletedDate = userPlans.stream()
                .filter(WorkoutPlan::isStatus)
                .map(WorkoutPlan::getPlannedDate)
                .max(LocalDate::compareTo)
                .orElseThrow(() -> new RuntimeException("No completed workout plans found."));

        // Reset status and adjust dates
        LocalDate newStartDate = lastCompletedDate.plusDays(1);
        for (int i = 0; i < userPlans.size(); i++) {
            WorkoutPlan plan = userPlans.get(i);
            plan.setStatus(false); // Reset status to not done
            plan.setPlannedDate(newStartDate.plusDays(i)); // Adjust planned date
        }

        // Save updated plans
        return workoutPlanRepository.saveAll(userPlans);
    }

}

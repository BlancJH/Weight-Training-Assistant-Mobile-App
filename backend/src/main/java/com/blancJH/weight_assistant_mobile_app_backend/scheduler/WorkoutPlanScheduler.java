package com.blancJH.weight_assistant_mobile_app_backend.scheduler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.blancJH.weight_assistant_mobile_app_backend.model.User;
import com.blancJH.weight_assistant_mobile_app_backend.repository.UserRepository;
import com.blancJH.weight_assistant_mobile_app_backend.service.WorkoutPlanService;

@Component
public class WorkoutPlanScheduler {

    @Autowired
    private WorkoutPlanService workoutPlanService;

    @Autowired
    private UserRepository userRepository;

    // Run at 00:01 AM daily
    @Scheduled(cron = "0 1 0 * * ?")
    public void reschedulePlansForAllUsers() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            workoutPlanService.rescheduleUserIncompletedWorkoutPlans(user.getId());
        }
    }
}

package com.blancJH.weight_assistant_mobile_app_backend.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.http.ResponseEntity;

import com.blancJH.weight_assistant_mobile_app_backend.model.User;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPlan;
import com.blancJH.weight_assistant_mobile_app_backend.service.WorkoutPlanService;
import com.blancJH.weight_assistant_mobile_app_backend.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

public class WorkoutPlanControllerTest {

    @Mock
    private WorkoutPlanService workoutPlanService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private WorkoutPlanController workoutPlanController;

    @Test
    public void testGetWorkoutPlansSuccess() {
        // Mock JWT token and user ID
        String mockToken = "mockJwtToken";
        Long mockUserId = 1L;

        when(jwtUtil.extractTokenFromRequest(request)).thenReturn(mockToken);
        when(jwtUtil.extractUserId(mockToken)).thenReturn(mockUserId);

        // Mock workout plans
        User user = new User();
        user.setId(mockUserId);
        user.setUsername("testuser");

        WorkoutPlan plan1 = new WorkoutPlan(null, user, LocalDate.now(), "Chest", "[{\"exerciseName\":\"Bench Press\",\"sets\":3,\"reps\":10}]", false);
        WorkoutPlan plan2 = new WorkoutPlan(null, user, LocalDate.now().plusDays(1), "Back", "[{\"exerciseName\":\"Deadlift\",\"sets\":3,\"reps\":8}]", false);

        List<WorkoutPlan> mockPlans = Arrays.asList(plan1, plan2);
        when(workoutPlanService.getWorkoutPlansByUserId(mockUserId)).thenReturn(mockPlans);

        // Call the controller method
        ResponseEntity<?> response = workoutPlanController.getWorkoutPlans(request);

        // Verify and assert
        verify(workoutPlanService, times(1)).getWorkoutPlansByUserId(mockUserId);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockPlans, response.getBody());
    }

    @Test
    public void testGetWorkoutPlansUnauthorized() {
        // Mock exception when extracting token
        when(jwtUtil.extractTokenFromRequest(request)).thenThrow(new IllegalArgumentException("JWT token is missing or invalid"));

        // Call the controller method
        ResponseEntity<?> response = workoutPlanController.getWorkoutPlans(request);

        // Verify and assert
        verify(workoutPlanService, times(0)).getWorkoutPlansByUserId(any());
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Error fetching workout plans: JWT token is missing or invalid", response.getBody());
    }

    @Test
    public void testGetWorkoutPlansNoPlans() {
        // Mock JWT token and user ID
        String mockToken = "mockJwtToken";
        Long mockUserId = 1L;

        when(jwtUtil.extractTokenFromRequest(request)).thenReturn(mockToken);
        when(jwtUtil.extractUserId(mockToken)).thenReturn(mockUserId);

        // Mock empty workout plans
        when(workoutPlanService.getWorkoutPlansByUserId(mockUserId)).thenReturn(List.of());

        // Call the controller method
        ResponseEntity<?> response = workoutPlanController.getWorkoutPlans(request);

        // Verify and assert
        verify(workoutPlanService, times(1)).getWorkoutPlansByUserId(mockUserId);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(List.of(), response.getBody());
    }

    @Test
    public void testGetWorkoutPlansInternalError() {
        // Mock JWT token and user ID
        String mockToken = "mockJwtToken";
        Long mockUserId = 1L;

        when(jwtUtil.extractTokenFromRequest(request)).thenReturn(mockToken);
        when(jwtUtil.extractUserId(mockToken)).thenReturn(mockUserId);

        // Mock exception in service
        when(workoutPlanService.getWorkoutPlansByUserId(mockUserId)).thenThrow(new RuntimeException("Database error"));

        // Call the controller method
        ResponseEntity<?> response = workoutPlanController.getWorkoutPlans(request);

        // Verify and assert
        verify(workoutPlanService, times(1)).getWorkoutPlansByUserId(mockUserId);
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Error fetching workout plans: Database error", response.getBody());
    }
}

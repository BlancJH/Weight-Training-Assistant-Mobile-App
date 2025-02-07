package com.blancJH.weight_assistant_mobile_app_backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.blancJH.weight_assistant_mobile_app_backend.controller.WorkoutPlanController;
import com.blancJH.weight_assistant_mobile_app_backend.model.User;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPlan;
import com.blancJH.weight_assistant_mobile_app_backend.service.ChatGptService;
import com.blancJH.weight_assistant_mobile_app_backend.service.UserService;
import com.blancJH.weight_assistant_mobile_app_backend.service.WorkoutPlanService;
import com.blancJH.weight_assistant_mobile_app_backend.util.JwtUtil;

@WebMvcTest(WorkoutPlanController.class)
public class WorkoutPlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Mock dependencies used in your controller
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private UserService userService;
    @MockBean
    private ChatGptService chatGptService;
    @MockBean
    private WorkoutPlanService workoutPlanService;

    @Test
    public void testGenerateAndSaveWorkoutPlan() throws Exception {
        // Prepare test data
        String token = "test-token";
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        
        // Simulate ChatGPT response (adjust JSON structure as expected)
        String chatGptResponse = "{ \"workout_plan\": [ { \"day\": 1, \"split\": \"Full Body\", \"exercises\": [ { \"exerciseName\": \"Push Up\", \"sets\": 3, \"reps\": 10 } ] } ] }";
        List<WorkoutPlan> workoutPlans = new ArrayList<>();
        WorkoutPlan workoutPlan = new WorkoutPlan();
        workoutPlan.setId(1L);
        workoutPlans.add(workoutPlan);

        // Set up mock behavior
        when(jwtUtil.extractTokenFromRequest(any(HttpServletRequest.class))).thenReturn(token);
        when(jwtUtil.extractUserId(token)).thenReturn(userId);
        when(userService.findById(userId)).thenReturn(user);
        when(chatGptService.sendUserDetailsToChatGpt(anyMap())).thenReturn(chatGptResponse);
        when(workoutPlanService.saveChatgptWorkoutPlan(chatGptResponse, user)).thenReturn(workoutPlans);

        // Perform the POST request to /generate endpoint
        mockMvc.perform(post("/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"key\": \"value\" }")  // sample request body
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            // Optionally, verify the returned JSON; adjust based on your response structure
            .andExpect(jsonPath("$[0].id").value(1L));

        // Verify that deleteIncompleteWorkoutPlansForUser was called before generating a new plan
        verify(workoutPlanService, times(1)).deleteIncompleteWorkoutPlans(user);
    }
}

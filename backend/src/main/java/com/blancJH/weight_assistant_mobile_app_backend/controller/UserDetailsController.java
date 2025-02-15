package com.blancJH.weight_assistant_mobile_app_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blancJH.weight_assistant_mobile_app_backend.model.User;
import com.blancJH.weight_assistant_mobile_app_backend.model.UserDetails;
import com.blancJH.weight_assistant_mobile_app_backend.service.UserDetailsService;
import com.blancJH.weight_assistant_mobile_app_backend.service.UserService;
import com.blancJH.weight_assistant_mobile_app_backend.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/userDetails")
public class UserDetailsController {

    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserDetailsController(UserDetailsService userDetailsService, UserService userService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveOrUpdateUserDetails(@RequestBody UserDetails userDetails, HttpServletRequest request) {
        // Extract user ID from JWT
        String token = jwtUtil.extractTokenFromRequest(request);
        Long userId = jwtUtil.extractUserId(token);

        // Find the User object
        User user = userService.findById(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        // Check if UserDetails already exists for the user
        UserDetails existingDetails = userDetailsService.findByUser(user);
        if (existingDetails != null) {
            // Update the existing details
            existingDetails.setDob(userDetails.getDob());
            existingDetails.setGender(userDetails.getGender());
            existingDetails.setHeightValue(userDetails.getHeightValue());
            existingDetails.setHeightUnit(userDetails.getHeightUnit());
            existingDetails.setWeightValue(userDetails.getWeightValue());
            existingDetails.setWeightUnit(userDetails.getWeightUnit());
            existingDetails.setInjuriesOrConstraints(userDetails.getInjuriesOrConstraints());
            existingDetails.setWorkoutPurpose(userDetails.getWorkoutPurpose());
            existingDetails.setAdditionalNotes(userDetails.getAdditionalNotes());
            existingDetails.setWorkoutFrequency(userDetails.getWorkoutFrequency());
            existingDetails.setWorkoutDuration(userDetails.getWorkoutDuration());
            existingDetails.setNumberOfSplit(userDetails.getNumberOfSplit());

            userDetailsService.saveUserDetails(existingDetails);
        } else {
            // Set the User in UserDetails
            userDetails.setUser(user);

            // Save new UserDetails
            userDetailsService.saveUserDetails(userDetails);
        }

        return ResponseEntity.ok("User details saved or updated successfully!");
    }


    @GetMapping("/get")
    public ResponseEntity<UserDetails> getUserDetails(HttpServletRequest request) {
        // Extract user ID from JWT
        String token = jwtUtil.extractTokenFromRequest(request);
        Long userId = jwtUtil.extractUserId(token);

        // Find the User object
        User user = userService.findById(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body(null);
        }

        // Fetch UserDetails
        UserDetails userDetails = userDetailsService.findByUser(user);
        if (userDetails == null) {
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok(userDetails);
    }

}


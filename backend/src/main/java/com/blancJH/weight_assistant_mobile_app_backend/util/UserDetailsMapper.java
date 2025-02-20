package com.blancJH.weight_assistant_mobile_app_backend.util;

import java.time.LocalDate;
import java.time.Period;

import org.springframework.stereotype.Component;

import com.blancJH.weight_assistant_mobile_app_backend.dto.UserDetailsDTO;
import com.blancJH.weight_assistant_mobile_app_backend.model.UserDetails;

@Component
public class UserDetailsMapper {

    public UserDetailsDTO toDTO(UserDetails userDetails) {
        UserDetailsDTO dto = new UserDetailsDTO();
        dto.setAge(calculateAge(userDetails.getDob()));

        // Combine heightValue + heightUnit into one string
        if (userDetails.getHeightValue() != null && userDetails.getHeightUnit() != null) {
            String combinedHeight = userDetails.getHeightValue() + " " + userDetails.getHeightUnit().name();
            dto.setHeight(combinedHeight);
        } else {
            dto.setHeight(null); // Explicitly set to null if either value is missing
        }

        // Combine weightValue + weightUnit into one string
        if (userDetails.getWeightValue() != null && userDetails.getWeightUnit() != null) {
            String combinedWeight = userDetails.getWeightValue() + " " + userDetails.getWeightUnit().name();
            dto.setWeight(combinedWeight);
        } else {
            dto.setWeight(null); // Explicitly set to null if either value is missing
        }

        // Convert Gender enum to String
        if (userDetails.getGender() != null) {
            dto.setGender(userDetails.getGender().name()); // Convert enum to its String representation
        } else {
            dto.setGender(null); // Explicitly set to null if Gender is missing
        }
        
        // Convert workoutPurpose enum to String
        if (userDetails.getWorkoutPurpose() != null) {
            dto.setWorkoutPurpose(userDetails.getWorkoutPurpose().name());
        } else {
            dto.setWorkoutPurpose(null);
        }
        
        // Conver workoutFrequency enum to Integer
        if (userDetails.getWorkoutFrequency() != null) {
            dto.setWorkoutFrequency(userDetails.getWorkoutFrequency().ordinal());
        } else {
            dto.setWorkoutFrequency(null);
        }

        dto.setWorkoutDuration(userDetails.getWorkoutDuration());
        dto.setNumberOfSplit(userDetails.getNumberOfSplit());
        dto.setInjuriesOrConstraints(userDetails.getInjuriesOrConstraints());
        dto.setAdditionalNotes(userDetails.getAdditionalNotes());
        return dto;
    }

    private static Integer calculateAge(LocalDate dob) {
        if (dob == null) {
            return null; // or 0, or throw exception — your choice
        }
        // Use Period.between to get the difference in years
        return Period.between(dob, LocalDate.now()).getYears();
    }
}

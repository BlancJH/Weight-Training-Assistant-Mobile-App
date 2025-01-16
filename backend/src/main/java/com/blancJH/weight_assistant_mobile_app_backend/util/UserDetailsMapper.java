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
        }

        // Combine weightValue + weightUnit into one string
        if (userDetails.getWeightValue() != null && userDetails.getWeightUnit() != null) {
            String combinedWeight = userDetails.getWeightValue() + " " + userDetails.getWeightUnit().name();
            dto.setWeight(combinedWeight);
        }

        // Convert Gender enum to String
        if (userDetails.getGender() != null) {
            dto.setGender(userDetails.getGender().name()); // Convert enum to its String representation
        }
        
        dto.setPurpose(userDetails.getPurpose());
        dto.setWorkoutFrequency(userDetails.getWorkoutFrequency());
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

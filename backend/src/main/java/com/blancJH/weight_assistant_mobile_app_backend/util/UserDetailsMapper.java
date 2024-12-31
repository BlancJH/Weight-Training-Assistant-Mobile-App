package com.blancJH.weight_assistant_mobile_app_backend.util;

import com.blancJH.weight_assistant_mobile_app_backend.dto.UserDetailsDTO;
import com.blancJH.weight_assistant_mobile_app_backend.model.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsMapper {

    public UserDetailsDTO toDTO(UserDetails userDetails) {
        UserDetailsDTO dto = new UserDetailsDTO();
        dto.setAge(userDetails.getAge());
        dto.setHeight(userDetails.getHeight());
        dto.setGender(userDetails.getGender());
        dto.setPurpose(userDetails.getPurpose());
        dto.setWorkoutFrequency(userDetails.getWorkoutFrequency());
        dto.setWorkoutDuration(userDetails.getWorkoutDuration());
        return dto;
    }
}

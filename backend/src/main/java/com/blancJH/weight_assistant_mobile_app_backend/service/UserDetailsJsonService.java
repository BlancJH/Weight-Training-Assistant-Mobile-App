package com.blancJH.weight_assistant_mobile_app_backend.service;

import com.blancJH.weight_assistant_mobile_app_backend.dto.UserDetailsDTO;
import com.blancJH.weight_assistant_mobile_app_backend.model.UserDetails;
import com.blancJH.weight_assistant_mobile_app_backend.util.UserDetailsMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsJsonService {

    private final UserDetailsMapper userDetailsMapper;
    private final ObjectMapper objectMapper;

    public UserDetailsJsonService(UserDetailsMapper userDetailsMapper, ObjectMapper objectMapper) {
        this.userDetailsMapper = userDetailsMapper;
        this.objectMapper = objectMapper;
    }

    public String convertUserDetailsToJson(UserDetails userDetails) {
        UserDetailsDTO dto = userDetailsMapper.toDTO(userDetails);
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting UserDetails to JSON", e);
        }
    }
}

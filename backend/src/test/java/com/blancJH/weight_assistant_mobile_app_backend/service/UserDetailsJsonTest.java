package com.blancJH.weight_assistant_mobile_app_backend.service;

import com.blancJH.weight_assistant_mobile_app_backend.model.UserDetails;
import com.blancJH.weight_assistant_mobile_app_backend.util.UserDetailsMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsJsonTest {

    private UserDetailsJsonService userDetailsJsonService;

    @BeforeEach
    void setUp() {
        UserDetailsMapper userDetailsMapper = new UserDetailsMapper();
        ObjectMapper objectMapper = new ObjectMapper();
        userDetailsJsonService = new UserDetailsJsonService(userDetailsMapper, objectMapper);
    }

    @Test
    void testConvertUserDetailsToJson() {
        UserDetails userDetails = new UserDetails();
        userDetails.setAge(25);
        userDetails.setHeight(175.5);
        userDetails.setGender("Male");
        userDetails.setPurpose("Bulk up");

        String json = userDetailsJsonService.convertUserDetailsToJson(userDetails);

        assertNotNull(json);
        assertTrue(json.contains("\"age\":25"));
        assertTrue(json.contains("\"gender\":\"Male\""));
        assertTrue(json.contains("\"height\":175.5"));
        assertTrue(json.contains("\"purpose\":\"Bulk up\""));
    }
}

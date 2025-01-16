package com.blancJH.weight_assistant_mobile_app_backend.service;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.blancJH.weight_assistant_mobile_app_backend.model.Gender;
import com.blancJH.weight_assistant_mobile_app_backend.model.HeightUnit;
import com.blancJH.weight_assistant_mobile_app_backend.model.UserDetails;
import com.blancJH.weight_assistant_mobile_app_backend.model.WeightUnit;
import com.blancJH.weight_assistant_mobile_app_backend.util.UserDetailsMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        // Create and populate UserDetails object
        UserDetails userDetails = new UserDetails();
        userDetails.setDob(LocalDate.of(1998, 5, 10));
        userDetails.setHeightValue(175.5);
        userDetails.setHeightUnit(HeightUnit.cm);
        userDetails.setWeightValue(70.0);
        userDetails.setWeightUnit(WeightUnit.kg);
        userDetails.setGender(Gender.Male);
        userDetails.setPurpose("Bulk up");
        userDetails.setWorkoutFrequency("5 times a week");
        userDetails.setWorkoutDuration(90);
        userDetails.setNumberOfSplit(3);
        userDetails.setInjuriesOrConstraints("mild lower-back strain");
        userDetails.setAdditionalNotes("Focus on proper form, avoid heavy loads on deadlift");

        // Convert UserDetails to JSON
        String json = userDetailsJsonService.convertUserDetailsToJson(userDetails);

        // Verify JSON structure
        assertNotNull(json);
        assertTrue(json.contains("\"dob\":\"1998-05-10\""));
        assertTrue(json.contains("\"heightValue\":175.5"));
        assertTrue(json.contains("\"heightUnit\":\"cm\""));
        assertTrue(json.contains("\"weightValue\":70.0"));
        assertTrue(json.contains("\"weightUnit\":\"kg\""));
        assertTrue(json.contains("\"gender\":\"Male\""));
        assertTrue(json.contains("\"purpose\":\"Bulk up\""));
        assertTrue(json.contains("\"workoutFrequency\":\"5 times a week\""));
        assertTrue(json.contains("\"workoutDuration\":90"));
        assertTrue(json.contains("\"numberOfSplit\":3"));
        assertTrue(json.contains("\"injuriesOrConstraints\":\"mild lower-back strain\""));
        assertTrue(json.contains("\"additionalNotes\":\"Focus on proper form, avoid heavy loads on deadlift\""));
    }
}

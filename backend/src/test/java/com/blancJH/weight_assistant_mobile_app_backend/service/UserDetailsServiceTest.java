package com.blancJH.weight_assistant_mobile_app_backend.service;

import com.blancJH.weight_assistant_mobile_app_backend.model.*;
import com.blancJH.weight_assistant_mobile_app_backend.repository.UserDetailsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailsServiceTest {

    private UserDetailsRepository userDetailsRepository;
    private UserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        userDetailsRepository = Mockito.mock(UserDetailsRepository.class);
        userDetailsService = new UserDetailsService(userDetailsRepository);
    }

    @Test
    void testSaveUserDetails() {
        // Create User object
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        // Create UserDetails object
        UserDetails userDetails = new UserDetails();
        userDetails.setId(1L);
        userDetails.setUser(user);
        userDetails.setDob(LocalDate.of(1998, 1, 1));
        userDetails.setHeightValue(180.0);
        userDetails.setHeightUnit(HeightUnit.cm);
        userDetails.setWeightValue(75.0);
        userDetails.setWeightUnit(WeightUnit.kg);
        userDetails.setGender(Gender.Male);
        userDetails.setPurpose("Build Muscle");
        userDetails.setWorkoutFrequency("3 times a week");
        userDetails.setWorkoutDuration(60);
        userDetails.setNumberOfSplit(3);
        userDetails.setInjuriesOrConstraints("None");
        userDetails.setAdditionalNotes("Focus on compound lifts");

        // Mock repository save behavior
        when(userDetailsRepository.save(userDetails)).thenReturn(userDetails);

        // Invoke saveUserDetails method
        UserDetails savedUserDetails = userDetailsService.saveUserDetails(userDetails);

        // Verify results
        assertNotNull(savedUserDetails);
        assertEquals(user, savedUserDetails.getUser());
        assertEquals(180.0, savedUserDetails.getHeightValue());
        assertEquals(HeightUnit.cm, savedUserDetails.getHeightUnit());
        assertEquals(Gender.Male, savedUserDetails.getGender());
        verify(userDetailsRepository, times(1)).save(userDetails);
    }

    @Test
    void testFindByUser() {
        // Create User object
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        // Create UserDetails object
        UserDetails userDetails = new UserDetails();
        userDetails.setId(1L);
        userDetails.setUser(user);
        userDetails.setDob(LocalDate.of(1998, 1, 1));
        userDetails.setHeightValue(180.0);
        userDetails.setHeightUnit(HeightUnit.cm);
        userDetails.setWeightValue(75.0);
        userDetails.setWeightUnit(WeightUnit.kg);
        userDetails.setGender(Gender.Male);
        userDetails.setPurpose("Build Muscle");
        userDetails.setWorkoutFrequency("3 times a week");
        userDetails.setWorkoutDuration(60);
        userDetails.setNumberOfSplit(3);
        userDetails.setInjuriesOrConstraints("None");
        userDetails.setAdditionalNotes("Focus on compound lifts");

        // Mock repository find behavior
        when(userDetailsRepository.findByUser(user)).thenReturn(userDetails);

        // Invoke findByUser method
        UserDetails foundUserDetails = userDetailsService.findByUser(user);

        // Verify results
        assertNotNull(foundUserDetails);
        assertEquals(user, foundUserDetails.getUser());
        assertEquals(180.0, foundUserDetails.getHeightValue());
        assertEquals(HeightUnit.cm, foundUserDetails.getHeightUnit());
        assertEquals(Gender.Male, foundUserDetails.getGender());
        assertEquals("Build Muscle", foundUserDetails.getPurpose());
        verify(userDetailsRepository, times(1)).findByUser(user);
    }
}

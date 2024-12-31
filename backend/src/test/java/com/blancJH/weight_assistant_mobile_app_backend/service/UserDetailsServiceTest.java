package com.blancJH.weight_assistant_mobile_app_backend.service;

import com.blancJH.weight_assistant_mobile_app_backend.model.User;
import com.blancJH.weight_assistant_mobile_app_backend.model.UserDetails;
import com.blancJH.weight_assistant_mobile_app_backend.repository.UserDetailsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        UserDetails userDetails = new UserDetails();
        userDetails.setId(1L);
        userDetails.setUser(user);
        userDetails.setAge(25);
        userDetails.setHeight(180.0);
        userDetails.setGender("Male");

        when(userDetailsRepository.save(userDetails)).thenReturn(userDetails);

        UserDetails savedUserDetails = userDetailsService.saveUserDetails(userDetails);

        assertNotNull(savedUserDetails);
        assertEquals(user, savedUserDetails.getUser());
        verify(userDetailsRepository, times(1)).save(userDetails);
    }

    @Test
    void testFindByUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        UserDetails userDetails = new UserDetails();
        userDetails.setId(1L);
        userDetails.setUser(user);
        userDetails.setAge(25);
        userDetails.setHeight(180.0);
        userDetails.setGender("Male");

        when(userDetailsRepository.findByUser(user)).thenReturn(userDetails);

        UserDetails foundUserDetails = userDetailsService.findByUser(user);

        assertNotNull(foundUserDetails);
        assertEquals(user, foundUserDetails.getUser());
        assertEquals(25, foundUserDetails.getAge());
        verify(userDetailsRepository, times(1)).findByUser(user);
    }
}

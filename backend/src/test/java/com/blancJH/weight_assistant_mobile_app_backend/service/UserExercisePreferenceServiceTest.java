package com.blancJH.weight_assistant_mobile_app_backend.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.blancJH.weight_assistant_mobile_app_backend.model.DislikeReason;
import com.blancJH.weight_assistant_mobile_app_backend.model.Exercise;
import com.blancJH.weight_assistant_mobile_app_backend.model.User;
import com.blancJH.weight_assistant_mobile_app_backend.model.UserExercisePreference;
import com.blancJH.weight_assistant_mobile_app_backend.repository.ExerciseRepository;
import com.blancJH.weight_assistant_mobile_app_backend.repository.UserExercisePreferenceRepository;
import com.blancJH.weight_assistant_mobile_app_backend.repository.UserRepository;
import com.blancJH.weight_assistant_mobile_app_backend.util.JwtUtil;

@ExtendWith(MockitoExtension.class)
public class UserExercisePreferenceServiceTest {

    @InjectMocks
    private UserExercisePreferenceService preferenceService;

    @Mock
    private UserExercisePreferenceRepository preferenceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private JwtUtil jwtUtil;

    private User mockUser;
    private Exercise mockExercise;
    private UserExercisePreference mockPreference;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("test_user");

        mockExercise = new Exercise();
        mockExercise.setId(10L);
        mockExercise.setExerciseName("Squats");

        mockPreference = new UserExercisePreference();
        mockPreference.setUser(mockUser);
        mockPreference.setExercise(mockExercise);
        mockPreference.setFavorite(true);
        mockPreference.setDislike(false);
    }

    @Test
    void testUpdatePreference_NewEntry() {
        // Mock JWT extraction
        when(jwtUtil.extractUserId("mock_token")).thenReturn(1L);

        // Mock database responses
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(exerciseRepository.findById(10L)).thenReturn(Optional.of(mockExercise));
        when(preferenceRepository.findByUserIdAndExerciseId(1L, 10L)).thenReturn(Optional.empty());
        when(preferenceRepository.save(any(UserExercisePreference.class))).thenReturn(mockPreference);

        // Call service method
        UserExercisePreference result = preferenceService.updateExercisePreference("mock_token", 10L, true, false, null);

        // Verify behavior
        assertNotNull(result);
        assertTrue(result.isFavorite());
        assertFalse(result.isDislike());
        verify(preferenceRepository, times(1)).save(any(UserExercisePreference.class));
    }

    @Test
    void testUpdatePreference_ExistingEntry() {
        // Mock JWT extraction
        when(jwtUtil.extractUserId("mock_token")).thenReturn(1L);

        // Mock database responses
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(exerciseRepository.findById(10L)).thenReturn(Optional.of(mockExercise));
        when(preferenceRepository.findByUserIdAndExerciseId(1L, 10L)).thenReturn(Optional.of(mockPreference));
        when(preferenceRepository.save(any(UserExercisePreference.class))).thenReturn(mockPreference);

        // Call service method
        UserExercisePreference result = preferenceService.updateExercisePreference("mock_token", 10L, false, true, DislikeReason.DIFFICULT);

        // Verify updated fields
        assertFalse(result.isFavorite());
        assertTrue(result.isDislike());
        assertEquals(DislikeReason.DIFFICULT, result.getDislikeReason());
        verify(preferenceRepository, times(1)).save(any(UserExercisePreference.class));
    }

    @Test
    void testUpdatePreference_InvalidJWT() {
        when(jwtUtil.extractUserId("invalid_token")).thenReturn(null);

        // Expect exception
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            preferenceService.updateExercisePreference("invalid_token", 10L, true, false, null);
        });

        assertEquals("Invalid JWT token: unable to extract user ID.", thrown.getMessage());
    }

    @Test
    void testUpdatePreference_InvalidExercise() {
        when(jwtUtil.extractUserId("mock_token")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(exerciseRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            preferenceService.updateExercisePreference("mock_token", 99L, true, false, null);
        });

        assertEquals("Exercise not found.", thrown.getMessage());
    }

    @Test
    void testUpdatePreference_BothFavoriteAndDislike() {
        when(jwtUtil.extractUserId("mock_token")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(exerciseRepository.findById(10L)).thenReturn(Optional.of(mockExercise));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            preferenceService.updateExercisePreference("mock_token", 10L, true, true, DislikeReason.ETC);
        });

        assertEquals("An exercise cannot be marked as both favorite and disliked.", thrown.getMessage());
    }
}

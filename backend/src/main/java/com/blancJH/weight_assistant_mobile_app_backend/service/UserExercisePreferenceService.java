package com.blancJH.weight_assistant_mobile_app_backend.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blancJH.weight_assistant_mobile_app_backend.model.DislikeReason;
import com.blancJH.weight_assistant_mobile_app_backend.model.Exercise;
import com.blancJH.weight_assistant_mobile_app_backend.model.User;
import com.blancJH.weight_assistant_mobile_app_backend.model.UserExercisePreference;
import com.blancJH.weight_assistant_mobile_app_backend.repository.ExerciseRepository;
import com.blancJH.weight_assistant_mobile_app_backend.repository.UserExercisePreferenceRepository;
import com.blancJH.weight_assistant_mobile_app_backend.repository.UserRepository;
import com.blancJH.weight_assistant_mobile_app_backend.util.JwtUtil;

@Service
public class UserExercisePreferenceService {

    @Autowired
    private UserExercisePreferenceRepository preferenceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Updates the user's exercise preference.
     * <p>
     * This method extracts the user ID from the provided JWT token, retrieves the User and Exercise,
     * and then either updates an existing preference record or creates a new one with the specified values.
     *
     * @param jwtToken      The JWT token from which to extract the user ID.
     * @param exerciseId    The ID of the exercise.
     * @param favorite      True if the exercise is marked as a favorite.
     * @param dislike       True if the exercise is marked as disliked.
     * @param dislikeReason The reason for disliking the exercise (enum value).
     * @return The saved UserExercisePreference entity.
     */
    public UserExercisePreference updateExercisePreference(String jwtToken, Long exerciseId, boolean favorite, boolean dislike, DislikeReason dislikeReason) {
        // Extract the user ID from the JWT token.
        Long userId = jwtUtil.extractUserId(jwtToken);
        if (userId == null) {
            throw new RuntimeException("Invalid JWT token: unable to extract user ID.");
        }

        // Prevent both favorite and dislike from being true.
        if (favorite && dislike) {
            throw new IllegalArgumentException("An exercise cannot be marked as both favorite and disliked.");
        }
        
        // Retrieve the User entity.
        User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found."));
                        
        // Retrieve the Exercise entity.
        Exercise exercise = exerciseRepository.findById(exerciseId)
                        .orElseThrow(() -> new RuntimeException("Exercise not found."));
                        
        // Check if a preference already exists for this user and exercise.
        Optional<UserExercisePreference> existingPreference = preferenceRepository.findByUserIdAndExerciseId(userId, exerciseId);
        UserExercisePreference preference;
        if (existingPreference.isPresent()) {
            // Update the existing record.
            preference = existingPreference.get();
            preference.setFavorite(favorite);
            preference.setDislike(dislike);
            preference.setDislikeReason(dislikeReason);
            // Optionally, update a separate updatedAt field instead of overriding createdAt.
            preference.setCreatedAt(LocalDateTime.now());
        } else {
            // Create a new preference record.
            preference = new UserExercisePreference();
            preference.setUser(user);
            preference.setExercise(exercise);
            preference.setFavorite(favorite);
            preference.setDislike(dislike);
            preference.setDislikeReason(dislikeReason);
            preference.setCreatedAt(LocalDateTime.now());
        }
        return preferenceRepository.save(preference);
    }
}

package com.blancJH.weight_assistant_mobile_app_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.blancJH.weight_assistant_mobile_app_backend.model.Exercise;
import com.blancJH.weight_assistant_mobile_app_backend.model.UserExercisePreference;

public interface UserExercisePreferenceRepository extends JpaRepository<UserExercisePreference, Long> {

    Optional<UserExercisePreference> findByUserIdAndExerciseId(Long userId, Long exerciseId);
    List<UserExercisePreference> findAllByUserId(Long userId);

    /**
     * Counts only the latest preference for each user for the given exercise where the user marked it as favorite.
     */
    @Query("SELECT COUNT(u) FROM UserExercisePreference u " +
           "WHERE u.exercise = :exercise " +
           "AND u.favorite = true " +
           "AND u.createdAt = (SELECT MAX(u2.createdAt) FROM UserExercisePreference u2 " +
                              "WHERE u2.user = u.user AND u2.exercise = :exercise)")
    int countLatestFavoritesByExercise(@Param("exercise") Exercise exercise);

    /**
     * Counts only the latest preference for each user for the given exercise where the user marked it as disliked.
     */
    @Query("SELECT COUNT(u) FROM UserExercisePreference u " +
           "WHERE u.exercise = :exercise " +
           "AND u.dislike = true " +
           "AND u.createdAt = (SELECT MAX(u2.createdAt) FROM UserExercisePreference u2 " +
                              "WHERE u2.user = u.user AND u2.exercise = :exercise)")
    int countLatestDislikesByExercise(@Param("exercise") Exercise exercise);

    /**
     * Get the latest user preference.
     * @param userId
     * @param exerciseId
     * @return
     */
    @Query("SELECT u FROM UserExercisePreference u " +
       "WHERE u.user.id = :userId " +
       "AND u.exercise.id = :exerciseId " +
       "ORDER BY u.createdAt DESC")
    Optional<UserExercisePreference> findLatestPreferenceByUserAndExercise(
            @Param("userId") Long userId,
            @Param("exerciseId") Long exerciseId);
    // Define custom query methods here if needed.

}

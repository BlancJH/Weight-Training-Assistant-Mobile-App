package com.blancJH.weight_assistant_mobile_app_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "exercises")
@Getter @Setter
public class Exercise { // try public Enum

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // System-generated ID

    @Column(nullable = false)
    private String exerciseName; // Name of the exercise

    @Enumerated(EnumType.STRING)
    @Column(name = "exercise_category", nullable = true)
    private ExerciseCategory exerciseCategory;

    @Column(nullable = true)
    private String primaryMuscle;// Targeted muscles

    @Column(nullable = true)
    private String secondaryMuscle;// Second Targeted muscles

    @Column(nullable = true)
    private String exerciseGifUrl; // URL for the exercise's GIF

    @Enumerated(EnumType.STRING)
    @Column(name = "workout_split_category", nullable = true)
    private WorkoutSplitCategory workoutSplitCategory;

    @Column(nullable = false)
    private boolean advantage;

    // These fields are not stored in the database but can be set programmatically.
    @Transient
    private int latestFavoriteCount;

    @Transient
    private int latestDislikeCount;
    
    // Optionally, you could add helper methods:
    public int getLatestFavoriteCount() {
        return latestFavoriteCount;
    }
    
    public int getLatestDislikeCount() {
        return latestDislikeCount;
    }
}

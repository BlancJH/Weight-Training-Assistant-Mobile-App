package com.blancJH.weight_assistant_mobile_app_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "exercises")
@Getter @Setter
public class Exercise { // try public Enum

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exerciseId; // System-generated ID

    @Column(nullable = false)
    private String exerciseName; // Name of the exercise

    @Enumerated(EnumType.STRING)
    @Column(name = "exercise_category", nullable = true)
    private ExerciseCategory exerciseCategory;

    @Column(nullable = false)
    private String primaryMuscle;// Targeted muscles

    @Column(nullable = true)
    private String secondaryMuscle;// Second Targeted muscles

    @Column(nullable = true)
    private String exerciseGifUrl; // URL for the exercise's GIF
}

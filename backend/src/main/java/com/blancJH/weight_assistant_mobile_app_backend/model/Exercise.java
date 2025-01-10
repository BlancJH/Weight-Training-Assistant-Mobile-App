package com.blancJH.weight_assistant_mobile_app_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "exercises")
@Getter @Setter
public class Exercise { // try public Enum

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exerciseId; // System-generated ID

    @Column(nullable = false)
    private String exerciseName; // Name of the exercise

    @Column(nullable = false)
    private String exerciseCategory; // Category (e.g., "Machine", "Cardio", "Bodyweight", "Barbell", "Dumbbell", "Assisted Bodyweight") //Need to edit as Enum

    @ElementCollection // Allows storing a list of strings
    @CollectionTable(name = "exercise_muscles", joinColumns = @JoinColumn(name = "exercise_id"))
    @Column(name = "muscle")
    private List<String> muscles; // Targeted muscles (e.g., ["Chest", "Triceps"])

    @Column(nullable = false)
    private String exerciseGifUrl; // URL for the exercise's GIF
}

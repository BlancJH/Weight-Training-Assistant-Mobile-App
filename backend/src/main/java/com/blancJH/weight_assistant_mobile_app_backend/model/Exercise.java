package com.blancJH.weight_assistant_mobile_app_backend.model;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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

    @ElementCollection // Allows storing a list of strings
    @CollectionTable(name = "exercise_muscles", joinColumns = @JoinColumn(name = "exercise_id"))
    @Column(name = "muscle")
    private List<String> muscles; // Targeted muscles (e.g., ["Chest", "Triceps"])

    @Column(nullable = false)
    private String exerciseGifUrl; // URL for the exercise's GIF
}

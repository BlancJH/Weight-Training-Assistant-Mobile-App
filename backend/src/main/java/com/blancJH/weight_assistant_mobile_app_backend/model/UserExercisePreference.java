package com.blancJH.weight_assistant_mobile_app_backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_preference_exercises")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserExercisePreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Reference to the user who marked the exercise as favorite.
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Reference to the exercise.
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    // Indicates whether the user has marked this exercise as a favorite.
    @Column(name = "favorite", nullable = false)
    private boolean favorite;

    // Indicates whether the user has marked this exercise as a dislike.
    @Column(name = "dislike", nullable = false)
    private boolean dislike;

    // Record when this preference was created.
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

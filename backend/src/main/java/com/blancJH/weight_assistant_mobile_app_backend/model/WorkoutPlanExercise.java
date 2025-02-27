package com.blancJH.weight_assistant_mobile_app_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
    
@Entity
@Table(name = "workout_plan_exercises")
@Getter @Setter
public class WorkoutPlanExercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private Integer sets;

    @Column(nullable = true)
    private Integer reps;

    @Column(nullable = true)
    private String duration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", referencedColumnName = "id", nullable = false)
    private Exercise exercise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_plan_id", referencedColumnName = "id", nullable = false)
    private WorkoutPlan workoutPlan;
}

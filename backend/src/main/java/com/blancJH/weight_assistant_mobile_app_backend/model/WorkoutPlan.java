package com.blancJH.weight_assistant_mobile_app_backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "workout_plans")
public class WorkoutPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "planned_date", nullable = false)
    private LocalDate plannedDate;

    @Column(name = "split", nullable = false)
    private String split;

    @Column(name = "exercises", columnDefinition = "TEXT", nullable = false)
    private String exercises;

    @Column(name = "status", nullable = false)
    private boolean status; // true = done, false = not done

    public WorkoutPlan() {
        // Default constructor
    }

    public WorkoutPlan(User user, LocalDate plannedDate, String split, String exercises, boolean status) {
        this.user = user;
        this.plannedDate = plannedDate;
        this.split = split;
        this.exercises = exercises;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getPlannedDate() {
        return plannedDate;
    }

    public void setPlannedDate(LocalDate plannedDate) {
        this.plannedDate = plannedDate;
    }

    public String getSplit() {
        return split;
    }

    public void setSplit(String split) {
        this.split = split;
    }

    public String getExercises() {
        return exercises;
    }

    public void setExercises(String exercises) {
        this.exercises = exercises;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}

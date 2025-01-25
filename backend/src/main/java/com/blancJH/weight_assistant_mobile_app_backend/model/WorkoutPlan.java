package com.blancJH.weight_assistant_mobile_app_backend.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "workout_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    public WorkoutPlan(User user, LocalDate plannedDate, String split, String exercises, boolean status) {
        this.user = user;
        this.plannedDate = plannedDate;
        this.split = split;
        this.exercises = exercises;
        this.status = status;
    }
}

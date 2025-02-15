package com.blancJH.weight_assistant_mobile_app_backend.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_details")
@Getter
@Setter
public class UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "dob", nullable = true)
    private LocalDate dob;

    @Column(name = "height_value", nullable = true)
    private Double heightValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "height_unit", nullable = true, length = 2)
    private HeightUnit heightUnit;

    @Column(name = "weight_value", nullable = true)
    private Double weightValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "weight_unit", nullable = true, length = 3)
    private WeightUnit weightUnit;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = true, length = 6)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "workout_purpose", nullable = true)
    private WorkoutPurpose workoutPurpose;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private WorkoutFrequency workoutFrequency;

    @Column(nullable = true)
    private Integer workoutDuration;

    @Column(nullable = true)
    private Integer numberOfSplit;

    @Column(name = "injuries_constraints", nullable = true, length = 20)
    private String injuriesOrConstraints;

    @Column(nullable = true)
    private String additionalNotes;
}

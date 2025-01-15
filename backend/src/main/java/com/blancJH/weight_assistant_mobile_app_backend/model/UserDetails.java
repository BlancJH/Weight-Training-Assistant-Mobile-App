package com.blancJH.weight_assistant_mobile_app_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

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
    @Column(name = "height_unit", nullable = true)
    private HeightUnit heightUnit;

    @Column(name = "weight_value", nullable = true)
    private Double weightValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "weight_unit", nullable = true)
    private WeightUnit weightUnit;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = true)
    private Gender gender;

    @Column(nullable = true)
    private String purpose;

    @Column(nullable = true)
    private String workoutFrequency;

    @Column(nullable = true)
    private Integer workoutDuration;

    @Column(nullable = true)
    private Integer numberOfSplit;

    @Column(nullable = true)
    private String injuriesOrConstraints;

    @Column(nullable = true)
    private String additionalNotes;
}

package com.blancJH.weight_assistant_mobile_app_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_details")
public class UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = true)
    private Integer age;

    @Column(nullable = true)
    private Double height;

    @Column(nullable = true)
    private String gender;

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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getWorkoutFrequency() {
        return workoutFrequency;
    }

    public void setWorkoutFrequency(String workoutFrequency) {
        this.workoutFrequency = workoutFrequency;
    }

    public Integer getWorkoutDuration() {
        return workoutDuration;
    }

    public void setWorkoutDuration(Integer workoutDuration) {
        this.workoutDuration = workoutDuration;
    }

    public Integer getNumberOfSplit() {
        return numberOfSplit;
    }

    public void setNumberOfSplit(Integer numberOfSplit) {
        this.numberOfSplit = numberOfSplit;
    }

    public String getInjuriesOrConstraints() {
        return injuriesOrConstraints;
    }

    public void setInjuriesOrConstraints(String injuriesOrConstraints) {
        this.injuriesOrConstraints = injuriesOrConstraints;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }
}

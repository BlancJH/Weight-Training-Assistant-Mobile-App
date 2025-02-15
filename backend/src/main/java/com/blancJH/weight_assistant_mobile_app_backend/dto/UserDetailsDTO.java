package com.blancJH.weight_assistant_mobile_app_backend.dto;

import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutFrequency;
import com.blancJH.weight_assistant_mobile_app_backend.model.WorkoutPurpose;

public class UserDetailsDTO {
    private Integer age;
    private String height;
    private String weight;
    private String gender;
    private WorkoutPurpose workoutPurpose;
    private WorkoutFrequency workoutFrequency;
    private Integer workoutDuration;
    private Integer numberOfSplit;
    private String injuriesOrConstraints;
    private String additionalNotes;

    // Getters and Setters
    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public WorkoutPurpose getWorkoutPurpose() {
        return workoutPurpose;
    }

    public void setWorkoutPurpose(WorkoutPurpose workoutPurpose) {
        this.workoutPurpose = workoutPurpose;
    }

    public WorkoutFrequency getWorkoutFrequency() {
        return workoutFrequency;
    }

    public void setWorkoutFrequency(WorkoutFrequency workoutFrequency) {
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

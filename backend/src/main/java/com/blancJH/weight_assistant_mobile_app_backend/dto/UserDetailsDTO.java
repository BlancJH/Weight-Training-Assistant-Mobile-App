package com.blancJH.weight_assistant_mobile_app_backend.dto;

public class UserDetailsDTO {
    private Integer age;
    private Double height;
    private String gender;
    private String purpose;
    private String workoutFrequency;
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

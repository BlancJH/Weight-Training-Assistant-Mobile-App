package com.blancJH.weight_assistant_mobile_app_backend.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum WorkoutFrequency {
    ONE(1, "1 Time a Week"),
    TWO(2, "2 Times a Week"),
    THREE(3, "3 Times a Week"),
    FOUR(4, "4 Times a Week"),
    FIVE(5, "5 Times a Week"),
    SIX(6, "6 Times a Week"),
    SEVEN(7, "7 Times a Week");

    private final int value;
    private final String description;

    WorkoutFrequency(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
    
    @JsonCreator
    public static WorkoutFrequency from(String input) {
        try {
            int intValue = Integer.parseInt(input);
            for (WorkoutFrequency wf : values()) {
                if (wf.getValue() == intValue) {
                    return wf;
                }
            }
        } catch (NumberFormatException e) {
            // Fall back to standard valueOf if input isn't numeric
        }
        // Optionally, you can also try: return WorkoutFrequency.valueOf(input.toUpperCase());
        throw new IllegalArgumentException("No enum constant for value " + input);
    }
    
}

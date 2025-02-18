package com.blancJH.weight_assistant_mobile_app_backend.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import lombok.Getter;

@Getter
public enum WorkoutSplitCategory {

    // Top level
    FULL_BODY("Full Body", Collections.emptySet()),

    // Top-level categories
    CARDIO("Cardio", Set.of(FULL_BODY)),
    STRENGTH("Strength", Set.of(FULL_BODY)),
    FUNCTIONAL("Functional", Set.of(FULL_BODY)),

    // Subcategories for Cardio
    HIIT("HIIT", Set.of(CARDIO)),
    STEADY_STATE("Steady-State", Set.of(CARDIO)),
    ENDURANCE("Endurance", Set.of(CARDIO)),
    LOW_IMPACT("Low Impact", Set.of(CARDIO)),
    SPRINT("Sprint/Agility", Set.of(CARDIO)),

    // Subcategories for Strength
    UPPER_BODY("Upper Body", Set.of(STRENGTH)),
    LOWER_BODY("Lower Body", Set.of(STRENGTH)),

    // Further subcategories for Upper Body
    PUSH("Push", Set.of(UPPER_BODY)),
    PULL("Pull", Set.of(UPPER_BODY)),
    ARMS("Arms", Set.of(UPPER_BODY)),
    SHOULDERS("Shoulders", Set.of(UPPER_BODY, ARMS)),

    // Further subcategories for Lower Body
    QUADRICEPS("Quadriceps", Set.of(LOWER_BODY)),
    HAMSTRINGS("Hamstrings", Set.of(LOWER_BODY)),
    GLUTES("Glutes", Set.of(LOWER_BODY)),
    CALVES("Calves", Set.of(LOWER_BODY)),
    ADDUCTORS("Adductors", Set.of(LOWER_BODY)),

    // Further subcategories for Push
    CHEST("Chest", Set.of(PUSH)),
    TRICEPS("Triceps", Set.of(PUSH, ARMS)),

    // Further subcategories for Pull
    BACK("Back", Set.of(PULL)),
    BICEPS("Biceps", Set.of(PULL, ARMS)),

    // Subcategories for Functional training
    MOBILITY("Mobility", Set.of(FUNCTIONAL)),
    STABILITY("Stability", Set.of(FUNCTIONAL)),
    CORE("Core & Balance", Set.of(FUNCTIONAL)),
    PLYOMETRICS("Plyometrics", Set.of(FUNCTIONAL)),
    AGILITY("Agility & Coordination", Set.of(FUNCTIONAL)),
    CIRCUIT("Circuit Training", Set.of(FUNCTIONAL)),

    RECOVERY("Recovery", Set.of(MOBILITY));

    private final String displayName;
    private final Set<WorkoutSplitCategory> parents;
    
    WorkoutSplitCategory(String displayName, Set<WorkoutSplitCategory> parents) {
        this.displayName = displayName;
        this.parents = parents;
    }

    /**
     * Checks if this category is a descendant of the provided category.
     * 
     * @param potentialAncestor the potential parent category
     * @return true if this category is a descendant of potentialAncestor, false otherwise
     */
    public boolean isDescendantOf(WorkoutSplitCategory potentialAncestor) {
        if (this.parents.contains(potentialAncestor)) {
            return true;
        }
        return this.parents.stream().anyMatch(parent -> parent.isDescendantOf(potentialAncestor));
    }
    
    /**
     * Retrieves the enum constant corresponding to the given display name (case-insensitive).
     * 
     * @param name the display name
     * @return the matching WorkoutSplitCategory, or null if not found
     */
    public static WorkoutSplitCategory fromDisplayName(String name) {
        return Arrays.stream(values())
                     .filter(cat -> cat.displayName.equalsIgnoreCase(name))
                     .findFirst()
                     .orElse(null);
    }

}
package com.blancJH.weight_assistant_mobile_app_backend.util;

public class StringUtil {

    public static String normaliseExerciseName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }

        // Convert to lowercase
        String normalised = name.toLowerCase();

        // Replace special characters with spaces
        normalised = normalised.replaceAll("[-_]", " ");

        // Remove other non-alphanumeric characters
        normalised = normalised.replaceAll("[^a-z0-9 ]", "");

        // Collapse multiple spaces into a single space
        normalised = normalised.replaceAll("\\s+", " ");

        // Trim leading and trailing spaces
        normalised = normalised.trim();

        return normalised;
    }
}


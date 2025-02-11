package com.blancJH.weight_assistant_mobile_app_backend.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ChatGptService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${chatgpt.api.url}")
    private String apiUrl;

    @Value("${chatgpt.api.key}")
    private String apiKey;

    public ChatGptService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public String sendUserDetailsToChatGpt(Object userDetails) {
        try {
            // Convert UserDetails object to JSON string
            String userDetailsJson = objectMapper.writeValueAsString(userDetails);

            // System message with specific workout plan instructions
            String prompt = """
                You are to create a personalized workout plan in JSON format based on the user demographic details and training requirements provided. Your output must be strictly in JSON with no additional text, commentary, or markdown formatting.

                ### Instructions:
                1. **Gather Input Details:**  
                - User Demographic: Age, gender, height, weight, current fitness level.
                - Training Purpose: e.g., weight loss, muscle gain, endurance improvement, or specific sports (boxing, Hyrox, Taekwondo, etc.).
                - Frequency and Duration: How many days a week the user wants to train and how much time per session.
                - numberOfSplit: The number of splits for the workout plan (e.g., targeting specific body parts like Pull, Push, Legs, Chest, Back, Arms, Shoulders).

                2. **Steps to Follow:**
                - Understand the user's goals and demographics.
                - Allocate time and frequency based on user input.
                - Select appropriate exercises considering the user’s fitness level and training purpose.
                - Create a workout plan distributed over the number of days equal to "numberOfSplit".
                - Adjust exercises if there are any injuries or constraints.

                3. **Output Requirements:**
                - The JSON must have a key "workout_plan" which is an array of objects.
                - Each object in the "workout_plan" array should include:
                    - "day": The day number (starting from 1).
                    - "split": The targeted body part or workout split.
                    - "exercises": An array of exercise objects, each containing only:
                    - "exerciseName": Name of the exercise.
                    - "sets": Number of sets.
                    - "reps": Number of repetitions.
                    - Optionally, "duration": Duration of the exercise if it is time-based.
                - Do not include any additional fields such as weight, exerciseCategory, or exercise_muscles.

                4. **Important:**
                - Ensure that the number of workout days is exactly equal to the "numberOfSplit" provided in the input.
                - Do not include any text or explanation outside of the JSON output.

                ### Example Input:
                {
                "User details": {
                    "userAge": 30,
                    "userHeight": "175cm",
                    "userWeight": "70kg",
                    "userGender": "Male",
                    "purposeOfWorkout": "Bulk up",
                    "workoutFrequency": "5 times a week",
                    "workoutDuration": "90 min",
                    "numberOfSplit": 3,
                    "injuriesOrConstraints": "mild lower-back strain"
                },
                }

                ### Expected JSON Output Format:
                {
                "workout_plan": [
                    {
                    "day": 1,
                    "split": "Chest",
                    "exercises": [
                        {
                        "exerciseName": "Bench Press",
                        "sets": 3,
                        "reps": 8
                        },
                        {
                        "exerciseName": "Push-up",
                        "sets": 3,
                        "reps": 12
                        }
                    ]
                    },
                    {
                    "day": 2,
                    "split": "Back",
                    "exercises": [
                        {
                        "exerciseName": "Deadlift",
                        "sets": 3,
                        "reps": 8
                        }
                    ]
                    },
                    {
                    "day": 3,
                    "split": "Legs",
                    "exercises": [
                        {
                        "exerciseName": "Squat",
                        "sets": 3,
                        "reps": 10
                        }
                    ]
                    }
                ]
                }

                Return only the JSON output exactly as specified above.
                """;

            // Prepare ChatGPT request payload
            Map<String, Object> requestPayload = Map.of(
                "model", "gpt-4o-mini",
                "temperature", 1.0,
                "max_tokens", 4096,
                "top_p", 1.0,
                "frequency_penalty", 0.0,
                "presence_penalty", 0.0,
                "messages", new Object[] {
                    Map.of("role", "system", "content", prompt),
                    Map.of("role", "user", "content", userDetailsJson)
                }
            );

            // Retrieve the API response
            String apiResponse = webClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestPayload)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            // Parse the response
            Map<String, Object> responseMap = objectMapper.readValue(apiResponse, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");

            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return (String) message.get("content");
            }

            throw new RuntimeException("No valid response content found in ChatGPT API response.");
        } catch (Exception e) {
            throw new RuntimeException("Error sending data to ChatGPT API", e);
        }
    }
}

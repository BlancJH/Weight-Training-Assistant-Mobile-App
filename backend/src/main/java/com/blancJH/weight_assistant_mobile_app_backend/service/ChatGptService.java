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
            String prompt = "Create a personalized workout plan based on user demographic details and training requirements. Response is only in JSON no additional text.\n\n" +
            "Gather the following information:\n" +
            "- **User Demographic**: Age, gender, height, weight and current fitness level.\n" +
            "- **Training Purpose**: Specific goals e.g., weight loss, muscle gain, endurance improvement, specific sports such as boxing, Hyrox, Taekwondo, etc.\n" +
            "- **Frequency and Duration**: How many days a week the user wants to train and how much time they can dedicate per session.\n" +
            "- **numberOfSplit**: The number of split for the workout plan targeting specific body parts, e.g., Pull, Push, Legs, Chest, Back, Arms, Shoulders.\n\n" +
            "# Steps\n\n" +
            "1. **Understand User Goals**: Identify user demographic and training goals to tailor the workout plan effectively.\n" +
            "2. **Allocate Time and Frequency**: Consider the user's available time and desired frequency to design a balanced workout routine.\n" +
            "3. **Exercise Selection**: Choose exercises appropriate for the user's goals and fitness level, ensuring variety and progression.\n" +
            "4. **Create Workout Plan**: Schedule exercises up to the numberOfSplit.\n" +
            "5. **Review and Adjust**: Adjust exercises and intensities to align with user restrictions or preferences if necessary.\n\n" +
            "# Output Format\n\n" +
            "The output should be a detailed workout plan based on split, structured in JSON format. Each day should list:\n" +
            "- Day as many as the split.\n" +
            "- Specific exercise names with repetitions, sets, exercise category and targeting muscles (if the exercise dumbbell or barbell, indicate the weight.).\n\n" +
            "# Examples\n\n" +
            "**Input:**\n" +
            "{\n" +
            "  \"User details\": {\n" +
            "    \"userAge\": 30,\n" +
            "    \"userHeight\": 175cm,\n" +
            "    \"userWeight\": 70kg,\n" +
            "    \"userGender\": \"Male\", \n" +
            "    \"purposeOfWorkout\": \"Bulk up\",\n" +
            "    \"workoutFrequency\": \"5 times a week\",\n" +
            "    \"workoutDuration\": \"90 min\",\n" +
            "    \"numberOfSplit\": 3\n" +
            "  },\n" +
            "  \"injuriesOrConstraints\": \"mild lower-back strain\"\n" +
            "  },\n" +
            "  \"additionalNotes\": \"Focus on proper form, avoid heavy loads on deadlift\"\n" +
            "}\n\n" +
            "**Output:**\n" +
            "{\n" +
            "  \"workout_plan\": [\n" +
            "    {\n" +
            "      \"day\": 1,\n" +
            "      \"split\": \"Chest\",\n" +
            "      \"exercises\": [\n" +
            "        {\n" +
            "          \"exerciseName\": \"Bench Press\",\n" +
            "          \"exercise_muscles\": [\"Chest\", \"Triceps\"],\n" +
            "          \"exerciseCategory\": \"Barbell\",\n" +
            "          \"sets\": 3,\n" +
            "          \"reps\": 8,\n" +
            "          \"weight\": 60\n" +
            "        },\n" +
            "        {\n" +
            "          \"exerciseName\": \"Push-up\",\n" +
            "          \"exercise_muscles\": [\"Chest\", \"Triceps\"],\n" +
            "          \"exerciseCategory\": \"Bodyweight\",\n" +
            "          \"sets\": 3,\n" +
            "          \"reps\": 12\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"day\": 2,\n" +
            "      \"split\": \"Back\",\n" +
            "      \"exercises\": [\n" +
            "        {\n" +
            "          \"exerciseName\": \"Deadlift\",\n" +
            "          \"exercise_muscles\": [\"Back\", \"Glutes\", \"Hamstrings\"],\n" +
            "          \"exerciseCategory\": \"Barbell\",\n" +
            "          \"sets\": 3,\n" +
            "          \"reps\": 8,\n" +
            "          \"weight\": 80\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"day\": 3,\n" +
            "      \"split\": \"Legs\",\n" +
            "      \"exercises\": [\n" +
            "        {\n" +
            "          \"exerciseName\": \"Squat\",\n" +
            "          \"exercise_muscles\": [\"Quadriceps\", \"Glutes\"],\n" +
            "          \"exerciseCategory\": \"Bodyweight\",\n" +
            "          \"sets\": 3,\n" +
            "          \"reps\": 10\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}\n\n" +
            "# Notes\n\n" +
            "- Consider adding modifications for users with specific physical limitations.\n" +
            "- Ensure a progressive increase in intensity to foster continuous improvement.\n" +
            "- Ensure exercises are effective based on researches.\n" +
            "- Ensure days do not exceed the number of split. e.g., numberOfSplit: 3 == maximum days for plan is 3.";


            // Prepare ChatGPT request payload
            Map<String, Object> requestPayload = Map.of(
                "model", "gpt-4o-mini",
                "temperature", 1.0,
                "max_tokens", 2048,
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

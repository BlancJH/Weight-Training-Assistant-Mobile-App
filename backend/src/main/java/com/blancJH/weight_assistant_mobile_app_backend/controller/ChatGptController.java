package com.blancJH.weight_assistant_mobile_app_backend.controller;

import com.blancJH.weight_assistant_mobile_app_backend.service.ChatGptService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/chatgpt")
public class ChatGptController {

    private final ChatGptService chatGptService;

    public ChatGptController(ChatGptService chatGptService) {
        this.chatGptService = chatGptService;
    }

    @PostMapping
    public ResponseEntity<?> processChatGptRequest(@RequestBody Map<String, Object> userDetails) {
        try {
            // Ensure that userDetails is not null or empty
            if (userDetails == null || userDetails.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                     .body("User details cannot be null or empty.");
            }

            // Process the user details through the ChatGPT service
            String response = chatGptService.sendUserDetailsToChatGpt(userDetails);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Return a 500 error with the exception message for better debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error processing the request: " + e.getMessage());
        }
    }
}

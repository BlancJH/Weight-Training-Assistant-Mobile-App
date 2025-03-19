package com.blancJH.weight_assistant_mobile_app_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Generates getters, setters, equals, hashCode, and toString methods
@AllArgsConstructor // Generates a constructor with all fields
@NoArgsConstructor  // Generates a default constructor
public class UserSphereDTO {
    private Long id;
    private Long sphereId;
    private String sphereName;
    private int level;
    private int quantity;
    private boolean representator;
}

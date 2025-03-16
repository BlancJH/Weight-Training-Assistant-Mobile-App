package com.blancJH.weight_assistant_mobile_app_backend.dto;

import com.blancJH.weight_assistant_mobile_app_backend.model.SphereRank;

import lombok.Data;

@Data
public class SphereDTO {
    private Long id;
    private String sphereName;
    private String sphereUrl;
    private SphereRank sphereRank;
}

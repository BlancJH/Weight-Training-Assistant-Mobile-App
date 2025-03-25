package com.blancJH.weight_assistant_mobile_app_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "consents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private boolean registerConsent;

    @Column(nullable = false)
    private boolean analysisConsent;

    @Column(nullable = false)
    private LocalDateTime consentTimestamp;
}

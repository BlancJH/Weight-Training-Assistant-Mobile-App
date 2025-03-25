package com.blancJH.weight_assistant_mobile_app_backend.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.blancJH.weight_assistant_mobile_app_backend.model.Consent;
import com.blancJH.weight_assistant_mobile_app_backend.repository.ConsentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConsentService {

    private final ConsentRepository consentRepository;

    public Consent saveConsent(String userId, boolean registerConsent, boolean analysisConsent) {
        Consent consent = Consent.builder()
                .userId(userId)
                .registerConsent(registerConsent)
                .analysisConsent(analysisConsent)
                .consentTimestamp(LocalDateTime.now())
                .build();
        return consentRepository.save(consent);
    }
}
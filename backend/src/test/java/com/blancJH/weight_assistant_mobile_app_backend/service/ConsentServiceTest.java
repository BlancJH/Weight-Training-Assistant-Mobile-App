package com.blancJH.weight_assistant_mobile_app_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.blancJH.weight_assistant_mobile_app_backend.model.Consent;
import com.blancJH.weight_assistant_mobile_app_backend.repository.ConsentRepository;

@SpringBootTest
@Transactional
public class ConsentServiceTest {

    @Autowired
    private ConsentService consentService;

    @Autowired
    private ConsentRepository consentRepository;

    @Test
    public void testSaveConsent() {
        // Given
        String userId = "user123";
        boolean registerConsent = true;
        boolean analysisConsent = false;

        // When
        Consent savedConsent = consentService.saveConsent(userId, registerConsent, analysisConsent);

        // Then: Verify that a consent ID has been generated, and all values are saved correctly.
        assertNotNull(savedConsent.getId(), "Consent ID should not be null after saving.");
        assertEquals(userId, savedConsent.getUserId(), "User ID should match the input.");
        assertEquals(registerConsent, savedConsent.isRegisterConsent(), "Register consent flag should match the input.");
        assertEquals(analysisConsent, savedConsent.isAnalysisConsent(), "Analysis consent flag should match the input.");
        assertNotNull(savedConsent.getConsentTimestamp(), "Consent timestamp should not be null.");

        // Also verify that the entity can be retrieved from the repository.
        Consent foundConsent = consentRepository.findById(savedConsent.getId()).orElse(null);
        assertNotNull(foundConsent, "Consent should be found in the repository.");
        assertEquals(userId, foundConsent.getUserId(), "Retrieved user ID should match.");
    }
}
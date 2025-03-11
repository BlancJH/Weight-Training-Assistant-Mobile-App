package com.blancJH.weight_assistant_mobile_app_backend.service;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.blancJH.weight_assistant_mobile_app_backend.config.CardPackConfig;
import com.blancJH.weight_assistant_mobile_app_backend.model.Sphere;
import com.blancJH.weight_assistant_mobile_app_backend.model.SphereCardPackRank;
import com.blancJH.weight_assistant_mobile_app_backend.model.SphereRank;
import com.blancJH.weight_assistant_mobile_app_backend.repository.SphereRepository;

@ExtendWith(MockitoExtension.class)
class SpherePackServiceTest {

    @Mock
    private SphereRepository sphereRepository;

    @Mock
    private CardPackConfig cardPackConfig;

    @InjectMocks
    private SpherePackService spherePackService;

    private Map<String, Map<String, Integer>> mockPackRatios;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock rarity probability weights for each pack type
        Map<String, Integer> bronzePackRatios = new HashMap<>();
        bronzePackRatios.put("normal", 70);
        bronzePackRatios.put("unique", 20);
        bronzePackRatios.put("epic", 7);
        bronzePackRatios.put("rare", 2);
        bronzePackRatios.put("legend", 1);

        Map<String, Integer> silverPackRatios = new HashMap<>();
        silverPackRatios.put("normal", 50);
        silverPackRatios.put("unique", 25);
        silverPackRatios.put("epic", 15);
        silverPackRatios.put("rare", 7);
        silverPackRatios.put("legend", 3);

        mockPackRatios = new HashMap<>();
        mockPackRatios.put("bronze", bronzePackRatios);
        mockPackRatios.put("silver", silverPackRatios);

        // Mock config to return these ratios
        when(cardPackConfig.getRatios()).thenReturn(mockPackRatios);
    }

    @Test
    void testSelectRandomRarity_UsesPropertyValues() {
        Set<SphereRank> validRanks = EnumSet.allOf(SphereRank.class);
        for (int i = 0; i < 100; i++) {
            SphereRank selectedRank = spherePackService.selectRandomRarity(SphereCardPackRank.BRONZE);
            assertTrue(validRanks.contains(selectedRank), "Selected rank should be a valid SphereRank.");
        }
    }

    @Test
    void testSelectRandomRarity_ThrowsExceptionForMissingConfig() {
        when(cardPackConfig.getRatios()).thenReturn(new HashMap<>()); // No config for any pack

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            spherePackService.selectRandomRarity(SphereCardPackRank.BRONZE);
        });

        assertTrue(exception.getMessage().contains("No configuration found for pack type: BRONZE"));
    }

    @Test
    void testGetRandomSphereByRarity_ReturnsMockSphere() {
        Sphere mockSphere = new Sphere();
        mockSphere.setSphereName("Mock Sphere");
        mockSphere.setSphereUrl("http://mockurl.com");
        mockSphere.setSphereRank(SphereRank.EPIC);

        when(sphereRepository.findRandomByRank("EPIC")).thenReturn(mockSphere);

        Sphere result = spherePackService.getRandomSphereByRarity(SphereRank.EPIC);
        assertNotNull(result);
        assertEquals(SphereRank.EPIC, result.getSphereRank());
        assertEquals("Mock Sphere", result.getSphereName());
    }

    @Test
    void testGenerateSpherePack_ReturnsCorrectPackSize() {
        int packSize = 5;

        // Mock repository behavior to return random spheres
        when(sphereRepository.findRandomByRank(anyString())).thenAnswer(invocation -> {
            String rarity = invocation.getArgument(0, String.class);
            Sphere sphere = new Sphere();
            sphere.setSphereName("Mock Sphere of " + rarity);
            sphere.setSphereUrl("http://example.com/" + rarity);
            sphere.setSphereRank(SphereRank.valueOf(rarity));
            return sphere;
        });

        List<Sphere> spherePack = spherePackService.generateSpherePack(SphereCardPackRank.BRONZE, packSize);

        assertNotNull(spherePack);
        assertEquals(packSize, spherePack.size(), "Sphere pack should contain exactly " + packSize + " spheres.");
        for (Sphere sphere : spherePack) {
            assertNotNull(sphere);
            assertNotNull(sphere.getSphereRank());
        }
    }

    @Test
    void testGenerateSpherePack_ThrowsExceptionIfDatabaseIsEmpty() {
        when(sphereRepository.findRandomByRank(anyString())).thenReturn(null);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            spherePackService.generateSpherePack(SphereCardPackRank.SILVER, 5);
        });

        assertTrue(exception.getMessage().contains("No sphere found for rarity"));
    }
}

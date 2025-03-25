package com.blancJH.weight_assistant_mobile_app_backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blancJH.weight_assistant_mobile_app_backend.config.CardPackConfig;
import com.blancJH.weight_assistant_mobile_app_backend.model.Sphere;
import com.blancJH.weight_assistant_mobile_app_backend.model.SphereCardPackRank;
import com.blancJH.weight_assistant_mobile_app_backend.model.SphereRank;
import com.blancJH.weight_assistant_mobile_app_backend.repository.SphereRepository;

@Service
public class SpherePackService {

    private static final Logger logger = LoggerFactory.getLogger(SpherePackService.class);

    private final SphereRepository sphereRepository;
    private final CardPackConfig packProperties;
    private final Random random = new Random();

    @Autowired
    public SpherePackService(SphereRepository sphereRepository, CardPackConfig packProperties) {
        this.sphereRepository = sphereRepository;
        this.packProperties = packProperties;
    }

    /**
     * Selects a sphere rarity using weighted random selection based on the pack type.
     */
    public SphereRank selectRandomRarity(SphereCardPackRank packType) {
        // Retrieve the map of ratios for the specified pack type (e.g., "bronze")
        Map<String, Integer> ratios = packProperties.getRatios().get(packType.name().toLowerCase());
        if (ratios == null) {
            logger.error("No configuration found for pack type: {}", packType);
            throw new IllegalArgumentException("Pack is not found.");
        }

        int totalWeight = ratios.values().stream().mapToInt(Integer::intValue).sum();
        int randomNumber = random.nextInt(totalWeight);
        int currentSum = 0;
        for (Map.Entry<String, Integer> entry : ratios.entrySet()) {
            currentSum += entry.getValue();
            if (randomNumber < currentSum) {
                // Convert the key (e.g., "normal") to the corresponding enum value (e.g., SphereRank.NORMAL)
                return SphereRank.valueOf(entry.getKey().toUpperCase());
            }
        }
        // Should never reach here if ratios are configured correctly
        throw new IllegalStateException("Error selecting sphere rarity.");
    }

    /**
     * Retrieves a random sphere from the database based on the provided rarity.
     */
    public Sphere getRandomSphereByRarity(SphereRank rarity) {
        // Using the repository’s native query to pick a random sphere by rarity.
        Sphere sphere = sphereRepository.findRandomByRank(rarity.name());
        if (sphere == null) {
            logger.error("No sphere found for the rarity:" + rarity);
            throw new IllegalStateException("No sphere found ");
        }
        return sphere;
    }

    /**
     * Generates a sphere pack of the specified size using the weighted ratios for the given pack type.
     *
     * @param packType the type of pack (BRONZE, SILVER, GOLD, or PLATINUM)
     * @param packSize the number of spheres to pick (e.g., 5)
     * @return a list of randomly selected spheres
     */
    public List<Sphere> generateSpherePack(SphereCardPackRank packType, int packSize) {
        List<Sphere> spherePack = new ArrayList<>();
        for (int i = 0; i < packSize; i++) {
            // Determine the rarity based on the weighted ratios from the property file
            SphereRank rarity = selectRandomRarity(packType);
            // Retrieve a random sphere of that rarity from the database
            Sphere sphere = getRandomSphereByRarity(rarity);
            spherePack.add(sphere);
        }
        return spherePack;
    }
}

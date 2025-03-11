package com.blancJH.weight_assistant_mobile_app_backend.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "pack")
public class CardPackConfig {

    /**
     * A map where the key is the pack type (e.g. "bronze", "silver", etc.)
     * and the value is another map that holds rarity keys (e.g. "normal", "legend") and their integer weights.
     */
    private Map<String, Map<String, Integer>> ratios;

    public Map<String, Map<String, Integer>> getRatios() {
        return ratios;
    }

    public void setRatios(Map<String, Map<String, Integer>> ratios) {
        this.ratios = ratios;
    }
}

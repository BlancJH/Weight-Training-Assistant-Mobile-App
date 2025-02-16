package com.blancJH.weight_assistant_mobile_app_backend.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:split_ratio.properties")
@ConfigurationProperties(prefix = "ratios")
public class RatioConfig {
    /**
     * This map will hold the nested ratio configuration loaded from split_ratio.properties.
     * For example, the properties file might contain:
     *
     * ratios.ARMS.Biceps=0.4
     * ratios.ARMS.Triceps=0.5
     * ratios.ARMS.Arms=0.1
     *
     * Which will be loaded into this map as:
     * { "ARMS" : { "Biceps": 0.4, "Triceps": 0.5, "Arms": 0.1 } }
     */
    private Map<String, Map<String, Double>> ratios = new HashMap<>();

    public Map<String, Map<String, Double>> getRatios() {
        return ratios;
    }

    public void setRatios(Map<String, Map<String, Double>> ratios) {
        this.ratios = ratios;
    }
}

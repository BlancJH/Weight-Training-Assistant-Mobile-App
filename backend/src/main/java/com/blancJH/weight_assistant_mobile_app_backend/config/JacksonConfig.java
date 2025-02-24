package com.blancJH.weight_assistant_mobile_app_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;

@Configuration
public class JacksonConfig {

    @Bean
    public Module hibernate6Module() {
        Hibernate6Module module = new Hibernate6Module();
        // This setting prevents Jackson from forcing initialization of lazy-loaded properties
        module.disable(Hibernate6Module.Feature.FORCE_LAZY_LOADING);
        return module;
    }

}


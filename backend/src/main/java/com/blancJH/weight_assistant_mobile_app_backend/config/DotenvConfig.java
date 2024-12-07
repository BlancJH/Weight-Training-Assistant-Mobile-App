package com.blancJH.weight_assistant_mobile_app_backend.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

/**
 * DotenvConfig is a configuration class that loads environment variables
 * from a `.env` file using the Dotenv library.
 * 
 * This configuration is used throughout the application to access
 * environment-specific variables in a centralized manner.
 * 
 * Example usage:
 * <pre>
 * String dbUrl = DotenvConfig.dotenv.get("DB_URL");
 * </pre>
 */
@Configuration
public class DotenvConfig {
    public static final Dotenv dotenv = Dotenv.load();
}

package com.blancJH.weight_assistant_mobile_app_backend.config;

import com.blancJH.weight_assistant_mobile_app_backend.config.DotenvConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnvTestController {

    @GetMapping("/api/test-env")
    public String getEnvVariable() {
        return "Database URL: " + DotenvConfig.dotenv.get("DB_URL");
    }
}

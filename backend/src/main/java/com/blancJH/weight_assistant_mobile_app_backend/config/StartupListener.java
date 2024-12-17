package com.blancJH.weight_assistant_mobile_app_backend.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Component
public class StartupListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            String port = event.getApplicationContext().getEnvironment().getProperty("server.port", "8080");
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            String contextPath = event.getApplicationContext().getEnvironment().getProperty("server.servlet.context-path", "");

            System.out.println("----------------------------------------------------------");
            System.out.println("Application is running at:");
            System.out.println("Local:      http://localhost:" + port + contextPath);
            System.out.println("External:   http://" + hostAddress + ":" + port + contextPath);
            System.out.println("----------------------------------------------------------");
        } catch (Exception e) {
            System.err.println("Failed to determine server address: " + e.getMessage());
        }
    }
}

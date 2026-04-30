package com.agriserve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * AgriServe — Agricultural Extension & Farmer Advisory System
 *
 * Entry point for the Spring Boot application.
 * All modules (Farmer, Advisory, Training, Feedback, Compliance, Reports, Notifications)
 * are available via RESTful APIs with JWT-based authentication.
 */
@SpringBootApplication
@EnableScheduling
public class AgriServeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgriServeApplication.class, args);
    }
}

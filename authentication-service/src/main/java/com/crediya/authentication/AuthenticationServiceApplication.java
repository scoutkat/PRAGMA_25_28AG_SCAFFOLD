package com.crediya.authentication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

/**
 * Main application class for CrediYa Authentication Service
 * This microservice handles user registration and authentication
 * Uses WebFlux for reactive programming and R2DBC for reactive database access
 */
@SpringBootApplication
@EnableR2dbcRepositories
public class AuthenticationServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AuthenticationServiceApplication.class, args);
    }
}

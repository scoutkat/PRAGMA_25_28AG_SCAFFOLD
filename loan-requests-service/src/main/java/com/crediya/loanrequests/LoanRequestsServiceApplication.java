package com.crediya.loanrequests;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

/**
 * Main application class for CrediYa Loan Requests Service
 * This microservice handles loan request management and processing
 * Uses WebFlux for reactive programming and R2DBC for reactive database access
 * Communicates with Authentication service for user validation
 */
@SpringBootApplication
@EnableR2dbcRepositories
public class LoanRequestsServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(LoanRequestsServiceApplication.class, args);
    }
}

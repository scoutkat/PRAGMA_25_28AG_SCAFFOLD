package com.crediya.loanrequests.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for API documentation
 * This class configures the Swagger UI and OpenAPI documentation
 * Provides comprehensive API documentation for the loan requests service
 */
@Configuration
public class OpenApiConfig {
    
    /**
     * Creates OpenAPI configuration for API documentation
     * @return OpenAPI instance with service information
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CrediYa Loan Requests Service API")
                        .description("""
                                This API provides endpoints for managing loan requests in the CrediYa platform.
                                
                                ## Features
                                - Create loan requests with user validation
                                - Validate loan types and calculate loan details
                                - Track loan request status and history
                                
                                ## Authentication
                                This service communicates with the Authentication service to validate users.
                                
                                ## Loan Types
                                Different loan types are available with varying interest rates and terms.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("CrediYa Development Team")
                                .email("dev@crediya.com")
                                .url("https://crediya.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8082")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.crediya.com/loan-requests")
                                .description("Production Server")
                ));
    }
}

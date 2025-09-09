package com.crediya.authentication.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for API documentation
 * Provides comprehensive API documentation for the Authentication Service
 * Includes server information, contact details, and API metadata
 */
@Configuration
public class OpenApiConfig {
    
    @Value("${server.port:8081}")
    private String serverPort;
    
    /**
     * Creates OpenAPI configuration for Swagger documentation
     * @return OpenAPI configuration
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CrediYa Authentication Service API")
                        .description("""
                                Microservice for user authentication and management in the CrediYa platform.
                                
                                This service provides the following functionality:
                                - User registration with validation
                                - User existence validation for other microservices
                                - User information retrieval
                                
                                The service follows reactive programming principles using WebFlux
                                and implements hexagonal architecture for better maintainability.
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
                                .url("http://localhost:" + serverPort)
                                .description("Local development server"),
                        new Server()
                                .url("https://api.crediya.com/auth")
                                .description("Production server")
                ));
    }
}

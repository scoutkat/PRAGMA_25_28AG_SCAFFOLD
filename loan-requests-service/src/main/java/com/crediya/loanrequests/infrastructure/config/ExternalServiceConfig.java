package com.crediya.loanrequests.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for external service endpoints
 * Maps configuration properties for inter-service communication
 */
@Configuration
@ConfigurationProperties(prefix = "external-services")
public class ExternalServiceConfig {
    
    private Authentication authentication = new Authentication();
    
    public Authentication getAuthentication() {
        return authentication;
    }
    
    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }
    
    /**
     * Authentication service configuration
     */
    public static class Authentication {
        private String baseUrl;
        private String validateUserEndpoint;
        
        public String getBaseUrl() {
            return baseUrl;
        }
        
        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
        
        public String getValidateUserEndpoint() {
            return validateUserEndpoint;
        }
        
        public void setValidateUserEndpoint(String validateUserEndpoint) {
            this.validateUserEndpoint = validateUserEndpoint;
        }
    }
}

package com.crediya.loanrequests.infrastructure.service;

import com.crediya.loanrequests.domain.port.UserValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Implementation of UserValidationService for external service communication
 * This class handles communication with the Authentication service
 * Uses WebClient for reactive HTTP communication
 */
@Service
public class UserValidationServiceImpl implements UserValidationService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserValidationServiceImpl.class);
    
    private final WebClient webClient;
    private final String validateUserEndpoint;
    
    public UserValidationServiceImpl(WebClient.Builder webClientBuilder,
                                   @Value("${external-services.authentication.base-url}") String baseUrl,
                                   @Value("${external-services.authentication.validate-user-endpoint}") String validateUserEndpoint) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.validateUserEndpoint = validateUserEndpoint;
    }
    
    @Override
    public Mono<Boolean> validateUserExists(String email) {
        logger.debug("Validating user existence with Authentication service for email: {}", email);
        
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(validateUserEndpoint)
                        .queryParam("email", email)
                        .build())
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .map(response -> {
                    if (response.isSuccess() && response.getData() != null) {
                        Boolean userExists = (Boolean) response.getData();
                        logger.debug("User validation result for {}: {}", email, userExists);
                        return userExists;
                    } else {
                        logger.warn("User validation failed for {}: {}", email, response.getError());
                        return false;
                    }
                })
                .onErrorResume(error -> {
                    logger.error("Error validating user with Authentication service for email: {}, error: {}", 
                            email, error.getMessage());
                    return Mono.just(false);
                });
    }
    
    /**
     * Internal class for API response deserialization
     * This class is used to deserialize the response from the Authentication service
     */
    private static class ApiResponse {
        private boolean success;
        private String message;
        private Object data;
        private String error;
        
        // Getters and Setters
        public boolean isSuccess() {
            return success;
        }
        
        public void setSuccess(boolean success) {
            this.success = success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public Object getData() {
            return data;
        }
        
        public void setData(Object data) {
            this.data = data;
        }
        
        public String getError() {
            return error;
        }
        
        public void setError(String error) {
            this.error = error;
        }
    }
}

package com.crediya.loanrequests.infrastructure.service;

import com.crediya.loanrequests.domain.port.UserValidationService;
import com.crediya.loanrequests.infrastructure.config.ExternalServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Implementation of UserValidationService for inter-service communication
 * This class handles communication with the authentication service
 * Uses WebClient for reactive HTTP communication
 */
@Service
public class UserValidationServiceImpl implements UserValidationService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserValidationServiceImpl.class);
    
    private final WebClient webClient;
    private final ExternalServiceConfig config;
    
    public UserValidationServiceImpl(WebClient.Builder webClientBuilder, ExternalServiceConfig config) {
        this.webClient = webClientBuilder
                .baseUrl(config.getAuthentication().getBaseUrl())
                .build();
        this.config = config;
    }
    
    @Override
    public Mono<Boolean> validateUserExists(String userEmail) {
        logger.debug("Validating user existence for email: {}", userEmail);
        
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(config.getAuthentication().getValidateUserEndpoint())
                        .queryParam("email", userEmail)
                        .build())
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .map(response -> {
                    if (response.isSuccess() && response.getData() instanceof Boolean) {
                        Boolean exists = (Boolean) response.getData();
                        logger.debug("User existence validation result for {}: {}", userEmail, exists);
                        return exists;
                    } else {
                        logger.warn("Invalid response from authentication service for user: {}", userEmail);
                        return false;
                    }
                })
                .onErrorResume(error -> {
                    logger.error("Error validating user existence for email: {}, error: {}", 
                            userEmail, error.getMessage());
                    return Mono.just(false);
                });
    }
    
    @Override
    public Mono<UserInfo> getUserInfo(String userEmail) {
        logger.debug("Getting user information for email: {}", userEmail);
        
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/usuarios")
                        .queryParam("email", userEmail)
                        .build())
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .map(response -> {
                    if (response.isSuccess() && response.getData() != null) {
                        // Parse user data from response
                        // This would need to be adapted based on the actual response structure
                        logger.debug("User information retrieved for: {}", userEmail);
                        return new UserInfo(
                                1L, // This would come from the actual response
                                "User", // This would come from the actual response
                                "Name", // This would come from the actual response
                                userEmail,
                                BigDecimal.ZERO, // This would come from the actual response
                                true // This would come from the actual response
                        );
                    } else {
                        logger.warn("User information not found for: {}", userEmail);
                        return null;
                    }
                })
                .onErrorResume(error -> {
                    logger.error("Error getting user information for email: {}, error: {}", 
                            userEmail, error.getMessage());
                    return Mono.empty();
                });
    }
    
    /**
     * Generic API response class for external service communication
     */
    private static class ApiResponse {
        private boolean success;
        private String message;
        private Object data;
        
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
    }
}

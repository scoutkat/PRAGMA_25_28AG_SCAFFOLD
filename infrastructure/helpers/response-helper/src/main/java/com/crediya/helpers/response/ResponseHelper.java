package com.crediya.helpers.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Helper class for creating standardized API responses
 * Provides consistent response formatting across all microservices
 * Following the principle of DRY (Don't Repeat Yourself)
 */
public class ResponseHelper {
    
    /**
     * Creates a successful response with data
     * @param message the success message
     * @param data the response data
     * @param httpStatus the HTTP status code
     * @return ResponseEntity with success response
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data, HttpStatus httpStatus) {
        ApiResponse<T> response = new ApiResponse<>(true, message, data, LocalDateTime.now());
        return ResponseEntity.status(httpStatus).body(response);
    }
    
    /**
     * Creates a successful response with data (default 200 OK)
     * @param message the success message
     * @param data the response data
     * @return ResponseEntity with success response
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data) {
        return success(message, data, HttpStatus.OK);
    }
    
    /**
     * Creates a successful response without data (default 200 OK)
     * @param message the success message
     * @return ResponseEntity with success response
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(String message) {
        return success(message, null, HttpStatus.OK);
    }
    
    /**
     * Creates a successful response with data (201 Created)
     * @param message the success message
     * @param data the response data
     * @return ResponseEntity with created response
     */
    public static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        return success(message, data, HttpStatus.CREATED);
    }
    
    /**
     * Creates an error response
     * @param message the error message
     * @param httpStatus the HTTP status code
     * @return ResponseEntity with error response
     */
    public static <T> ResponseEntity<ApiResponse<T>> error(String message, HttpStatus httpStatus) {
        ApiResponse<T> response = new ApiResponse<>(false, message, null, LocalDateTime.now());
        return ResponseEntity.status(httpStatus).body(response);
    }
    
    /**
     * Creates an error response (default 400 Bad Request)
     * @param message the error message
     * @return ResponseEntity with error response
     */
    public static <T> ResponseEntity<ApiResponse<T>> error(String message) {
        return error(message, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Creates a not found error response
     * @param message the error message
     * @return ResponseEntity with not found error response
     */
    public static <T> ResponseEntity<ApiResponse<T>> notFound(String message) {
        return error(message, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Creates a conflict error response
     * @param message the error message
     * @return ResponseEntity with conflict error response
     */
    public static <T> ResponseEntity<ApiResponse<T>> conflict(String message) {
        return error(message, HttpStatus.CONFLICT);
    }
    
    /**
     * Creates an internal server error response
     * @param message the error message
     * @return ResponseEntity with internal server error response
     */
    public static <T> ResponseEntity<ApiResponse<T>> internalServerError(String message) {
        return error(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Creates a reactive successful response with data
     * @param message the success message
     * @param data the response data
     * @param httpStatus the HTTP status code
     * @return Mono<ResponseEntity> with success response
     */
    public static <T> Mono<ResponseEntity<ApiResponse<T>>> successReactive(String message, T data, HttpStatus httpStatus) {
        return Mono.just(success(message, data, httpStatus));
    }
    
    /**
     * Creates a reactive successful response with data (default 200 OK)
     * @param message the success message
     * @param data the response data
     * @return Mono<ResponseEntity> with success response
     */
    public static <T> Mono<ResponseEntity<ApiResponse<T>>> successReactive(String message, T data) {
        return successReactive(message, data, HttpStatus.OK);
    }
    
    /**
     * Creates a reactive error response
     * @param message the error message
     * @param httpStatus the HTTP status code
     * @return Mono<ResponseEntity> with error response
     */
    public static <T> Mono<ResponseEntity<ApiResponse<T>>> errorReactive(String message, HttpStatus httpStatus) {
        return Mono.just(error(message, httpStatus));
    }
    
    /**
     * Creates a reactive error response (default 400 Bad Request)
     * @param message the error message
     * @return Mono<ResponseEntity> with error response
     */
    public static <T> Mono<ResponseEntity<ApiResponse<T>>> errorReactive(String message) {
        return errorReactive(message, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Generic API response wrapper
     * Provides a consistent response format for all API endpoints
     */
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;
        private LocalDateTime timestamp;
        
        public ApiResponse(boolean success, String message, T data, LocalDateTime timestamp) {
            this.success = success;
            this.message = message;
            this.data = data;
            this.timestamp = timestamp;
        }
        
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
        
        public T getData() {
            return data;
        }
        
        public void setData(T data) {
            this.data = data;
        }
        
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
        
        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }
    }
}

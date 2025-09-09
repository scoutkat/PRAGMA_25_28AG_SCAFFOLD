package com.crediya.loanrequests.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Generic API response wrapper for consistent response format
 * This DTO provides a standardized structure for all API responses
 * Includes success status, message, and data payload
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private boolean success;
    private String message;
    private T data;
    private String error;
    
    // Default constructor
    public ApiResponse() {}
    
    // Constructor for success responses
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    
    // Constructor for error responses
    public ApiResponse(boolean success, String error) {
        this.success = success;
        this.error = error;
    }
    
    // Static factory methods for creating responses
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }
    
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null);
    }
    
    public static <T> ApiResponse<T> error(String error) {
        return new ApiResponse<>(false, error);
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
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
}

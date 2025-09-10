package com.crediya.helpers.logging;

import org.slf4j.Logger;
import org.slf4j.MDC;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Helper class for standardized logging across the application
 * Provides consistent logging patterns and MDC (Mapped Diagnostic Context) management
 * Following the principle of DRY (Don't Repeat Yourself)
 */
public class LoggingHelper {
    
    private static final String REQUEST_ID_KEY = "requestId";
    private static final String USER_ID_KEY = "userId";
    private static final String SERVICE_KEY = "service";
    private static final String OPERATION_KEY = "operation";
    
    /**
     * Sets up MDC context for a request
     * @param requestId the unique request identifier
     * @param userId the user identifier (optional)
     * @param service the service name
     * @param operation the operation being performed
     */
    public static void setupRequestContext(String requestId, String userId, String service, String operation) {
        MDC.put(REQUEST_ID_KEY, requestId != null ? requestId : generateRequestId());
        if (userId != null) {
            MDC.put(USER_ID_KEY, userId);
        }
        MDC.put(SERVICE_KEY, service);
        MDC.put(OPERATION_KEY, operation);
    }
    
    /**
     * Clears MDC context
     */
    public static void clearContext() {
        MDC.clear();
    }
    
    /**
     * Generates a unique request ID
     * @return unique request identifier
     */
    public static String generateRequestId() {
        return "req_" + UUID.randomUUID().toString().substring(0, 8) + "_" + 
               LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
    
    /**
     * Logs the start of an operation
     * @param logger the logger instance
     * @param operation the operation name
     * @param details additional details
     */
    public static void logOperationStart(Logger logger, String operation, String details) {
        logger.info("Starting operation: {} - {}", operation, details);
    }
    
    /**
     * Logs the successful completion of an operation
     * @param logger the logger instance
     * @param operation the operation name
     * @param details additional details
     */
    public static void logOperationSuccess(Logger logger, String operation, String details) {
        logger.info("Operation completed successfully: {} - {}", operation, details);
    }
    
    /**
     * Logs the failure of an operation
     * @param logger the logger instance
     * @param operation the operation name
     * @param error the error details
     * @param throwable the exception (optional)
     */
    public static void logOperationError(Logger logger, String operation, String error, Throwable throwable) {
        if (throwable != null) {
            logger.error("Operation failed: {} - Error: {}", operation, error, throwable);
        } else {
            logger.error("Operation failed: {} - Error: {}", operation, error);
        }
    }
    
    /**
     * Logs a validation error
     * @param logger the logger instance
     * @param field the field that failed validation
     * @param value the value that failed validation
     * @param reason the reason for validation failure
     */
    public static void logValidationError(Logger logger, String field, Object value, String reason) {
        logger.warn("Validation error - Field: {}, Value: {}, Reason: {}", field, value, reason);
    }
    
    /**
     * Logs a business rule violation
     * @param logger the logger instance
     * @param rule the business rule that was violated
     * @param context the context of the violation
     */
    public static void logBusinessRuleViolation(Logger logger, String rule, String context) {
        logger.warn("Business rule violation - Rule: {}, Context: {}", rule, context);
    }
    
    /**
     * Logs external service communication
     * @param logger the logger instance
     * @param service the external service name
     * @param operation the operation being performed
     * @param status the status of the communication
     */
    public static void logExternalServiceCall(Logger logger, String service, String operation, String status) {
        logger.info("External service call - Service: {}, Operation: {}, Status: {}", 
                   service, operation, status);
    }
    
    /**
     * Logs external service error
     * @param logger the logger instance
     * @param service the external service name
     * @param operation the operation being performed
     * @param error the error details
     */
    public static void logExternalServiceError(Logger logger, String service, String operation, String error) {
        logger.error("External service error - Service: {}, Operation: {}, Error: {}", 
                    service, operation, error);
    }
    
    /**
     * Logs performance metrics
     * @param logger the logger instance
     * @param operation the operation name
     * @param duration the duration in milliseconds
     * @param additionalInfo additional performance information
     */
    public static void logPerformance(Logger logger, String operation, long duration, String additionalInfo) {
        logger.info("Performance metrics - Operation: {}, Duration: {}ms, Info: {}", 
                   operation, duration, additionalInfo);
    }
    
    /**
     * Logs security events
     * @param logger the logger instance
     * @param event the security event
     * @param details the event details
     */
    public static void logSecurityEvent(Logger logger, String event, String details) {
        logger.warn("Security event - Event: {}, Details: {}", event, details);
    }
    
    /**
     * Logs data access operations
     * @param logger the logger instance
     * @param operation the database operation
     * @param table the table being accessed
     * @param recordCount the number of records affected
     */
    public static void logDataAccess(Logger logger, String operation, String table, int recordCount) {
        logger.debug("Data access - Operation: {}, Table: {}, Records: {}", 
                    operation, table, recordCount);
    }
    
    /**
     * Gets the current request ID from MDC
     * @return the current request ID or null if not set
     */
    public static String getCurrentRequestId() {
        return MDC.get(REQUEST_ID_KEY);
    }
    
    /**
     * Gets the current user ID from MDC
     * @return the current user ID or null if not set
     */
    public static String getCurrentUserId() {
        return MDC.get(USER_ID_KEY);
    }
    
    /**
     * Gets the current service name from MDC
     * @return the current service name or null if not set
     */
    public static String getCurrentService() {
        return MDC.get(SERVICE_KEY);
    }
    
    /**
     * Gets the current operation from MDC
     * @return the current operation or null if not set
     */
    public static String getCurrentOperation() {
        return MDC.get(OPERATION_KEY);
    }
}

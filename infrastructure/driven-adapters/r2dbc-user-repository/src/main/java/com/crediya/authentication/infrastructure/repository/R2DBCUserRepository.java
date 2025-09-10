package com.crediya.authentication.infrastructure.repository;

import com.crediya.authentication.domain.model.User;
import com.crediya.authentication.domain.port.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * R2DBC implementation of UserRepository
 * This class handles database operations using reactive R2DBC
 * Following hexagonal architecture - this is a driven adapter in the infrastructure layer
 */
@Repository
public class R2DBCUserRepository implements UserRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(R2DBCUserRepository.class);
    
    private final DatabaseClient databaseClient;
    
    public R2DBCUserRepository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }
    
    @Override
    public Mono<User> save(User user) {
        logger.debug("Saving user to database: {}", user.getEmail());
        
        String insertQuery = """
            INSERT INTO users (first_name, last_name, birth_date, address, phone, email, base_salary, created_at, updated_at, is_active)
            VALUES (:firstName, :lastName, :birthDate, :address, :phone, :email, :baseSalary, :createdAt, :updatedAt, :isActive)
            """;
        
        return databaseClient.sql(insertQuery)
                .bind("firstName", user.getFirstName())
                .bind("lastName", user.getLastName())
                .bind("birthDate", user.getBirthDate())
                .bind("address", user.getAddress())
                .bind("phone", user.getPhone())
                .bind("email", user.getEmail())
                .bind("baseSalary", user.getBaseSalary())
                .bind("createdAt", user.getCreatedAt())
                .bind("updatedAt", user.getUpdatedAt())
                .bind("isActive", user.getIsActive())
                .filter((statement, executeFunction) -> statement.returnGeneratedValues("id"))
                .map((row, metadata) -> {
                    user.setId(row.get("id", Long.class));
                    return user;
                })
                .one()
                .doOnSuccess(savedUser -> logger.debug("User saved successfully with ID: {}", savedUser.getId()))
                .doOnError(error -> logger.error("Error saving user: {}", error.getMessage()));
    }
    
    @Override
    public Mono<User> findByEmail(String email) {
        logger.debug("Finding user by email: {}", email);
        
        String selectQuery = """
            SELECT id, first_name, last_name, birth_date, address, phone, email, base_salary, 
                   created_at, updated_at, is_active
            FROM users 
            WHERE email = :email AND is_active = true
            """;
        
        return databaseClient.sql(selectQuery)
                .bind("email", email)
                .map((row, metadata) -> mapRowToUser(row))
                .one()
                .doOnSuccess(user -> {
                    if (user != null) {
                        logger.debug("User found by email: {}", email);
                    }
                })
                .doOnError(error -> logger.error("Error finding user by email: {}, error: {}", 
                        email, error.getMessage()));
    }
    
    @Override
    public Mono<User> findById(Long id) {
        logger.debug("Finding user by ID: {}", id);
        
        String selectQuery = """
            SELECT id, first_name, last_name, birth_date, address, phone, email, base_salary, 
                   created_at, updated_at, is_active
            FROM users 
            WHERE id = :id AND is_active = true
            """;
        
        return databaseClient.sql(selectQuery)
                .bind("id", id)
                .map((row, metadata) -> mapRowToUser(row))
                .one()
                .doOnSuccess(user -> {
                    if (user != null) {
                        logger.debug("User found by ID: {}", id);
                    }
                })
                .doOnError(error -> logger.error("Error finding user by ID: {}, error: {}", 
                        id, error.getMessage()));
    }
    
    @Override
    public Mono<Boolean> existsByEmail(String email) {
        logger.debug("Checking if user exists by email: {}", email);
        
        String countQuery = "SELECT COUNT(*) FROM users WHERE email = :email AND is_active = true";
        
        return databaseClient.sql(countQuery)
                .bind("email", email)
                .map((row, metadata) -> row.get(0, Long.class) > 0)
                .one()
                .doOnSuccess(exists -> logger.debug("User existence check for {}: {}", email, exists))
                .doOnError(error -> logger.error("Error checking user existence for email: {}, error: {}", 
                        email, error.getMessage()));
    }
    
    @Override
    public Mono<User> update(User user) {
        logger.debug("Updating user: {}", user.getId());
        
        String updateQuery = """
            UPDATE users 
            SET first_name = :firstName, last_name = :lastName, birth_date = :birthDate, 
                address = :address, phone = :phone, email = :email, base_salary = :baseSalary, 
                updated_at = :updatedAt, is_active = :isActive
            WHERE id = :id
            """;
        
        return databaseClient.sql(updateQuery)
                .bind("id", user.getId())
                .bind("firstName", user.getFirstName())
                .bind("lastName", user.getLastName())
                .bind("birthDate", user.getBirthDate())
                .bind("address", user.getAddress())
                .bind("phone", user.getPhone())
                .bind("email", user.getEmail())
                .bind("baseSalary", user.getBaseSalary())
                .bind("updatedAt", user.getUpdatedAt())
                .bind("isActive", user.getIsActive())
                .then(Mono.just(user))
                .doOnSuccess(updatedUser -> logger.debug("User updated successfully: {}", updatedUser.getId()))
                .doOnError(error -> logger.error("Error updating user: {}, error: {}", 
                        user.getId(), error.getMessage()));
    }
    
    @Override
    public Mono<Boolean> deleteById(Long id) {
        logger.debug("Deleting user by ID: {}", id);
        
        String deleteQuery = "UPDATE users SET is_active = false, updated_at = :updatedAt WHERE id = :id";
        
        return databaseClient.sql(deleteQuery)
                .bind("id", id)
                .bind("updatedAt", LocalDateTime.now())
                .fetch()
                .rowsUpdated()
                .map(rowsUpdated -> rowsUpdated > 0)
                .doOnSuccess(deleted -> logger.debug("User deletion result for ID {}: {}", id, deleted))
                .doOnError(error -> logger.error("Error deleting user by ID: {}, error: {}", 
                        id, error.getMessage()));
    }
    
    /**
     * Maps database row to User entity
     * @param row the database row
     * @return User entity
     */
    private User mapRowToUser(org.springframework.r2dbc.core.Row row) {
        User user = new User();
        user.setId(row.get("id", Long.class));
        user.setFirstName(row.get("first_name", String.class));
        user.setLastName(row.get("last_name", String.class));
        user.setBirthDate(row.get("birth_date", LocalDate.class));
        user.setAddress(row.get("address", String.class));
        user.setPhone(row.get("phone", String.class));
        user.setEmail(row.get("email", String.class));
        user.setBaseSalary(row.get("base_salary", BigDecimal.class));
        user.setCreatedAt(row.get("created_at", LocalDateTime.class));
        user.setUpdatedAt(row.get("updated_at", LocalDateTime.class));
        user.setIsActive(row.get("is_active", Boolean.class));
        return user;
    }
}

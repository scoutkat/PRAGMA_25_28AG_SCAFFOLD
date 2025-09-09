package com.crediya.authentication.infrastructure.mapper;

import com.crediya.authentication.domain.model.User;
import com.crediya.authentication.infrastructure.dto.UserRegistrationRequest;
import com.crediya.authentication.infrastructure.dto.UserResponse;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between DTOs and domain entities
 * Follows the principle of separation of concerns
 * Handles all data transformation between API layer and domain layer
 */
@Component
public class UserMapper {
    
    /**
     * Converts UserRegistrationRequest DTO to User domain entity
     * @param request the registration request DTO
     * @return User domain entity
     */
    public User toDomain(UserRegistrationRequest request) {
        if (request == null) {
            return null;
        }
        
        return new User(
                request.getFirstName(),
                request.getLastName(),
                request.getBirthDate(),
                request.getAddress(),
                request.getPhone(),
                request.getEmail(),
                request.getBaseSalary()
        );
    }
    
    /**
     * Converts User domain entity to UserResponse DTO
     * @param user the user domain entity
     * @return UserResponse DTO
     */
    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setFullName(user.getFullName());
        response.setBirthDate(user.getBirthDate());
        response.setAddress(user.getAddress());
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        response.setBaseSalary(user.getBaseSalary());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        response.setIsActive(user.getIsActive());
        
        return response;
    }
    
    /**
     * Updates an existing User domain entity with data from UserRegistrationRequest
     * @param user the existing user entity
     * @param request the registration request DTO
     */
    public void updateDomain(User user, UserRegistrationRequest request) {
        if (user == null || request == null) {
            return;
        }
        
        user.updateUser(
                request.getFirstName(),
                request.getLastName(),
                request.getBirthDate(),
                request.getAddress(),
                request.getPhone(),
                request.getEmail(),
                request.getBaseSalary()
        );
    }
}

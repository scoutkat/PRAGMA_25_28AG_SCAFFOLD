package com.crediya.loanrequests.infrastructure.mapper;

import com.crediya.loanrequests.domain.model.LoanRequest;
import com.crediya.loanrequests.infrastructure.dto.LoanRequestRequest;
import com.crediya.loanrequests.infrastructure.dto.LoanRequestResponse;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between domain entities and DTOs
 * This class handles the mapping between the domain layer and infrastructure layer
 * Following the separation of concerns principle
 */
@Component
public class LoanRequestMapper {
    
    /**
     * Converts a LoanRequestRequest DTO to a LoanRequest domain entity
     * @param request the request DTO
     * @return LoanRequest domain entity
     */
    public LoanRequest toDomain(LoanRequestRequest request) {
        if (request == null) {
            return null;
        }
        
        return new LoanRequest(
                request.getUserEmail(),
                request.getLoanTypeId(),
                request.getAmount(),
                request.getTermMonths()
        );
    }
    
    /**
     * Converts a LoanRequest domain entity to a LoanRequestResponse DTO
     * @param loanRequest the domain entity
     * @return LoanRequestResponse DTO
     */
    public LoanRequestResponse toResponse(LoanRequest loanRequest) {
        if (loanRequest == null) {
            return null;
        }
        
        LoanRequestResponse response = new LoanRequestResponse();
        response.setId(loanRequest.getId());
        response.setUserEmail(loanRequest.getUserEmail());
        response.setLoanTypeId(loanRequest.getLoanTypeId());
        response.setAmount(loanRequest.getAmount());
        response.setTermMonths(loanRequest.getTermMonths());
        response.setStatus(loanRequest.getStatus() != null ? loanRequest.getStatus().name() : null);
        response.setMonthlyPayment(loanRequest.getMonthlyPayment());
        response.setTotalInterest(loanRequest.getTotalInterest());
        response.setTotalAmount(loanRequest.getTotalAmount());
        response.setCreatedAt(loanRequest.getCreatedAt());
        response.setUpdatedAt(loanRequest.getUpdatedAt());
        response.setReviewedAt(loanRequest.getReviewedAt());
        response.setReviewedBy(loanRequest.getReviewedBy());
        response.setRejectionReason(loanRequest.getRejectionReason());
        
        return response;
    }
}

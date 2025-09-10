package com.crediya.loanrequests.infrastructure.mapper;

import com.crediya.loanrequests.domain.model.LoanRequest;
import com.crediya.loanrequests.infrastructure.dto.LoanRequestRequest;
import com.crediya.loanrequests.infrastructure.dto.LoanRequestResponse;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between DTOs and domain entities
 * Follows the principle of separation of concerns
 * Handles all data transformation between API layer and domain layer
 */
@Component
public class LoanRequestMapper {
    
    /**
     * Converts LoanRequestRequest DTO to LoanRequest domain entity
     * @param request the loan request creation request DTO
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
     * Converts LoanRequest domain entity to LoanRequestResponse DTO
     * @param loanRequest the loan request domain entity
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
        response.setStatusDisplayName(loanRequest.getStatus() != null ? loanRequest.getStatus().getDisplayName() : null);
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
    
    /**
     * Updates an existing LoanRequest domain entity with data from LoanRequestRequest
     * @param loanRequest the existing loan request entity
     * @param request the loan request creation request DTO
     */
    public void updateDomain(LoanRequest loanRequest, LoanRequestRequest request) {
        if (loanRequest == null || request == null) {
            return;
        }
        
        loanRequest.setUserEmail(request.getUserEmail());
        loanRequest.setLoanTypeId(request.getLoanTypeId());
        loanRequest.setAmount(request.getAmount());
        loanRequest.setTermMonths(request.getTermMonths());
    }
}

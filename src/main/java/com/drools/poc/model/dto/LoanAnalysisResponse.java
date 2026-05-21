package com.drools.poc.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO para respuesta de análisis de préstamo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanAnalysisResponse {
    private String applicationId;
    private String customerId;
    private String status;
    private String riskLevel;
    private double riskScore;
    private List<String> riskFactors;
    private List<String> appliedRules;
    private String recommendedAction;
    private double approvedAmount;
    private double interestRate;
    private int approvedTerm;
    private boolean requiresManualReview;
    private List<String> requiredDocuments;
    private String decisionReason;
    private double confidenceScore;
    private long processingTimeMs;
}

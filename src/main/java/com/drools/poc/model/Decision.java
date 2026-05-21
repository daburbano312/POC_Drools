package com.drools.poc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Decisión Final del Sistema
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Decision implements Serializable {
    private static final long serialVersionUID = 1L;

    private String decisionId;
    private String applicationId;
    private String customerId;
    private String status; // PENDING, APPROVED, REJECTED, PENDING_REVIEW
    private double approvedAmount;
    private double approvedInterestRate;
    private int approvedTerm;
    private String decisionReason;
    private String decisionEngine; // AUTOMATED, MANUAL, HYBRID
    private LocalDateTime decisionDate;
    private String decidedBy;
    private String validityPeriod; // e.g., 30_DAYS, 60_DAYS
    private boolean canApply;
    private String rejectionReason;
    private String conditions; // Condiciones especiales si las hay
    private int priorityLevel; // Para priorizar casos en cola
    private double confidenceScore; // 0-100, qué tan confiable es la decisión automatizada
}

package com.drools.poc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Evaluación de Riesgos - Resultado del análisis de reglas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskAssessment implements Serializable {
    private static final long serialVersionUID = 1L;

    private String assessmentId;
    private String applicationId;
    private String riskLevel; // LOW, MEDIUM, HIGH, CRITICAL
    private double riskScore; // 0-100
    @Builder.Default
    private List<String> riskFactors = new ArrayList<>();
    @Builder.Default
    private List<String> appliedRules = new ArrayList<>();
    private String recommendedAction; // APPROVE, APPROVE_WITH_CONDITIONS, REJECT, MANUAL_REVIEW
    private double maxApprovedAmount;
    private double minInterestRate;
    private String assessmentTimestamp;
    private boolean requiresManualReview;
    private boolean requiresDocumentation;
    @Builder.Default
    private List<String> requiredDocuments = new ArrayList<>();
    private String assessmentNotes;

    public RiskAssessment(String assessmentId, String applicationId) {
        this.assessmentId = assessmentId;
        this.applicationId = applicationId;
        this.riskFactors = new ArrayList<>();
        this.appliedRules = new ArrayList<>();
        this.requiredDocuments = new ArrayList<>();
    }

    public void addRiskFactor(String factor) {
        if (!this.riskFactors.contains(factor)) {
            this.riskFactors.add(factor);
        }
    }

    public void addAppliedRule(String rule) {
        if (!this.appliedRules.contains(rule)) {
            this.appliedRules.add(rule);
        }
    }

    public void addRequiredDocument(String doc) {
        if (!this.requiredDocuments.contains(doc)) {
            this.requiredDocuments.add(doc);
        }
    }
}

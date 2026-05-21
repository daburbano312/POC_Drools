package com.drools.poc.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitud de análisis de préstamo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanAnalysisRequest {
    private String customerId;
    private String name;
    private int age;
    private double monthlyIncome;
    private int creditScore;
    private double currentDebt;
    private int yearsWithBank;
    private String employmentStatus;
    private boolean hasSavings;
    private double savingsAmount;
    private boolean isPoliticallyExposed;
    private String nationality;
    
    // Datos del préstamo
    private double loanAmount;
    private int loanTerm;
    private String loanPurpose;
    private String productType;
    private String collateralType;
    private double collateralValue;
    private boolean isRefinancing;
    private double previousLoansAmount;
    private int numberOfApplicationsLast6Months;

    // Datos del inmueble tipo bodega (nuevas reglas)
    private String assetType;
    private String propertyCondition;
    private String propertyAgeRange;
    private String propertyLocation;
    private String clearHeightRange;
    private String accessDoorHeightRange;
    private String parkingAvailability;
    private String parkingIndexRange;
    private String saleableAreaRange;
    private String accessRoadType;
    private boolean sustainabilityCertification;
    private String subleaseContractRange;
}

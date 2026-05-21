package com.drools.poc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Solicitud de Préstamo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplication implements Serializable {
    private static final long serialVersionUID = 1L;

    private String applicationId;
    private String customerId;
    private double loanAmount;
    private int loanTerm; // en meses
    private String loanPurpose; // PERSONAL, BUSINESS, MORTGAGE, AUTO
    private LocalDateTime applicationDate;
    private String productType; // STANDARD, PREMIUM, FAST_TRACK
    private double proposedInterestRate;
    private String collateralType; // NONE, PROPERTY, VEHICLE, OTHER
    private double collateralValue;
    private boolean isRefinancing;
    private double previousLoansAmount;
    private int numberOfApplicationsLast6Months;

    // Campos para evaluación de inmuebles tipo bodega
    private String assetType; // BODEGA
    private String propertyCondition; // NUEVO, USADO
    private String propertyAgeRange; // MENOR_5, ENTRE_5_20, MAYOR_20
    private String propertyLocation; // PARQUE_INDUSTRIAL, ZONA_FRANCA, ZONA_INDUSTRIAL, OTRA_UBICACION
    private String clearHeightRange; // MENOR_7, DOBLE_ALTURA, TRIPLE_ALTURA
    private String accessDoorHeightRange; // MENOR_3_5, ENTRE_3_5_4_5, MAYOR_4_5
    private String parkingAvailability; // SI, NO, INCIERTO
    private String parkingIndexRange; // MENOR_50, ENTRE_50_100, MAYOR_100
    private String saleableAreaRange; // MENOR_500, ENTRE_500_1000, ENTRE_1000_5000, MAYOR_5000
    private String accessRoadType; // VIA_PRINCIPAL, VIA_SECUNDARIA_BUENAS, VIA_SECUNDARIA_REGULARES
    private boolean sustainabilityCertification;
    private String subleaseContractRange; // PROPIO, MENOR_1, ENTRE_1_5, ENTRE_5_10, MAYOR_10
    
    // Datos calculados/evaluados
    private double debtToIncomeRatio;
    private double loanToValueRatio;
}

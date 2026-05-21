package com.drools.poc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * Entidad de Cliente - Contexto financiero
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    private String customerId;
    private String name;
    private int age;
    private double monthlyIncome;
    private int creditScore;
    private double currentDebt;
    private int yearsWithBank;
    private String employmentStatus; // EMPLOYED, SELF_EMPLOYED, UNEMPLOYED
    private boolean hasSavings;
    private double savingsAmount;
    private boolean isPoliticallyExposed;
    private String nationality;
}

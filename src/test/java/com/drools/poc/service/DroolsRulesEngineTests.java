package com.drools.poc.service;

import com.drools.poc.config.DroolsConfig;
import com.drools.poc.model.Customer;
import com.drools.poc.model.LoanApplication;
import com.drools.poc.model.RiskAssessment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del Motor de Reglas Drools
 * Valida evaluación de diferentes escenarios de préstamo
 */
@SpringBootTest
@DisplayName("Drools Rules Engine Tests")
public class DroolsRulesEngineTests {

    @Autowired
    private DroolsRulesEngineService rulesEngine;

    private Customer defaultCustomer;
    private LoanApplication defaultApplication;

    @BeforeEach
    void setUp() {
        // Cliente por defecto
        defaultCustomer = Customer.builder()
                .customerId("CUST-001")
                .name("John Doe")
                .age(35)
                .monthlyIncome(5000)
                .creditScore(750)
                .currentDebt(1000)
                .yearsWithBank(8)
                .employmentStatus("EMPLOYED")
                .hasSavings(true)
                .savingsAmount(15000)
                .isPoliticallyExposed(false)
                .nationality("US")
                .build();

        // Solicitud por defecto
        defaultApplication = LoanApplication.builder()
                .loanAmount(50000)
                .loanTerm(60)
                .loanPurpose("PERSONAL")
                .productType("STANDARD")
                .proposedInterestRate(5.5)
                .collateralType("NONE")
                .collateralValue(0)
                .isRefinancing(false)
                .previousLoansAmount(0)
                .numberOfApplicationsLast6Months(0)
                .build();
    }

    // ============ PRUEBAS BÁSICAS ============

    @Test
    @DisplayName("✅ Debe rechazar cliente menor de edad")
    void testUnderageCustomerRejection() {
        // Arrange
        defaultCustomer.setAge(17);

        // Act
        RiskAssessment result = rulesEngine.evaluateLoanApplicationStateless(
            defaultCustomer, defaultApplication);

        // Assert
        assertEquals("CRITICAL", result.getRiskLevel());
        assertEquals("REJECT", result.getRecommendedAction());
        assertTrue(result.getRiskFactors().contains("CUSTOMER_UNDERAGE"));
    }

    @Test
    @DisplayName("✅ Debe rechazar cliente sin empleo")
    void testUnemployedCustomerRejection() {
        // Arrange
        defaultCustomer.setEmploymentStatus("UNEMPLOYED");

        // Act
        RiskAssessment result = rulesEngine.evaluateLoanApplicationStateless(
            defaultCustomer, defaultApplication);

        // Assert
        assertTrue(result.getRiskFactors().contains("UNEMPLOYED_STATUS"));
        assertTrue(result.getRiskScore() >= 30);
    }

    @Test
    @DisplayName("✅ Debe detectar cliente PEP")
    void testPoliticallyExposedPersonDetection() {
        // Arrange
        defaultCustomer.setPoliticallyExposed(true);

        // Act
        RiskAssessment result = rulesEngine.evaluateLoanApplicationStateless(
            defaultCustomer, defaultApplication);

        // Assert
        assertTrue(result.isRequiresManualReview());
        assertTrue(result.getRiskFactors().contains("PEP_CUSTOMER"));
    }

    // ============ PRUEBAS DE RATIOS FINANCIEROS ============

    @Test
    @DisplayName("✅ Debe calcular ratio deuda/ingreso")
    void testDebtToIncomeRatioCalculation() {
        // Arrange
        defaultCustomer.setMonthlyIncome(3000);
        defaultCustomer.setCurrentDebt(2000);
        defaultApplication.setLoanAmount(30000);
        defaultApplication.setLoanTerm(60);

        // Act
        RiskAssessment result = rulesEngine.evaluateLoanApplicationStateless(
            defaultCustomer, defaultApplication);

        // Assert
        assertTrue(result.getRiskFactors().contains("HIGH_DEBT_TO_INCOME"));
    }

    @Test
    @DisplayName("✅ Debe rechazar monto de préstamo excesivo")
    void testLargeLoanAmountDetection() {
        // Arrange
        defaultApplication.setLoanAmount(500000);

        // Act
        RiskAssessment result = rulesEngine.evaluateLoanApplicationStateless(
            defaultCustomer, defaultApplication);

        // Assert
        assertTrue(result.isRequiresManualReview());
        assertTrue(result.getRiskFactors().contains("LARGE_LOAN_AMOUNT"));
    }

    // ============ PRUEBAS DE MITIGACIÓN DE RIESGO ============

    @Test
    @DisplayName("✅ Debe reducir riesgo para cliente con ahorros")
    void testSavingsMitigatesRisk() {
        // Arrange
        Customer lowCreditCustomer = defaultCustomer;
        lowCreditCustomer.setCreditScore(550); // Bajo score
        lowCreditCustomer.setHasSavings(true);
        lowCreditCustomer.setSavingsAmount(25000); // Ahorros altos

        // Act
        RiskAssessment result = rulesEngine.evaluateLoanApplicationStateless(
            lowCreditCustomer, defaultApplication);

        // Assert
        assertTrue(result.getRiskFactors().contains("MITIGATING_SAVINGS"));
    }

    @Test
    @DisplayName("✅ Debe reduce riesgo para cliente con historial positivo")
    void testLongBankHistoryReducesRisk() {
        // Arrange
        defaultCustomer.setYearsWithBank(15);
        defaultCustomer.setCreditScore(800);

        // Act
        RiskAssessment result = rulesEngine.evaluateLoanApplicationStateless(
            defaultCustomer, defaultApplication);

        // Assert
        assertTrue(result.getRiskFactors().contains("POSITIVE_BANK_HISTORY"));
    }

    // ============ PRUEBAS DE DECISIÓN ============

    @Test
    @DisplayName("✅ Debe aprobar cliente con bajo riesgo")
    void testLowRiskAutoApproval() {
        // Arrange - Cliente excelente
        defaultCustomer.setAge(45);
        defaultCustomer.setCreditScore(800);
        defaultCustomer.setMonthlyIncome(8000);
        defaultCustomer.setEmploymentStatus("EMPLOYED");
        defaultApplication.setLoanAmount(30000);

        // Act
        RiskAssessment result = rulesEngine.evaluateLoanApplicationStateless(
            defaultCustomer, defaultApplication);

        // Assert
        assertEquals("LOW", result.getRiskLevel());
        assertEquals("APPROVE", result.getRecommendedAction());
        assertTrue(result.getRiskScore() < 40);
    }

    @Test
    @DisplayName("✅ Debe requerir revisión manual para riesgo medio")
    void testMediumRiskManualReview() {
        // Arrange - Cliente con riesgo medio
        defaultCustomer.setCreditScore(600);
        defaultCustomer.setMonthlyIncome(2500);
        defaultApplication.setLoanAmount(40000);

        // Act
        RiskAssessment result = rulesEngine.evaluateLoanApplicationStateless(
            defaultCustomer, defaultApplication);

        // Assert
        assertTrue(
            "MEDIUM".equals(result.getRiskLevel()) || 
            "HIGH".equals(result.getRiskLevel())
        );
    }

    // ============ COMPARACIÓN SESIONES ============

    @Test
    @DisplayName("✅ Sesión STATELESS completa evaluación")
    void testStatelessSessionCompletion() {
        // Act
        RiskAssessment result = rulesEngine.evaluateLoanApplicationStateless(
            defaultCustomer, defaultApplication);

        // Assert
        assertNotNull(result.getRiskLevel());
        assertNotNull(result.getRecommendedAction());
        assertTrue(result.getRiskScore() >= 0);
        assertFalse(result.getAppliedRules().isEmpty());
    }

    @Test
    @DisplayName("✅ Sesión STATEFUL completa evaluación")
    void testStatefulSessionCompletion() {
        // Act
        RiskAssessment result = rulesEngine.evaluateLoanApplicationStateful(
            defaultCustomer, defaultApplication);

        // Assert
        assertNotNull(result.getRiskLevel());
        assertNotNull(result.getRecommendedAction());
        assertTrue(result.getRiskScore() >= 0);
        assertFalse(result.getAppliedRules().isEmpty());
    }

    // ============ PRUEBAS DE DOCUMENTOS REQUERIDOS ============

    @Test
    @DisplayName("✅ Debe requerir documentación para riesgo alto")
    void testDocumentRequirements() {
        // Arrange - Cliente con riesgo
        defaultCustomer.setCreditScore(500);

        // Act
        RiskAssessment result = rulesEngine.evaluateLoanApplicationStateless(
            defaultCustomer, defaultApplication);

        // Assert
        if (result.getRiskScore() >= 40) {
            assertTrue(result.isRequiresDocumentation());
            assertFalse(result.getRequiredDocuments().isEmpty());
        }
    }

    // ============ PRUEBAS DE REFINANCIAMIENTO ============

    @Test
    @DisplayName("✅ Debe reducir riesgo para refinanciamiento")
    void testRefinancingLoanRiskReduction() {
        // Arrange
        defaultApplication.setRefinancing(true);
        defaultApplication.setPreviousLoansAmount(50000);

        // Act
        RiskAssessment result = rulesEngine.evaluateLoanApplicationStateless(
            defaultCustomer, defaultApplication);

        // Assert
        assertTrue(result.getRiskFactors().contains("REFINANCING_LOAN"));
    }

    // ============ PRUEBAS DE APLICACIONES MÚLTIPLES ============

    @Test
    @DisplayName("✅ Debe detectar múltiples aplicaciones recientes")
    void testMultipleRecentApplications() {
        // Arrange
        defaultApplication.setNumberOfApplicationsLast6Months(5);

        // Act
        RiskAssessment result = rulesEngine.evaluateLoanApplicationStateless(
            defaultCustomer, defaultApplication);

        // Assert
        assertTrue(result.getRiskFactors().contains("MULTIPLE_RECENT_APPLICATIONS"));
        assertTrue(result.getRiskScore() >= 20);
    }
}

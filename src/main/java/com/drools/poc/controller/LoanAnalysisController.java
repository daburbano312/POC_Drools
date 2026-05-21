package com.drools.poc.controller;

import com.drools.poc.model.Customer;
import com.drools.poc.model.LoanApplication;
import com.drools.poc.model.RiskAssessment;
import com.drools.poc.model.dto.LoanAnalysisRequest;
import com.drools.poc.model.dto.LoanAnalysisResponse;
import com.drools.poc.service.DroolsRulesEngineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST - Endpoints de la API
 */
@Slf4j
@RestController
@RequestMapping("/loan-analysis")
@CrossOrigin(origins = "*")
public class LoanAnalysisController {

    private final DroolsRulesEngineService droolsService;

    public LoanAnalysisController(DroolsRulesEngineService droolsService) {
        this.droolsService = droolsService;
    }

    /**
     * Endpoint: Evaluar solicitud usando sesión STATELESS
     * POST /api/loan-analysis/evaluate-stateless
     */
    @PostMapping("/evaluate-stateless")
    public ResponseEntity<LoanAnalysisResponse> evaluateStateless(
            @RequestBody LoanAnalysisRequest request) {

        try {
            long startTime = System.currentTimeMillis();

            // Mapear DTO a modelo
            Customer customer = mapToCustomer(request);
            LoanApplication application = mapToApplication(request);

            // Ejecutar evaluación
            RiskAssessment riskAssessment = 
                droolsService.evaluateLoanApplicationStateless(customer, application);

            // Mapear respuesta
            LoanAnalysisResponse response = mapToResponse(riskAssessment, application, 
                                                         System.currentTimeMillis() - startTime);

            log.info("✅ Evaluación STATELESS completada: {} - {} - Score: {}",
                    riskAssessment.getApplicationId(),
                    riskAssessment.getRiskLevel(),
                    riskAssessment.getRiskScore());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error en evaluación STATELESS", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint: Evaluar solicitud usando sesión STATEFUL
     * POST /api/loan-analysis/evaluate-stateful
     */
    @PostMapping("/evaluate-stateful")
    public ResponseEntity<LoanAnalysisResponse> evaluateStateful(
            @RequestBody LoanAnalysisRequest request) {

        try {
            long startTime = System.currentTimeMillis();

            Customer customer = mapToCustomer(request);
            LoanApplication application = mapToApplication(request);

            RiskAssessment riskAssessment = 
                droolsService.evaluateLoanApplicationStateful(customer, application);

            LoanAnalysisResponse response = mapToResponse(riskAssessment, application,
                                                         System.currentTimeMillis() - startTime);

            log.info("✅ Evaluación STATEFUL completada: {} - {} - Score: {}",
                    riskAssessment.getApplicationId(),
                    riskAssessment.getRiskLevel(),
                    riskAssessment.getRiskScore());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error en evaluación STATEFUL", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint: Comparar ambas sesiones
     * POST /api/loan-analysis/compare-sessions
     */
    @PostMapping("/compare-sessions")
    public ResponseEntity<Map<String, Object>> compareSessions(
            @RequestBody LoanAnalysisRequest request) {

        try {
            Customer customer = mapToCustomer(request);
            LoanApplication app1 = mapToApplication(request);
            LoanApplication app2 = mapToApplication(request);

            long startStateless = System.currentTimeMillis();
            RiskAssessment statelessResult = droolsService.evaluateLoanApplicationStateless(customer, app1);
            long timeStateless = System.currentTimeMillis() - startStateless;

            long startStateful = System.currentTimeMillis();
            RiskAssessment statefulResult = droolsService.evaluateLoanApplicationStateful(customer, app2);
            long timeStateful = System.currentTimeMillis() - startStateful;

            Map<String, Object> comparison = new HashMap<>();
            comparison.put("stateless", Map.of(
                "riskScore", statelessResult.getRiskScore(),
                "riskLevel", statelessResult.getRiskLevel(),
                "processingTime", timeStateless + "ms",
                "appliedRules", statelessResult.getAppliedRules().size()
            ));
            comparison.put("stateful", Map.of(
                "riskScore", statefulResult.getRiskScore(),
                "riskLevel", statefulResult.getRiskLevel(),
                "processingTime", timeStateful + "ms",
                "appliedRules", statefulResult.getAppliedRules().size()
            ));
            comparison.put("performance", Map.of(
                "fasterBy", Math.abs(timeStateless - timeStateful) + "ms",
                "fasterSession", timeStateless < timeStateful ? "STATELESS" : "STATEFUL"
            ));

            return ResponseEntity.ok(comparison);

        } catch (Exception e) {
            log.error("❌ Error en comparación de sesiones", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint: Health check
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "Drools Loan Analysis Engine",
            "timestamp", LocalDateTime.now().toString(),
            "sessions", droolsService.getSessionInfo()
        ));
    }

    /**
     * Endpoint: Obtener métricas de la aplicación
     */
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        return ResponseEntity.ok(Map.of(
            "service", "Drools Rules Engine POC",
            "version", "1.0.0",
            "timestamp", LocalDateTime.now().toString(),
            "description", "Evaluación de solicitudes de préstamo usando reglas de negocio"
        ));
    }

    // ============ Métodos de mapeo privados ============

    private Customer mapToCustomer(LoanAnalysisRequest request) {
        return Customer.builder()
                .customerId(request.getCustomerId())
                .name(request.getName())
                .age(request.getAge())
                .monthlyIncome(request.getMonthlyIncome())
                .creditScore(request.getCreditScore())
                .currentDebt(request.getCurrentDebt())
                .yearsWithBank(request.getYearsWithBank())
                .employmentStatus(request.getEmploymentStatus())
                .hasSavings(request.isHasSavings())
                .savingsAmount(request.getSavingsAmount())
                .isPoliticallyExposed(request.isPoliticallyExposed())
                .nationality(request.getNationality())
                .build();
    }

    private LoanApplication mapToApplication(LoanAnalysisRequest request) {
        return LoanApplication.builder()
                .customerId(request.getCustomerId())
                .loanAmount(request.getLoanAmount())
                .loanTerm(request.getLoanTerm())
                .loanPurpose(request.getLoanPurpose())
                .productType(request.getProductType())
                .proposedInterestRate(3.5)
                .collateralType(request.getCollateralType())
                .collateralValue(request.getCollateralValue())
                .isRefinancing(request.isRefinancing())
                .previousLoansAmount(request.getPreviousLoansAmount())
                .numberOfApplicationsLast6Months(request.getNumberOfApplicationsLast6Months())
                .assetType(request.getAssetType())
                .propertyCondition(request.getPropertyCondition())
                .propertyAgeRange(request.getPropertyAgeRange())
                .propertyLocation(request.getPropertyLocation())
                .clearHeightRange(request.getClearHeightRange())
                .accessDoorHeightRange(request.getAccessDoorHeightRange())
                .parkingAvailability(request.getParkingAvailability())
                .parkingIndexRange(request.getParkingIndexRange())
                .saleableAreaRange(request.getSaleableAreaRange())
                .accessRoadType(request.getAccessRoadType())
                .sustainabilityCertification(request.isSustainabilityCertification())
                .subleaseContractRange(request.getSubleaseContractRange())
                .applicationDate(LocalDateTime.now())
                .build();
    }

    private LoanAnalysisResponse mapToResponse(RiskAssessment assessment, 
                                              LoanApplication application,
                                              long processingTime) {
        return LoanAnalysisResponse.builder()
                .applicationId(assessment.getApplicationId())
                .customerId(application.getCustomerId())
                .status(assessment.getRecommendedAction())
                .riskLevel(assessment.getRiskLevel())
                .riskScore(assessment.getRiskScore())
                .riskFactors(assessment.getRiskFactors())
                .appliedRules(assessment.getAppliedRules())
                .recommendedAction(assessment.getRecommendedAction())
                .approvedAmount(assessment.getMaxApprovedAmount())
                .interestRate(assessment.getMinInterestRate() > 0 ? 
                    assessment.getMinInterestRate() : application.getProposedInterestRate())
                .approvedTerm(application.getLoanTerm())
                .requiresManualReview(assessment.isRequiresManualReview())
                .requiredDocuments(assessment.getRequiredDocuments())
                .decisionReason(assessment.getAssessmentNotes())
                .confidenceScore(0)
                .processingTimeMs(processingTime)
                .build();
    }
}

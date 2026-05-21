package com.drools.poc.service;

import com.drools.poc.model.Customer;
import com.drools.poc.model.Decision;
import com.drools.poc.model.LoanApplication;
import com.drools.poc.model.RiskAssessment;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Servicio de Motor de Reglas Drools
 * Ejecuta análisis de préstamos usando sesiones stateless y stateful
 */
@Slf4j
@Service
public class DroolsRulesEngineService {

    private final StatelessKieSession statelessSession;
    private final KieSession statefulSession;

    public DroolsRulesEngineService(
            @Qualifier("statelessSession") StatelessKieSession statelessSession,
            @Qualifier("statefulSession") KieSession statefulSession) {
        this.statelessSession = statelessSession;
        this.statefulSession = statefulSession;
    }

    /**
     * Evaluar solicitud de préstamo usando SESIÓN STATELESS
     * Ideal para decisiones independientes sin necesidad de mantener estado
     *
     * @param customer Datos del cliente
     * @param application Datos de la solicitud
     * @return Evaluación de riesgos y decisión
     */
    public RiskAssessment evaluateLoanApplicationStateless(
            Customer customer,
            LoanApplication application) {

        long startTime = System.currentTimeMillis();

        // Crear identificadores
        String assessmentId = UUID.randomUUID().toString();
        String applicationId = UUID.randomUUID().toString();

        // Asignar IDs si no los tienen
        if (customer.getCustomerId() == null) {
            customer.setCustomerId(UUID.randomUUID().toString());
        }
        if (application.getApplicationId() == null) {
            application.setApplicationId(applicationId);
        }
        application.setCustomerId(customer.getCustomerId());

        // Crear objetos de evaluación
        RiskAssessment riskAssessment = RiskAssessment.builder()
                .assessmentId(assessmentId)
                .applicationId(application.getApplicationId())
                .riskScore(0)
                .riskLevel("PENDING")
                .assessmentTimestamp(LocalDateTime.now().toString())
                .build();

        Decision decision = Decision.builder()
                .decisionId(UUID.randomUUID().toString())
                .applicationId(application.getApplicationId())
                .customerId(customer.getCustomerId())
                .status("PENDING")
                .decisionDate(LocalDateTime.now())
                .decisionEngine("AUTOMATED")
                .build();

        try {
            log.info("🚀 Iniciando evaluación STATELESS para cliente: {}", customer.getName());

            // Ejecutar reglas en sesión stateless
            statelessSession.execute(java.util.List.of(
                    customer,
                    application,
                    riskAssessment,
                    decision
            ));
            log.debug("✅ Reglas ejecutadas. Risk Score: {}", riskAssessment.getRiskScore());

            // Normalizar riesgo si no se estableció
            if ("PENDING".equals(riskAssessment.getRiskLevel())) {
                determineRiskLevel(riskAssessment);
            }

            // Establecer confianza en la decisión automatizada
            if ("APPROVED".equals(decision.getStatus())) {
                decision.setConfidenceScore(Math.min(95, 100 - riskAssessment.getRiskScore()));
            } else if ("REJECTED".equals(decision.getStatus())) {
                decision.setConfidenceScore(90);
            } else {
                decision.setConfidenceScore(75);
            }

        } catch (Exception e) {
            log.error("❌ Error durante evaluación STATELESS", e);
            riskAssessment.setRiskLevel("CRITICAL");
            riskAssessment.setRiskScore(100);
            decision.setStatus("PENDING_REVIEW");
            decision.setDecisionReason("Error during automatic evaluation: " + e.getMessage());
        }

        long processingTime = System.currentTimeMillis() - startTime;
        log.info("⏱️ Evaluación completada en {}ms", processingTime);

        return riskAssessment;
    }

    /**
     * Evaluar solicitud de préstamo usando SESIÓN STATEFUL
     * Ideal para análisis complejos que requieren mantener estado
     *
     * @param customer Datos del cliente
     * @param application Datos de la solicitud
     * @return Evaluación de riesgos con análisis avanzado
     */
    public RiskAssessment evaluateLoanApplicationStateful(
            Customer customer,
            LoanApplication application) {

        long startTime = System.currentTimeMillis();

        // Crear identificadores
        String assessmentId = UUID.randomUUID().toString();
        String applicationId = UUID.randomUUID().toString();

        if (customer.getCustomerId() == null) {
            customer.setCustomerId(UUID.randomUUID().toString());
        }
        if (application.getApplicationId() == null) {
            application.setApplicationId(applicationId);
        }
        application.setCustomerId(customer.getCustomerId());

        RiskAssessment riskAssessment = RiskAssessment.builder()
                .assessmentId(assessmentId)
                .applicationId(application.getApplicationId())
                .riskScore(0)
                .riskLevel("PENDING")
                .assessmentTimestamp(LocalDateTime.now().toString())
                .build();

        Decision decision = Decision.builder()
                .decisionId(UUID.randomUUID().toString())
                .applicationId(application.getApplicationId())
                .customerId(customer.getCustomerId())
                .status("PENDING")
                .decisionDate(LocalDateTime.now())
                .decisionEngine("AUTOMATED_STATEFUL")
                .build();

        try {
            log.info("🚀 Iniciando evaluación STATEFUL para cliente: {}", customer.getName());

            // Insertar objetos en sesión stateful
            statefulSession.insert(customer);
            statefulSession.insert(application);
            statefulSession.insert(riskAssessment);
            statefulSession.insert(decision);

            // Ejecutar reglas
            int firedRules = statefulSession.fireAllRules();
            log.debug("✅ {} reglas ejecutadas", firedRules);

            if ("PENDING".equals(riskAssessment.getRiskLevel())) {
                determineRiskLevel(riskAssessment);
            }

            if ("APPROVED".equals(decision.getStatus())) {
                decision.setConfidenceScore(Math.min(90, 100 - riskAssessment.getRiskScore()));
            } else if ("REJECTED".equals(decision.getStatus())) {
                decision.setConfidenceScore(88);
            } else {
                decision.setConfidenceScore(70);
            }

        } catch (Exception e) {
            log.error("❌ Error durante evaluación STATEFUL", e);
            riskAssessment.setRiskLevel("CRITICAL");
            riskAssessment.setRiskScore(100);
            decision.setStatus("PENDING_REVIEW");
            decision.setDecisionReason("Error during stateful evaluation: " + e.getMessage());
        }

        long processingTime = System.currentTimeMillis() - startTime;
        log.info("⏱️ Evaluación STATEFUL completada en {}ms", processingTime);

        return riskAssessment;
    }

    /**
     * Determinar nivel de riesgo basado en score
     */
    private void determineRiskLevel(RiskAssessment riskAssessment) {
        if (riskAssessment.getRiskScore() >= 85) {
            riskAssessment.setRiskLevel("CRITICAL");
        } else if (riskAssessment.getRiskScore() >= 60) {
            riskAssessment.setRiskLevel("HIGH");
        } else if (riskAssessment.getRiskScore() >= 40) {
            riskAssessment.setRiskLevel("MEDIUM");
        } else {
            riskAssessment.setRiskLevel("LOW");
        }
    }

    /**
     * Obtener información de sesión para propósitos de diagnóstico
     */
    public String getSessionInfo() {
        return "Stateless Session: " + (statelessSession != null ? "Active" : "Inactive") +
                ", Stateful Session: " + (statefulSession != null ? "Active" : "Inactive");
    }
}

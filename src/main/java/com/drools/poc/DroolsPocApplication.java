package com.drools.poc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Aplicación principal - POC de Drools como motor de reglas empresarial
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.drools.poc")
public class DroolsPocApplication {

    public static void main(String[] args) {
        SpringApplication.run(DroolsPocApplication.class, args);
        System.out.println("========================================");
        System.out.println("🚀 Drools Rules Engine POC iniciado");
        System.out.println("📍 API disponible en: http://localhost:8080");
        System.out.println("📊 Métricas en: http://localhost:8080/actuator");
        System.out.println("========================================");
    }
}

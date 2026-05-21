package com.drools.poc.config;

import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

/**
 * Configuración de Drools - Carga de reglas y creación de sesiones
 */
@Configuration
public class DroolsConfig {

    private static final String RULES_PATH = "rules/";

    /**
     * Cargar el contenedor de reglas (KieContainer)
     * Este bean carga todas las reglas de la carpeta resources/rules/
     */
    @Bean
    public KieContainer kieContainer() throws IOException {
        KieServices kieServices = KieServices.Factory.get();
        KieRepository kieRepository = kieServices.getRepository();
        
        // Crear sistema de archivos KIE
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        
        // Cargar archivos de reglas
        List<String> ruleFiles = List.of(
            "01-simple-rules.drl",
            "02-chained-rules.drl",
            "03-decision-rules.drl",
            "04-decision-tables.drl",
            "05-bodega-rules.drl"
        );
        
        for (String ruleFile : ruleFiles) {
            String resourcePath = RULES_PATH + ruleFile;
            ClassPathResource resource = new ClassPathResource(resourcePath);
            String content = new String(resource.getInputStream().readAllBytes());
            kieFileSystem.write(ResourceFactory.newClassPathResource(resourcePath));
            System.out.println("✅ Reglas cargadas: " + ruleFile);
        }
        
        // Construir KIE Base
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();
        
        // Verificar errores
        if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
            System.err.println("❌ Errores en compilación de reglas:");
            kieBuilder.getResults().getMessages(Message.Level.ERROR).forEach(
                msg -> System.err.println("   - " + msg.getText())
            );
            throw new IllegalStateException("Error en compilación de reglas");
        }
        
        KieContainer kieContainer = kieServices.newKieContainer(kieRepository.getDefaultReleaseId());
        System.out.println("🎯 KieContainer inicializado correctamente");
        
        return kieContainer;
    }

    /**
     * Crear una sesión STATELESS para decisiones independientes
     * Usada para análisis sin mantener estado entre ejecuciones
     */
    @Bean(name = "statelessSession")
    public org.kie.api.runtime.StatelessKieSession statelessKieSession(KieContainer kieContainer) {
        org.kie.api.runtime.StatelessKieSession session = kieContainer.newStatelessKieSession();
        System.out.println("✅ Sesión STATELESS creada");
        return session;
    }

    /**
     * Crear una sesión STATEFUL para análisis complejos
     * Usada para evaluaciones que requieren mantener contexto
     */
    @Bean(name = "statefulSession")
    public KieSession statefulKieSession(KieContainer kieContainer) {
        KieSession session = kieContainer.newKieSession();
        System.out.println("✅ Sesión STATEFUL creada");
        return session;
    }

    /**
     * Configuración personalizada de KieBase
     */
    @Bean
    public KieBaseConfiguration kieBaseConfiguration() {
        org.kie.api.KieBaseConfiguration config = 
            KieServices.Factory.get().newKieBaseConfiguration();
        
        // Usar identidad de objetos en lugar de igualdad (mejor performance)
        config.setOption(EqualityBehaviorOption.IDENTITY);
        
        System.out.println("✅ Configuración de KieBase establecida");
        return config;
    }
}

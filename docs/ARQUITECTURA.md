# 🏗️ Arquitectura - POC Drools Rules Engine

## Overview

Esta POC implementa un **motor de reglas de negocio desacoplado** usando **Drools** en un contexto financiero (evaluación de solicitudes de préstamo).

```
┌─────────────────────────────────────────────────────────────────┐
│                      API REST Layer                              │
│  (LoanAnalysisController)                                        │
│  - POST /evaluate-stateless                                      │
│  - POST /evaluate-stateful                                       │
│  - POST /compare-sessions                                        │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                  Business Logic Layer                             │
│  (DroolsRulesEngineService)                                      │
│  - evaluateLoanApplicationStateless()                            │
│  - evaluateLoanApplicationStateful()                             │
│  - determineRiskLevel()                                          │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                    Rules Engine Layer                             │
│  (Drools KIE Runtime)                                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐           │
│  │  Stateless   │  │  Stateful    │  │ KieContainer │           │
│  │  Session     │  │  Session     │  │  & KieBase   │           │
│  └──────────────┘  └──────────────┘  └──────────────┘           │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                     Rules Files Layer                             │
│  (DRL - Drools Rule Language)                                    │
│  ┌────────────────┐  ┌────────────────┐  ┌────────────────┐     │
│  │  01-Simple     │  │  02-Chained    │  │  03-Decision   │     │
│  │  Rules         │  │  Rules         │  │  Rules         │     │
│  └────────────────┘  └────────────────┘  └────────────────┘     │
│  ┌────────────────┐  ┌────────────────┐                         │
│  │  04-Decision   │  │  DSL Business  │                         │
│  │  Tables        │  │  Language      │                         │
│  └────────────────┘  └────────────────┘                         │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                      Domain Model Layer                           │
│  (POJOs)                                                          │
│  - Customer                                                      │
│  - LoanApplication                                               │
│  - RiskAssessment                                                │
│  - Decision                                                      │
└─────────────────────────────────────────────────────────────────┘
```

## Componentes Principales

### 1. **Capa de API REST**
- **Controlador**: `LoanAnalysisController`
- **Endpoints**:
  - `POST /api/loan-analysis/evaluate-stateless` → Evaluación sin estado
  - `POST /api/loan-analysis/evaluate-stateful` → Evaluación con estado
  - `POST /api/loan-analysis/compare-sessions` → Comparación de performance
  - `GET /api/loan-analysis/health` → Health check
  - `GET /api/loan-analysis/metrics` → Métricas

### 2. **Capa de Lógica de Negocio**
- **Servicio**: `DroolsRulesEngineService`
- **Responsabilidades**:
  - Mapeo de datos a objetos Drools
  - Ejecución de sesiones (stateless/stateful)
  - Cálculo de nivel de riesgo
  - Gestión de ciclo de vida

### 3. **Capa de Motor de Reglas**
- **Configuración**: `DroolsConfig`
- **Elementos**:
  - `KieContainer`: Contenedor de reglas compiladas
  - `StatelessKieSession`: Sesión sin estado (recomendado para decisiones independientes)
  - `KieSession`: Sesión con estado (para análisis complejos)
  - `KieBase`: Base de conocimiento compilada

### 4. **Capa de Reglas (DRL - Drools Rule Language)**
Four rule files with different complexity levels:

#### **01-simple-rules.drl** (7 reglas)
- Validación de edad (mínima/máxima)
- Verificación de empleo
- Evaluación de puntaje de crédito
- Detección de PEP (Politically Exposed Person)
- Validación de múltiples solicitudes
- Validación de ingresos

#### **02-chained-rules.drl** (9 reglas)
- Cálculo de DTI Ratio (Debt-to-Income)
- Evaluación de DTI alto (encadenada)
- Cálculo de LTV Ratio (Loan-to-Value)
- Evaluación de LTV alto (encadenada)
- Detección de monto de préstamo grande
- Análisis de historial del cliente
- Mitigación de riesgo por ahorros
- Mitigación por historial positivo
- Reducción de riesgo por refinanciamiento

#### **03-decision-rules.drl** (7 reglas)
- **Rechazo automático** (Critical Risk ≥85)
- **Revisión manual** (High Risk 60-85)
- **Aprobación condicional** (Medium Risk 40-60)
- **Aprobación automática** (Low Risk <40)
- Determinación de documentación requerida
- Ajuste de tasa de interés por tipo de producto
- Establecimiento de prioridad de procesamiento

#### **04-decision-tables.drl** (5 matrices de decisión)
- **Matriz Monto/Ingreso**: Validación de capacidad de pago
- **Matriz Colateral**: Evaluación de riesgo por tipo de colateral
- **Matriz Edad/Crédito**: Matriz bidimensional de riesgo
- **Matriz Propósito/Producto**: Evaluación según tipo de préstamo
- **DSL** (`loan-business.dsl`): Sintaxis cercana al negocio

### 5. **Capa de Modelo de Datos**
```
Customer
├── Información personal (edad, nombre)
├── Información financiera (ingresos, deuda, crédito)
├── Historial (años con banco, solicitudes)
└── Factores de riesgo (PEP, nacionalidad)

LoanApplication
├── Monto y plazo
├── Tipo y propósito
├── Colateral
└── Datos de refinanciamiento

RiskAssessment
├── Score de riesgo (0-100)
├── Nivel (LOW, MEDIUM, HIGH, CRITICAL)
├── Factores detectados
├── Reglas aplicadas
└── Documentación requerida

Decision
├── Estado (APPROVED, REJECTED, PENDING_REVIEW)
├── Monto aprobado
├── Tasa de interés
└── Confianza en decisión
```

## Flujo de Procesamiento

### **Flujo Stateless (Recomendado para decisiones independientes)**

```
Request API
    ↓
LoanAnalysisController
    ↓
DroolsRulesEngineService.evaluateLoanApplicationStateless()
    ↓
StatelessKieSession.execute()
    ├─ Insert(Customer)
    ├─ Insert(LoanApplication)
    ├─ Insert(RiskAssessment)
    ├─ Insert(Decision)
    └─ fireAllRules() → Ejecutar todas las reglas
    ↓
Retornar RiskAssessment
    ↓
Response API
```

**Ventajas**:
- ✅ Sesión temporal (sin gestión de estado)
- ✅ Ideal para procesamiento masivo (batch)
- ✅ Menor uso de memoria
- ✅ Mayor performance
- ✅ Desacoplamiento total

### **Flujo Stateful (Para análisis complejos)**

```
Request API
    ↓
LoanAnalysisController
    ↓
DroolsRulesEngineService.evaluateLoanApplicationStateful()
    ↓
KieSession.insert(Customer)
    ↓
KieSession.insert(LoanApplication)
    ↓
KieSession.insert(RiskAssessment)
    ↓
KieSession.insert(Decision)
    ↓
KieSession.fireAllRules()
    ↓
Retornar RiskAssessment
```

**Ventajas**:
- ✅ Mantiene estado entre inserciones
- ✅ Permite lógica más compleja
- ✅ Útil para streaming de datos
- ⚠️ Mayor uso de memoria

## Decisiones de Diseño

### 1. **Separación de Responsabilidades**
- ✅ Reglas completamente separadas del código Java
- ✅ Cambios en reglas sin recompilación
- ✅ Control de versiones independiente
- ✅ Gestión centralizada de lógica de negocio

### 2. **Modularidad de Reglas**
- ✅ Archivos DRL divididos por complejidad
- ✅ Simples → Encadenadas → Decisiones → Tablas
- ✅ Facilita mantenimiento y testing
- ✅ Escalabilidad clara

### 3. **Desacoplamiento**
```
SIN Drools (Tightly Coupled):
    LoanAnalysisService
    ├── if (age < 18) reject()
    ├── if (unemployed) reject()
    ├── if (creditScore < 500) addRisk()
    └── ... 50+ líneas de lógica entrelazada

CON Drools (Decoupled):
    LoanAnalysisService → DroolsRulesEngine → rules/*.drl
    - Service: Solo orquestación
    - Rules: Solo lógica de negocio
    - Archivos: Independientes y versionables
```

### 4. **Performance**
- Sesión **Stateless**: Recomendada para API REST (sin estado)
- Sesión **Stateful**: Para análisis iterativos
- **Rete Algorithm**: Compilación de reglas para eficiencia

## Ventajas Arquitectónicas

| Aspecto | Beneficio |
|---------|-----------|
| **Mantenibilidad** | Cambios de reglas sin modificar código Java |
| **Flexibilidad** | Reglas dinámicas, actualizables en runtime |
| **Testabilidad** | Reglas probables independientemente |
| **Escalabilidad** | Adicionar reglas sin afectar performance |
| **Documentación** | Reglas autodocumentadas (DSL) |
| **Cumplimiento** | Auditabilidad: qué regla disparó qué decisión |

## Stack Tecnológico

- **Framework**: Spring Boot 3.2.0
- **Motor de Reglas**: Drools 8.44.0.Final
- **JDK**: Java 17
- **Build**: Maven 3.8+
- **Testing**: JUnit 5
- **Logging**: SLF4J + Logback

## Configuración y Deployment

### Local Development
```bash
mvn clean install
mvn spring-boot:run
```

### Production Deployment
```bash
mvn clean package
java -jar target/drools-rules-engine-poc-1.0.0.jar
```

### Docker (opcional)
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/drools-rules-engine-poc-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Próximas Mejoras

- 🚀 Cargar reglas desde base de datos
- 🚀 Hot deployment de reglas
- 🚀 Auditoría de decisiones
- 🚀 ML integration para scoring
- 🚀 Eventos (Kafka) para procesamiento async
- 🚀 Caché de decisiones

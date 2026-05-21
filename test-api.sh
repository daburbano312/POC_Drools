#!/bin/bash

# Script de Testing - POC Drools Rules Engine
# Ejecuta casos de prueba contra la API REST

BASE_URL="http://localhost:8080/api/loan-analysis"

echo "=========================================="
echo "🚀 Drools Rules Engine - API Testing"
echo "=========================================="
echo ""

# Colores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 1. Health Check
echo -e "${BLUE}1. Health Check${NC}"
echo "---"
curl -s $BASE_URL/health | jq . || echo "⚠️  API no disponible"
echo ""

# 2. Test Excellent Customer
echo -e "${BLUE}2. Test: Cliente Excelente (Debe aprobar)${NC}"
echo "---"
curl -s -X POST $BASE_URL/evaluate-stateless \
  -H "Content-Type: application/json" \
  -d @examples/excellent-customer.json | jq '.' || echo "❌ Error"
echo ""

# 3. Test Medium Risk Customer
echo -e "${BLUE}3. Test: Cliente Riesgo Medio (Revisión manual)${NC}"
echo "---"
curl -s -X POST $BASE_URL/evaluate-stateless \
  -H "Content-Type: application/json" \
  -d @examples/medium-risk-customer.json | jq '.' || echo "❌ Error"
echo ""

# 4. Test High Risk Customer
echo -e "${BLUE}4. Test: Cliente Alto Riesgo (Debe rechazar)${NC}"
echo "---"
curl -s -X POST $BASE_URL/evaluate-stateless \
  -H "Content-Type: application/json" \
  -d @examples/high-risk-customer.json | jq '.' || echo "❌ Error"
echo ""

# 5. Comparison Test
echo -e "${BLUE}5. Test: Comparación Sesiones${NC}"
echo "---"
curl -s -X POST $BASE_URL/compare-sessions \
  -H "Content-Type: application/json" \
  -d @examples/excellent-customer.json | jq '.' || echo "❌ Error"
echo ""

# 6. Metrics
echo -e "${BLUE}6. Métricas${NC}"
echo "---"
curl -s $BASE_URL/metrics | jq '.' || echo "❌ Error"
echo ""

echo -e "${GREEN}=========================================="
echo "✅ Testing Completado"
echo "==========================================${NC}"

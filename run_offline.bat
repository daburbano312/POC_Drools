@echo off
REM Script para compilar y ejecutar en modo offline
setlocal enabledelayedexpansion

set MVN=.\.tools\apache-maven-3.9.9\bin\mvn.cmd
set JAVA_HOME=.\.tools\jdk-17.0.19+10

echo ========================================
echo Compilando en MODO OFFLINE...
echo ========================================

REM Primero intentamos descargar dependencias (puede fallar, es ok)
echo.
echo [1/3] Descargando dependencias (si es posible)...
%MVN% dependency:resolve -o 2>nul

echo.
echo [2/3] Compilando con Maven en modo offline...
%MVN% clean compile -o -DskipTests

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✓ Compilacion exitosa!
    echo.
    echo [3/3] Iniciando aplicacion...
    echo.
    %MVN% spring-boot:run -o
) else (
    echo.
    echo ✗ Error en compilacion
    echo Intenta primero: %MVN% clean install -U
    pause
)

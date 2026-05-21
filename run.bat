@echo off
setlocal

set JAVA_HOME=%~dp0.tools\jdk-17.0.19+10
set PATH=%~dp0.tools\apache-maven-3.9.9\bin;%~dp0.tools\jdk-17.0.19+10\bin;%PATH%

echo ========================================
echo  Drools Rules Engine POC
echo ========================================
echo.
echo [1/2] Compilando en modo offline...
mvn.cmd clean install -o -DskipTests

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Fallo la compilacion. Revisa los logs anteriores.
    pause
    exit /b 1
)

echo.
echo [2/2] Iniciando aplicacion...
echo.
echo  API disponible en: http://localhost:8080
echo  Salud:             http://localhost:8080/api/loan-analysis/health
echo  Presiona Ctrl+C para detener
echo.
mvn.cmd spring-boot:run -o
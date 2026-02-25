@echo off
title Office Bingo Manager
color 0A

echo ==========================================
echo      ATUALIZANDO E INICIANDO BINGO
echo ==========================================

:: --- 1. PARAR PROCESSOS ---
echo [1/4] Parando processos Java antigos... 
taskkill /F /IM java.exe >nul 2>&1

:: --- 2. BUILD DO BACKEND ---
echo.
echo [2/4] Compilando Backend (Spring Boot)... 
cd api
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo [ERRO] Falha no build do Backend!
    pause
    exit /b %errorlevel%
)

:: Inicia o Backend em segundo plano
start "Bingo Backend" /MIN java -jar target/office-bingo-api-0.0.1-SNAPSHOT.jar --server.port=8080 
cd ..

:: --- 3. EXECUÇÃO DO FRONTEND ---
echo.
echo [3/4] Preparando Frontend... 
cd front

:: Instala dependências se não existirem
if not exist node_modules (
    echo Instalando dependencias...
    call yarn install
)

echo Gerando Build de Producao (Vite)...
call yarn build
if %errorlevel% neq 0 (
    echo [ERRO] Falha no build do Frontend!
    pause
    exit /b %errorlevel%
)

echo.
echo [4/4] Servidores Iniciados!
echo ACESSE: http://localhost:3210 
echo.

:: Agora sim, visualiza o build recém-criado
call yarn preview --host --port 3210
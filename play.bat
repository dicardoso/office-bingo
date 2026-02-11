@echo off
setlocal enabledelayedexpansion
title Bingo Server (Auto-IP)
color 0B

:: --- 1. DESCOBRIR O IP LOCAL ---
for /f "tokens=2 delims=:" %%a in ('ipconfig ^| findstr /c:"IPv4"') do (
    set "MY_IP=%%a"
    goto :found_ip
)
:found_ip
:: Remove espaços em branco do IP
set "MY_IP=%MY_IP: =%"

echo ==========================================
echo    BINGO INICIADO NO IP: %MY_IP%
echo ==========================================

:: Mata processos antigos
taskkill /F /IM java.exe >nul 2>&1

:: --- 2. INICIAR BACKEND ---
cd api
start "Bingo Backend" /MIN java -jar target/office-bingo-api-0.0.1-SNAPSHOT.jar --server.port=8080
cd ..

:: --- 3. INICIAR FRONTEND COM IP DINAMICO ---
cd front
echo Acesse em: http://%MY_IP%:3000
:: Passamos o IP para o Vite através de uma variável de ambiente temporária
set VITE_API_URL=http://%MY_IP%:8080
yarn preview --host --port 3000
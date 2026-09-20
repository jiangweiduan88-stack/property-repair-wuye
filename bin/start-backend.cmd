@echo off
setlocal
rem Check port 8080 before starting the backend to avoid duplicate service processes.
cd /d "%~dp0.."
title property-repair-backend
netstat -ano | findstr /R /C:":8080 .*LISTENING" >nul
if not errorlevel 1 (
    echo Backend is already running on port 8080. Startup skipped.
    exit /b 0
)
call "%~dp0run-backend.cmd"

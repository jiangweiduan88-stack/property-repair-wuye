@echo off
setlocal
rem Check port 80 before starting Vite to avoid duplicate frontend processes.
cd /d "%~dp0..\wuye-ui"
title property-repair-frontend
netstat -ano | findstr /R /C:":80 .*LISTENING" >nul
if not errorlevel 1 (
    echo Frontend is already running on port 80. Startup skipped.
    exit /b 0
)
"D:\chifan\Node\node.exe" "node_modules\vite\bin\vite.js" --host 0.0.0.0 --port 80

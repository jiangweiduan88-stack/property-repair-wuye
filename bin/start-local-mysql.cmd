@echo off
setlocal
rem Start MySQL on port 3333 with the project-local data directory.
set "MYSQLD=C:\Program Files\MySQL\MySQL Server 8.4\bin\mysqld.exe"
set "DATADIR=%~dp0..\.mysql\Data-runtime"
if not exist "%DATADIR%\ibdata1" set "DATADIR=%~dp0..\.mysql\Data"
title property-repair-mysql
netstat -ano | findstr /R /C:":3333 .*LISTENING" >nul
if not errorlevel 1 (
    echo MySQL is already running on port 3333. Startup skipped.
    exit /b 0
)
"%MYSQLD%" --datadir="%DATADIR%" --port=3333 --console

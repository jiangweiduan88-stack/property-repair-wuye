@echo off
setlocal
rem Start the local environment in dependency order: database, cache, backend, and frontend.
rem MySQL is managed by the PropertyRepairMySQL Windows service so it is not tied to a console window.
sc query PropertyRepairMySQL >nul 2>&1
if errorlevel 1 goto mysqlServiceMissing
sc query PropertyRepairMySQL | findstr /I "RUNNING" >nul
if errorlevel 1 sc start PropertyRepairMySQL >nul
call :waitPort 3333 30
if errorlevel 1 goto mysqlFailed

start "property-repair-redis" cmd /c "%~dp0start-local-redis.cmd"
call :waitPort 6379 20
if errorlevel 1 goto redisFailed

start "property-repair-backend" cmd /c "%~dp0start-backend.cmd"
call :waitPort 8080 60
if errorlevel 1 goto backendFailed

start "property-repair-frontend" cmd /c "%~dp0start-frontend.cmd"
call :waitPort 80 30
if errorlevel 1 goto frontendFailed

echo.
echo [OK] Property repair system started successfully.
echo [OK] URL: http://localhost/property/order
if /I not "%PROPERTY_REPAIR_SKIP_BROWSER%"=="1" start "" "http://localhost/property/order"
ping -n 6 127.0.0.1 >nul
exit /b 0

:waitPort
rem Poll a service port before starting the next dependency.
set "WAIT_PORT=%~1"
set /a "WAIT_SECONDS=%~2"
:waitLoop
netstat -ano | findstr /R /C:":%WAIT_PORT% .*LISTENING" >nul
if not errorlevel 1 exit /b 0
if %WAIT_SECONDS% LEQ 0 exit /b 1
set /a "WAIT_SECONDS-=1"
ping -n 2 127.0.0.1 >nul
goto waitLoop

:mysqlFailed
echo [ERROR] MySQL failed to start on port 3333.
goto startupFailed

:mysqlServiceMissing
echo [ERROR] PropertyRepairMySQL service is not installed. Run bin\install-property-repair-mysql-service.ps1 as administrator.
goto startupFailed

:redisFailed
echo [ERROR] Redis failed to start on port 6379.
goto startupFailed

:backendFailed
echo [ERROR] Backend failed to start on port 8080. Check logs\backend-service.log.
goto startupFailed

:frontendFailed
echo [ERROR] Frontend failed to start on port 80.

:startupFailed
echo Startup stopped. Press any key to close this window.
pause >nul
exit /b 1

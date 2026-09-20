@echo off
setlocal
rem Start the backend Jar from the project root and write startup details to the service log.
cd /d "%~dp0.."
if not exist "logs" mkdir "logs"
rem Run from a separate copy so Maven can rebuild the target Jar while the service is running.
if not exist "wuye-admin\target\wuye-admin.jar" (
    echo [%date% %time%] ERROR: build jar not found: wuye-admin\target\wuye-admin.jar>> "logs\backend-service.log"
    exit /b 1
)
if not exist "runtime" mkdir "runtime"
copy /Y "wuye-admin\target\wuye-admin.jar" "runtime\wuye-admin-runtime.jar" >nul
if errorlevel 1 (
    echo [%date% %time%] ERROR: failed to prepare runtime jar>> "logs\backend-service.log"
    exit /b 1
)
echo [%date% %time%] run-backend.cmd starting>> "logs\backend-service.log"
echo [%date% %time%] JAVA_EXE=D:\chifan\java17\bin\java.exe>> "logs\backend-service.log"
echo [%date% %time%] APP_JAR=runtime\wuye-admin-runtime.jar>> "logs\backend-service.log"
"D:\chifan\java17\bin\java.exe" -jar "runtime\wuye-admin-runtime.jar" >> "logs\backend-service.log" 2>&1
echo [%date% %time%] java exited with errorlevel %errorlevel%>> "logs\backend-service.log"

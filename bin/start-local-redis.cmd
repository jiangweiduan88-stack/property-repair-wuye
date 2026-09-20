@echo off
setlocal
rem Start local Redis on port 6379 for login sessions and application caches.
set "REDIS_SERVER=C:\Program Files\Redis\redis-server.exe"
title property-repair-redis
netstat -ano | findstr /R /C:":6379 .*LISTENING" >nul
if not errorlevel 1 (
    echo Redis is already running on port 6379. Startup skipped.
    exit /b 0
)
"%REDIS_SERVER%" --bind 127.0.0.1 --port 6379

@echo off
chcp 65001 >nul

rem 清理各后端模块的 target 目录，避免旧构建产物影响后续打包。
echo.
echo [信息] 正在清理后端构建产物。
echo.

cd /d "%~dp0.."
call ".tools\apache-maven-3.9.16\bin\mvn.cmd" clean

pause

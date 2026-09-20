@echo off
chcp 65001 >nul

rem 清理并打包全部后端模块，生成可直接运行的 wuye-admin.jar。
echo.
echo [信息] 正在打包物业报修系统后端。
echo.

cd /d "%~dp0.."
call ".tools\apache-maven-3.9.16\bin\mvn.cmd" clean package -Dmaven.test.skip=true

pause

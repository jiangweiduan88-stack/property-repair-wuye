@echo off
chcp 65001 >nul

rem 安装前端依赖并生成 node_modules，供本地开发和生产构建使用。
echo.
echo [信息] 正在安装物业报修系统前端依赖。
echo.

cd /d "%~dp0.."
npm install --registry=https://registry.npmmirror.com

pause

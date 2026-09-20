@echo off
chcp 65001 >nul

rem 启动 Vite 前端开发服务器，为本地页面调试提供热更新能力。
echo.
echo [信息] 正在启动物业报修系统前端开发服务。
echo.

cd /d "%~dp0.."
npm run dev

pause

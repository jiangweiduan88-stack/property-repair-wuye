@echo off
chcp 65001 >nul

rem 执行前端生产构建，在 dist 目录生成可部署的静态资源。
echo.
echo [信息] 正在构建物业报修系统前端。
echo.

cd /d "%~dp0.."
npm run build:prod

pause

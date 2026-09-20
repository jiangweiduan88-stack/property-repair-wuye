@echo off
chcp 65001 >nul

rem 从脚本目录定位后端构建产物，避免启动结果受当前工作目录影响。
echo.
echo [信息] 使用 Jar 包启动物业报修系统后端服务。
echo.

cd /d "%~dp0"
cd "..\wuye-admin\target"

rem 设置 Java 堆内存与元空间上限，控制服务运行时的资源占用。
set JAVA_OPTS=-Xms256m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m

rem 前台运行后端服务，便于直接查看启动日志和异常信息。
java %JAVA_OPTS% -jar wuye-admin.jar

pause

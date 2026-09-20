@echo off
chcp 65001 >nul

rem 指定需要管理的后端 Jar 包名称，供启动、停止和状态检查共同使用。
set AppName=wuye-admin.jar

rem 配置时区、内存和垃圾回收参数，保证服务按统一运行参数启动。
set "JVM_OPTS=-Dname=%AppName% -Duser.timezone=Asia/Shanghai -Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m -XX:+HeapDumpOnOutOfMemoryError -Xlog:gc* -XX:NewRatio=1 -XX:SurvivorRatio=30 -XX:+UseParallelGC"

echo.
echo   [1] 启动 %AppName%
echo   [2] 停止 %AppName%
echo   [3] 重启 %AppName%
echo   [4] 查看 %AppName% 状态
echo   [5] 退出
echo.

set /p ID=请输入操作编号:
if "%ID%"=="1" goto start
if "%ID%"=="2" goto stop
if "%ID%"=="3" goto restart
if "%ID%"=="4" goto status
if "%ID%"=="5" exit /b 0
echo 输入无效，请重新运行脚本。
pause
exit /b 1

:start
rem 使用 JDK 的 jps 命令查找目标进程，防止同一服务被重复启动。
set pid=
set image_name=
for /f "usebackq tokens=1-2" %%a in (`jps -l ^| findstr /i "%AppName%"`) do (
    set pid=%%a
    set image_name=%%b
)
if defined pid (
    echo %AppName% 已在运行，进程编号为 %pid%。
    pause
    goto:eof
)

rem 使用后台 Java 进程启动服务，关闭脚本窗口后服务仍可继续运行。
start "物业报修系统" javaw %JVM_OPTS% -jar %AppName%
echo 正在启动 %AppName%...
echo %AppName% 启动命令已执行。
goto:eof

:stop
rem 根据 Jar 包名称定位进程并结束服务，避免误关闭其他 Java 进程。
set pid=
set image_name=
for /f "usebackq tokens=1-2" %%a in (`jps -l ^| findstr /i "%AppName%"`) do (
    set pid=%%a
    set image_name=%%b
)
if not defined pid (
    echo %AppName% 当前未运行。
) else (
    echo 正在停止 %image_name%，进程编号为 %pid%...
    taskkill /f /pid %pid%
)
goto:eof

:restart
rem 先停止现有进程再重新启动，确保新构建包和配置能够生效。
call :stop
call :start
goto:eof

:status
rem 查询目标 Jar 是否存在于当前 Java 进程列表并输出运行状态。
set pid=
set image_name=
for /f "usebackq tokens=1-2" %%a in (`jps -l ^| findstr /i "%AppName%"`) do (
    set pid=%%a
    set image_name=%%b
)
if not defined pid (
    echo %AppName% 当前未运行。
) else (
    echo %image_name% 正在运行，进程编号为 %pid%。
)
goto:eof

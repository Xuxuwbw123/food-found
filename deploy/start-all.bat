@echo off
chcp 65001 >nul
title 生鲜农产品溯源电商平台 - 启动中

echo ==========================================
echo   生鲜农产品溯源电商平台 - 启动中...
echo ==========================================
echo.

:: 检查Java环境
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未找到Java环境，请先安装JDK
    pause
    exit /b 1
)

:: 检查Node环境
node -v >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未找到Node.js环境，请先安装Node.js
    pause
    exit /b 1
)

:: 从 config.json 读取数据库配置
for /f "tokens=*" %%i in ('node -e "var c=require('./config.json'); var d=c.database; console.log('jdbc:mysql://'+d.host+':'+d.port+'/'+d.name+'?useUnicode=true^&characterEncoding=utf-8^&serverTimezone=Asia/Shanghai')"') do set DB_URL=%%i
set JAVA_OPTS=-Xmx512m -Xms256m -Djava.awt.headless=true

echo [1/4] 启动 溯源+农户端 (端口 8080)...
start "溯源+农户端-8080" cmd /k "java %JAVA_OPTS% -jar fresh-trace-traceability-1.0.0.jar --spring.datasource.url=%DB_URL%"
echo       PID: 已启动

echo.
echo [2/4] 启动 管理员后端 (端口 8083)...
start "管理员后端-8083" cmd /k "java %JAVA_OPTS% -jar fresh-trace-admin-1.0.0.jar --server.port=8083 --spring.datasource.url=%DB_URL%"
echo       PID: 已启动

echo.
echo [3/4] 启动 用户端后端 (端口 8082)...
start "用户端后端-8082" cmd /k "java %JAVA_OPTS% -jar foodfound-1.0-SNAPSHOT.jar --spring.datasource.url=%DB_URL%"
echo       PID: 已启动

echo.
echo [4/4] 启动 前端融合服务 (端口 8088)...
start "前端融合服务-8088" cmd /k "node server.js"
echo       PID: 已启动

echo.
echo ==========================================
echo   等待启动完成（约30秒）...
echo ==========================================
echo.

:: 等待30秒
timeout /t 30 /nobreak >nul

echo   检查服务状态：
echo.

:: 检查8080
curl -s -o nul -w "%%{http_code}" http://localhost:8080/api/trace/list 2>nul | findstr "200" >nul
if %errorlevel% equ 0 (
    echo   [OK] 溯源+农户端   http://localhost:8080  运行正常
) else (
    echo   [!!] 溯源+农户端   http://localhost:8080  启动中或失败，请查看窗口
)

:: 检查8083
curl -s -o nul -w "%%{http_code}" http://localhost:8083/admin/statistics/home 2>nul | findstr "200" >nul
if %errorlevel% equ 0 (
    echo   [OK] 管理员后端    http://localhost:8083  运行正常
) else (
    echo   [!!] 管理员后端    http://localhost:8083  启动中或失败，请查看窗口
)

:: 检查8082
curl -s -o nul -w "%%{http_code}" http://localhost:8082/home 2>nul | findstr "200" >nul
if %errorlevel% equ 0 (
    echo   [OK] 用户端后端    http://localhost:8082  运行正常
) else (
    echo   [!!] 用户端后端    http://localhost:8082  启动中或失败，请查看窗口
)

:: 检查前端
curl -s -o nul -w "%%{http_code}" http://localhost:8088/ 2>nul | findstr "200" >nul
if %errorlevel% equ 0 (
    echo   [OK] 前端融合服务  http://localhost:8088     运行正常
) else (
    echo   [!!] 前端融合服务  http://localhost:8088     启动中或失败，请查看窗口
)

echo.
echo ==========================================
echo   四个服务已在独立窗口中运行
echo.
echo   访问地址: http://localhost:8088   (统一入口)
echo   管理后台: http://localhost:8088/admin/dashboard
echo   溯源查询: http://localhost:8088/trace
echo.
echo   关闭对应窗口即可停止该服务
echo ==========================================
echo.
pause

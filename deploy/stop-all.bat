@echo off
chcp 65001 >nul
title 生鲜农产品溯源电商平台 - 停止服务

echo ==========================================
echo   正在停止所有服务...
echo ==========================================
echo.

taskkill /FI "WINDOWTITLE eq 溯源+农户端-8080*" /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq 管理员后端-8083*" /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq 用户端后端-8082*" /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq 前端融合服务-8088*" /F >nul 2>&1

:: 也通过端口杀进程
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8080 "') do taskkill /PID %%a /F >nul 2>&1
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8083 "') do taskkill /PID %%a /F >nul 2>&1
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8082 "') do taskkill /PID %%a /F >nul 2>&1
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8088 "') do taskkill /PID %%a /F >nul 2>&1

echo   [OK] 所有服务已停止
echo.
pause

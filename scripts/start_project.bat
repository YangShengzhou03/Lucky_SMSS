@echo off
echo ========================================
echo    Lucky SMS 项目启动脚本
echo ========================================

REM 检查Java环境
echo 检查Java环境...
java -version >nul 2>&1
if errorlevel 1 (
    echo Java未安装，请先安装JDK 17
    echo 下载地址: https://adoptium.net/
    pause
    exit /b 1
)

REM 检查Node.js环境
echo 检查Node.js环境...
node --version >nul 2>&1
if errorlevel 1 (
    echo Node.js未安装，请先安装Node.js
    echo 下载地址: https://nodejs.org/
    pause
    exit /b 1
)

REM 检查Redis是否运行
echo 检查Redis服务...
redis-cli ping >nul 2>&1
if errorlevel 1 (
    echo Redis未运行，正在启动Redis...
    call scripts\start_redis.bat
    if errorlevel 1 (
        echo Redis启动失败，请手动启动Redis
        pause
        exit /b 1
    )
)

REM 启动后端服务
echo 启动后端服务...
cd backend
start "Lucky SMS Backend" mvn spring-boot:run
cd ..

REM 等待后端启动
echo 等待后端服务启动...
timeout /t 10 /nobreak >nul

REM 启动前端服务
echo 启动前端服务...
cd frontend
start "Lucky SMS Frontend" npm run serve
cd ..

echo ========================================
echo    项目启动完成！
echo ========================================
echo 后端服务: http://localhost:8081
echo 前端服务: http://localhost:8080
echo Redis服务: localhost:6379
echo ========================================
echo 按任意键退出...
pause >nul
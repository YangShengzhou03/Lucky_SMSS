@echo off
echo 正在启动Redis服务器...

REM 检查Redis是否已安装
where redis-server >nul 2>&1
if errorlevel 1 (
    echo Redis未安装，请先安装Redis
    echo 下载地址: https://redis.io/download/
    pause
    exit /b 1
)

REM 启动Redis服务器
echo 启动Redis服务...
start "Redis Server" redis-server

REM 等待服务启动
timeout /t 3 /nobreak >nul

REM 测试Redis连接
echo 测试Redis连接...
redis-cli ping >nul 2>&1
if errorlevel 1 (
    echo Redis启动失败，请检查配置
    pause
    exit /b 1
)

echo Redis服务器启动成功！
echo Redis运行在: localhost:6379
pause
@echo off
echo 正在啟動 MTR 輕鐵時刻表小組件...
echo.

REM Check if Node.js is installed
where node >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo 未找到 Node.js，將使用預設瀏覽器開啟...
    start index.html
    exit /b 0
)

REM Check if http-server is available
where http-server >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo 安裝 http-server...
    npm install -g http-server
)

REM Start local server
echo 啟動本地伺服器 http://localhost:8080
start http://localhost:8080
http-server -p 8080 -o

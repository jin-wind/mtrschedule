@echo off
echo ========================================
echo MTR 輕鐵時刻表桌面小組件 - 安裝
echo ========================================
echo.

REM 檢查 Node.js
where node >nul 2>nul
if errorlevel 1 (
    echo [錯誤] 未找到 Node.js
    echo.
    echo 請先安裝 Node.js: https://nodejs.org/
    echo.
    pause
    exit /b 1
)

echo [1/2] 檢測到 Node.js
node --version
echo.

echo [2/2] 安裝依賴...
call npm install

if errorlevel 1 (
    echo.
    echo [錯誤] 安裝失敗
    pause
    exit /b 1
)

echo.
echo ========================================
echo 安裝完成！
echo ========================================
echo.
echo 啟動方式：
echo   1. 雙擊 start.bat
echo   2. 或執行: npm start
echo.
pause

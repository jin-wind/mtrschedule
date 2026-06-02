@echo off
echo 啟動 MTR 輕鐵時刻表桌面小組件...
echo.

REM 檢查是否已安裝依賴
if not exist "node_modules\" (
    echo 首次運行，正在安裝依賴...
    call npm install
    if errorlevel 1 (
        echo.
        echo 安裝失敗，請手動執行: npm install
        pause
        exit /b 1
    )
)

REM 啟動應用
npm start

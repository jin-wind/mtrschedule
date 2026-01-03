@echo off
REM Windows launcher script for MTR Schedule Widget

echo Starting MTR Light Rail Schedule Widget...
echo.

REM Check if Python is installed
python --version >nul 2>&1
if errorlevel 1 (
    echo Error: Python is not installed or not in PATH
    echo Please install Python 3.8 or higher from https://www.python.org/
    pause
    exit /b 1
)

REM Check if dependencies are installed
python -c "import aiohttp" >nul 2>&1
if errorlevel 1 (
    echo Installing dependencies...
    pip install -r requirements.txt
    if errorlevel 1 (
        echo Error: Failed to install dependencies
        pause
        exit /b 1
    )
)

REM Run the application
python main.py

if errorlevel 1 (
    echo.
    echo Application exited with an error
    pause
)

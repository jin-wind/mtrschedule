#!/bin/bash
# Linux/Mac launcher script for MTR Schedule Widget

echo "Starting MTR Light Rail Schedule Widget..."
echo ""

# Check if Python is installed
if ! command -v python3 &> /dev/null; then
    echo "Error: Python 3 is not installed"
    echo "Please install Python 3.8 or higher"
    exit 1
fi

# Check if dependencies are installed
python3 -c "import aiohttp" 2>/dev/null
if [ $? -ne 0 ]; then
    echo "Installing dependencies..."
    pip3 install -r requirements.txt
    if [ $? -ne 0 ]; then
        echo "Error: Failed to install dependencies"
        exit 1
    fi
fi

# Run the application
python3 main.py

if [ $? -ne 0 ]; then
    echo ""
    echo "Application exited with an error"
    read -p "Press Enter to exit..."
fi

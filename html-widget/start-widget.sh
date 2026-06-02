#!/bin/bash

echo "正在啟動 MTR 輕鐵時刻表小組件..."
echo ""

# Check if Node.js is installed
if ! command -v node &> /dev/null; then
    echo "未找到 Node.js，將使用預設瀏覽器開啟..."
    
    # Try to open with default browser
    if command -v xdg-open &> /dev/null; then
        xdg-open index.html
    elif command -v open &> /dev/null; then
        open index.html
    else
        echo "請手動在瀏覽器中開啟 index.html"
    fi
    exit 0
fi

# Check if http-server is available
if ! command -v http-server &> /dev/null; then
    echo "安裝 http-server..."
    npm install -g http-server
fi

# Start local server
echo "啟動本地伺服器 http://localhost:8080"
http-server -p 8080 -o

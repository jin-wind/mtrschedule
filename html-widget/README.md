# MTR 輕鐵時刻表 - HTML 桌面小組件

一個美觀、輕量級的 HTML/CSS/JavaScript 桌面小組件，顯示香港 MTR 輕鐵即時到站資訊。

## ✨ 特點

- 🎨 **現代化 UI** - 漸變背景、毛玻璃效果、流暢動畫
- 🚉 **雙月台顯示** - 並排顯示月台 1 和月台 2 的列車
- 🔄 **自動刷新** - 每 30 秒自動更新時刻表
- 📌 **置頂功能** - 可固定在桌面上方
- 📱 **響應式設計** - 自適應不同螢幕尺寸
- 🌐 **跨平台** - Windows、Linux、macOS 均可使用

## 🚀 快速開始

### 方法 1: 直接在瀏覽器中開啟

1. 雙擊 `index.html` 檔案
2. 或在瀏覽器中開啟 `index.html`

就這麼簡單！無需安裝任何東西。

### 方法 2: 作為桌面應用（推薦）

使用 Electron 包裝器將其轉換為真正的桌面應用：

1. 確保已安裝 Node.js
2. 執行啟動腳本：
   ```bash
   # Windows
   start-widget.bat
   
   # Linux/Mac
   ./start-widget.sh
   ```

### 方法 3: 使用本地伺服器

如果遇到 CORS 問題，可使用本地伺服器：

```bash
# 使用 Python
python -m http.server 8000

# 使用 Node.js (需要先安裝 http-server)
npx http-server
```

然後在瀏覽器中訪問 `http://localhost:8000`

## 📋 功能說明

### 車站選擇
- 下拉選單包含所有 68 個輕鐵站點
- 選擇站點後立即更新時刻表

### 時刻表顯示
每個列車卡片顯示：
- 🚊 路線號碼（大字體、彩色）
- 📍 目的地（中文）
- ⏰ 到站時間
- 🚋 車廂類型（單卡/雙卡）

### 控制按鈕
- 🔄 **刷新按鈕** - 手動更新時刻表
- 📌 **置頂按鈕** - 固定視窗（在桌面應用中有效）

## 🎨 UI 設計

- **配色方案**：
  - 月台 1：紅色主題 (#ef4444)
  - 月台 2：藍色主題 (#3b82f6)
  - 背景：紫色漸變

- **視覺效果**：
  - 毛玻璃背景（backdrop-filter）
  - 平滑動畫過渡
  - 卡片懸停效果
  - 載入脈衝動畫

## 📁 文件結構

```
html-widget/
├── index.html       # 主 HTML 檔案
├── styles.css       # 樣式表
├── app.js           # 主要邏輯
├── stations.js      # 車站資料
├── README.md        # 本文件
├── start-widget.bat # Windows 啟動腳本
└── start-widget.sh  # Linux/Mac 啟動腳本
```

## 🔧 技術細節

### API
使用香港政府開放資料 API：
```
https://rt.data.gov.hk/v1/transport/mtr/lrt/getSchedule?station_id={id}
```

### 自動刷新
- 間隔：30 秒
- 使用 `setInterval` 實現
- 可手動刷新

### 瀏覽器兼容性
- Chrome/Edge ✅
- Firefox ✅
- Safari ✅
- Opera ✅

需要支援：
- ES6+ JavaScript
- CSS Grid
- Flexbox
- Backdrop Filter（毛玻璃效果）

## 🌟 進階使用

### 自訂樣式
編輯 `styles.css` 檔案來自訂外觀：
- 修改配色方案
- 調整卡片大小
- 更改字體

### 修改刷新間隔
在 `app.js` 中修改：
```javascript
const REFRESH_INTERVAL = 30000; // 毫秒
```

### 添加更多功能
可以輕鬆擴展：
- 通知功能
- 收藏車站
- 歷史記錄
- 多車站同時監控

## 🐛 故障排除

**問題：無法載入資料**
- 檢查網路連接
- 確認 API 可訪問
- 查看瀏覽器控制台錯誤訊息

**問題：CORS 錯誤**
- 使用本地伺服器運行
- 或使用瀏覽器擴展禁用 CORS（僅用於開發）

**問題：置頂功能無效**
- 在純瀏覽器環境中，置頂功能受限
- 建議使用 Electron 包裝器

## 📱 移動端支持

雖然主要設計為桌面小組件，但也支援移動設備：
- 響應式佈局
- 觸控友好的按鈕
- 在小螢幕上自動切換為單欄佈局

## 📄 授權

此專案遵循原始儲存庫的授權條款。

## 🙏 鳴謝

- MTR API 由香港政府提供
- 車站資料來自 mtrschedule Android 應用

---

**享受使用！如有問題或建議，歡迎反饋。**

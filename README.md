# mtrschedule

一款中學生打造嘅輕鐵時刻表 App，提供即時班次、路線模式、站點收藏等功能。

## 🆕 桌面小組件 (Desktop Widgets)

本項目現在提供桌面小組件實現！

### 方案 A: Electron 桌面小組件 ⭐ 推薦

**真正可以固定在桌面的小組件！** 使用 Electron 構建的獨立桌面應用程式。

- 📍 **路徑**: `desktop-widget/`
- 🚀 **快速開始**: 
  ```bash
  cd desktop-widget
  npm install
  npm start
  ```
- ✨ **特點**: 
  - ✅ 真正的桌面應用程式（不是瀏覽器頁面）
  - ✅ 可固定在桌面（始終置頂功能）
  - ✅ 無邊框視窗，可拖動
  - ✅ 記憶視窗位置和大小
  - ✅ 獨立運行，不依賴瀏覽器
- 🌐 **跨平台**: Windows、Linux、macOS
- ✅ **狀態**: 完全可用，真正的桌面小組件
- 📖 **詳細文檔**: 查看 [`desktop-widget/README.md`](desktop-widget/README.md)

### 方案 B: HTML 網頁版本

輕量級的 HTML/CSS/JavaScript 網頁版本（需要在瀏覽器中開啟）。

- 📍 **路徑**: `html-widget/`
- 🚀 **快速開始**: 雙擊 `html-widget/index.html` 即可使用
- 🎨 **特點**: 現代化 UI、毛玻璃效果、流暢動畫
- ⚠️ **限制**: 需要瀏覽器，無法真正固定在桌面
- 📖 **詳細文檔**: 查看 [`html-widget/README.md`](html-widget/README.md)

### 方案 C: Windows 11 原生小組件 (概念驗證)

基於 Windows App SDK 的概念驗證專案。

- 📍 **路徑**: `MTRWidget/`
- 🔧 **技術**: C# + Windows App SDK
- ⚠️ **狀態**: Windows Widgets API 尚未公開，無法編譯
- 💡 **用途**: 為未來 API 發布做準備的概念代碼
- 📖 **詳細文檔**: 查看 [`MTRWidget/README.md`](MTRWidget/README.md)

### ~~方案 D: Python tkinter 版本~~ (已棄用)

- 📍 **路徑**: `windows-widget/`
- ⚠️ **狀態**: 已被更好的 Electron 版本取代

---

## 功能特色
- **卡片模式**：顯示各個車站下一班及後備班次，支援骨架載入效果。
- **路線模式**：按路線瀏覽所有沿線車站，支援正逆方向切換及列車卡片動畫。
- **站點詳情**：顯示站名、代號、列車車廂數、預計到達時間及狀態。
- **系統主題同步**：自動跟隨裝置光暗模式，提供日夜版色盤。
- **語言支援**：繁體中文為主，可經設定頁面切換語言選項。
- **離線快取**：曾查詢過嘅站點班次會暫存，減少重複網絡請求。

## 系統需求
- Android Studio Ladybug 或更新版本
- Android SDK 34（最少支援 Android 8.0）
- Gradle 8.6（專案內置 Wrapper）
- Kotlin 1.9.x

## 建置步驟
1. Clone 呢個 repository：
	```bash
	git clone https://github.com/jin-wind/mtrschedule.git
	```
2. 用 Android Studio 開啟 `mtrschedule` 專案。
3. 讓 IDE 同步 Gradle，等待依賴下載完成。
4. 選擇一部裝置或模擬器，執行 `Run > Run 'app'` 或使用命令：
	```powershell
	.\gradlew.bat assembleDebug
	```

## 使用指南
- 進入 App 後預設顯示卡片模式，可下拉刷新獲取最新班次。
- 點擊側邊抽屜或工具列可切換至路線模式，再按路線號碼查看各站資料。
- 在路線模式中重覆點擊同一路線號可切換正逆方向，頂部會顯示「起點 → 終點」。
- 搜尋欄可快速定位車站；收藏站點會以粉紅色標示。
- 設定頁面可調整自動刷新、語言等偏好設定。

## 專案結構概覽
- `app/src/main/java/com/jinwind/mtrschedule/`：Kotlin 程式碼（ViewModel、Adapter、資料來源）。
- `app/src/main/res/layout/`：UI 版面資源，包含卡片、路線、設定等畫面。
- `app/src/main/res/drawable/`：自訂背景、圖示與動畫資源。
- `app/src/main/res/values/`：色盤、字串、樣式與主題。
- `app/src/main/res/mipmap-*/`：應用圖示（目前以提供嘅照片為主題）。
- `windows-widget/`：**Python 桌面小工具**（新增），支援 Windows/Linux/macOS。

## 測試
- 單元測試入口：`app/src/test/java`。
- 若要運行所有測試，可使用：
  ```powershell
  .\gradlew.bat testDebug
  ```

## 注意事項
- 本專案使用公開 API 取得輕鐵車務資料，如 API 變動需相應更新。
- 目前部分 Android 原生 API（例如 `startActivityForResult`）已被標示為棄用，後續會逐步改用最新方案。

## 授權
此專案依原作者授權條款使用。若需引用或二次開發，請保留原始出處並遵守相關條款。

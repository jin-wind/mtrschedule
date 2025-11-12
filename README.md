# mtrschedule

一款中學生打造嘅輕鐵時刻表 App，提供即時班次、路線模式、站點收藏等功能。

## 🆕 Windows Desktop Widget (Python)

本項目新增了 **Windows 桌面小工具**，使用 Python 開發，可在 Windows、Linux 和 macOS 上運行！

- 📍 **路徑**: `windows-widget/`
- 🚀 **快速開始**: 
  ```bash
  cd windows-widget
  pip install -r requirements.txt
  python main.py
  ```
- 📖 **詳細文檔**: 查看 [`windows-widget/README.md`](windows-widget/README.md)

**功能特點**：雙平台顯示、自動刷新、68個輕鐵站點、MVVM架構、異步API調用

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

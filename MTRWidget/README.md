# MTR 輕鐵時刻表桌面小組件

這是一個顯示香港 MTR 輕鐵即時到站資訊的 Windows 桌面小組件。

## ⚠️ 重要提示

**Windows 11 原生小組件 API 目前尚未完全公開。** 

`Microsoft.Windows.Widgets` 套件在公開 NuGet 源中不可用，此方案僅為概念驗證和未來準備。

### 📌 推薦使用方案

**請使用 `html-widget/` 目錄中的 HTML 桌面小組件！**

HTML 版本：
- ✅ **立即可用** - 無需編譯，雙擊即可使用
- ✅ **跨平台** - Windows、Linux、macOS
- ✅ **美觀 UI** - 現代化設計，紫色漸變 + 毛玻璃效果
- ✅ **功能完整** - 所有需求功能都已實現

查看 `../html-widget/README.md` 了解詳情。

---

## 🎯 關於此 C# 專案

這是一個基於 Windows App SDK 的概念驗證專案，展示如何創建 Windows 11 原生小組件。

### 系統要求

- Windows 11（版本 22H2 或更高）
- .NET 8.0 或更高版本
- Windows App SDK 1.6+

### 當前狀態

⚠️ **無法編譯** - Windows Widgets API 尚未公開發布

Windows Widgets 功能目前處於以下狀態之一：
1. 僅限內部/預覽版本
2. 需要特殊的 SDK 或註冊
3. API 可能在未來的 Windows App SDK 更新中發布

### 專案結構

```
MTRWidget/
├── Program.cs              # 主程式入口（概念代碼）
├── MTRWidgetProvider.cs    # 小組件提供者（概念代碼）
├── Models/
│   └── Models.cs           # 資料模型
├── Data/
│   └── StationData.cs      # 車站資料（68 個站點）
├── Services/
│   └── MTRApiService.cs    # MTR API 服務
├── Widgets/
│   ├── MTRWidget.json      # Adaptive Card 模板
│   └── WidgetDefinition.json # 小組件定義
└── MTRWidget.csproj        # 專案檔案
```

### 如果 API 未來可用

當 Windows Widgets API 公開後，此專案可以：

1. 更新 NuGet 套件到正確版本
2. 編譯專案：
   ```bash
   dotnet restore
   dotnet build
   ```
3. 打包為 MSIX
4. 安裝到 Windows 11
5. 在小組件面板中添加

### 設計功能

此專案設計實現以下功能（當 API 可用時）：

- ✅ 固定在 Windows 11 小組件面板
- ✅ 雙月台並排顯示（月台 1 和月台 2）
- ✅ 使用 Adaptive Cards 的現代化 UI
- ✅ 自動刷新數據
- ✅ 支援淺色/深色主題
- ✅ 68 個輕鐵站點選擇

## 📱 API 整合

使用香港政府開放資料 API：
```
https://rt.data.gov.hk/v1/transport/mtr/lrt/getSchedule?station_id={id}
```

此專案已包含完整的 API 客戶端實現，可以在 API 可用時直接使用。

## 📖 參考文檔

- [Windows Widgets 開發指南](https://learn.microsoft.com/zh-cn/windows/apps/develop/widgets/widget-providers)
- [Adaptive Cards](https://adaptivecards.io/)
- [Windows App SDK](https://learn.microsoft.com/windows/apps/windows-app-sdk/)
- [Windows App SDK Release Notes](https://learn.microsoft.com/windows/apps/windows-app-sdk/release-notes/)

## 💡 替代方案

### 立即可用的解決方案

**HTML 桌面小組件** (`../html-widget/`)
- 無需編譯
- 雙擊 `index.html` 即可使用
- 美觀的現代化 UI
- 完整功能實現

### 未來可能的方案

1. **等待 Windows Widgets API 公開發布**
   - 關注 Windows App SDK 更新
   - 當 API 可用時更新此專案

2. **使用 WinUI 3 桌面應用**
   - 創建完整的桌面應用程式
   - 使用 WinUI 3 實現相同的 UI

3. **使用 Progressive Web App (PWA)**
   - 基於現有的 HTML 版本
   - 添加 PWA 功能以獲得更好的整合

## 📝 授權

此專案遵循原始儲存庫的授權條款。

---

**再次提醒：請使用 `html-widget/` 中的 HTML 版本，它已經完全可用且功能完整！**

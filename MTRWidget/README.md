# MTR 輕鐵時刻表桌面小組件

這是一個顯示香港 MTR 輕鐵即時到站資訊的 Windows 桌面小組件。

## 🎯 兩種實現方式

### 方案 A: Windows 11 原生小組件（推薦）

使用 Windows 11 的小組件 API，可固定在小組件面板中。

**系統要求:**
- Windows 11（版本 22H2 或更高）
- .NET 8.0 或更高版本
- Windows App SDK 1.6+

**安裝步驟:**

1. 安裝依賴：
```bash
dotnet restore
```

2. 建置專案：
```bash
dotnet build
```

3. 執行小組件提供者：
```bash
dotnet run
```

4. 在 Windows 11 的小組件面板中添加「MTR 輕鐵時刻表」小組件

**功能特點:**
- ✅ 固定在 Windows 11 小組件面板
- ✅ 雙月台並排顯示（月台 1 和月台 2）
- ✅ 自動刷新
- ✅ 現代化 Adaptive Card UI
- ✅ 支援淺色/深色主題
- ✅ 68 個輕鐵站點選擇

**注意事項:**
- 需要在 Windows 開發者模式下測試
- 需要 MSIX 打包才能在非開發環境使用
- 小組件必須通過 Microsoft Store 或旁載安裝

---

### 方案 B: HTML 桌面小組件（立即可用）

使用 HTML/CSS/JavaScript 創建的輕量級桌面小組件，可作為獨立視窗使用。

**查看:** `html-widget/` 目錄

**優點:**
- ✅ 無需安裝，雙擊即可運行
- ✅ 跨平台（Windows、Linux、macOS）
- ✅ 可固定在桌面任意位置
- ✅ 始終置頂選項
- ✅ 美觀的現代 UI

---

## 📱 功能

- 🚉 **雙月台顯示**: 並排顯示月台 1 和月台 2 的列車資訊
- 🚊 **詳細資訊**: 路線號、目的地、到站時間、車廂類型（單卡/雙卡）
- 🔄 **自動刷新**: 每 30 秒自動更新
- 🎨 **現代化 UI**: 清晰、美觀的介面設計
- 🏁 **68 個站點**: 支援所有 MTR 輕鐵站點

## 🔧 開發

### 專案結構

```
MTRWidget/
├── Program.cs              # 主程式入口
├── MTRWidgetProvider.cs    # 小組件提供者
├── Models/
│   └── Models.cs           # 資料模型
├── Data/
│   └── StationData.cs      # 車站資料
├── Services/
│   └── MTRApiService.cs    # MTR API 服務
├── Widgets/
│   ├── MTRWidget.json      # 小組件模板
│   └── WidgetDefinition.json # 小組件定義
└── MTRWidget.csproj        # 專案檔案
```

### API

使用香港政府開放資料 API：
```
https://rt.data.gov.hk/v1/transport/mtr/lrt/getSchedule?station_id={id}
```

## 📖 參考文檔

- [Windows Widgets 開發指南](https://learn.microsoft.com/zh-cn/windows/apps/develop/widgets/widget-providers)
- [Adaptive Cards](https://adaptivecards.io/)
- [Windows App SDK](https://learn.microsoft.com/windows/apps/windows-app-sdk/)

## ⚠️ 限制

- Windows 11 小組件目前僅支援 Windows 11
- 需要 MSIX 打包才能發布到 Microsoft Store
- 開發階段需要開啟 Windows 開發者模式

## 📝 授權

此專案遵循原始儲存庫的授權條款。

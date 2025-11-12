const { app, BrowserWindow, ipcMain, screen } = require('electron');
const path = require('path');

let mainWindow;

// 配置選項
const config = {
  alwaysOnTop: false,
  bounds: null
};

function createWindow() {
  // 獲取主屏幕尺寸
  const primaryDisplay = screen.getPrimaryDisplay();
  const { width: screenWidth, height: screenHeight } = primaryDisplay.workAreaSize;
  
  // 默認位置：右上角
  const windowWidth = 750;
  const windowHeight = 600;
  const defaultX = screenWidth - windowWidth - 20;
  const defaultY = 20;

  // 創建瀏覽器窗口
  mainWindow = new BrowserWindow({
    width: config.bounds?.width || windowWidth,
    height: config.bounds?.height || windowHeight,
    x: config.bounds?.x || defaultX,
    y: config.bounds?.y || defaultY,
    frame: false, // 無邊框
    transparent: false,
    resizable: true,
    alwaysOnTop: config.alwaysOnTop,
    skipTaskbar: false,
    backgroundColor: '#667eea',
    webPreferences: {
      nodeIntegration: false,
      contextIsolation: true,
      preload: path.join(__dirname, 'preload.js')
    }
  });

  // 加載 HTML
  mainWindow.loadFile(path.join(__dirname, 'renderer', 'index.html'));

  // 處理窗口關閉
  mainWindow.on('closed', function () {
    mainWindow = null;
  });

  // 保存窗口位置和大小
  mainWindow.on('close', () => {
    config.bounds = mainWindow.getBounds();
  });
}

// 當 Electron 完成初始化時創建窗口
app.whenReady().then(createWindow);

// 當所有窗口關閉時退出應用
app.on('window-all-closed', function () {
  if (process.platform !== 'darwin') {
    app.quit();
  }
});

app.on('activate', function () {
  if (mainWindow === null) {
    createWindow();
  }
});

// IPC 處理程序
ipcMain.handle('toggle-always-on-top', () => {
  config.alwaysOnTop = !config.alwaysOnTop;
  mainWindow.setAlwaysOnTop(config.alwaysOnTop);
  return config.alwaysOnTop;
});

ipcMain.handle('minimize-window', () => {
  mainWindow.minimize();
});

ipcMain.handle('close-window', () => {
  mainWindow.close();
});

ipcMain.handle('get-always-on-top', () => {
  return config.alwaysOnTop;
});

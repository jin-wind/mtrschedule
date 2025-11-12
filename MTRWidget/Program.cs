using Microsoft.UI.Dispatching;
using Microsoft.Windows.Widgets.Providers;

namespace MTRWidget;

public class Program
{
    [STAThread]
    static void Main(string[] args)
    {
        WinRT.ComWrappersSupport.InitializeComWrappers();
        
        var widgetProvider = new MTRWidgetProvider();
        var manager = WidgetManager.GetDefault();
        
        // Register widget provider
        manager.RegisterWidgetProvider(widgetProvider);
        
        // Keep the application running
        var dispatcherQueue = DispatcherQueue.GetForCurrentThread();
        var dispatcherQueueController = DispatcherQueueController.CreateOnCurrentThread();
        
        Console.WriteLine("MTR Widget Provider is running...");
        Console.WriteLine("Press Ctrl+C to exit.");
        
        dispatcherQueueController.DispatcherQueueShutdownStarting += (s, e) =>
        {
            manager.UnregisterWidgetProvider(widgetProvider);
        };
        
        // Message pump
        MSG msg;
        while (GetMessage(out msg, IntPtr.Zero, 0, 0))
        {
            TranslateMessage(ref msg);
            DispatchMessage(ref msg);
        }
    }
    
    [System.Runtime.InteropServices.DllImport("user32.dll")]
    static extern bool GetMessage(out MSG lpMsg, IntPtr hWnd, uint wMsgFilterMin, uint wMsgFilterMax);
    
    [System.Runtime.InteropServices.DllImport("user32.dll")]
    static extern bool TranslateMessage([In] ref MSG lpMsg);
    
    [System.Runtime.InteropServices.DllImport("user32.dll")]
    static extern IntPtr DispatchMessage([In] ref MSG lpmsg);
    
    [System.Runtime.InteropServices.StructLayout(System.Runtime.InteropServices.LayoutKind.Sequential)]
    public struct MSG
    {
        public IntPtr hwnd;
        public uint message;
        public IntPtr wParam;
        public IntPtr lParam;
        public uint time;
        public POINT pt;
    }
    
    [System.Runtime.InteropServices.StructLayout(System.Runtime.InteropServices.LayoutKind.Sequential)]
    public struct POINT
    {
        public int X;
        public int Y;
    }
}

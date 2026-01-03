"""
MTR Light Rail Schedule Widget - Main Application
Windows Desktop Widget using tkinter
"""
import asyncio
import tkinter as tk
from tkinter import ttk, messagebox
import threading
from viewmodel import ScheduleViewModel
from stations import STATIONS


class MTRScheduleWidget:
    """Main application window for MTR Schedule Widget"""
    
    def __init__(self, root):
        self.root = root
        self.root.title("MTR Light Rail Schedule Widget")
        self.root.geometry("900x600")
        self.root.configure(bg="#f0f0f0")
        
        # ViewModel
        self.viewmodel = ScheduleViewModel()
        self.viewmodel.on_data_changed = self.on_data_changed
        self.viewmodel.on_loading_changed = self.on_loading_changed
        self.viewmodel.on_error = self.on_error
        
        # Auto-refresh timer
        self.refresh_timer = None
        self.auto_refresh_interval = 30000  # 30 seconds in milliseconds
        
        # Setup UI
        self.setup_ui()
        
        # Load initial station (first station by default)
        if STATIONS:
            self.station_combo.set(str(STATIONS[0]))
            self.load_schedule()
    
    def setup_ui(self):
        """Setup the user interface"""
        # Top bar with station selector and last updated
        top_frame = tk.Frame(self.root, bg="#2c3e50", height=60)
        top_frame.pack(fill=tk.X, padx=0, pady=0)
        top_frame.pack_propagate(False)
        
        # Station selector
        station_label = tk.Label(top_frame, text="Station:", bg="#2c3e50", fg="white", font=("Arial", 12, "bold"))
        station_label.pack(side=tk.LEFT, padx=10, pady=15)
        
        # Station dropdown
        station_names = [str(station) for station in STATIONS]
        self.station_combo = ttk.Combobox(top_frame, values=station_names, width=40, font=("Arial", 10))
        self.station_combo.pack(side=tk.LEFT, padx=5, pady=15)
        self.station_combo.bind("<<ComboboxSelected>>", lambda e: self.load_schedule())
        
        # Refresh button
        refresh_btn = tk.Button(top_frame, text="↻ Refresh", command=self.load_schedule,
                               bg="#3498db", fg="white", font=("Arial", 10, "bold"),
                               relief=tk.FLAT, padx=15, pady=5, cursor="hand2")
        refresh_btn.pack(side=tk.LEFT, padx=10, pady=15)
        
        # Last updated label
        self.last_updated_label = tk.Label(top_frame, text="Last Updated: Never", 
                                          bg="#2c3e50", fg="#bdc3c7", font=("Arial", 9))
        self.last_updated_label.pack(side=tk.RIGHT, padx=10, pady=15)
        
        # Main content area with two platforms
        content_frame = tk.Frame(self.root, bg="#ecf0f1")
        content_frame.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)
        
        # Platform 1
        platform1_frame = tk.Frame(content_frame, bg="white", relief=tk.RAISED, borderwidth=2)
        platform1_frame.pack(side=tk.LEFT, fill=tk.BOTH, expand=True, padx=(0, 5))
        
        platform1_title = tk.Label(platform1_frame, text="Platform 1", 
                                   bg="#e74c3c", fg="white", font=("Arial", 14, "bold"), pady=10)
        platform1_title.pack(fill=tk.X)
        
        # Platform 1 train list
        platform1_scroll_frame = tk.Frame(platform1_frame, bg="white")
        platform1_scroll_frame.pack(fill=tk.BOTH, expand=True)
        
        platform1_scrollbar = tk.Scrollbar(platform1_scroll_frame)
        platform1_scrollbar.pack(side=tk.RIGHT, fill=tk.Y)
        
        self.platform1_canvas = tk.Canvas(platform1_scroll_frame, bg="white", 
                                         yscrollcommand=platform1_scrollbar.set, highlightthickness=0)
        self.platform1_canvas.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
        platform1_scrollbar.config(command=self.platform1_canvas.yview)
        
        self.platform1_inner_frame = tk.Frame(self.platform1_canvas, bg="white")
        self.platform1_canvas.create_window((0, 0), window=self.platform1_inner_frame, anchor='nw')
        
        # Platform 2
        platform2_frame = tk.Frame(content_frame, bg="white", relief=tk.RAISED, borderwidth=2)
        platform2_frame.pack(side=tk.RIGHT, fill=tk.BOTH, expand=True, padx=(5, 0))
        
        platform2_title = tk.Label(platform2_frame, text="Platform 2", 
                                   bg="#3498db", fg="white", font=("Arial", 14, "bold"), pady=10)
        platform2_title.pack(fill=tk.X)
        
        # Platform 2 train list
        platform2_scroll_frame = tk.Frame(platform2_frame, bg="white")
        platform2_scroll_frame.pack(fill=tk.BOTH, expand=True)
        
        platform2_scrollbar = tk.Scrollbar(platform2_scroll_frame)
        platform2_scrollbar.pack(side=tk.RIGHT, fill=tk.Y)
        
        self.platform2_canvas = tk.Canvas(platform2_scroll_frame, bg="white", 
                                         yscrollcommand=platform2_scrollbar.set, highlightthickness=0)
        self.platform2_canvas.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
        platform2_scrollbar.config(command=self.platform2_canvas.yview)
        
        self.platform2_inner_frame = tk.Frame(self.platform2_canvas, bg="white")
        self.platform2_canvas.create_window((0, 0), window=self.platform2_inner_frame, anchor='nw')
        
        # Configure scroll regions
        self.platform1_inner_frame.bind('<Configure>', 
                                       lambda e: self.platform1_canvas.configure(scrollregion=self.platform1_canvas.bbox("all")))
        self.platform2_inner_frame.bind('<Configure>', 
                                       lambda e: self.platform2_canvas.configure(scrollregion=self.platform2_canvas.bbox("all")))
        
        # Loading overlay
        self.loading_overlay = None
    
    def create_loading_overlay(self):
        """Create loading overlay"""
        if self.loading_overlay:
            return
        
        self.loading_overlay = tk.Frame(self.root, bg="#000000")
        self.loading_overlay.place(relx=0, rely=0, relwidth=1, relheight=1)
        self.loading_overlay.lift()
        
        # Semi-transparent effect (simulated with alpha)
        loading_label = tk.Label(self.loading_overlay, text="⟳ Loading...", 
                                bg="#000000", fg="#ffffff", font=("Arial", 20, "bold"))
        loading_label.place(relx=0.5, rely=0.5, anchor='center')
    
    def remove_loading_overlay(self):
        """Remove loading overlay"""
        if self.loading_overlay:
            self.loading_overlay.destroy()
            self.loading_overlay = None
    
    def show_loading_skeleton(self, frame):
        """Show loading skeleton in a frame"""
        for widget in frame.winfo_children():
            widget.destroy()
        
        for i in range(5):
            skeleton = tk.Frame(frame, bg="#e0e0e0", height=80, relief=tk.FLAT, borderwidth=1)
            skeleton.pack(fill=tk.X, padx=10, pady=5)
            
            # Skeleton content
            line1 = tk.Frame(skeleton, bg="#c0c0c0", height=15, width=150)
            line1.place(x=10, y=10)
            
            line2 = tk.Frame(skeleton, bg="#c0c0c0", height=12, width=200)
            line2.place(x=10, y=35)
            
            line3 = tk.Frame(skeleton, bg="#c0c0c0", height=12, width=100)
            line3.place(x=10, y=55)
    
    def load_schedule(self):
        """Load schedule for selected station"""
        selected = self.station_combo.get()
        if not selected:
            return
        
        # Extract station ID from selection
        station_id = None
        for station in STATIONS:
            if str(station) == selected:
                station_id = station.id
                break
        
        if not station_id:
            return
        
        # Show loading skeletons
        self.show_loading_skeleton(self.platform1_inner_frame)
        self.show_loading_skeleton(self.platform2_inner_frame)
        
        # Run async load in thread
        def run_async():
            loop = asyncio.new_event_loop()
            asyncio.set_event_loop(loop)
            loop.run_until_complete(self.viewmodel.load_schedule(station_id))
            loop.close()
        
        thread = threading.Thread(target=run_async)
        thread.start()
    
    def on_data_changed(self):
        """Callback when data changes"""
        self.root.after(0, self.update_ui)
    
    def on_loading_changed(self, loading):
        """Callback when loading state changes"""
        if loading:
            self.root.after(0, self.create_loading_overlay)
        else:
            self.root.after(0, self.remove_loading_overlay)
    
    def on_error(self, error_message):
        """Callback when error occurs"""
        self.root.after(0, lambda: messagebox.showerror("Error", error_message))
    
    def update_ui(self):
        """Update UI with new data"""
        # Update last updated time
        self.last_updated_label.config(text=f"Last Updated: {self.viewmodel.get_last_updated_string()}")
        
        # Update Platform 1
        self.update_platform_display(self.platform1_inner_frame, self.viewmodel.get_platform_1_trains(), "#e74c3c")
        
        # Update Platform 2
        self.update_platform_display(self.platform2_inner_frame, self.viewmodel.get_platform_2_trains(), "#3498db")
        
        # Schedule next auto-refresh
        self.schedule_auto_refresh()
    
    def update_platform_display(self, frame, trains, color):
        """Update platform display with train cards"""
        # Clear existing widgets
        for widget in frame.winfo_children():
            widget.destroy()
        
        if not trains:
            # No trains message
            no_trains_label = tk.Label(frame, text="No trains scheduled", 
                                      bg="white", fg="#7f8c8d", font=("Arial", 12))
            no_trains_label.pack(pady=50)
            return
        
        # Create train cards
        for i, train in enumerate(trains):
            card = self.create_train_card(frame, train, color)
            card.pack(fill=tk.X, padx=10, pady=5)
    
    def create_train_card(self, parent, train, color):
        """Create a train card widget"""
        card = tk.Frame(parent, bg="white", relief=tk.RAISED, borderwidth=1)
        
        # Left colored bar
        left_bar = tk.Frame(card, bg=color, width=5)
        left_bar.pack(side=tk.LEFT, fill=tk.Y)
        
        # Content
        content = tk.Frame(card, bg="white")
        content.pack(side=tk.LEFT, fill=tk.BOTH, expand=True, padx=10, pady=10)
        
        # Route number (large and bold)
        route_label = tk.Label(content, text=f"Route {train.route_no}", 
                              bg="white", fg=color, font=("Arial", 16, "bold"))
        route_label.pack(anchor='w')
        
        # Destination
        dest_label = tk.Label(content, text=f"→ {train.destination_en}", 
                             bg="white", fg="#2c3e50", font=("Arial", 11))
        dest_label.pack(anchor='w', pady=(2, 0))
        
        # Chinese destination
        dest_ch_label = tk.Label(content, text=f"   {train.destination_ch}", 
                                bg="white", fg="#7f8c8d", font=("Arial", 9))
        dest_ch_label.pack(anchor='w')
        
        # Bottom row: time and car type
        bottom_row = tk.Frame(content, bg="white")
        bottom_row.pack(anchor='w', pady=(5, 0))
        
        time_label = tk.Label(bottom_row, text=f"⏰ {train.arrival_time}", 
                             bg="white", fg="#27ae60", font=("Arial", 10, "bold"))
        time_label.pack(side=tk.LEFT, padx=(0, 15))
        
        car_type_label = tk.Label(bottom_row, text=f"🚋 {train.car_type} Car", 
                                 bg="white", fg="#7f8c8d", font=("Arial", 9))
        car_type_label.pack(side=tk.LEFT)
        
        return card
    
    def schedule_auto_refresh(self):
        """Schedule automatic refresh"""
        if self.refresh_timer:
            self.root.after_cancel(self.refresh_timer)
        
        self.refresh_timer = self.root.after(self.auto_refresh_interval, self.load_schedule)
    
    def run(self):
        """Run the application"""
        self.root.mainloop()


def main():
    """Main entry point"""
    root = tk.Tk()
    app = MTRScheduleWidget(root)
    app.run()


if __name__ == "__main__":
    main()

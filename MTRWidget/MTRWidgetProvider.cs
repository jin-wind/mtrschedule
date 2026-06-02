using Microsoft.Windows.Widgets.Providers;
using MTRWidget.Services;
using MTRWidget.Data;
using System.Text.Json;

namespace MTRWidget;

public class MTRWidgetProvider : IWidgetProvider
{
    private readonly MTRApiService _apiService;
    private readonly Dictionary<string, WidgetState> _widgetStates = new();
    
    public MTRWidgetProvider()
    {
        _apiService = new MTRApiService();
    }
    
    public void CreateWidget(WidgetContext widgetContext)
    {
        var widgetId = widgetContext.Id;
        var state = new WidgetState
        {
            StationId = "100", // Default: Siu Hong
            LastUpdate = DateTime.Now
        };
        
        _widgetStates[widgetId] = state;
        
        // Initial update
        _ = UpdateWidgetAsync(widgetContext);
    }
    
    public void DeleteWidget(string widgetId, string customState)
    {
        _widgetStates.Remove(widgetId);
    }
    
    public void OnActionInvoked(WidgetActionInvokedArgs actionInvokedArgs)
    {
        var verb = actionInvokedArgs.Verb;
        var widgetId = actionInvokedArgs.WidgetContext.Id;
        
        if (verb == "refresh")
        {
            _ = UpdateWidgetAsync(actionInvokedArgs.WidgetContext);
        }
        else if (verb == "changeStation")
        {
            var data = JsonSerializer.Deserialize<Dictionary<string, string>>(actionInvokedArgs.Data);
            if (data != null && data.TryGetValue("stationId", out var stationId))
            {
                if (_widgetStates.TryGetValue(widgetId, out var state))
                {
                    state.StationId = stationId;
                    _ = UpdateWidgetAsync(actionInvokedArgs.WidgetContext);
                }
            }
        }
    }
    
    public void OnWidgetContextChanged(WidgetContextChangedArgs contextChangedArgs)
    {
        var widgetContext = contextChangedArgs.WidgetContext;
        if (contextChangedArgs.WidgetContext.IsActive)
        {
            _ = UpdateWidgetAsync(widgetContext);
        }
    }
    
    public void Activate(WidgetContext widgetContext)
    {
        _ = UpdateWidgetAsync(widgetContext);
    }
    
    public void Deactivate(string widgetId)
    {
        // Widget is no longer visible
    }
    
    private async Task UpdateWidgetAsync(WidgetContext widgetContext)
    {
        var widgetId = widgetContext.Id;
        
        if (!_widgetStates.TryGetValue(widgetId, out var state))
        {
            return;
        }
        
        var station = StationData.GetStationById(state.StationId);
        if (station == null)
        {
            return;
        }
        
        var schedule = await _apiService.GetScheduleAsync(state.StationId);
        
        // Generate widget template data
        var templateData = GenerateTemplateData(station, schedule);
        
        // Update widget
        var updateOptions = new WidgetUpdateRequestOptions(widgetId)
        {
            Data = templateData,
            Template = GetWidgetTemplate(),
            CustomState = JsonSerializer.Serialize(state)
        };
        
        WidgetManager.GetDefault().UpdateWidget(updateOptions);
        
        state.LastUpdate = DateTime.Now;
    }
    
    private string GenerateTemplateData(Models.Station station, Models.ScheduleResponse? schedule)
    {
        var data = new
        {
            stationName = station.NameCh,
            stationNameEn = station.NameEn,
            lastUpdate = DateTime.Now.ToString("HH:mm"),
            platform1Trains = schedule?.Platforms.FirstOrDefault(p => p.PlatformId == 1)?.Trains.Take(3).Select(t => new
            {
                route = t.RouteNo,
                destination = t.DestinationCh,
                time = t.ArrivalTime,
                carType = t.CarType
            }).ToList() ?? new List<object>(),
            platform2Trains = schedule?.Platforms.FirstOrDefault(p => p.PlatformId == 2)?.Trains.Take(3).Select(t => new
            {
                route = t.RouteNo,
                destination = t.DestinationCh,
                time = t.ArrivalTime,
                carType = t.CarType
            }).ToList() ?? new List<object>()
        };
        
        return JsonSerializer.Serialize(data);
    }
    
    private string GetWidgetTemplate()
    {
        // This would normally be loaded from a JSON file
        // For now, return a simple template structure
        return File.ReadAllText(Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "Widgets", "MTRWidget.json"));
    }
    
    private class WidgetState
    {
        public string StationId { get; set; } = string.Empty;
        public DateTime LastUpdate { get; set; }
    }
}

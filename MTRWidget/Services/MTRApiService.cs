using System.Net.Http;
using System.Text.Json;
using MTRWidget.Models;

namespace MTRWidget.Services;

public class MTRApiService
{
    private readonly HttpClient _httpClient;
    private const string BaseUrl = "https://rt.data.gov.hk/v1/transport/mtr/lrt/getSchedule";
    
    public MTRApiService()
    {
        _httpClient = new HttpClient
        {
            Timeout = TimeSpan.FromSeconds(30)
        };
    }
    
    public async Task<ScheduleResponse?> GetScheduleAsync(string stationId)
    {
        try
        {
            var url = $"{BaseUrl}?station_id={stationId}";
            var response = await _httpClient.GetAsync(url);
            
            if (!response.IsSuccessStatusCode)
            {
                return null;
            }
            
            var json = await response.Content.ReadAsStringAsync();
            var apiResponse = JsonSerializer.Deserialize<MTRApiResponse>(json, new JsonSerializerOptions
            {
                PropertyNameCaseInsensitive = true
            });
            
            if (apiResponse == null)
            {
                return null;
            }
            
            var scheduleResponse = new ScheduleResponse
            {
                Status = apiResponse.Status,
                SystemTime = apiResponse.SystemTime ?? string.Empty,
                Platforms = new List<Platform>()
            };
            
            foreach (var platformData in apiResponse.PlatformList ?? new List<ApiPlatform>())
            {
                var platform = new Platform
                {
                    PlatformId = platformData.PlatformId,
                    Trains = new List<Train>()
                };
                
                foreach (var routeData in platformData.RouteList ?? new List<ApiRoute>())
                {
                    var train = new Train
                    {
                        RouteNo = routeData.RouteNo ?? string.Empty,
                        DestinationEn = routeData.DestEn ?? string.Empty,
                        DestinationCh = routeData.DestCh ?? string.Empty,
                        ArrivalTime = routeData.TimeEn ?? string.Empty,
                        TrainLength = routeData.TrainLength,
                        PlatformId = platformData.PlatformId
                    };
                    
                    platform.Trains.Add(train);
                }
                
                scheduleResponse.Platforms.Add(platform);
            }
            
            return scheduleResponse;
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Error fetching schedule: {ex.Message}");
            return null;
        }
    }
    
    // Internal API response models
    private class MTRApiResponse
    {
        public int Status { get; set; }
        public string? SystemTime { get; set; }
        public List<ApiPlatform>? PlatformList { get; set; }
    }
    
    private class ApiPlatform
    {
        public int PlatformId { get; set; }
        public List<ApiRoute>? RouteList { get; set; }
    }
    
    private class ApiRoute
    {
        public string? RouteNo { get; set; }
        public string? DestEn { get; set; }
        public string? DestCh { get; set; }
        public string? TimeEn { get; set; }
        public string? TimeCh { get; set; }
        public int TrainLength { get; set; }
        public string? ArrivalDeparture { get; set; }
        public int Stop { get; set; }
    }
}

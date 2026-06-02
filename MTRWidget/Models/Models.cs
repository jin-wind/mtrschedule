namespace MTRWidget.Models;

public class Station
{
    public string Id { get; set; } = string.Empty;
    public string NameEn { get; set; } = string.Empty;
    public string NameCh { get; set; } = string.Empty;
    
    public override string ToString() => $"{NameEn} ({NameCh})";
}

public class Train
{
    public string RouteNo { get; set; } = string.Empty;
    public string DestinationEn { get; set; } = string.Empty;
    public string DestinationCh { get; set; } = string.Empty;
    public string ArrivalTime { get; set; } = string.Empty;
    public int TrainLength { get; set; }
    public int PlatformId { get; set; }
    
    public string CarType => TrainLength == 2 ? "雙卡" : "單卡";
}

public class Platform
{
    public int PlatformId { get; set; }
    public List<Train> Trains { get; set; } = new();
}

public class ScheduleResponse
{
    public int Status { get; set; }
    public string SystemTime { get; set; } = string.Empty;
    public List<Platform> Platforms { get; set; } = new();
}

package Models.DirectionsModel;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Direction Header
/// </summary>
public class DirectionHeader {
    /// <summary>
    /// Trip Distance
    /// </summary>
    @SerializedName("distance")
    public DistDur Distance;

    /// <summary>
    /// Trip Duration
    /// </summary>
    @SerializedName("duration")
    public DistDur Duration;

    /// <summary>
    /// City name start location
    /// </summary>
    @SerializedName("cityName")
    public String CityName;

    public DirectionHeader(DistDur distance, DistDur duration, String cityName) {
        Distance = distance;
        Duration = duration;
        CityName = cityName;
    }
}

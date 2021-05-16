package Models.DriversModels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import Models.CitiesModels.CityHeader;
import Models.DaysModels.DayHeaderExt;

/// <summary>
/// Model Created to Create and Update Driver
/// </summary>
public class DriverModel extends DriverModelHeader {
    /// <summary>
    /// List of Cities
    /// </summary>
    @SerializedName("cities")
    public List<CityHeader> Cities;

    /// <summary>
    /// List of Days
    /// </summary>
    @SerializedName("days")
    public List<DayHeaderExt> Days;

    public DriverModel(String name, Double startPrice, Double pricePerUnit, Double maximalDistance, List<CityHeader> cities, List<DayHeaderExt> days) {
        super(name, startPrice, pricePerUnit, maximalDistance);
        Cities = cities;
        Days = days;
    }
}

package Models.DriversModels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import Models.CitiesModels.CityDetails;
import Models.CitiesModels.CityHeader;
import Models.DaysModels.DayDetailsExt;
import Models.DaysModels.DayHeaderExt;

/// <summary>
/// Model Created to Create and Update Driver
/// </summary>
public class DriverModelExt extends DriverModelHeader {
    /// <summary>
    /// List of Cities
    /// </summary>
    @SerializedName("cities")
    public List<CityDetails> Cities;

    /// <summary>
    /// List of Days
    /// </summary>
    @SerializedName("days")
    public List<DayDetailsExt> Days;

    public DriverModelExt(String name, Double startPrice, Double pricePerUnit, Double maximalDistance, List<CityDetails> cities, List<DayDetailsExt> days) {
        super(name, startPrice, pricePerUnit, maximalDistance);
        Cities = cities;
        Days = days;
    }
}

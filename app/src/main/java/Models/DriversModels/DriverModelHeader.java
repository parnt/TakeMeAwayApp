package Models.DriversModels;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Model Get Driver Summary
/// </summary>
public class DriverModelHeader implements IDriverName {
    /// <summary>
    /// Driver/Company Name
    /// </summary>
    @SerializedName("name")
    public String Name;

    /// <summary>
    /// Start Price
    /// </summary>
    @SerializedName("startPrice")
    public Double StartPrice;

    /// <summary>
    /// Price Per Kilometer
    /// </summary>
    @SerializedName("pricePerUnit")
    public Double PricePerUnit;

    /// <summary>
    /// Maximal driver trip distance
    /// </summary>
    @SerializedName("maximalDistance")
    public Double MaximalDistance;

    public DriverModelHeader(String name, Double startPrice, Double pricePerUnit, Double maximalDistance) {
        Name = name;
        StartPrice = startPrice;
        PricePerUnit = pricePerUnit;
        MaximalDistance = maximalDistance;
    }
}

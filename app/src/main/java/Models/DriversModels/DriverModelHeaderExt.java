package Models.DriversModels;

import com.google.gson.annotations.SerializedName;

import java.text.DecimalFormat;

/// <summary>
/// Model for driver model and get drivers
/// </summary>
public class DriverModelHeaderExt extends DriverModelHeader {
    /// <summary>
    /// Driver ID
    /// </summary>
    @SerializedName("id")
    public int Id;

    /// <summary>
    /// Trip price
    /// </summary>
    @SerializedName("price")
    public Double Price;

    public DriverModelHeaderExt(String name, Double startPrice, Double pricePerUnit, Double maximalDistance, int id, Double price) {
        super(name, startPrice, pricePerUnit, maximalDistance);
        Id = id;
        Price = price;
    }
}

package Models.DriversModels;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Model created to Get Driver ID and then convert on Driver data
/// </summary>
public class DriverIdHeader {
    /// <summary>
    /// Driver ID
    /// </summary>
    @SerializedName("id")
    public Integer Id;

    public DriverIdHeader(Integer id) {
        Id = id;
    }
}

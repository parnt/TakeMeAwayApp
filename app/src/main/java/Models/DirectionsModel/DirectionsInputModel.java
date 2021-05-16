package Models.DirectionsModel;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Input model to Get directions
/// </summary>
public class DirectionsInputModel {
    /// <summary>
    /// Origin place
    /// </summary>
    @SerializedName("origin")
    public String Origin;

    /// <summary>
    /// Destination place
    /// </summary>
    @SerializedName("destination")
    public String Destination;

    /// <summary>
    /// Do user want fastest route
    /// </summary>
    @SerializedName("wantFastestRoute")
    public Boolean WantFastestRoute;
}

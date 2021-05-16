package Models.DirectionsModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

/// <summary>
/// Model created to return drivers
/// </summary>
public class DirectionResultHeader extends DirectionHeader {
    /// <summary>
    /// Route ID
    /// </summary>
    @SerializedName("id")
    public String Id;

    /// <summary>
    /// Text From place
    /// </summary>
    @SerializedName("fromPlace")
    public String FromPlace;

    /// <summary>
    /// Text To place
    /// </summary>
    @SerializedName("toPlace")
    public String ToPlace;

    /// <summary>
    /// Coordinates from place
    /// </summary>
    @SerializedName("fromPlaceLatLng")
    public LatLng FromPlaceLatLng;

    /// <summary>
    /// Coordinates to place
    /// </summary>
    @SerializedName("toPlaceLatLng")
    public LatLng ToPlaceLatLng;

    /// <summary>
    /// URL static map
    /// </summary>
    @SerializedName("staticMapUrl")
    public String StaticMapUrl;

    public DirectionResultHeader(DistDur distance, DistDur duration, String cityName, String id, String fromPlace, String toPlace, LatLng fromPlaceLatLng, LatLng toPlaceLatLng, String staticMapUrl) {
        super(distance, duration, cityName);
        Id = id;
        FromPlace = fromPlace;
        ToPlace = toPlace;
        FromPlaceLatLng = fromPlaceLatLng;
        ToPlaceLatLng = toPlaceLatLng;
        StaticMapUrl = staticMapUrl;
    }
}
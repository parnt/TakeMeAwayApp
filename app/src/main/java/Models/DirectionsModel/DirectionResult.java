package Models.DirectionsModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/// <summary>
/// Model created to return directions
/// </summary>
public class DirectionResult extends DirectionResultHeader {
    /// <summary>
    /// List of polylines points
    /// </summary>
    @SerializedName("polylinesPoints")
    public List<LatLng> PolylinesPoints;

    @SerializedName("polyline")
    public String Polyline;

    public DirectionResult(DistDur distance, DistDur duration, String cityName, String id, String fromPlace, String toPlace, LatLng fromPlaceLatLng, LatLng toPlaceLatLng, String staticMapUrl, List<LatLng> polylinesPoints, String polyline) {
        super(distance, duration, cityName, id, fromPlace, toPlace, fromPlaceLatLng, toPlaceLatLng, staticMapUrl);
        PolylinesPoints = polylinesPoints;
        Polyline = polyline;
    }
}

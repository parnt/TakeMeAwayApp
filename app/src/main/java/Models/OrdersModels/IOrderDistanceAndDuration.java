package Models.OrdersModels;

import com.google.gson.annotations.SerializedName;

import java.time.Duration;

/// <summary>
/// Interface with Order distance and duration time
/// </summary>
public interface IOrderDistanceAndDuration {
    /// <summary>
    /// Trip Distance
    /// </summary>
    @SerializedName("distance")
    Double Distance = null;

    /// <summary>
    /// Trip Duration
    /// </summary>
    @SerializedName("duration")
    Duration Duration = null;
}
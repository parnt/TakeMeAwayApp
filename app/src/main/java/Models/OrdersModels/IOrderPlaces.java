package Models.OrdersModels;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Interface for Orders places
/// </summary>
public interface IOrderPlaces {
    /// <summary>
    /// Trip start
    /// </summary>
    @SerializedName("fromPlace")
    String FromPlace = null;

    /// <summary>
    /// Trip End
    /// </summary>
    @SerializedName("toPlace")
    String ToPlace = null;
}

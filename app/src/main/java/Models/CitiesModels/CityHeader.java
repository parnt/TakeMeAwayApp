package Models.CitiesModels;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// City Header
/// </summary>
public class CityHeader {
    /// <summary>
    /// City ID
    /// </summary>
    @SerializedName("id")
    public Integer Id;

    public CityHeader(Integer id) {
        Id = id;
    }
}

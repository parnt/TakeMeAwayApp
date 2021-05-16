package Models.DaysModels;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Day Header
/// </summary>
public class DayHeader {
    /// <summary>
    /// Day ID
    /// </summary>
    @SerializedName("id")
    public Integer Id;

    public DayHeader(Integer id) {
        Id = id;
    }
}

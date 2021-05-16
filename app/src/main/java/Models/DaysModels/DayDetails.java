package Models.DaysModels;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Model to Get Days List
/// </summary>
public class DayDetails extends DayHeader implements IDayName {
    /// <summary>
    /// Day name
    /// </summary>
    @SerializedName("name")
    public String Name;

    public DayDetails(Integer id, String name) {
        super(id);
        Name = name;
    }
}

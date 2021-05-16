package Models.DaysModels;

import com.google.gson.annotations.SerializedName;

import javax.xml.datatype.Duration;

/// <summary>
/// Model created to get driver days
/// </summary>
public class DayDetailsExt extends DayHeaderExt implements IDayName {
    /// <summary>
    /// Day name
    /// </summary>
    @SerializedName("name")
    public String Name;

    public DayDetailsExt(Integer id, String startTime, String endTime, String name) {
        super(id, startTime, endTime);
        Name = name;
    }
}

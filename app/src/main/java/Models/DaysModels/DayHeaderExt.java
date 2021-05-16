package Models.DaysModels;

import com.google.gson.annotations.SerializedName;

import javax.xml.datatype.Duration;

/// <summary>
/// Day Header Extended by Start and End time
/// </summary>
public class DayHeaderExt extends DayHeader {
    /// <summary>
    /// Driver StartTime of day
    /// </summary>
    @SerializedName("startTime")
    public String StartTime;

    /// <summary>
    /// Driver EndTime of day
    /// </summary>
    @SerializedName("endTime")
    public String EndTime;

    public DayHeaderExt(Integer id, String startTime, String endTime) {
        super(id);
        StartTime = startTime;
        EndTime = endTime;
    }
}

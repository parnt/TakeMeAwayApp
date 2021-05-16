package Models.DaysModels;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Interface to set day Name
/// </summary>
public interface IDayName
{
    /// <summary>
    /// Day name
    /// </summary>
    @SerializedName("name")
    String Name = null;
}

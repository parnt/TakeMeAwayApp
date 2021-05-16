package Models.DriversModels;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Interface for Driver/Company Name
/// </summary>
public interface IDriverName {
    /// <summary>
    /// Driver/Company Name
    /// </summary>
    @SerializedName("name")
    String Name = null;
}

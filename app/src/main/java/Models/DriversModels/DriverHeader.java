package Models.DriversModels;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Model Created to get Drivers header
/// </summary>
public class DriverHeader extends DriverIdHeader implements IDriverName {
    /// <summary>
    /// Driver/Company Name
    /// </summary>
    @SerializedName("name")
    public String Name;

    public DriverHeader(Integer id, String name) {
        super(id);
        Name = name;
    }
}

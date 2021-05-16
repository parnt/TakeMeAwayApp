package Models.CitiesModels;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Model to Getting Cities List
/// </summary>
public class CityDetails extends CityHeader {
    /// <summary>
    /// City name
    /// </summary>
    @SerializedName("name")
    public String Name;

    public CityDetails(Integer id, String name) {
        super(id);
        Name = name;
    }
}

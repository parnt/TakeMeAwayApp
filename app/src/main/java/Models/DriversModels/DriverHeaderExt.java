package Models.DriversModels;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Model created to Get Drivers data with Phone Number for Get user Order
/// </summary>
public class DriverHeaderExt extends DriverHeader {
    /// <summary>
    /// Driver's Phone Number
    /// </summary>
    @SerializedName("phoneNumber")
    public String PhoneNumber;

    public DriverHeaderExt(Integer id, String name, String phoneNumber) {
        super(id, name);
        PhoneNumber = phoneNumber;
    }
}

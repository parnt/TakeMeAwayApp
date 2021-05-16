package Models.AccountsModels;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Account Header
/// </summary>
public class AccountHeader {
    /// <summary>
    /// User first name
    /// </summary>
    @SerializedName("firstName")
    public String FirstName;

    /// <summary>
    /// User last name
    /// </summary>
    @SerializedName("lastName")
    public String LastName;

    /// <summary>
    /// User mobile phone number
    /// </summary>
    @SerializedName("phoneNumber")
    public String PhoneNumber;

}

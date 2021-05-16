package Models.AccountsModels;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Model to represent an output account summary
/// </summary>
public class UserSummary extends UserHeader implements IAccountIdHeader {
    /// <summary>
    /// GUID user id
    /// </summary>
    @SerializedName("id")
    public String Id;
}

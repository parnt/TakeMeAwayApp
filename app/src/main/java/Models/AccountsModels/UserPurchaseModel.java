package Models.AccountsModels;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Model created for Driver Order
/// </summary>
public class UserPurchaseModel extends AccountHeader implements IAccountIdHeader {
    /// <summary>
    /// Client ID
    /// </summary>
    @SerializedName("id")
    public String Id;
}

package Models.AccountsModels;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Model represents a most deep fields for account details
/// </summary>
public class UserHeader extends AccountHeader implements IEmailModel {
    /// <summary>
    /// User E-mail
    /// </summary>
    @SerializedName("email")
    public String Email;
}

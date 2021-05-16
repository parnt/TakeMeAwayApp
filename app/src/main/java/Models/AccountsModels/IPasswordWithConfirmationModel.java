package Models.AccountsModels;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// interface using IPasswordModel. Represents a Password with confirmation
/// </summary>
public interface IPasswordWithConfirmationModel {
    /// <summary>
    /// User password
    /// </summary>
    @SerializedName("password")
    String Password = null;

    /// <summary>
    /// Confirmation user password
    /// </summary>
    @SerializedName("confirmPassword")
    String ConfirmPassword = null;
}

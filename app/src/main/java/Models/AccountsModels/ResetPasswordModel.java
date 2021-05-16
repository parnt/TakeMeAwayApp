package Models.AccountsModels;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Model created to reset forgotten password
/// </summary>
public class ResetPasswordModel implements IPasswordWithConfirmationModel {
    /// <summary>
    /// New account password
    /// </summary>
    @SerializedName("Password")
    public String Password;

    /// <summary>
    /// Confirmation new account password
    /// </summary>
    @SerializedName("ConfirmPassword")
    public String ConfirmPassword;

    /// <summary>
    /// Reset Password Token
    /// </summary>
    @SerializedName("token")
    public String Token;
}

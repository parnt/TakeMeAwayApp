package Models.AccountsModels;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Model created to change password
/// </summary>
public class ChangePasswordModel implements IPasswordWithConfirmationModel {
    /// <summary>
    /// Current user password
    /// </summary>
    @SerializedName("oldPassword")
    public String OldPassword;

    /// <summary>
    /// New password
    /// </summary>
    @SerializedName("password")
    public String Password;

    /// <summary>
    /// Confirmation new password
    /// </summary>
    @SerializedName("confirmPassword")
    public String ConfirmPassword;
}

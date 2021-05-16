package Models.AccountsModels;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Model to represent input user registration. Using IPasswordWithConfirmationModel to define a passwords
/// </summary>
public class UserRegistration extends UserHeader implements IPasswordWithConfirmationModel {
    /// <summary>
    /// User password
    /// </summary>
    @SerializedName("password")
    public String Password;

    /// <summary>
    /// Confirmation user password
    /// </summary>
    @SerializedName("confirmPassword")
    public String ConfirmPassword;
}


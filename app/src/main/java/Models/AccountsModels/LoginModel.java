package Models.AccountsModels;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Model created for Login Page
/// </summary>
public class LoginModel implements IPasswordModel, IEmailModel {
    /// <summary>
    /// Email address
    /// </summary>
    @SerializedName("email")
    public String Email;

    /// <summary>
    /// User password
    /// </summary>
    @SerializedName("password")
    public String Password;

    /// <summary>
    /// Checkbox to remember logging
    /// </summary>
    @SerializedName("rememberMe")
    public Boolean RememberMe;
}

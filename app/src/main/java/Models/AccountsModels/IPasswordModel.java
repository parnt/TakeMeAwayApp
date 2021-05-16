package Models.AccountsModels;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// interface most deep for registration/sign in/update account. Keeps only Password field
/// </summary>
public interface IPasswordModel {
    /// <summary>
    /// User Password
    /// </summary>
    @SerializedName("password")
    String Password = null;
}

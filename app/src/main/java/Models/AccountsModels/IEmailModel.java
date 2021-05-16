package Models.AccountsModels;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Interface with Email adress
/// </summary>
public interface IEmailModel {
    /// <summary>
    /// Email adress
    /// </summary>
    @SerializedName("email")
    String Email = null;
}

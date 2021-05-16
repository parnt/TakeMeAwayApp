package Models.AccountsModels;

import com.google.gson.annotations.SerializedName;

public class AccountDetails {
    /// <summary>
    /// Is user a driver
    /// </summary>
    @SerializedName("isDriver")
    public Boolean IsDriver;

    /// <summary>
    /// Username
    /// </summary>
    @SerializedName("username")
    public String Username;
}

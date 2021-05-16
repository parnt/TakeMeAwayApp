package Models.AccountsModels;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Interface with Account ID
/// </summary>
public interface IAccountIdHeader {
    /// <summary>
    /// GUID user id
    /// </summary>
    @SerializedName("id")
    String Id = null;
}

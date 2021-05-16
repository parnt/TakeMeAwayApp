package Models.AccountsModels;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Model created to result GET/accounts/summary
/// </summary>
public class AccountSummaryResult extends UserSummary {
    /// <summary>
    /// Details of account
    /// </summary>
    @SerializedName("details")
    public AccountDetails Details;
}

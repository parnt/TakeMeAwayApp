package Models.EmailPatterns;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Pattern email sender for reset password
/// </summary>
public class ResetPasswordEmailPattern implements IEmailPattern
        {
    /// <summary>
    /// Topic message
    /// </summary>
    @SerializedName("subject")
    public String Subject;

    /// <summary>
    /// Message of e-mail
    /// </summary>
    @SerializedName("body")
    public String Body;
}

package Models.EmailPatterns;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Interface for Emails Patterns
/// </summary>
public interface IEmailPattern
{
    /// <summary>
    /// Topic message
    /// </summary>
    @SerializedName("subject")
    String Subject = null;

    /// <summary>
    /// message of e-mail
    /// </summary>
    @SerializedName("body")
    String Body = null;
}

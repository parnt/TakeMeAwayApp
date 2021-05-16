package Models;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Object represents an single error
/// </summary>
public class Error {
    /// <summary>
    /// Display in string status code of given error
    /// </summary>
    @SerializedName("code")
    public String Code;

    /// <summary>
    /// Keep message details of error
    /// </summary>
    @SerializedName("message")
    public String Message;

    /// <summary>
    /// Keep name of variable which one is invalid
    /// </summary>
    @SerializedName("field")
    public String Field;
}

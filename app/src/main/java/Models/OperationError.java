package Models;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Object keeps a general message of error and his details
/// </summary>
public class OperationError {
    /// <summary>
    /// General message of error
    /// </summary>
    @SerializedName("message")
    public String Message;

    /// <summary>
    /// Error's details
    /// </summary>
    @SerializedName("errorDetails")
    public Models.Error ErrorDetails;
}

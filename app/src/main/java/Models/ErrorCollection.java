package Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/// <summary>
/// List of errors with count of them
/// </summary>
public class ErrorCollection {
    /// <summary>
    /// List of errors
    /// </summary>
    @SerializedName("errors")
    public List<OperationError> Errors;

    /// <summary>
    /// Sum errors
    /// </summary>
    @SerializedName("errorCount")
    public Integer ErrorCount;
}

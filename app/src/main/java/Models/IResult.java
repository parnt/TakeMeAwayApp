package Models;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Represents an succeed response with list of Items
/// </summary>
/// <typeparam name="T"></typeparam>
public interface IResult {
    /// <summary>
    /// Sum Items
    /// </summary>
    @SerializedName("totalCount")
    Integer TotalCount = null;

    /// <summary>
    /// Success message for user information
    /// </summary>
    @SerializedName("message")
    String Message = null;
}

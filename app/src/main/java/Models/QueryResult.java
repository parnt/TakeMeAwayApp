package Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/// <summary>
/// Represents an succeed response with list of Items
/// </summary>
/// <typeparam name="T"></typeparam>
public class QueryResult<T> implements IResult {
    /// <summary>
    /// Sum Items
    /// </summary>
    @SerializedName("totalCount")
    public Integer TotalCount;

    /// <summary>
    /// Success message for user information
    /// </summary>
    @SerializedName("message")
    public String Message;

    /// <summary>
    /// List of items
    /// </summary>
    @SerializedName("items")
    public T Items;
}

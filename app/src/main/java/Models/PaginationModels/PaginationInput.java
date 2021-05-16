package Models.PaginationModels;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Model to represent custom input settings pagination
/// </summary>
public class PaginationInput {
    /// <summary>
    /// Defines how many user want to display items on one page
    /// </summary>
    @SerializedName("pageSize")
    public Integer PageSize;

    /// <summary>
    /// Defines which one page user want to see
    /// </summary>
    @SerializedName("currentPage")
    public Integer CurrentPage;
}

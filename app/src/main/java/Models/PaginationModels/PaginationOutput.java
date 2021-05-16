package Models.PaginationModels;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Model represents pagination in response
/// </summary>
public class PaginationOutput implements IPaginationOutput {
    /// <summary>
    /// How many pages has displayed list
    /// </summary>
    @SerializedName("pagesCount")
    public Integer PagesCount;

    /// <summary>
    /// Represents a number of current page
    /// </summary>
    @SerializedName("currentPage")
    public Integer CurrentPage;

    /// <summary>
    /// Sum of all Items with current settings
    /// </summary>
    @SerializedName("totalCount")
    public Integer TotalCount;
}

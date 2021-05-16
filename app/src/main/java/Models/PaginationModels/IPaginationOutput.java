package Models.PaginationModels;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Interface to summary of pagination
/// </summary>
public interface IPaginationOutput {
    /// <summary>
    /// Count of pages with current settings
    /// </summary>
    @SerializedName("pagesCount")
    Integer PagesCount = null;

    /// <summary>
    /// Current page of Items
    /// </summary>
    @SerializedName("currentPage")
    Integer CurrentPage = null;

    /// <summary>
    /// Items total count
    /// </summary>
    @SerializedName("totalCount")
    Integer TotalCount = null;
}

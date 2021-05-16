package Models;

import com.google.gson.annotations.SerializedName;

import Models.PaginationModels.IPaginationOutput;

/// <summary>
/// Represents an succeed response with list of Items with pagination
/// </summary>
/// <typeparam name="T"></typeparam>
public class QueryResultList<T> extends QueryResult<T> implements IResult, IPaginationOutput {
    /// <summary>
    /// Pepresents Count of pages with current settings
    /// </summary>
    @SerializedName("pagesCount")
    public Integer PagesCount;

    /// <summary>
    /// Keeps a current page number
    /// </summary>
    @SerializedName("currentPage")
    public Integer CurrentPage;
}

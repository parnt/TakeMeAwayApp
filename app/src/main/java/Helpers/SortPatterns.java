package Helpers;

/// <summary>
/// Class with list of sorting patterns for given method
/// </summary>
public class SortPatterns
{
    /// <summary>
    /// Orders sort possibilities
    /// </summary>
    public enum OrdersSort
    {
        /// <summary>
        /// Default sort from Configuration file
        /// </summary>
        Default,

        /// <summary>
        /// sort by OrderDate(earlier - first)
        /// </summary>
        OrderDate,

        /// <summary>
        /// sort by OrderDate(later - first)
        /// </summary>
        OrderDate_Desc,

        /// <summary>
        /// sort by Price(cheaper - first)
        /// </summary>
        Price,

        /// <summary>
        /// sort by Price(more expensive - first
        /// </summary>
        Price_Desc,

        /// <summary>
        /// sort by Status
        /// </summary>
        StatusId,

        /// <summary>
        /// sort by Status
        /// </summary>
        StatusId_Desc
    }

    /// <summary>
    /// Driver Orders sort possibilities
    /// </summary>
    public enum DriverOrdersSort
    {
        /// <summary>
        /// Default sort from Configuration file
        /// </summary>
        Default,

        /// <summary>
        /// sort by OrderDate(earlier - first)
        /// </summary>
        OrderDate,

        /// <summary>
        /// sort by OrderDate(later - first)
        /// </summary>
        OrderDate_Desc,

        /// <summary>
        /// sort by Status
        /// </summary>
        StatusId,

        /// <summary>
        /// sort by Status
        /// </summary>
        StatusId_Desc
    }

    /// <summary>
    /// Drivers sort possibilities
    /// </summary>
    public enum DriversSort
    {
        /// <summary>
        /// Default sort ("Price")
        /// </summary>
        Default,

        /// <summary>
        /// sort by price
        /// </summary>
        Price,

        /// <summary>
        /// sort by price desc
        /// </summary>
        Price_Desc,

        /// <summary>
        /// sort by driver name
        /// </summary>
        Name,

        /// <summary>
        /// sort by driver name desc
        /// </summary>
        Name_Desc,

        /// <summary>
        /// sort by start price
        /// </summary>
        StartPrice,

        /// <summary>
        /// sort by start price desc
        /// </summary>
        StartPrice_Desc,

        /// <summary>
        /// sort by prive per unit
        /// </summary>
        PricePerUnit,

        /// <summary>
        /// sort by price per unit desc
        /// </summary>
        PricePerUnit_Desc
    }
}

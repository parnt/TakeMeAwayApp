package Models.OrdersModels;

/// <summary>
/// List of possible order statuses
/// </summary>
public /*static*/ class OrderStatuses {
    /// <summary>
    /// Possible order statuses
    /// </summary>
    public enum OrderStatus {
        /// <summary>
        /// Order is new, Driver didn't go to client yet
        /// </summary>
        New,

        /// <summary>
        /// Order is being processed, driver is comming to client
        /// </summary>
        Processed,

        /// <summary>
        /// Order is on way, Driver keeps client
        /// </summary>
        OnWay,

        /// <summary>
        /// Order is done, Driver comes to target place with client
        /// </summary>
        Done
    }
}

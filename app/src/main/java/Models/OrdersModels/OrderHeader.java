package Models.OrdersModels;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/// <summary>
/// Basic Order header
/// </summary>
public class OrderHeader extends OrderIdHeader {
    /// <summary>
    /// Order Date
    /// </summary>
    @SerializedName("orderDate")
    public String OrderDate;

    /// <summary>
    /// Order Status
    /// </summary>
    @SerializedName("status")
    public OrderStatus Status;

    /// <summary>
    /// Route Id
    /// </summary>
    @SerializedName("routeId")
    public String RouteId;

    public OrderHeader(Integer id, String orderDate, OrderStatus status, String routeId) {
        super(id);
        OrderDate = orderDate;
        Status = status;
        RouteId = routeId;
    }
}

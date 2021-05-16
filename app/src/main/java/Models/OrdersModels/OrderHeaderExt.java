package Models.OrdersModels;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/// <summary>
/// Model Created to get Orders from DB
/// </summary>
public class OrderHeaderExt extends OrderHeader {
    /// <summary>
    /// Order Price
    /// </summary>
    @SerializedName("price")
    public Double Price;

    public OrderHeaderExt(Integer id, String orderDate, OrderStatus status, String routeId, Double price) {
        super(id, orderDate, status, routeId);
        Price = price;
    }
}

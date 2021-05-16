package Models.OrdersModels;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import Models.DriversModels.DriverHeader;
import Models.DriversModels.DriverHeaderExt;
import Models.DriversModels.DriverIdHeader;

/// <summary>
/// Output model for GET Orders
/// </summary>
public class OrdersList extends OrderHeaderExt {
    /// <summary>
    /// Driver Model
    /// </summary>
    @SerializedName("driver")
    public DriverHeaderExt Driver;

    public OrdersList(Integer id, String orderDate, OrderStatus status, String routeId, Double price, DriverHeaderExt driver) {
        super(id, orderDate, status, routeId, price);
        Driver = driver;
    }
}

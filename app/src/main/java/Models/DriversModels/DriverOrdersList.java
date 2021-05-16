package Models.DriversModels;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import Models.OrdersModels.IOrderPlaces;
import Models.OrdersModels.OrderHeader;
import Models.OrdersModels.OrderStatus;
import Models.OrdersModels.OrderStatusHeader;

/// <summary>
/// Model created to GET driver orders
/// </summary>
public class DriverOrdersList extends OrderHeader implements IOrderPlaces {
    /// <summary>
    /// Trip start
    /// </summary>
    @SerializedName("fromPlace")
    public String FromPlace;

    /// <summary>
    /// Trip End
    /// </summary>
    @SerializedName("toPlace")
    public String ToPlace;

    public DriverOrdersList(Integer id, String orderDate, OrderStatus status, String routeId, String fromPlace, String toPlace) {
        super(id, orderDate, status, routeId);
        FromPlace = fromPlace;
        ToPlace = toPlace;
    }
}

package Models.DriversModels;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import javax.xml.datatype.Duration;

import Models.AccountsModels.UserPurchaseModel;
import Models.OrdersModels.IOrderDistanceAndDuration;
import Models.OrdersModels.IOrderPlaces;
import Models.OrdersModels.OrderHeaderExt;
import Models.OrdersModels.OrderStatus;
import Models.OrdersModels.OrderStatusHeader;

/// <summary>
/// Model created to get specific driver order
/// </summary>
public class DriverOrder extends OrderHeaderExt implements IOrderPlaces, IOrderDistanceAndDuration {
    /// <summary>
    /// Trip start place
    /// </summary>
    @SerializedName("fromPlace")
    public String FromPlace;

    /// <summary>
    /// Trip End place
    /// </summary>
    @SerializedName("toPlace")
    public String ToPlace;

    /// <summary>
    /// Trip distance
    /// </summary>
    @SerializedName("distance")
    public Double Distance;

    /// <summary>
    /// Trip duration
    /// </summary>
    @SerializedName("duration")
    public String Duration;

    /// <summary>
    /// Trip Google static Map with directions
    /// </summary>
    @SerializedName("staticMapUrl")
    public String StaticMapUrl;

    /// <summary>
    /// Order client
    /// </summary>
    @SerializedName("user")
    public UserPurchaseModel User;

    public DriverOrder(Integer id, String orderDate, OrderStatus status, String routeId, Double price, String fromPlace, String toPlace, Double distance, String duration, String staticMapUrl, UserPurchaseModel user) {
        super(id, orderDate, status, routeId, price);
        FromPlace = fromPlace;
        ToPlace = toPlace;
        Distance = distance;
        Duration = duration;
        StaticMapUrl = staticMapUrl;
        User = user;
    }
}

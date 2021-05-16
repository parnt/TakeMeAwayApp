package Models.OrdersModels;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import javax.xml.datatype.Duration;

import Models.DriversModels.DriverHeader;
import Models.DriversModels.DriverHeaderExt;
import Models.DriversModels.DriverIdHeader;

/// <summary>
/// Model created for extended Order details
/// </summary>
public class OrderDetails extends OrdersList implements IOrderPlaces, IOrderDistanceAndDuration {
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

    /// <summary>
    /// Trip Distance
    /// </summary>
    @SerializedName("distance")
    public Double Distance;

    /// <summary>
    /// Trip Duration
    /// </summary>
    @SerializedName("duration")
    public String Duration;

    public OrderDetails(Integer id, String orderDate, OrderStatus status, String routeId, Double price, DriverHeaderExt driver, String fromPlace, String toPlace, Double distance, String duration) {
        super(id, orderDate, status, routeId, price, driver);
        FromPlace = fromPlace;
        ToPlace = toPlace;
        Distance = distance;
        Duration = duration;
    }
}

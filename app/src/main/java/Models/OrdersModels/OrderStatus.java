package Models.OrdersModels;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Order status
/// </summary>
public class OrderStatus extends OrderStatusHeader {
    /// <summary>
    /// Status of order
    /// </summary>
    @SerializedName("status")
    public String Status;

    public OrderStatus(Integer id, String status) {
        super(id);
        Status = status;
    }
}

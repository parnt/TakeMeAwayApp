package Models.OrdersModels;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Order status header
/// </summary>
public class OrderStatusHeader {
    /// <summary>
    /// Status header
    /// </summary>
    @SerializedName("id")
    public Integer Id;

    public OrderStatusHeader(Integer id) {
        Id = id;
    }
}

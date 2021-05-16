package Models.OrdersModels;

import com.google.gson.annotations.SerializedName;

/// <summary>
/// Model with order ID
/// </summary>
public class OrderIdHeader {
    /// <summary>
    /// Order ID
    /// </summary>
    @SerializedName("id")
    public Integer Id;

    public OrderIdHeader(Integer id) {
        Id = id;
    }
}

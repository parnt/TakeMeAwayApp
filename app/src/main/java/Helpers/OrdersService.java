package Helpers;

import java.util.List;
import java.util.Map;

import Models.OrdersModels.OrderDetails;
import Models.OrdersModels.OrderIdHeader;
import Models.OrdersModels.OrdersList;
import Models.QueryResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface OrdersService {
    @GET("api/Orders/")
    Call<QueryResult<List<OrdersList>>> GetOrders(@Header("Authorization") String authorization);
    //, @QueryMap Map<String, Integer> pagination, @Query("sort")SortPatterns.OrdersSort sort

    @GET("api/Orders/{id}")
    Call<QueryResult<OrderDetails>> GetSpecificOrder(@Header("Authorization") String authorization, @Path("id") Integer id);

    @POST("api/Orders/")
    Call<QueryResult<OrderIdHeader>> CreateOrder(@Header("Authorization") String authorization, @Query("driverId") Integer driverId, @Query("routeId") String routeId);
}

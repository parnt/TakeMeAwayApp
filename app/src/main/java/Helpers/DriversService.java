package Helpers;

import java.util.List;
import java.util.Map;

import Models.DriversModels.DriverModel;
import Models.DriversModels.DriverModelExt;
import Models.DriversModels.DriverModelHeaderExt;
import Models.DriversModels.DriverOrder;
import Models.DriversModels.DriverOrdersList;
import Models.OrdersModels.OrderStatuses;
import Models.QueryResult;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface DriversService {
    @GET("api/Drivers/Orders")
    Call<QueryResult<List<DriverOrdersList>>> GetDriverOrders(@Header("Authorization") String authorization);
    //, @QueryMap Map<String, Integer> pagination, @Query("sort")SortPatterns.DriverOrdersSort sort

    @GET("api/Drivers/Orders/{id}")
    Call<QueryResult<DriverOrder>> GetSpecificDriverOrder(@Header("Authorization") String authorization, @Path("id") Integer id);

    @POST("api/Drivers/")
    Call<QueryResult<Object>> CreateDriver(@Header("Authorization") String authorization, @Body DriverModel driverModel);

    @GET("api/Drivers/Summary")
    Call<QueryResult<DriverModelExt>> GetDriverSummary(@Header("Authorization") String authorization);

    @PUT("api/Drivers/")
    Call<QueryResult<Object>> UpdateDriver(@Header("Authorization") String authorization, @Body DriverModel driverModel);

    @DELETE("api/Drivers/")
    Call<QueryResult<Object>> DeactivateDriver(@Header("Authorization") String authorization);

    @PUT("api/Drivers/Orders/{id}")
    Call<QueryResult<Object>> ChangeOrderStatus(@Header("Authorization") String authorization, @Path("id") Integer id, @Query("orderStatus")String orderStatus);

    @GET("api/Drivers/")
    Call<QueryResult<List<DriverModelHeaderExt>>> GetDrivers(@Header("Authorization") String authorization, @Query("routeId") String routeId);
    //, @QueryMap Map<String, Integer> pagination, @Query("sort")SortPatterns.DriversSort sort

    @GET("api/Drivers/{id}")
    Call<QueryResult<DriverModelHeaderExt>> GetDriverForOrder(@Header("Authorization") String authorization, @Path("id") Integer id, @Query("routeId") String routeId);
}

package Helpers;

import Models.DirectionsModel.DirectionResult;
import Models.DirectionsModel.DirectionResultHeader;
import Models.DirectionsModel.DirectionsInputModel;
import Models.QueryResult;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DirectionsService {
    @POST("api/Directions/")
    Call<QueryResult<DirectionResult>> CreateDirections(@Header("Authorization") String authorization, @Body DirectionsInputModel directionsInputModel);

    @GET("api/Directions/{id}")
    Call<QueryResult<DirectionResultHeader>> GetDirections(@Header("Authorization") String authorization, @Path("id") String id);
}

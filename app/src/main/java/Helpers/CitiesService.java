package Helpers;

import java.util.List;

import Models.CitiesModels.CityDetails;
import Models.QueryResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface CitiesService {
    @GET("api/Cities/")
    Call<QueryResult<List<CityDetails>>> GetCities(@Header("Authorization") String authorization);
}

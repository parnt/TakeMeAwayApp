package Helpers;

import java.util.List;

import Models.DaysModels.DayDetails;
import Models.DaysModels.DayDetailsExt;
import Models.QueryResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface DaysService {
    @GET("api/Days/")
    Call<QueryResult<List<DayDetailsExt>>> GetDays(@Header("Authorization") String authorization);
}

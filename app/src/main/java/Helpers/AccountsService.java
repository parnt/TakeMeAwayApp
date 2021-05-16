package Helpers;

import java.util.Map;

import Models.AccountsModels.AccountDetails;
import Models.AccountsModels.AccountSummaryResult;
import Models.AccountsModels.ChangePasswordModel;
import Models.AccountsModels.LoginModel;
import Models.AccountsModels.ResetPasswordModel;
import Models.AccountsModels.UserHeader;
import Models.AccountsModels.UserRegistration;
import Models.QueryResult;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface AccountsService {
    @POST("api/Accounts/RegisterAccount")
    Call<QueryResult<Object>> RegisterAccount(@Header("Authorization") String authorization, @Body UserRegistration userRegistration);

    @POST("api/Accounts/Login")
    Call<QueryResult<String>> LoginUser(@Header("Authorization") String authorization, @Body LoginModel loginModel);

    @PUT("api/Accounts/Update")
    Call<QueryResult<Object>> UpdateUser(@Header("Authorization") String authorization, @Body UserHeader userHeader);

    @PUT("api/Accounts/ChangePassword")
    Call<QueryResult<Object>> ChangePassword(@Header("Authorization") String authorization, @Body ChangePasswordModel changePasswordModel);

    @GET("api/Accounts/Details")
    Call<QueryResult<AccountDetails>> GetAccountDetails(@Header("Authorization") String authorization);

    @GET("api/Accounts/Summary")
    Call<QueryResult<AccountSummaryResult>> GetUserSummary(@Header("Authorization") String authorization);

    @DELETE("api/Accounts/")
    Call<QueryResult<Object>> DeleteAccount(@Header("Authorization") String authorization);

    @POST("api/Accounts/ForgotPassword")
    Call<QueryResult<Object>> ForgotPassword(@Header("Authorization") String authorization, @Query("email") String email);

    @PUT("api/Accounts/ResetPassword")
    Call<QueryResult<Object>> ResetPassword(@Header("Authorization") String authorization, @Query("email") String email, @Body ResetPasswordModel resetPasswordModel);

    @GET("api/Accounts/IsLogged")
    Call<Boolean> isUserLogged(@Header("Authorization") String authorization);
}

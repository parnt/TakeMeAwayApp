package com.app.takemeaway.takemeaway;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import Helpers.AccountsService;
import Models.AccountsModels.ChangePasswordModel;
import Models.AccountsModels.ResetPasswordModel;
import Models.ErrorCollection;
import Models.OperationError;
import Models.QueryResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResetPasswordActivity extends AppCompatActivity {
    //start global
    private SharedPreferences preferences;
    private SharedPreferences.Editor preferencesEdit;
    String accessToken;
    String forgotPasswordEmail;
    //end global

    //start elements
    EditText token;
    EditText password;
    EditText confirmPassword;
    //end elements


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        //start global
        preferences = getSharedPreferences("TakeMeAwayApp", 0);
        accessToken = preferences.getString("accessToken", "");
        forgotPasswordEmail = preferences.getString("forgotPasswordEmail", "");
        //end global

        //start elements
        token = (EditText) findViewById(R.id.text_token);
        password = (EditText) findViewById(R.id.text_password_reset);
        confirmPassword = (EditText) findViewById(R.id.text_confirmPassword_reset);
        //end elements
    }

    public void resetPassword(View view) {
        //start model
        ResetPasswordModel resetPasswordModel = new ResetPasswordModel();
        resetPasswordModel.Token = token.getText().toString();
        resetPasswordModel.Password = password.getText().toString();
        resetPasswordModel.ConfirmPassword = confirmPassword.getText().toString();
        //end model

        //start http request - put account reset password
        Retrofit.Builder builderAccountResetPassword = new Retrofit.Builder().baseUrl(getString(R.string.baseUrl))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofitAccountResetPassword = builderAccountResetPassword.build();

        AccountsService accountsServiceAccountResetPassword = retrofitAccountResetPassword.create(AccountsService.class);
        Call<QueryResult<Object>> callAccountResetPassword = accountsServiceAccountResetPassword.ResetPassword(accessToken, forgotPasswordEmail, resetPasswordModel);


        callAccountResetPassword.enqueue(new Callback<QueryResult<Object>>() {
            @Override
            public void onResponse(Call<QueryResult<Object>> call, Response<QueryResult<Object>> response) {
                if (response.code() == 400) {
                    //wykonanie HTTP
                    String responseJson = null;
                    try {
                        responseJson = response.errorBody().string();
                    } catch (IOException e) {
                        Toast.makeText(ResetPasswordActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    Gson gson = new GsonBuilder().create();

                    ErrorCollection errorResponseValidation;
                    OperationError errorResponseAuthorization;

                    errorResponseValidation = gson.fromJson(responseJson, ErrorCollection.class);

                    if(errorResponseValidation.Errors != null) {
                        //Jeżeli błędy walidacji
                        for (OperationError error : errorResponseValidation.Errors) {
                            switch(error.ErrorDetails.Field)
                            {
                                case "email":
                                {
                                    Toast.makeText(ResetPasswordActivity.this, error.ErrorDetails.Message, Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(ResetPasswordActivity.this, ForgotPasswordActivity.class);
                                    startActivity(intent);
                                    break;
                                }
                                case "Token":
                                {
                                    token.setError(error.ErrorDetails.Message);
                                    break;
                                }
                                case "Password":
                                {
                                    password.setError(error.ErrorDetails.Message);
                                    break;
                                }
                                case "ConfirmPassword":
                                {
                                    confirmPassword.setError(error.ErrorDetails.Message);
                                    break;
                                }
                                default:
                                {
                                    Toast.makeText(ResetPasswordActivity.this, error.ErrorDetails.Message, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    } else {
                        //Jeżeli nie błędy walidacji to..
                        errorResponseAuthorization = gson.fromJson(responseJson, OperationError.class);

                        Toast.makeText(ResetPasswordActivity.this, errorResponseAuthorization.ErrorDetails.Message, Toast.LENGTH_LONG).show();

                        if (errorResponseAuthorization.ErrorDetails.Code == "500") {
                            //jeżeli błąd Bazy danych
                            Toast.makeText(ResetPasswordActivity.this, "Wysyłam raport", Toast.LENGTH_LONG).show();
                        } else {
                            //jeżeli błąd autoryzacji
                            Intent intent = new Intent(ResetPasswordActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                    }
                } else if (response.code() == 200){
                    //Jeżeli sukces
                    QueryResult<Object> result = response.body();

                    if (result != null) {
                        Toast.makeText(ResetPasswordActivity.this, result.Message, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                } else {
                    //jeżeli brak internetu
                    Toast.makeText(ResetPasswordActivity.this, "Brak połączenia z Internetem", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<QueryResult<Object>> call, Throwable t) {
                Toast.makeText(ResetPasswordActivity.this, "Coś poszło nie tak: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        //end http request - put account reset password
    }

    public void backButton(View view) {
        super.onBackPressed();
    }
}

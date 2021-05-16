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
import Models.ErrorCollection;
import Models.OperationError;
import Models.QueryResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChangePasswordActivity extends AppCompatActivity {
    //start global
    private SharedPreferences preferences;
    private SharedPreferences.Editor preferencesEdit;
    String accessToken;
    //end global

    //start elements
    EditText oldPassword;
    EditText password;
    EditText confirmPassword;
    //end elements

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        //start global
        preferences = getSharedPreferences("TakeMeAwayApp", 0);
        accessToken = preferences.getString("accessToken", "");
        //end global

        //start elements
        oldPassword = (EditText) findViewById(R.id.text_oldPassword);
        password = (EditText) findViewById(R.id.text_password);
        confirmPassword = (EditText) findViewById(R.id.text_confirmPassword);
        //end elements
    }

    public void changePassword(View view) {
        //start model
        ChangePasswordModel changePasswordModel = new ChangePasswordModel();
        changePasswordModel.OldPassword = oldPassword.getText().toString();
        changePasswordModel.Password = password.getText().toString();
        changePasswordModel.ConfirmPassword = confirmPassword.getText().toString();
        //end model

        //start http request - put account change password
        Retrofit.Builder builderAccountChangePassword = new Retrofit.Builder().baseUrl(getString(R.string.baseUrl))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofitAccountChangePassword = builderAccountChangePassword.build();

        AccountsService accountsServiceAccountChangePassword = retrofitAccountChangePassword.create(AccountsService.class);
        Call<QueryResult<Object>> callAccountChangePassword = accountsServiceAccountChangePassword.ChangePassword(accessToken, changePasswordModel);


        callAccountChangePassword.enqueue(new Callback<QueryResult<Object>>() {
            @Override
            public void onResponse(Call<QueryResult<Object>> call, Response<QueryResult<Object>> response) {
                if (response.code() == 400) {
                    //wykonanie HTTP
                    String responseJson = null;
                    try {
                        responseJson = response.errorBody().string();
                    } catch (IOException e) {
                        Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                                case "OldPassword":
                                {
                                    oldPassword.setError(error.ErrorDetails.Message);
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
                                    Toast.makeText(ChangePasswordActivity.this, error.ErrorDetails.Message, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    } else {
                        //Jeżeli nie błędy walidacji to..
                        errorResponseAuthorization = gson.fromJson(responseJson, OperationError.class);

                        Toast.makeText(ChangePasswordActivity.this, errorResponseAuthorization.ErrorDetails.Message, Toast.LENGTH_LONG).show();

                        if (errorResponseAuthorization.ErrorDetails.Code == "500") {
                            //jeżeli błąd Bazy danych
                            Toast.makeText(ChangePasswordActivity.this, "Wysyłam raport", Toast.LENGTH_LONG).show();
                        } else {
                            //jeżeli błąd autoryzacji
                            Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                } else if (response.code() == 200){
                    //Jeżeli sukces
                    QueryResult<Object> result = response.body();

                    if (result != null) {
                        Toast.makeText(ChangePasswordActivity.this, result.Message, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(ChangePasswordActivity.this, SettingsActivity.class);
                        startActivity(intent);
                    }
                } else {
                    //jeżeli brak internetu
                    Toast.makeText(ChangePasswordActivity.this, "Brak połączenia z Internetem", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<QueryResult<Object>> call, Throwable t) {
                Toast.makeText(ChangePasswordActivity.this, "Coś poszło nie tak: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        //end http request - put account change password
    }

    public void backButton(View view) {
        super.onBackPressed();
    }
}

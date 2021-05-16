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
import Models.ErrorCollection;
import Models.OperationError;
import Models.QueryResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForgotPasswordActivity extends AppCompatActivity {
    //start global
    private SharedPreferences preferences;
    private SharedPreferences.Editor preferencesEdit;
    String accessToken;
    String forgotEmail;
    //end global

    //start elements
    EditText email;
    //end elements

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //start global
        preferences = getSharedPreferences("TakeMeAwayApp", 0);
        accessToken = preferences.getString("accessToken", "");
        forgotEmail = getIntent().getStringExtra("forgotEmail");
        //end global

        //start elements
        email = (EditText) findViewById(R.id.text_login_forgot);
        //end elements

        //start fill
        if (!forgotEmail.isEmpty()) {
            email.setText(forgotEmail, EditText.BufferType.EDITABLE);
        }
        //end fill
    }

    public void sendKey(View view) {
        //start model
            final String Email = email.getText().toString();
        //end model

        //start http request - put account forgot password
        Retrofit.Builder builderAccountForgotPassword = new Retrofit.Builder().baseUrl(getString(R.string.baseUrl))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofitAccountForgotPassword = builderAccountForgotPassword.build();

        AccountsService accountsServiceAccountForgotPassword = retrofitAccountForgotPassword.create(AccountsService.class);
        Call<QueryResult<Object>> callAccountForgotPassword = accountsServiceAccountForgotPassword.ForgotPassword(accessToken, Email);


        callAccountForgotPassword.enqueue(new Callback<QueryResult<Object>>() {
            @Override
            public void onResponse(Call<QueryResult<Object>> call, Response<QueryResult<Object>> response) {
                if (response.code() == 400) {
                    //wykonanie HTTP
                    String responseJson = null;
                    try {
                        responseJson = response.errorBody().string();
                    } catch (IOException e) {
                        Toast.makeText(ForgotPasswordActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                                    email.setError(error.ErrorDetails.Message);
                                    break;
                                }
                                default:
                                {
                                    Toast.makeText(ForgotPasswordActivity.this, error.ErrorDetails.Message, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    } else {
                        //Jeżeli nie błędy walidacji to..
                        errorResponseAuthorization = gson.fromJson(responseJson, OperationError.class);

                        Toast.makeText(ForgotPasswordActivity.this, errorResponseAuthorization.ErrorDetails.Message, Toast.LENGTH_LONG).show();

                        if (errorResponseAuthorization.ErrorDetails.Code == "500") {
                            //jeżeli błąd Bazy danych
                            Toast.makeText(ForgotPasswordActivity.this, "Wysyłam raport", Toast.LENGTH_LONG).show();
                        } else {
                            //jeżeli błąd autoryzacji
                            Intent intent = new Intent(ForgotPasswordActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                    }
                } else if (response.code() == 200){
                    //Jeżeli sukces
                    QueryResult<Object> result = response.body();

                    if (result != null) {
                        Toast.makeText(ForgotPasswordActivity.this, result.Message, Toast.LENGTH_SHORT).show();

                        preferencesEdit = preferences.edit();
                        preferencesEdit.putString("forgotPasswordEmail", Email);
                        preferencesEdit.commit();

                        Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                        startActivity(intent);
                    }
                } else {
                    //jeżeli brak internetu
                    Toast.makeText(ForgotPasswordActivity.this, "Brak połączenia z Internetem", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<QueryResult<Object>> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, "Coś poszło nie tak: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        //end http request - put account forgot password
    }

    public void haveKey(View view) {
        Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
        startActivity(intent);
    }

    public void backButton(View view) {
        super.onBackPressed();
    }
}

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
import Models.AccountsModels.ResetPasswordModel;
import Models.AccountsModels.UserRegistration;
import Models.ErrorCollection;
import Models.OperationError;
import Models.QueryResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {
    //start global
    private SharedPreferences preferences;
    private SharedPreferences.Editor preferencesEdit;
    String accessToken;
    //end global

    //start elements
    EditText firstName;
    EditText lastName;
    EditText login;
    EditText phoneNumber;
    EditText password;
    EditText confirmPassword;
    //end elements

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //start global
        preferences = getSharedPreferences("TakeMeAwayApp", 0);
        accessToken = preferences.getString("accessToken", "");
        //end global

        //start elements
        firstName = (EditText) findViewById(R.id.text_firstname_register);
        lastName = (EditText) findViewById(R.id.text_lastname_register);
        login = (EditText) findViewById(R.id.text_login_register);
        phoneNumber = (EditText) findViewById(R.id.text_phoneNumber_register);
        password = (EditText) findViewById(R.id.text_password_register);
        confirmPassword = (EditText) findViewById(R.id.text_confirmPassword_register);
        //end elements
    }

    public void registerAccount(View view) {
        //start model
        final UserRegistration userRegistration = new UserRegistration();
        userRegistration.FirstName = firstName.getText().toString();
        userRegistration.LastName = lastName.getText().toString();
        userRegistration.Email = login.getText().toString();
        userRegistration.PhoneNumber = phoneNumber.getText().toString();
        userRegistration.Password = password.getText().toString();
        userRegistration.ConfirmPassword = confirmPassword.getText().toString();
        //end model

        //start http request - put account register
        Retrofit.Builder builderAccountRegister = new Retrofit.Builder().baseUrl(getString(R.string.baseUrl))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofitAccountRegister = builderAccountRegister.build();

        AccountsService accountsServiceAccountRegister = retrofitAccountRegister.create(AccountsService.class);
        Call<QueryResult<Object>> callAccountRegister = accountsServiceAccountRegister.RegisterAccount(accessToken, userRegistration);


        callAccountRegister.enqueue(new Callback<QueryResult<Object>>() {
            @Override
            public void onResponse(Call<QueryResult<Object>> call, Response<QueryResult<Object>> response) {
                if (response.code() == 400) {
                    //wykonanie HTTP
                    String responseJson = null;
                    try {
                        responseJson = response.errorBody().string();
                    } catch (IOException e) {
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                                case "FirstName":
                                {
                                    firstName.setError(error.ErrorDetails.Message);
                                    break;
                                }
                                case "LastName":
                                {
                                    lastName.setError(error.ErrorDetails.Message);
                                    break;
                                }
                                case "Email":
                                {
                                    login.setError(error.ErrorDetails.Message);
                                    break;
                                }
                                case "PhoneNumber":
                                {
                                    phoneNumber.setError(error.ErrorDetails.Message);
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
                                    Toast.makeText(RegisterActivity.this, error.ErrorDetails.Message, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    } else {
                        //Jeżeli nie błędy walidacji to..
                        errorResponseAuthorization = gson.fromJson(responseJson, OperationError.class);

                        Toast.makeText(RegisterActivity.this, errorResponseAuthorization.ErrorDetails.Message, Toast.LENGTH_LONG).show();

                        if (errorResponseAuthorization.ErrorDetails.Code == "500") {
                            //jeżeli błąd Bazy danych
                            Toast.makeText(RegisterActivity.this, "Wysyłam raport", Toast.LENGTH_LONG).show();
                        } else {
                            //jeżeli błąd autoryzacji
                            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                    }
                } else if (response.code() == 200){
                    //Jeżeli sukces
                    QueryResult<Object> result = response.body();

                    if (result != null) {
                        Toast.makeText(RegisterActivity.this, result.Message, Toast.LENGTH_SHORT).show();

                        preferencesEdit = preferences.edit();
                        preferencesEdit.putString("login", userRegistration.Email);
                        preferencesEdit.putString("password", userRegistration.Password);
                        preferencesEdit.commit();

                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                } else {
                    //jeżeli brak internetu
                    Toast.makeText(RegisterActivity.this, "Brak połączenia z Internetem", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<QueryResult<Object>> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Coś poszło nie tak: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        //end http request - put account register
    }

    public void backButton(View view) {
        super.onBackPressed();
    }
}

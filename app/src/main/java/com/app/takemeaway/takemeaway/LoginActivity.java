package com.app.takemeaway.takemeaway;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import Helpers.AccountsService;
import Models.AccountsModels.LoginModel;
import Models.AccountsModels.UserRegistration;
import Models.ErrorCollection;
import Models.OperationError;
import Models.QueryResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    //start global
    private SharedPreferences preferences;
    private SharedPreferences.Editor preferencesEdit;
    String accessToken;
    String loginGlobal;
    String passwordGlobal;
    //end global

    //start elements
    EditText login;
    EditText password;
    Switch remember;
    //end elements

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //start global
        preferences = getSharedPreferences("TakeMeAwayApp", 0);
        accessToken = preferences.getString("accessToken", "");
        loginGlobal = preferences.getString("login", "");
        passwordGlobal = preferences.getString("password", "");
        //end global

        //start elements
        login = (EditText) findViewById(R.id.text_login_login);
        password = (EditText) findViewById(R.id.text_password_login);
        remember = (Switch) findViewById(R.id.switch_remember);
        //end elements

        //start fill
        if (!loginGlobal.isEmpty() && !passwordGlobal.isEmpty()) {
           login.setText(loginGlobal, EditText.BufferType.EDITABLE);
           password.setText(passwordGlobal, EditText.BufferType.EDITABLE);
        }
        //end fill
    }

    public void forgotPassword(View view) {
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        if(!login.getText().toString().isEmpty()) {
            intent.putExtra("forgotEmail", login.getText().toString());
        }
        startActivity(intent);
    }

    public void registerAccount(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void loginAccount(View view) {
        //start model
        final LoginModel loginModel = new LoginModel();
        loginModel.Email = login.getText().toString();
        loginModel.Password = password.getText().toString();
        loginModel.RememberMe = remember.isChecked();
        //end model

        //start http request - put account login
        Retrofit.Builder builderAccountLogin = new Retrofit.Builder().baseUrl(getString(R.string.baseUrl))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofitAccountLogin = builderAccountLogin.build();

        AccountsService accountsServiceAccountLogin = retrofitAccountLogin.create(AccountsService.class);
        Call<QueryResult<String>> callAccountLogin = accountsServiceAccountLogin.LoginUser(accessToken, loginModel);


        callAccountLogin.enqueue(new Callback<QueryResult<String>>() {
            @Override
            public void onResponse(Call<QueryResult<String>> call, Response<QueryResult<String>> response) {
                if (response.code() == 400) {
                    //wykonanie HTTP
                    String responseJson = null;
                    try {
                        responseJson = response.errorBody().string();
                    } catch (IOException e) {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                                case "Email":
                                {
                                    login.setError(error.ErrorDetails.Message);
                                    break;
                                }
                                case "Password":
                                {
                                    password.setError(error.ErrorDetails.Message);
                                    break;
                                }
                                default:
                                {
                                    Toast.makeText(LoginActivity.this, error.ErrorDetails.Message, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    } else {
                        //Jeżeli nie błędy walidacji to..
                        errorResponseAuthorization = gson.fromJson(responseJson, OperationError.class);

                        Toast.makeText(LoginActivity.this, errorResponseAuthorization.ErrorDetails.Message, Toast.LENGTH_LONG).show();

                        if (errorResponseAuthorization.ErrorDetails.Code == "500") {
                            //jeżeli błąd Bazy danych
                            Toast.makeText(LoginActivity.this, "Wysyłam raport", Toast.LENGTH_LONG).show();
                        } else {
                            //jeżeli błąd autoryzacji
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                    }
                } else if (response.code() == 200){
                    //Jeżeli sukces
                    QueryResult<String> result = response.body();

                    if (result != null) {
                        Toast.makeText(LoginActivity.this, result.Message, Toast.LENGTH_SHORT).show();

                        preferencesEdit = preferences.edit();
                        preferencesEdit.putString("accessToken", "Bearer " + result.Items);
                        preferencesEdit.putString("login", loginModel.Email);
                        preferencesEdit.putString("password", loginModel.Password);
                        preferencesEdit.commit();

                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                } else {
                    //jeżeli brak internetu
                    Toast.makeText(LoginActivity.this, "Brak połączenia z Internetem", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<QueryResult<String>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Coś poszło nie tak: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        //end http request - put account login
    }

    //TODO - odkomentować
    /*@Override
    public void onBackPressed() {
    }*/
}

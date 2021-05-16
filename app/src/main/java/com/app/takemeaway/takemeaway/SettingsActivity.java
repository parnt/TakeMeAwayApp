package com.app.takemeaway.takemeaway;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import Helpers.AccountsService;
import Helpers.OnSwipeTouchListener;
import Models.AccountsModels.AccountDetails;
import Models.AccountsModels.AccountSummaryResult;
import Models.AccountsModels.UserHeader;
import Models.ErrorCollection;
import Models.OperationError;
import Models.QueryResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Header;

public class SettingsActivity extends AppCompatActivity {
    //start menu
    View layoutContent; //wszystko po za menu i przyciskiem

    View menu;
    boolean isOpened;
    //end menu

    //start global
    private SharedPreferences preferences;
    private SharedPreferences.Editor preferencesEdit;
    String accessToken;
    //end global

    //start elements
    TextView menuEmail;
    Button menuBecomeDriver;
    Button menuPurchases;
    Button menuDriverSettings;
    ImageView menuBecomeDriverPicture;
    ImageView menuPurchasesPicture;
    ImageView menuDriverSettingsPicture;
    EditText firstName;
    EditText lastName;
    EditText login;
    EditText phoneNumber;
    Button becomeDriver;
    //end elements

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //start menu
        layoutContent = (View) findViewById(R.id.layoutContent); //wszystko po za menu i przyciskiem

        menu = (View) findViewById(R.id.menuViev);
        menu.setVisibility(View.INVISIBLE);
        setClickable(menu, false);
        isOpened = false;

        findViewById(R.id.layoutContent).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event){
                //return gestureScanner.onTouchEvent(event);
                if (isOpened) {
                    slideLeft(menu);
                    isOpened = false;
                }
                return true;
            }
        });

        findViewById(R.id.layoutContent).setOnTouchListener(new OnSwipeTouchListener(SettingsActivity.this) {
            public void onSwipeLeft() {
                if (isOpened) {
                    slideLeft(menu);
                    isOpened = false;
                }
            }
        });
        //end menu

        //start global
        preferences = getSharedPreferences("TakeMeAwayApp", 0);
        accessToken = preferences.getString("accessToken", "");
        //end global

        //start elements
        menuEmail = (TextView) findViewById(R.id.label_manuEmail) ;
        menuBecomeDriver = (Button) findViewById(R.id.button_menuRegisterDriver);
        menuPurchases = (Button) findViewById(R.id.button_menuPurchases);
        menuDriverSettings = (Button) findViewById(R.id.button_menuDriverSettings);
        menuBecomeDriverPicture = (ImageView) findViewById(R.id.imageView6);
        menuPurchasesPicture = (ImageView) findViewById(R.id.imageView7);
        menuDriverSettingsPicture = (ImageView) findViewById(R.id.imageView8);
        firstName = (EditText) findViewById(R.id.text_firstname);
        lastName = (EditText) findViewById(R.id.text_lastname);
        login = (EditText) findViewById(R.id.text_login);
        phoneNumber = (EditText) findViewById(R.id.text_phoneNumber);
        becomeDriver = (Button) findViewById(R.id.button_becomeDriver);
        //end elements

        //start http request - get Account Summary
        Retrofit.Builder builderAccountSummary = new Retrofit.Builder().baseUrl(getString(R.string.baseUrl))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofitAccountSummary = builderAccountSummary.build();

        AccountsService accountsServiceAccountSummary = retrofitAccountSummary.create(AccountsService.class);
        Call<QueryResult<AccountSummaryResult>> callAccountSummary = accountsServiceAccountSummary.GetUserSummary(accessToken);


        callAccountSummary.enqueue(new Callback<QueryResult<AccountSummaryResult>>() {
            @Override
            public void onResponse(Call<QueryResult<AccountSummaryResult>> call, Response<QueryResult<AccountSummaryResult>> response) {
                if (response.code() == 400) {
                    //wykonanie HTTP
                    String responseJson = null;
                    try {
                        responseJson = response.errorBody().string();
                    } catch (IOException e) {
                        Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    Gson gson = new GsonBuilder().create();

                    ErrorCollection errorResponseValidation;
                    OperationError errorResponseAuthorization;

                    errorResponseValidation = gson.fromJson(responseJson, ErrorCollection.class);

                    if(errorResponseValidation.Errors != null) {
                        //Jeżeli błędy walidacji (nie ma walidacji)
                        String errors = new String();

                        for (OperationError error : errorResponseValidation.Errors) {
                            errors += error.ErrorDetails.Message + "\n";
                        }

                        Toast.makeText(SettingsActivity.this, errors, Toast.LENGTH_LONG).show();
                    } else {
                        //Jeżeli nie błędy walidacji to..
                        errorResponseAuthorization = gson.fromJson(responseJson, OperationError.class);

                        Toast.makeText(SettingsActivity.this, errorResponseAuthorization.ErrorDetails.Message, Toast.LENGTH_LONG).show();

                        if (errorResponseAuthorization.ErrorDetails.Code == "500") {
                            //jeżeli błąd Bazy danych
                            Toast.makeText(SettingsActivity.this, "Wysyłam raport", Toast.LENGTH_LONG).show();
                        } else {
                            //jeżeli błąd autoryzacji
                            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                } else if (response.code() == 200){
                    //Jeżeli sukces
                    QueryResult<AccountSummaryResult> result = response.body();

                    if (result != null) {
                        firstName.setText(result.Items.FirstName);
                        lastName.setText(result.Items.LastName);
                        login.setText(result.Items.Email);
                        phoneNumber.setText(result.Items.PhoneNumber);
                    }
                } else {
                    //jeżeli brak internetu
                    Toast.makeText(SettingsActivity.this, "Brak połączenia z Internetem", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<QueryResult<AccountSummaryResult>> call, Throwable t) {
                Toast.makeText(SettingsActivity.this, "Coś poszło nie tak: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        //end http request - get Account Summary

        //start delay
        try {
            TimeUnit.MILLISECONDS.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //end delay

        //start http request - get Account Details
        Retrofit.Builder builderAccountDetails = new Retrofit.Builder().baseUrl(getString(R.string.baseUrl))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofitAccoundDetails = builderAccountDetails.build();

        AccountsService accountsServiceAccountDetails = retrofitAccoundDetails.create(AccountsService.class);
        Call<QueryResult<AccountDetails>> callAccounDetails = accountsServiceAccountDetails.GetAccountDetails(accessToken);


        callAccounDetails.enqueue(new Callback<QueryResult<AccountDetails>>() {
            @Override
            public void onResponse(Call<QueryResult<AccountDetails>> call, Response<QueryResult<AccountDetails>> response) {
                if (response.code() == 400) {
                    //wykonanie HTTP
                    String responseJson = null;
                    try {
                        responseJson = response.errorBody().string();
                    } catch (IOException e) {
                        Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    Gson gson = new GsonBuilder().create();

                    ErrorCollection errorResponseValidation;
                    OperationError errorResponseAuthorization;

                    errorResponseValidation = gson.fromJson(responseJson, ErrorCollection.class);

                    if(errorResponseValidation.Errors != null) {
                        //Jeżeli błędy walidacji (nie ma walidacji)
                        String errors = new String();

                        for (OperationError error : errorResponseValidation.Errors) {
                            errors += error.ErrorDetails.Message + "\n";
                        }

                        Toast.makeText(SettingsActivity.this, errors, Toast.LENGTH_LONG).show();
                    } else {
                        //Jeżeli nie błędy walidacji to..
                        errorResponseAuthorization = gson.fromJson(responseJson, OperationError.class);

                        Toast.makeText(SettingsActivity.this, errorResponseAuthorization.ErrorDetails.Message, Toast.LENGTH_LONG).show();

                        if (errorResponseAuthorization.ErrorDetails.Code == "500") {
                            //jeżeli błąd Bazy danych
                            Toast.makeText(SettingsActivity.this, "Wysyłam raport", Toast.LENGTH_LONG).show();
                        } else {
                            //jeżeli błąd autoryzacji
                            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                } else if (response.code() == 200){
                    //Jeżeli sukces
                    QueryResult<AccountDetails> result = response.body();

                    if (result != null) {
                        menuEmail.setText(result.Items.Username);

                        if (!result.Items.IsDriver){
                            menuBecomeDriver.setVisibility(View.VISIBLE);
                            menuPurchases.setVisibility(View.INVISIBLE);
                            menuDriverSettings.setVisibility(View.INVISIBLE);
                            menuBecomeDriverPicture.setVisibility(View.VISIBLE);
                            menuPurchasesPicture.setVisibility(View.INVISIBLE);
                            menuDriverSettingsPicture.setVisibility(View.INVISIBLE);

                            becomeDriver.setVisibility(View.VISIBLE); //dotyczy już samych ustawień
                        } else {
                            menuBecomeDriver.setVisibility(View.INVISIBLE);
                            menuBecomeDriverPicture.setVisibility(View.INVISIBLE);
                        }
                    }
                } else {
                    //jeżeli brak internetu
                    Toast.makeText(SettingsActivity.this, "Brak połączenia z Internetem", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<QueryResult<AccountDetails>> call, Throwable t) {
                Toast.makeText(SettingsActivity.this, "Coś poszło nie tak: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        //end http request - get Account Details
    }

    public void changePassword(View view) {
        Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
        startActivity(intent);
    }

    public void saveSettings(View view) {
        //start model
        UserHeader userHeader = new UserHeader();
        userHeader.FirstName = firstName.getText().toString();
        userHeader.LastName = lastName.getText().toString();
        userHeader.PhoneNumber = phoneNumber.getText().toString();
        userHeader.Email = login.getText().toString();
        //end model

        //start http request - put account update
        Retrofit.Builder builderAccountUpdate = new Retrofit.Builder().baseUrl(getString(R.string.baseUrl))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofitAccountUpdate = builderAccountUpdate.build();

        AccountsService accountsServiceAccountUpdate = retrofitAccountUpdate.create(AccountsService.class);
        Call<QueryResult<Object>> callAccountUpdate = accountsServiceAccountUpdate.UpdateUser(accessToken, userHeader);


        callAccountUpdate.enqueue(new Callback<QueryResult<Object>>() {
            @Override
            public void onResponse(Call<QueryResult<Object>> call, Response<QueryResult<Object>> response) {
                if (response.code() == 400) {
                    //wykonanie HTTP
                    String responseJson = null;
                    try {
                        responseJson = response.errorBody().string();
                    } catch (IOException e) {
                        Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                                case "PhoneNumber":
                                {
                                    phoneNumber.setError(error.ErrorDetails.Message);
                                    break;
                                }
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
                                default:
                                {
                                    Toast.makeText(SettingsActivity.this, error.ErrorDetails.Message, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    } else {
                        //Jeżeli nie błędy walidacji to..
                        errorResponseAuthorization = gson.fromJson(responseJson, OperationError.class);

                        Toast.makeText(SettingsActivity.this, errorResponseAuthorization.ErrorDetails.Message, Toast.LENGTH_LONG).show();

                        if (errorResponseAuthorization.ErrorDetails.Code == "500") {
                            //jeżeli błąd Bazy danych
                            Toast.makeText(SettingsActivity.this, "Wysyłam raport", Toast.LENGTH_LONG).show();
                        } else {
                            //jeżeli błąd autoryzacji
                            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                } else if (response.code() == 200){
                    //Jeżeli sukces
                    QueryResult<Object> result = response.body();

                    if (result != null) {
                        Toast.makeText(SettingsActivity.this, result.Message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //jeżeli brak internetu
                    Toast.makeText(SettingsActivity.this, "Brak połączenia z Internetem", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<QueryResult<Object>> call, Throwable t) {
                Toast.makeText(SettingsActivity.this, "Coś poszło nie tak: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        //end http request - put account update
    }

    public void becomeDriver(View view) {
        Intent intent = new Intent(SettingsActivity.this, RegisterDriverActivity.class);
        startActivity(intent);
    }

    public void deleteAccount(View view) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
        dialogBuilder.setMessage("Czy jesteś pewny, że chcesz usunąć konto??")
                .setCancelable(false)
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //start http request - post account delete
                        Retrofit.Builder builderAccountDelete = new Retrofit.Builder().baseUrl(getString(R.string.baseUrl))
                                .addConverterFactory(GsonConverterFactory.create());
                        Retrofit retrofitAccountDelete = builderAccountDelete.build();

                        AccountsService accountsServiceAccountDelete = retrofitAccountDelete.create(AccountsService.class);
                        Call<QueryResult<Object>> callAccountDelete = accountsServiceAccountDelete.DeleteAccount(accessToken);


                        callAccountDelete.enqueue(new Callback<QueryResult<Object>>() {
                            @Override
                            public void onResponse(Call<QueryResult<Object>> call, Response<QueryResult<Object>> response) {
                                if (response.code() == 400) {
                                    //wykonanie HTTP
                                    String responseJson = null;
                                    try {
                                        responseJson = response.errorBody().string();
                                    } catch (IOException e) {
                                        Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }

                                    Gson gson = new GsonBuilder().create();

                                    ErrorCollection errorResponseValidation;
                                    OperationError errorResponseAuthorization;

                                    errorResponseValidation = gson.fromJson(responseJson, ErrorCollection.class);

                                    if(errorResponseValidation.Errors != null) {
                                        //Jeżeli błędy walidacji (nie ma walidacji)
                                        String errors = new String();

                                        for (OperationError error : errorResponseValidation.Errors) {
                                            errors += error.ErrorDetails.Message + "\n";
                                        }

                                        Toast.makeText(SettingsActivity.this, errors, Toast.LENGTH_LONG).show();
                                    } else {
                                        //Jeżeli nie błędy walidacji to..
                                        errorResponseAuthorization = gson.fromJson(responseJson, OperationError.class);

                                        Toast.makeText(SettingsActivity.this, errorResponseAuthorization.ErrorDetails.Message, Toast.LENGTH_LONG).show();

                                        if (errorResponseAuthorization.ErrorDetails.Code == "500") {
                                            //jeżeli błąd Bazy danych
                                            Toast.makeText(SettingsActivity.this, "Wysyłam raport", Toast.LENGTH_LONG).show();
                                        } else {
                                            //jeżeli błąd autoryzacji
                                            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                } else if (response.code() == 200){
                                    //Jeżeli sukces
                                    QueryResult<Object> result = response.body();

                                    if (result != null) {
                                        preferencesEdit = preferences.edit();
                                        preferencesEdit.putString("accessToken", "");
                                        preferencesEdit.commit();

                                        Toast.makeText(SettingsActivity.this, result.Message, Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    //jeżeli brak internetu
                                    Toast.makeText(SettingsActivity.this, "Brak połączenia z Internetem", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<QueryResult<Object>> call, Throwable t) {
                                Toast.makeText(SettingsActivity.this, "Coś poszło nie tak: " + t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                        //end http request - post account delete

                        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = dialogBuilder.create();
        alert.setTitle("Uwaga");
        alert.show();
    }

    public void menuButton(View view) {
        if(isOpened) {
            slideLeft(menu);
            isOpened = false;
        }
        else {
            slideRight(menu);
            isOpened = true;
        }
    }

    public void menuHome(View view) {
        Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    public void menuOrders(View view) {
        Intent intent = new Intent(SettingsActivity.this, OrdersActivity.class);
        startActivity(intent);
    }

    public void menuSettings(View view) {
        Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void menuBacomeDriver(View view) {
        Intent intent = new Intent(SettingsActivity.this, RegisterDriverActivity.class);
        startActivity(intent);
    }

    public void menuPurchases(View view) {
        Intent intent = new Intent(SettingsActivity.this, PurchasesActivity.class);
        startActivity(intent);
    }

    public void menuDriverSettings(View view) {
        Intent intent = new Intent(SettingsActivity.this, DriverSettingsActivity.class);
        startActivity(intent);
    }

    public void menuLogout(View view) {
        preferencesEdit = preferences.edit();
        preferencesEdit.putString("accessToken", "");
        preferencesEdit.commit();

        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        startActivity(intent);
    }



    ////////////////////////////////////////////////////////////////////////////////////////
    //start menu
    public void setClickable(View view, boolean set) {
        if (view != null) {
            view.setClickable(set);
            if (view instanceof ViewGroup) {
                ViewGroup vg = ((ViewGroup) view);
                for (int i = 0; i < vg.getChildCount(); i++) {
                    setClickable(vg.getChildAt(i), set);
                }
            }
        }
    }

    public void slideRight(View view){
        view.setVisibility(View.VISIBLE);
        setClickable(view, true);
        TranslateAnimation animate = new TranslateAnimation(
                -view.getWidth(),                 // fromXDelta
                0,                 // toXDelta
                0,  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);

        AlphaAnimation animation1 = new AlphaAnimation(1.0f, 0.3f);
        animation1.setDuration(500);
        animation1.setFillAfter(true);
        layoutContent.startAnimation(animation1);
    }

    // slide the view from its current position to below itself
    public void slideLeft(View view){
        view.setVisibility(View.INVISIBLE);
        setClickable(view, false);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                -view.getWidth(),                 // toXDelta
                0,                 // fromYDelta
                0); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);

        AlphaAnimation animation1 = new AlphaAnimation(0.3f, 1.0f);
        animation1.setDuration(500);
        animation1.setFillAfter(true);
        layoutContent.startAnimation(animation1);
    }
    //end menu
}

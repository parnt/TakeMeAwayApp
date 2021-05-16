package com.app.takemeaway.takemeaway;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import Helpers.AccountsService;
import Helpers.DriversService;
import Helpers.OnSwipeTouchListener;
import Helpers.OrdersService;
import Helpers.SortPatterns;
import Models.AccountsModels.AccountDetails;
import Models.DriversModels.DriverOrder;
import Models.ErrorCollection;
import Models.OperationError;
import Models.OrdersModels.OrderDetails;
import Models.OrdersModels.OrdersList;
import Models.QueryResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SpecificPurchaseActivity extends AppCompatActivity {
    //start global
    private SharedPreferences preferences;
    private SharedPreferences.Editor preferencesEdit;
    String accessToken;
    int specificPurchaseId;
    //end global

    //start elements
    TextView specificPurchaseDate;
    TextView specificPurchaseFirstName;
    TextView specificPurchaseLastName;
    TextView specificPurchasePrice;
    TextView specificPurchaseStatus;
    TextView specificPurchaseDistance;
    TextView specificPurchaseClientPhoneNumber;
    TextView specificPurchaseDuration;
    TextView specificPurchaseFromPlace;
    TextView specificPurchaseToPlace;
    ImageView imageStaticMap;
    Button changeStatus;
    //end elements

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_purchase);

        //start global
        preferences = getSharedPreferences("TakeMeAwayApp", 0);
        accessToken = preferences.getString("accessToken", "");

        Intent intent = getIntent ();
        Bundle extras = intent.getExtras();
        specificPurchaseId = extras.getInt("specific");
        //end global

        //start elements
        specificPurchaseDate = (TextView) findViewById(R.id.label_spiecificPurchaseDate);
        specificPurchaseFirstName = (TextView) findViewById(R.id.label_spiecificPurchaseFirstnameUser);
        specificPurchaseLastName = (TextView) findViewById(R.id.label_spiecificPurchaseLastnameUser);
        specificPurchasePrice = (TextView) findViewById(R.id.label_spiecificPurchasePrice);
        specificPurchaseStatus = (TextView) findViewById(R.id.label_spiecificPurchaseStatus);
        specificPurchaseDistance = (TextView) findViewById(R.id.label_spiecificPurchaseDistance);
        specificPurchaseClientPhoneNumber = (TextView) findViewById(R.id.label_spiecificPurchasePhoneNumberUser);
        specificPurchaseDuration = (TextView) findViewById(R.id.label_spiecificPurchaseTime);
        specificPurchaseFromPlace = (TextView) findViewById(R.id.label_spiecificPurchaseFromPlace);
        specificPurchaseToPlace = (TextView) findViewById(R.id.label_spiecificPurchaseToPlace);
        imageStaticMap =  (ImageView) findViewById(R.id.image_staticMap);
        changeStatus = (Button) findViewById(R.id.change_status);
        //end elements

        //start http request - get Purchase details
        Retrofit.Builder builderPurchase = new Retrofit.Builder().baseUrl(getString(R.string.baseUrl))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofitPurchase = builderPurchase.build();

        DriversService DriversServicePurchase = retrofitPurchase.create(DriversService.class);
        Call<QueryResult<DriverOrder>> callPurchase = DriversServicePurchase.GetSpecificDriverOrder(accessToken, specificPurchaseId);


        callPurchase.enqueue(new Callback<QueryResult<DriverOrder>>() {
            @Override
            public void onResponse(Call<QueryResult<DriverOrder>> call, Response<QueryResult<DriverOrder>> response) {
                if (response.code() == 400) {
                    //wykonanie HTTP
                    String responseJson = null;
                    try {
                        responseJson = response.errorBody().string();
                    } catch (IOException e) {
                        Toast.makeText(SpecificPurchaseActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

                        Toast.makeText(SpecificPurchaseActivity.this, errors, Toast.LENGTH_LONG).show();
                    } else {
                        //Jeżeli nie błędy walidacji to..
                        errorResponseAuthorization = gson.fromJson(responseJson, OperationError.class);

                        Toast.makeText(SpecificPurchaseActivity.this, errorResponseAuthorization.ErrorDetails.Message, Toast.LENGTH_LONG).show();

                        if (errorResponseAuthorization.ErrorDetails.Code == "500") {
                            //jeżeli błąd Bazy danych
                            Toast.makeText(SpecificPurchaseActivity.this, "Wysyłam raport", Toast.LENGTH_LONG).show();
                        } else {
                            //jeżeli błąd autoryzacji
                            Intent intent = new Intent(SpecificPurchaseActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                } else if (response.code() == 200){
                    //Jeżeli sukces
                    QueryResult<DriverOrder> result = response.body();

                    if (result != null) {
                        String[] date = result.Items.OrderDate.split("[T.]");
                        String[] fromPlace = result.Items.FromPlace.split(",");
                        String[] toPlace = result.Items.ToPlace.split(",");

                        specificPurchaseDate.setText(date[0] + ", " + date[1]);
                        specificPurchaseClientPhoneNumber.setText(result.Items.User.PhoneNumber);
                        specificPurchaseDistance.setText(result.Items.Distance.toString() + " km");
                        specificPurchaseDuration.setText(result.Items.Duration);
                        specificPurchaseFirstName.setText(result.Items.User.FirstName);
                        specificPurchaseFromPlace.setText(fromPlace[0] + ", " + (fromPlace[1].split(" "))[2]);
                        specificPurchaseLastName.setText(result.Items.User.LastName);
                        specificPurchasePrice.setText(result.Items.Price + " zł");
                        specificPurchaseStatus.setText(result.Items.Status.Status);
                        specificPurchaseToPlace.setText(toPlace[0] + ", " + (toPlace[1].split(" "))[2]);

                        if(result.Items.Status.Id == 1){
                            changeStatus.setText("Zmień status na \"W realizacji\"");
                            specificPurchaseStatus.setTextColor(Color.RED);
                        } else if(result.Items.Status.Id == 2) {
                            changeStatus.setText("Zmień status na \"W trasie\"");
                            specificPurchaseStatus.setTextColor(Color.rgb(255, 165, 0));
                        } else if(result.Items.Status.Id == 3) {
                            changeStatus.setText("Zmień status na \"Zrealizowane\"");
                            specificPurchaseStatus.setTextColor(Color.BLUE);
                        } else {
                            changeStatus.setClickable(false);
                            changeStatus.setVisibility(View.INVISIBLE);
                            specificPurchaseStatus.setTextColor(Color.GREEN);
                        }

                        Picasso.with(SpecificPurchaseActivity.this)
                                .load(result.Items.StaticMapUrl)
                                .into(imageStaticMap, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });
                    }
                } else {
                    //jeżeli brak internetu
                    Toast.makeText(SpecificPurchaseActivity.this, "Brak połączenia z Internetem", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<QueryResult<DriverOrder>> call, Throwable t) {
                Toast.makeText(SpecificPurchaseActivity.this, "Coś poszło nie tak: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        //end http request - get Purchase details
    }

    public void changeOrderStatus(View view) {
        String newStatus = "";

        if(changeStatus.getText() == "Zmień status na \"W realizacji\""){
            newStatus = "Processed";
        } else if(changeStatus.getText() == "Zmień status na \"W trasie\"") {
            newStatus = "OnWay";
        } else if(changeStatus.getText() == "Zmień status na \"Zrealizowane\"") {
            newStatus = "Done";
        }

        //start http request - put order change status
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(getString(R.string.baseUrl))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        DriversService driversService = retrofit.create(DriversService.class);
        Call<QueryResult<Object>> call = driversService.ChangeOrderStatus(accessToken, specificPurchaseId, newStatus);


        call.enqueue(new Callback<QueryResult<Object>>() {
            @Override
            public void onResponse(Call<QueryResult<Object>> call, Response<QueryResult<Object>> response) {
                if (response.code() == 400) {
                    //wykonanie HTTP
                    String responseJson = null;
                    try {
                        responseJson = response.errorBody().string();
                    } catch (IOException e) {
                        Toast.makeText(SpecificPurchaseActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

                        Toast.makeText(SpecificPurchaseActivity.this, errors, Toast.LENGTH_LONG).show();
                    } else {
                        //Jeżeli nie błędy walidacji to..
                        errorResponseAuthorization = gson.fromJson(responseJson, OperationError.class);

                        Toast.makeText(SpecificPurchaseActivity.this, errorResponseAuthorization.ErrorDetails.Message, Toast.LENGTH_LONG).show();

                        if (errorResponseAuthorization.ErrorDetails.Code == "500") {
                            //jeżeli błąd Bazy danych
                            Toast.makeText(SpecificPurchaseActivity.this, "Wysyłam raport", Toast.LENGTH_LONG).show();
                        } else {
                            //jeżeli błąd autoryzacji
                            Intent intent = new Intent(SpecificPurchaseActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                    }
                } else if (response.code() == 200){
                    //Jeżeli sukces
                    QueryResult<Object> result = response.body();

                    if (result != null) {
                        Toast.makeText(SpecificPurchaseActivity.this, result.Message, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(SpecificPurchaseActivity.this, SpecificPurchaseActivity.class);
                        intent.putExtra("specific", specificPurchaseId);
                        startActivity(intent);
                    }
                } else {
                    //jeżeli brak internetu
                    Toast.makeText(SpecificPurchaseActivity.this, "Brak połączenia z Internetem", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<QueryResult<Object>> call, Throwable t) {
                Toast.makeText(SpecificPurchaseActivity.this, "Coś poszło nie tak: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        //end http request - put order change status
    }

    public void backButton(View view) {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SpecificPurchaseActivity.this, PurchasesActivity.class);
        startActivity(intent);
    }
}

package com.app.takemeaway.takemeaway;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import Helpers.AccountsService;
import Helpers.DirectionsService;
import Helpers.DriversService;
import Helpers.OrdersService;
import Models.DirectionsModel.DirectionResultHeader;
import Models.DriversModels.DriverModelHeaderExt;
import Models.ErrorCollection;
import Models.OperationError;
import Models.OrdersModels.OrderIdHeader;
import Models.QueryResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SummaryActivity extends AppCompatActivity {
    //start global
    private SharedPreferences preferences;
    private SharedPreferences.Editor preferencesEdit;
    String accessToken;
    String routeId;
    String driverId;
    //end global

    //start elements
    TextView fromPlace;
    TextView toPlace;
    TextView distance;
    TextView duration;
    TextView name;
    TextView priceStart;
    TextView pricePerUnit;
    TextView price;
    ImageView image_staticMap;
    //end elements

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        //start global
        preferences = getSharedPreferences("TakeMeAwayApp", 0);
        accessToken = preferences.getString("accessToken", "");
        routeId = getIntent().getStringExtra("routeId");
        driverId = getIntent().getStringExtra("driverId");
        //end global

        //start elements
        fromPlace = (TextView) findViewById(R.id.label_orderSummaryFromPlace) ;
        toPlace = (TextView) findViewById(R.id.label_orderSummaryToPlace) ;
        distance = (TextView) findViewById(R.id.label_orderSummaryDistance) ;
        duration = (TextView) findViewById(R.id.label_orderSummaryDuration) ;
        name = (TextView) findViewById(R.id.label_orderSummaryCompanyName) ;
        priceStart = (TextView) findViewById(R.id.label_orderSummaryStartPrice) ;
        pricePerUnit = (TextView) findViewById(R.id.label_orderSummaryPricePerUnit);
        price = (TextView) findViewById(R.id.label_orderSummaryPrice);
        image_staticMap = (ImageView) findViewById(R.id.image_staticMap);
        //end elements

        //start http request - get route
        Retrofit.Builder builderRoute = new Retrofit.Builder().baseUrl(getString(R.string.baseUrl))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofitRoute = builderRoute.build();

        DirectionsService serviceRoute = retrofitRoute.create(DirectionsService.class);
        Call<QueryResult<DirectionResultHeader>> callRoute = serviceRoute.GetDirections(accessToken, routeId);


        callRoute.enqueue(new Callback<QueryResult<DirectionResultHeader>>() {
            @Override
            public void onResponse(Call<QueryResult<DirectionResultHeader>> call, Response<QueryResult<DirectionResultHeader>> response) {
                if (response.code() == 400) {
                    //wykonanie HTTP
                    String responseJson = null;
                    try {
                        responseJson = response.errorBody().string();
                    } catch (IOException e) {
                        Toast.makeText(SummaryActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

                        Toast.makeText(SummaryActivity.this, errors, Toast.LENGTH_LONG).show();
                    } else {
                        //Jeżeli nie błędy walidacji to..
                        errorResponseAuthorization = gson.fromJson(responseJson, OperationError.class);

                        Toast.makeText(SummaryActivity.this, errorResponseAuthorization.ErrorDetails.Message, Toast.LENGTH_LONG).show();

                        if (errorResponseAuthorization.ErrorDetails.Code == "500") {
                            //jeżeli błąd Bazy danych
                            Toast.makeText(SummaryActivity.this, "Wysyłam raport", Toast.LENGTH_LONG).show();
                        } else {
                            //jeżeli błąd autoryzacji
                            Intent intent = new Intent(SummaryActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                } else if (response.code() == 200){
                    //Jeżeli sukces
                    QueryResult<DirectionResultHeader> result = response.body();

                    if (result != null) {
                        fromPlace.setText(result.Items.FromPlace);
                        toPlace.setText(result.Items.ToPlace);
                        distance.setText(result.Items.Distance.Text);
                        duration.setText(result.Items.Duration.Text);


                        Picasso.with(SummaryActivity.this)
                                .load(result.Items.StaticMapUrl)
                                .into(image_staticMap, new com.squareup.picasso.Callback() {
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
                    Toast.makeText(SummaryActivity.this, "Brak połączenia z Internetem", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<QueryResult<DirectionResultHeader>> call, Throwable t) {
                Toast.makeText(SummaryActivity.this, "Coś poszło nie tak: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        //end http request - get route

        //start delay
        try {
            TimeUnit.MILLISECONDS.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //end delay

        //start http request - get driver
        Retrofit.Builder builderDrivers = new Retrofit.Builder().baseUrl(getString(R.string.baseUrl))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofitDrivers = builderDrivers.build();

        DriversService serviceDrivers = retrofitDrivers.create(DriversService.class);
        Call<QueryResult<DriverModelHeaderExt>> callDrivers = serviceDrivers.GetDriverForOrder(accessToken, Integer.parseInt(driverId), routeId);


        callDrivers.enqueue(new Callback<QueryResult<DriverModelHeaderExt>>() {
            @Override
            public void onResponse(Call<QueryResult<DriverModelHeaderExt>> call, Response<QueryResult<DriverModelHeaderExt>> response) {
                if (response.code() == 400) {
                    //wykonanie HTTP
                    String responseJson = null;
                    try {
                        responseJson = response.errorBody().string();
                    } catch (IOException e) {
                        Toast.makeText(SummaryActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

                        Toast.makeText(SummaryActivity.this, errors, Toast.LENGTH_LONG).show();
                    } else {
                        //Jeżeli nie błędy walidacji to..
                        errorResponseAuthorization = gson.fromJson(responseJson, OperationError.class);

                        Toast.makeText(SummaryActivity.this, errorResponseAuthorization.ErrorDetails.Message, Toast.LENGTH_LONG).show();

                        if (errorResponseAuthorization.ErrorDetails.Code == "500") {
                            //jeżeli błąd Bazy danych
                            Toast.makeText(SummaryActivity.this, "Wysyłam raport", Toast.LENGTH_LONG).show();
                        } else {
                            //jeżeli błąd autoryzacji
                            Intent intent = new Intent(SummaryActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                } else if (response.code() == 200){
                    //Jeżeli sukces
                    QueryResult<DriverModelHeaderExt> result = response.body();

                    if (result != null) {
                        name.setText(result.Items.Name);
                        priceStart.setText(Double.toString(result.Items.StartPrice) + " zł");
                        pricePerUnit.setText(Double.toString(result.Items.PricePerUnit) + " zł");
                        price.setText(Double.toString(result.Items.Price) + " zł");
                    }
                } else {
                    //jeżeli brak internetu
                    Toast.makeText(SummaryActivity.this, "Brak połączenia z Internetem", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<QueryResult<DriverModelHeaderExt>> call, Throwable t) {
                Toast.makeText(SummaryActivity.this, "Coś poszło nie tak: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        //end http request - get driver
    }

    public void readyButton(View view) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SummaryActivity.this);
        dialogBuilder.setMessage("Czy jesteś pewny??")
                .setCancelable(false)
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //start http request - post order
                        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(getString(R.string.baseUrl))
                                .addConverterFactory(GsonConverterFactory.create());
                        Retrofit retrofit = builder.build();

                        OrdersService service = retrofit.create(OrdersService.class);
                        Call<QueryResult<OrderIdHeader>> call = service.CreateOrder(accessToken, Integer.parseInt(driverId), routeId);


                        call.enqueue(new Callback<QueryResult<OrderIdHeader>>() {
                            @Override
                            public void onResponse(Call<QueryResult<OrderIdHeader>> call, Response<QueryResult<OrderIdHeader>> response) {
                                if (response.code() == 400) {
                                    //wykonanie HTTP
                                    String responseJson = null;
                                    try {
                                        responseJson = response.errorBody().string();
                                    } catch (IOException e) {
                                        Toast.makeText(SummaryActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

                                        Toast.makeText(SummaryActivity.this, errors, Toast.LENGTH_LONG).show();
                                    } else {
                                        //Jeżeli nie błędy walidacji to..
                                        errorResponseAuthorization = gson.fromJson(responseJson, OperationError.class);

                                        Toast.makeText(SummaryActivity.this, errorResponseAuthorization.ErrorDetails.Message, Toast.LENGTH_LONG).show();

                                        if (errorResponseAuthorization.ErrorDetails.Code == "500") {
                                            //jeżeli błąd Bazy danych
                                            Toast.makeText(SummaryActivity.this, "Wysyłam raport", Toast.LENGTH_LONG).show();
                                        } else {
                                            //jeżeli błąd autoryzacji
                                            Intent intent = new Intent(SummaryActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                } else if (response.code() == 200){
                                    //Jeżeli sukces
                                    QueryResult<OrderIdHeader> result = response.body();

                                    Toast.makeText(SummaryActivity.this, result.Message, Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(SummaryActivity.this, ThankYouActivity.class);
                                    startActivity(intent);
                                } else {
                                    //jeżeli brak internetu
                                    Toast.makeText(SummaryActivity.this, "Brak połączenia z Internetem", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<QueryResult<OrderIdHeader>> call, Throwable t) {
                                Toast.makeText(SummaryActivity.this, "Coś poszło nie tak: " + t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                        //end http request - post order
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

    public void backButton(View view) {
        super.onBackPressed();
    }
}

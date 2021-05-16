package com.app.takemeaway.takemeaway;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import Helpers.AccountsService;
import Helpers.DirectionsService;
import Helpers.DriversService;
import Helpers.OrdersService;
import Models.AccountsModels.AccountDetails;
import Models.DirectionsModel.DirectionResultHeader;
import Models.DriversModels.DriverModelHeaderExt;
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

public class DriversActivity extends AppCompatActivity {
    //start global
    private SharedPreferences preferences;
    private SharedPreferences.Editor preferencesEdit;
    String accessToken;
    String routeId;
    //end global

    //start elements
    ListView listView;
    MyListAdapter adapter;
    TextView fromPlace;
    TextView toPlace;
    TextView distance;
    TextView duration;
    //end elements

    //start model
    List<DriverModelHeaderExt> driversList;
    //end model

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivers);

        //start global
        preferences = getSharedPreferences("TakeMeAwayApp", 0);
        accessToken = preferences.getString("accessToken", "");
        routeId = getIntent().getStringExtra("routeId");
        //end global

        //start elements
        listView = (ListView) findViewById(R.id.list_drivers);
        fromPlace = (TextView) findViewById(R.id.label_routeSummaryFromPlace);
        toPlace = (TextView) findViewById(R.id.label_routeSummaryToPlace);
        distance = (TextView) findViewById(R.id.label_routeSummaryDistance);
        duration = (TextView) findViewById(R.id.label_routeSummaryDuration);
        //end elements

        //start model
        driversList = new ArrayList<>();
        //end model

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
                        Toast.makeText(DriversActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

                        Toast.makeText(DriversActivity.this, errors, Toast.LENGTH_LONG).show();
                    } else {
                        //Jeżeli nie błędy walidacji to..
                        errorResponseAuthorization = gson.fromJson(responseJson, OperationError.class);

                        Toast.makeText(DriversActivity.this, errorResponseAuthorization.ErrorDetails.Message, Toast.LENGTH_LONG).show();

                        if (errorResponseAuthorization.ErrorDetails.Code == "500") {
                            //jeżeli błąd Bazy danych
                            Toast.makeText(DriversActivity.this, "Wysyłam raport", Toast.LENGTH_LONG).show();
                        } else {
                            //jeżeli błąd autoryzacji
                            Intent intent = new Intent(DriversActivity.this, LoginActivity.class);
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
                    }
                } else {
                    //jeżeli brak internetu
                    Toast.makeText(DriversActivity.this, "Brak połączenia z Internetem", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<QueryResult<DirectionResultHeader>> call, Throwable t) {
                Toast.makeText(DriversActivity.this, "Coś poszło nie tak: " + t.getMessage(), Toast.LENGTH_LONG).show();
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

        //start http request - get drivers
        Retrofit.Builder builderDrivers = new Retrofit.Builder().baseUrl(getString(R.string.baseUrl))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofitDrivers = builderDrivers.build();

        DriversService serviceDrivers = retrofitDrivers.create(DriversService.class);
        Call<QueryResult<List<DriverModelHeaderExt>>> callDrivers = serviceDrivers.GetDrivers(accessToken, routeId);


        callDrivers.enqueue(new Callback<QueryResult<List<DriverModelHeaderExt>>>() {
            @Override
            public void onResponse(Call<QueryResult<List<DriverModelHeaderExt>>> call, Response<QueryResult<List<DriverModelHeaderExt>>> response) {
                if (response.code() == 400) {
                    //wykonanie HTTP
                    String responseJson = null;
                    try {
                        responseJson = response.errorBody().string();
                    } catch (IOException e) {
                        Toast.makeText(DriversActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

                        Toast.makeText(DriversActivity.this, errors, Toast.LENGTH_LONG).show();
                    } else {
                        //Jeżeli nie błędy walidacji to..
                        errorResponseAuthorization = gson.fromJson(responseJson, OperationError.class);

                        Toast.makeText(DriversActivity.this, errorResponseAuthorization.ErrorDetails.Message, Toast.LENGTH_LONG).show();

                        if (errorResponseAuthorization.ErrorDetails.Code == "500") {
                            //jeżeli błąd Bazy danych
                            Toast.makeText(DriversActivity.this, "Wysyłam raport", Toast.LENGTH_LONG).show();
                        } else {
                            //jeżeli błąd autoryzacji
                            Intent intent = new Intent(DriversActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                } else if (response.code() == 200){
                    //Jeżeli sukces
                    QueryResult<List<DriverModelHeaderExt>> result = response.body();

                    if (result != null) {
                        driversList = result.Items;

                        //start driver list
                        adapter = new MyListAdapter(DriversActivity.this, R.layout.custom_driver_item, driversList);
                        listView.setAdapter(adapter);
                        //end driver list
                    }
                } else {
                    //jeżeli brak internetu
                    Toast.makeText(DriversActivity.this, "Brak połączenia z Internetem", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<QueryResult<List<DriverModelHeaderExt>>> call, Throwable t) {
                Toast.makeText(DriversActivity.this, "Coś poszło nie tak: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        //end http request - get drivers
    }

    public void backButton(View view) {
        super.onBackPressed();
    }

    //start driver list
    public class MyListAdapter extends ArrayAdapter<DriverModelHeaderExt> {

        //the list values in the List of type hero
        List<DriverModelHeaderExt> driverList;

        //activity context
        Context context;

        //the layout resource file for the list items
        int resource;

        //constructor initializing the values
        public MyListAdapter(Context context, int resource, List<DriverModelHeaderExt> driverList) {
            super(context, resource, driverList);
            this.context = context;
            this.resource = resource;
            this.driverList = driverList;
        }

        //this will return the ListView Item as a View
        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater layoutInflater = LayoutInflater.from(context);

            View view = layoutInflater.inflate(resource, null, false);

            final TextView driverName = (TextView) view.findViewById(R.id.label_driverCompanyName);
            final TextView driverStartPrice = (TextView) view.findViewById(R.id.label_driverStartPrice);
            final TextView driverPricePerUnit = (TextView) view.findViewById(R.id.label_driverPricePerUnit);
            final TextView driverPrice = (TextView) view.findViewById(R.id.label_price);

            final DriverModelHeaderExt driver = driverList.get(position);

            driverName.setText(driver.Name);
            driverStartPrice.setText(Double.toString(driver.StartPrice) + " zł");
            driverPricePerUnit.setText(Double.toString(driver.PricePerUnit) + " zł");
            driverPrice.setText(Double.toString(driver.Price) + " zł");

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int driverId = driverList.get(position).Id;

                    Intent intent = new Intent(DriversActivity.this, SummaryActivity.class);
                    intent.putExtra("routeId", routeId);
                    intent.putExtra("driverId", Integer.toString(driverId));
                    startActivity(intent);
                }
            });

            //finally returning the view
            return view;
        }
    }
    //end driver list
}

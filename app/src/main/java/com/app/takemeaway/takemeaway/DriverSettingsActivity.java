package com.app.takemeaway.takemeaway;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import Helpers.AccountsService;
import Helpers.CitiesService;
import Helpers.DaysService;
import Helpers.DriversService;
import Helpers.OnSwipeTouchListener;
import Models.AccountsModels.AccountDetails;
import Models.CitiesModels.CityDetails;
import Models.CitiesModels.CityHeader;
import Models.DaysModels.DayDetails;
import Models.DaysModels.DayDetailsExt;
import Models.DaysModels.DayHeaderExt;
import Models.DriversModels.DriverModel;
import Models.DriversModels.DriverModelExt;
import Models.DriversModels.DriverOrdersList;
import Models.ErrorCollection;
import Models.OperationError;
import Models.QueryResult;
import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DriverSettingsActivity extends AppCompatActivity {
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
    ListView listViewDays;
    ListView listViewCities;
    EditText companyName;
    EditText startPrice;
    EditText pricePerUnit;
    EditText maximalDistance;
    //end elements

    //start model
    List<CityDetails> returnedCities;
    List<DayDetailsExt> returnedDays;
    private List<CityDetails> cityList;
    private List<DayDetailsExt> dayList;
    MyListAdapterCities adapterCities;
    MyListAdapterDays adapterDays;
    List<String> spinnerCitiesList;
    List<String> spinnerDaysList;
    List<String> spinnerStartTimesList;
    List<String> spinnerEndTimesList;
    //end model

    //start spinners
    Spinner spinnerCities;
    Spinner spinnerDays;
    Spinner spinnerStartTimes;
    Spinner spinnerEndTimes;
    //end spinners

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_settings);

        //disable focus
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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

        findViewById(R.id.layoutContent).setOnTouchListener(new OnSwipeTouchListener(DriverSettingsActivity.this) {
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
        spinnerCities = (Spinner) findViewById(R.id.spinner_cities);
        spinnerDays = (Spinner) findViewById(R.id.spinner_days);
        spinnerStartTimes = (Spinner) findViewById(R.id.spinner_times_start);
        spinnerEndTimes = (Spinner) findViewById(R.id.spinner_times_end);
        listViewCities = (ListView) findViewById(R.id.list_cities);
        listViewDays = (ListView) findViewById(R.id.list_days);
        companyName = (EditText) findViewById(R.id.text_companyName);
        startPrice = (EditText) findViewById(R.id.text_startPrice);
        pricePerUnit = (EditText) findViewById(R.id.text_pricePerUnit);
        maximalDistance = (EditText) findViewById(R.id.text_maximalDistance);
        //end elements

        //start model
        cityList = new ArrayList<>();
        dayList = new ArrayList<>();
        returnedCities = new ArrayList<>();
        returnedDays = new ArrayList<>();
        spinnerCitiesList = new ArrayList<>();
        spinnerDaysList = new ArrayList<>();
        spinnerStartTimesList = new ArrayList<>();
        spinnerEndTimesList = new ArrayList<>();
        //end model

        //start adapters
        adapterCities = new MyListAdapterCities(this, R.layout.custom_cities_item, cityList);
        listViewCities.setAdapter(adapterCities);
        adapterDays = new MyListAdapterDays(this, R.layout.custom_day_item, dayList);
        listViewDays.setAdapter(adapterDays);
        //end adapters

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
                        Toast.makeText(DriverSettingsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

                        Toast.makeText(DriverSettingsActivity.this, errors, Toast.LENGTH_LONG).show();
                    } else {
                        //Jeżeli nie błędy walidacji to..
                        errorResponseAuthorization = gson.fromJson(responseJson, OperationError.class);

                        Toast.makeText(DriverSettingsActivity.this, errorResponseAuthorization.ErrorDetails.Message, Toast.LENGTH_LONG).show();

                        if (errorResponseAuthorization.ErrorDetails.Code == "500") {
                            //jeżeli błąd Bazy danych
                            Toast.makeText(DriverSettingsActivity.this, "Wysyłam raport", Toast.LENGTH_LONG).show();
                        } else {
                            //jeżeli błąd autoryzacji
                            Intent intent = new Intent(DriverSettingsActivity.this, LoginActivity.class);
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
                        } else {
                            menuBecomeDriver.setVisibility(View.INVISIBLE);
                            menuBecomeDriverPicture.setVisibility(View.INVISIBLE);
                        }
                    }
                } else {
                    //jeżeli brak internetu
                    Toast.makeText(DriverSettingsActivity.this, "Brak połączenia z Internetem", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<QueryResult<AccountDetails>> call, Throwable t) {
                Toast.makeText(DriverSettingsActivity.this, "Coś poszło nie tak: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        //end http request - get Account Details

        //start delay
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //end delay

        //start http request - get cities
        Retrofit.Builder builderCities = new Retrofit.Builder().baseUrl(getString(R.string.baseUrl))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofitCities = builderCities.build();

        CitiesService citiesServiceCities = retrofitCities.create(CitiesService.class);
        Call<QueryResult<List<CityDetails>>> callCities = citiesServiceCities.GetCities(accessToken);


        callCities.enqueue(new Callback<QueryResult<List<CityDetails>>>() {
            @Override
            public void onResponse(Call<QueryResult<List<CityDetails>>> call, Response<QueryResult<List<CityDetails>>> response) {
                if (response.code() == 400) {
                    //wykonanie HTTP
                    String responseJson = null;
                    try {
                        responseJson = response.errorBody().string();
                    } catch (IOException e) {
                        Toast.makeText(DriverSettingsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

                        Toast.makeText(DriverSettingsActivity.this, errors, Toast.LENGTH_LONG).show();
                    } else {
                        //Jeżeli nie błędy walidacji to..
                        errorResponseAuthorization = gson.fromJson(responseJson, OperationError.class);

                        Toast.makeText(DriverSettingsActivity.this, errorResponseAuthorization.ErrorDetails.Message, Toast.LENGTH_LONG).show();

                        if (errorResponseAuthorization.ErrorDetails.Code == "500") {
                            //jeżeli błąd Bazy danych
                            Toast.makeText(DriverSettingsActivity.this, "Wysyłam raport", Toast.LENGTH_LONG).show();
                        } else {
                            //jeżeli błąd autoryzacji
                            Intent intent = new Intent(DriverSettingsActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                } else if (response.code() == 200){
                    //Jeżeli sukces
                    QueryResult<List<CityDetails>> result = response.body();

                    if (result != null) {
                        for (CityDetails item : result.Items) {
                            returnedCities.add(item);
                        }



                        //start http request - get days
                        Retrofit.Builder builderDays = new Retrofit.Builder().baseUrl(getString(R.string.baseUrl))
                                .addConverterFactory(GsonConverterFactory.create());
                        Retrofit retrofitDays = builderDays.build();

                        DaysService daysServiceDays = retrofitDays.create(DaysService.class);
                        Call<QueryResult<List<DayDetailsExt>>> callDays = daysServiceDays.GetDays(accessToken);


                        callDays.enqueue(new Callback<QueryResult<List<DayDetailsExt>>>() {
                            @Override
                            public void onResponse(Call<QueryResult<List<DayDetailsExt>>> call, Response<QueryResult<List<DayDetailsExt>>> response) {
                                if (response.code() == 400) {
                                    //wykonanie HTTP
                                    String responseJson = null;
                                    try {
                                        responseJson = response.errorBody().string();
                                    } catch (IOException e) {
                                        Toast.makeText(DriverSettingsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

                                        Toast.makeText(DriverSettingsActivity.this, errors, Toast.LENGTH_LONG).show();
                                    } else {
                                        //Jeżeli nie błędy walidacji to..
                                        errorResponseAuthorization = gson.fromJson(responseJson, OperationError.class);

                                        Toast.makeText(DriverSettingsActivity.this, errorResponseAuthorization.ErrorDetails.Message, Toast.LENGTH_LONG).show();

                                        if (errorResponseAuthorization.ErrorDetails.Code == "500") {
                                            //jeżeli błąd Bazy danych
                                            Toast.makeText(DriverSettingsActivity.this, "Wysyłam raport", Toast.LENGTH_LONG).show();
                                        } else {
                                            //jeżeli błąd autoryzacji
                                            Intent intent = new Intent(DriverSettingsActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                } else if (response.code() == 200){
                                    //Jeżeli sukces
                                    QueryResult<List<DayDetailsExt>> result = response.body();

                                    if (result != null) {
                                        for (DayDetailsExt item : result.Items) {
                                            switch (item.Name){
                                                case "Monday":{
                                                    item.Name = "Poniedziałek";
                                                    break;
                                                }
                                                case "Tuesday":{
                                                    item.Name = "Wtorek";
                                                    break;
                                                }
                                                case "Wednesday":{
                                                    item.Name = "Środa";
                                                    break;
                                                }
                                                case "Thursday":{
                                                    item.Name = "Czwartek";
                                                    break;
                                                }
                                                case "Friday":{
                                                    item.Name = "Piątek";
                                                    break;
                                                }
                                                case "Saturday":{
                                                    item.Name = "Sobota";
                                                    break;
                                                }
                                                default:{
                                                    item.Name = "Niedziela";
                                                }
                                            }
                                            returnedDays.add(item);
                                        }



                                        for (CityDetails item : returnedCities) {
                                            spinnerCitiesList.add(item.Name);
                                        }

                                        for (DayDetailsExt item : returnedDays) {
                                            spinnerDaysList.add(item.Name);
                                        }

                                        HintSpinner<String> hintSpinnerCities = new HintSpinner<>(
                                                spinnerCities,
                                                // Default layout - You don't need to pass in any layout id, just your hint text and
                                                // your list data
                                                new HintAdapter<String>(DriverSettingsActivity.this, "Miasto", spinnerCitiesList),
                                                new HintSpinner.Callback<String>() {
                                                    @Override
                                                    public void onItemSelected(int position, String itemAtPosition) {
                                                    }
                                                });
                                        hintSpinnerCities.init();

                                        HintSpinner<String> hintSpinnerDays = new HintSpinner<>(
                                                spinnerDays,
                                                // Default layout - You don't need to pass in any layout id, just your hint text and
                                                // your list data
                                                new HintAdapter<String>(DriverSettingsActivity.this, "Dzień", spinnerDaysList),
                                                new HintSpinner.Callback<String>() {
                                                    @Override
                                                    public void onItemSelected(int position, String itemAtPosition) {
                                                    }
                                                });
                                        hintSpinnerDays.init();
                                    }
                                } else {
                                    //jeżeli brak internetu
                                    Toast.makeText(DriverSettingsActivity.this, "Brak połączenia z Internetem", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<QueryResult<List<DayDetailsExt>>> call, Throwable t) {
                                Toast.makeText(DriverSettingsActivity.this, "Coś poszło nie tak: " + t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                        //end http request - get days
                    }
                } else {
                    //jeżeli brak internetu
                    Toast.makeText(DriverSettingsActivity.this, "Brak połączenia z Internetem", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<QueryResult<List<CityDetails>>> call, Throwable t) {
                Toast.makeText(DriverSettingsActivity.this, "Coś poszło nie tak: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        //end http request - get Cities

        //start start and end times
        String[] startTimes = new String[]{
                "00:00", "00:30", "01:00", "02:30", "03:00", "03:30", "04:00", "04:30", "05:00", "05:30", "06:00", "06:30", "07:00", "07:30", "08:00"
                , "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30"
                , "16:00", "16:30", "17:00", "17:30", "18:00", "18:30", "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00", "22:30", "23:00", "23:30"
        };

        String[] endTimes = new String[]{
                "00:30", "01:00", "02:30", "03:00", "03:30", "04:00", "04:30", "05:00", "05:30", "06:00", "06:30", "07:00", "07:30", "08:00"
                , "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30"
                , "16:00", "16:30", "17:00", "17:30", "18:00", "18:30", "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00", "22:30", "23:00", "23:30"
                , "24:00"
        };

        for (String str : startTimes)
            spinnerStartTimesList.add(str);

        for (String str : endTimes)
            spinnerEndTimesList.add(str);
        //end start and end times

        //start spinners
        HintSpinner<String> hintSpinnerStartTimes = new HintSpinner<>(
                spinnerStartTimes,
                // Default layout - You don't need to pass in any layout id, just your hint text and
                // your list data
                new HintAdapter<String>(this, "Od", spinnerStartTimesList),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                    }
                });
        hintSpinnerStartTimes.init();

        HintSpinner<String> hintSpinnerEndTimes = new HintSpinner<>(
                spinnerEndTimes,
                // Default layout - You don't need to pass in any layout id, just your hint text and
                // your list data
                new HintAdapter<String>(this, "Do", spinnerEndTimesList),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                    }
                });
        hintSpinnerEndTimes.init();
        //end spinners

        //start delay
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //end delay

        //start http request - get Driver
        Retrofit.Builder builderDriver = new Retrofit.Builder().baseUrl(getString(R.string.baseUrl))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofitDriver = builderDriver.build();

        DriversService DriversServiceDriver = retrofitDriver.create(DriversService.class);
        Call<QueryResult<DriverModelExt>> callDriver = DriversServiceDriver.GetDriverSummary(accessToken);


        callDriver.enqueue(new Callback<QueryResult<DriverModelExt>>() {
            @Override
            public void onResponse(Call<QueryResult<DriverModelExt>> call, Response<QueryResult<DriverModelExt>> response) {
                if (response.code() == 400) {
                    //wykonanie HTTP
                    String responseJson = null;
                    try {
                        responseJson = response.errorBody().string();
                    } catch (IOException e) {
                        Toast.makeText(DriverSettingsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

                        Toast.makeText(DriverSettingsActivity.this, errors, Toast.LENGTH_LONG).show();
                    } else {
                        //Jeżeli nie błędy walidacji to..
                        errorResponseAuthorization = gson.fromJson(responseJson, OperationError.class);

                        Toast.makeText(DriverSettingsActivity.this, errorResponseAuthorization.ErrorDetails.Message, Toast.LENGTH_LONG).show();

                        if (errorResponseAuthorization.ErrorDetails.Code == "500") {
                            //jeżeli błąd Bazy danych
                            Toast.makeText(DriverSettingsActivity.this, "Wysyłam raport", Toast.LENGTH_LONG).show();
                        } else {
                            //jeżeli błąd autoryzacji
                            Intent intent = new Intent(DriverSettingsActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                } else if (response.code() == 200){
                    //Jeżeli sukces
                    QueryResult<DriverModelExt> result = response.body();

                    if (result != null) {
                        companyName.setText(result.Items.Name);
                        startPrice.setText(result.Items.StartPrice.toString());
                        pricePerUnit.setText(result.Items.PricePerUnit.toString());
                        maximalDistance.setText(result.Items.MaximalDistance.toString());

                        for (CityDetails city : result.Items.Cities) {
                            cityList.add(city);
                        }

                        if (cityList.size() > 0) {
                            adapterCities.notifyDataSetChanged();
                            setListViewHeightBasedOnChildren(listViewCities);
                        }

                        for (DayDetailsExt day : result.Items.Days) {
                            switch (day.Name){
                                case "Monday":{
                                    day.Name = "Poniedziałek";
                                    break;
                                }
                                case "Tuesday":{
                                    day.Name = "Wtorek";
                                    break;
                                }
                                case "Wednesday":{
                                    day.Name = "Środa";
                                    break;
                                }
                                case "Thursday":{
                                    day.Name = "Czwartek";
                                    break;
                                }
                                case "Friday":{
                                    day.Name = "Piątek";
                                    break;
                                }
                                case "Saturday":{
                                    day.Name = "Sobota";
                                    break;
                                }
                                default:{
                                    day.Name = "Niedziela";
                                }
                            }

                            day.StartTime = day.StartTime.substring(0, 5);
                            day.EndTime = day.EndTime.substring(0, 5);

                            dayList.add(day);
                        }

                        if (dayList.size() > 0) {
                            adapterDays.notifyDataSetChanged();
                            setListViewHeightBasedOnChildren(listViewDays);
                        }
                    }
                } else {
                    //jeżeli brak internetu
                    Toast.makeText(DriverSettingsActivity.this, "Brak połączenia z Internetem", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<QueryResult<DriverModelExt>> call, Throwable t) {
                Toast.makeText(DriverSettingsActivity.this, "Coś poszło nie tak: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        //end http request - get Driver

//        //start fill lists
//        setListViewHeightBasedOnChildren(listViewCities);
//        setListViewHeightBasedOnChildren(listViewDays);
//        //end fill lists
    }

    public void driverRegisterAddCity(View view){

        try {
            cityList.add(new CityDetails(0, spinnerCities.getSelectedItem().toString()));

            adapterCities.notifyDataSetChanged();

            setListViewHeightBasedOnChildren(listViewCities);
        } catch (Exception e) {
            Toast.makeText(DriverSettingsActivity.this, "Wybierz prawidłowo miasto", Toast.LENGTH_LONG).show();
        }
    }

    public void driverRegisterAddDay(View view){

        try {
            dayList.add(new DayDetailsExt(0, spinnerStartTimes.getSelectedItem().toString(),
                    spinnerEndTimes.getSelectedItem().toString(),
                    spinnerDays.getSelectedItem().toString()));

            adapterDays.notifyDataSetChanged();

            setListViewHeightBasedOnChildren(listViewDays);
        } catch (Exception e) {
            Toast.makeText(DriverSettingsActivity.this, "Wybierz prawidłowo dzień i godziny", Toast.LENGTH_LONG).show();
        }
    }

    public void deleteDriver(View view) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DriverSettingsActivity.this);
        dialogBuilder.setMessage("Czy jesteś pewny, że chcesz usunąć konto kierowcy??")
                .setCancelable(false)
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //start http request - delete driver
                        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(getString(R.string.baseUrl))
                                .addConverterFactory(GsonConverterFactory.create());
                        Retrofit retrofit = builder.build();

                        DriversService driversService = retrofit.create(DriversService.class);
                        Call<QueryResult<Object>> call = driversService.DeactivateDriver(accessToken);


                        call.enqueue(new Callback<QueryResult<Object>>() {
                            @Override
                            public void onResponse(Call<QueryResult<Object>> call, Response<QueryResult<Object>> response) {
                                if (response.code() == 400) {
                                    //wykonanie HTTP
                                    String responseJson = null;
                                    try {
                                        responseJson = response.errorBody().string();
                                    } catch (IOException e) {
                                        Toast.makeText(DriverSettingsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }

                                    Gson gson = new GsonBuilder().create();

                                    ErrorCollection errorResponseValidation;
                                    OperationError errorResponseAuthorization;

                                    errorResponseValidation = gson.fromJson(responseJson, ErrorCollection.class);

                                    if (errorResponseValidation.Errors != null) {
                                        //Jeżeli błędy walidacji (nie ma walidacji)
                                        String errors = new String();

                                        for (OperationError error : errorResponseValidation.Errors) {
                                            errors += error.ErrorDetails.Message + "\n";
                                        }

                                        Toast.makeText(DriverSettingsActivity.this, errors, Toast.LENGTH_LONG).show();
                                    } else {
                                        //Jeżeli nie błędy walidacji to..
                                        errorResponseAuthorization = gson.fromJson(responseJson, OperationError.class);

                                        Toast.makeText(DriverSettingsActivity.this, errorResponseAuthorization.ErrorDetails.Message, Toast.LENGTH_LONG).show();

                                        if (errorResponseAuthorization.ErrorDetails.Code == "500") {
                                            //jeżeli błąd Bazy danych
                                            Toast.makeText(DriverSettingsActivity.this, "Wysyłam raport", Toast.LENGTH_LONG).show();
                                        } else {
                                            //jeżeli błąd autoryzacji
                                            Intent intent = new Intent(DriverSettingsActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                } else if (response.code() == 200) {
                                    //Jeżeli sukces
                                    QueryResult<Object> result = response.body();

                                    if (result != null) {
                                        Toast.makeText(DriverSettingsActivity.this, result.Message, Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(DriverSettingsActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                    }
                                } else {
                                    //jeżeli brak internetu
                                    Toast.makeText(DriverSettingsActivity.this, "Brak połączenia z Internetem", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<QueryResult<Object>> call, Throwable t) {
                                Toast.makeText(DriverSettingsActivity.this, "Coś poszło nie tak: " + t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                        //end http request - delete driver
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

    public void updateDriver(View view) {
        String modelCompanyName;
        Double modelStartPrice;
        Double modelPricePerUnit;
        Double modelMaximalDistance;
        List<CityHeader> modelCitiesIds;
        List<DayHeaderExt> modelDays;

        try {
            //start model
            modelCompanyName = companyName.getText().toString();
            modelStartPrice = Double.parseDouble(startPrice.getText().toString());
            modelPricePerUnit = Double.parseDouble(pricePerUnit.getText().toString());
            modelMaximalDistance = Double.parseDouble(maximalDistance.getText().toString());
            modelCitiesIds = new ArrayList<>();
            modelDays = new ArrayList<>();

            for (CityDetails city : cityList) {
                for (CityDetails doneCity : returnedCities) {
                    if (city.Name.equalsIgnoreCase(doneCity.Name)) {
                        modelCitiesIds.add(new CityHeader(doneCity.Id));
                        break;
                    }
                }
            }

            for (DayDetailsExt day : dayList) {
                for (DayDetailsExt doneDay : returnedDays) {
                    if (day.Name.equalsIgnoreCase(doneDay.Name)) {
                        modelDays.add(new DayHeaderExt(doneDay.Id, day.StartTime, day.EndTime));
                    }
                }
            }

            DriverModel driverModel = new DriverModel(modelCompanyName, modelStartPrice, modelPricePerUnit, modelMaximalDistance, modelCitiesIds, modelDays);
            //end model

            //start additional elements
            final TextView errorSpinnerCities = (TextView) spinnerCities.getSelectedView();
            final TextView errorSpinnerDays = (TextView) spinnerDays.getSelectedView();
            final TextView errorCities = (TextView) findViewById(R.id.text_errorCities);
            final TextView errorDays = (TextView) findViewById(R.id.text_errorDays);
            errorCities.setText("");
            errorDays.setText("");
            //end additional elements

            //start http request - register driver
            Retrofit.Builder builder = new Retrofit.Builder().baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit = builder.build();

            DriversService driversService = retrofit.create(DriversService.class);
            Call<QueryResult<Object>> call = driversService.CreateDriver(accessToken, driverModel);


            call.enqueue(new Callback<QueryResult<Object>>() {
                @Override
                public void onResponse(Call<QueryResult<Object>> call, Response<QueryResult<Object>> response) {
                    if (response.code() == 400) {
                        //wykonanie HTTP
                        String responseJson = null;
                        try {
                            responseJson = response.errorBody().string();
                        } catch (IOException e) {
                            Toast.makeText(DriverSettingsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                        Gson gson = new GsonBuilder().create();

                        ErrorCollection errorResponseValidation;
                        OperationError errorResponseAuthorization;

                        errorResponseValidation = gson.fromJson(responseJson, ErrorCollection.class);

                        if (errorResponseValidation.Errors != null) {
                            //Jeżeli błędy walidacji
                            for (OperationError error : errorResponseValidation.Errors) {
                                switch (error.ErrorDetails.Field) {
                                    case "Name": {
                                        companyName.setError(error.ErrorDetails.Message);
                                        break;
                                    }
                                    case "StartPrice": {
                                        startPrice.setError(error.ErrorDetails.Message);
                                        break;
                                    }
                                    case "PricePerUnit": {
                                        pricePerUnit.setError(error.ErrorDetails.Message);
                                        break;
                                    }
                                    case "MaximalDistance": {
                                        maximalDistance.setError(error.ErrorDetails.Message);
                                        break;
                                    }
                                    case "Cities": {
                                        errorSpinnerCities.setError("");
                                        errorCities.setText(error.ErrorDetails.Message);
                                        break;
                                    }
                                    case "Days": {
                                        errorSpinnerDays.setError("");
                                        errorDays.setText(error.ErrorDetails.Message);
                                        break;
                                    }
                                    default: {
                                        Toast.makeText(DriverSettingsActivity.this, error.ErrorDetails.Message, Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        } else {
                            //Jeżeli nie błędy walidacji to..
                            errorResponseAuthorization = gson.fromJson(responseJson, OperationError.class);

                            Toast.makeText(DriverSettingsActivity.this, errorResponseAuthorization.ErrorDetails.Message, Toast.LENGTH_LONG).show();

                            if (errorResponseAuthorization.ErrorDetails.Code == "500") {
                                //jeżeli błąd Bazy danych
                                Toast.makeText(DriverSettingsActivity.this, "Wysyłam raport", Toast.LENGTH_LONG).show();
                            } else {
                                //jeżeli błąd autoryzacji
                                Intent intent = new Intent(DriverSettingsActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }
                        }
                    } else if (response.code() == 200) {
                        //Jeżeli sukces
                        QueryResult<Object> result = response.body();

                        if (result != null) {
                            Toast.makeText(DriverSettingsActivity.this, result.Message, Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(DriverSettingsActivity.this, PurchasesActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        //jeżeli brak internetu
                        Toast.makeText(DriverSettingsActivity.this, "Brak połączenia z Internetem", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<QueryResult<Object>> call, Throwable t) {
                    Toast.makeText(DriverSettingsActivity.this, "Coś poszło nie tak: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            //end http request - register driver
        } catch (Exception e) {
            Toast.makeText(DriverSettingsActivity.this, "Uzupełnij poprawnie dane", Toast.LENGTH_LONG).show();
        }
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
        Intent intent = new Intent(DriverSettingsActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    public void menuOrders(View view) {
        Intent intent = new Intent(DriverSettingsActivity.this, OrdersActivity.class);
        startActivity(intent);
    }

    public void menuSettings(View view) {
        Intent intent = new Intent(DriverSettingsActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void menuBacomeDriver(View view) {
        Intent intent = new Intent(DriverSettingsActivity.this, RegisterDriverActivity.class);
        startActivity(intent);
    }

    public void menuPurchases(View view) {
        Intent intent = new Intent(DriverSettingsActivity.this, PurchasesActivity.class);
        startActivity(intent);
    }

    public void menuDriverSettings(View view) {
        Intent intent = new Intent(DriverSettingsActivity.this, DriverSettingsActivity.class);
        startActivity(intent);
    }

    public void menuLogout(View view) {
        preferencesEdit = preferences.edit();
        preferencesEdit.putString("accessToken", "");
        preferencesEdit.commit();

        Intent intent = new Intent(DriverSettingsActivity.this, LoginActivity.class);
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



    //////////////////////////////////////////////
    //start add days and cities
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if (listView == null)
            return;

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
    //end add days and cities

    //start cities
    public class MyListAdapterCities extends ArrayAdapter<CityDetails> {

        //the list values in the List of type hero
        List<CityDetails> citiesList;

        //activity context
        Context context;

        //the layout resource file for the list items
        int resource;

        //constructor initializing the values
        public MyListAdapterCities(Context context, int resource, List<CityDetails> citiesList) {
            super(context, resource, citiesList);
            this.context = context;
            this.resource = resource;
            this.citiesList = citiesList;
        }

        //this will return the ListView Item as a View
        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater layoutInflater = LayoutInflater.from(context);

            View view = layoutInflater.inflate(resource, null, false);

            final TextView cityName = (TextView) view.findViewById(R.id.label_registerDriverCityName);
            final Button buttonDelete = view.findViewById(R.id.button_driverRegisterCityDelete);

            CityDetails city = citiesList.get(position);

            cityName.setText(city.Name);

            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    removeCity(position);
                }
            });

            return view;
        }

        private void removeCity(final int position) {
            //Creating an alert dialog to confirm the deletion
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Jesteś pewny, że chcesz usunąć " + citiesList.get(position).Name + "?");

            //if the response is positive in the alert
            builder.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    //removing the item
                    citiesList.remove(position);

                    //reloading the list
                    notifyDataSetChanged();

                    setListViewHeightBasedOnChildren(listViewCities);
                }
            });

            //if response is negative nothing is being done
            builder.setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            //creating and displaying the alert dialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
    //end cities

    //start days
    public class MyListAdapterDays extends ArrayAdapter<DayDetailsExt> {

        //the list values in the List of type hero
        List<DayDetailsExt> daysList;

        //activity context
        Context context;

        //the layout resource file for the list items
        int resource;

        //constructor initializing the values
        public MyListAdapterDays(Context context, int resource, List<DayDetailsExt> daysList) {
            super(context, resource, daysList);
            this.context = context;
            this.resource = resource;
            this.daysList = daysList;
        }

        //this will return the ListView Item as a View
        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater layoutInflater = LayoutInflater.from(context);

            View view = layoutInflater.inflate(resource, null, false);

            final TextView dayName = (TextView) view.findViewById(R.id.label_registerDriverDayName);
            final TextView startTime = (TextView) view.findViewById(R.id.label_registerDriverDayStartTime);
            final TextView endTime = (TextView) view.findViewById(R.id.label_registerDriverDayEndTime);
            final Button buttonDelete = view.findViewById(R.id.button_driverRegisterDayDelete);

            DayDetailsExt day = daysList.get(position);

            dayName.setText(day.Name);
            startTime.setText(day.StartTime);
            endTime.setText(day.EndTime);

            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    removeDay(position);
                }
            });

            return view;
        }

        private void removeDay(final int position) {
            //Creating an alert dialog to confirm the deletion
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Jesteś pewny, że chcesz usunąć " + daysList.get(position).Name + "?");

            //if the response is positive in the alert
            builder.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    //removing the item
                    daysList.remove(position);

                    //reloading the list
                    notifyDataSetChanged();

                    setListViewHeightBasedOnChildren(listViewDays);
                }
            });

            //if response is negative nothing is being done
            builder.setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            //creating and displaying the alert dialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
    //End days
}

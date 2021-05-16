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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import Helpers.AccountsService;
import Helpers.DriversService;
import Helpers.OnSwipeTouchListener;
import Helpers.OrdersService;
import Models.AccountsModels.AccountDetails;
import Models.DriversModels.DriverOrder;
import Models.DriversModels.DriverOrdersList;
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

public class PurchasesActivity extends AppCompatActivity {
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
    MyListAdapter adapter;
    ListView listView;
    //end elements

    //start model
    List<DriverOrdersList> purchasesList;
    //end model

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchases);

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

        findViewById(R.id.layoutContent).setOnTouchListener(new OnSwipeTouchListener(PurchasesActivity.this) {
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
        listView = (ListView) findViewById(R.id.list_purchases);
        //end elements

        //start model
        purchasesList = new ArrayList<>();
        //end model

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
                        Toast.makeText(PurchasesActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

                        Toast.makeText(PurchasesActivity.this, errors, Toast.LENGTH_LONG).show();
                    } else {
                        //Jeżeli nie błędy walidacji to..
                        errorResponseAuthorization = gson.fromJson(responseJson, OperationError.class);

                        Toast.makeText(PurchasesActivity.this, errorResponseAuthorization.ErrorDetails.Message, Toast.LENGTH_LONG).show();

                        if (errorResponseAuthorization.ErrorDetails.Code == "500") {
                            //jeżeli błąd Bazy danych
                            Toast.makeText(PurchasesActivity.this, "Wysyłam raport", Toast.LENGTH_LONG).show();
                        } else {
                            //jeżeli błąd autoryzacji
                            Intent intent = new Intent(PurchasesActivity.this, LoginActivity.class);
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
                    Toast.makeText(PurchasesActivity.this, "Brak połączenia z Internetem", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<QueryResult<AccountDetails>> call, Throwable t) {
                Toast.makeText(PurchasesActivity.this, "Coś poszło nie tak: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        //end http request - get Account Details

        //start delay
        try {
            TimeUnit.MILLISECONDS.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //end delay

        //start http request - get Purchases
        Retrofit.Builder builderPurchases = new Retrofit.Builder().baseUrl(getString(R.string.baseUrl))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofitPurchases = builderPurchases.build();

        DriversService DriversServicePurchases = retrofitPurchases.create(DriversService.class);
        Call<QueryResult<List<DriverOrdersList>>> callPurchases = DriversServicePurchases.GetDriverOrders(accessToken);


        callPurchases.enqueue(new Callback<QueryResult<List<DriverOrdersList>>>() {
            @Override
            public void onResponse(Call<QueryResult<List<DriverOrdersList>>> call, Response<QueryResult<List<DriverOrdersList>>> response) {
                if (response.code() == 400) {
                    //wykonanie HTTP
                    String responseJson = null;
                    try {
                        responseJson = response.errorBody().string();
                    } catch (IOException e) {
                        Toast.makeText(PurchasesActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

                        Toast.makeText(PurchasesActivity.this, errors, Toast.LENGTH_LONG).show();
                    } else {
                        //Jeżeli nie błędy walidacji to..
                        errorResponseAuthorization = gson.fromJson(responseJson, OperationError.class);

                        Toast.makeText(PurchasesActivity.this, errorResponseAuthorization.ErrorDetails.Message, Toast.LENGTH_LONG).show();

                        if (errorResponseAuthorization.ErrorDetails.Code == "500") {
                            //jeżeli błąd Bazy danych
                            Toast.makeText(PurchasesActivity.this, "Wysyłam raport", Toast.LENGTH_LONG).show();
                        } else {
                            //jeżeli błąd autoryzacji
                            Intent intent = new Intent(PurchasesActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                } else if (response.code() == 200){
                    //Jeżeli sukces
                    QueryResult<List<DriverOrdersList>> result = response.body();

                    if (result != null) {
                        purchasesList = result.Items;

                        //start order list
                        adapter = new PurchasesActivity.MyListAdapter(PurchasesActivity.this, R.layout.custom_purchase_item, purchasesList);
                        listView.setAdapter(adapter);
                        //end order list
                    }
                } else {
                    //jeżeli brak internetu
                    Toast.makeText(PurchasesActivity.this, "Brak połączenia z Internetem", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<QueryResult<List<DriverOrdersList>>> call, Throwable t) {
                Toast.makeText(PurchasesActivity.this, "Coś poszło nie tak: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        //end http request - get Purchases
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
        Intent intent = new Intent(PurchasesActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    public void menuOrders(View view) {
        Intent intent = new Intent(PurchasesActivity.this, OrdersActivity.class);
        startActivity(intent);
    }

    public void menuSettings(View view) {
        Intent intent = new Intent(PurchasesActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void menuBacomeDriver(View view) {
        Intent intent = new Intent(PurchasesActivity.this, RegisterDriverActivity.class);
        startActivity(intent);
    }

    public void menuPurchases(View view) {
        Intent intent = new Intent(PurchasesActivity.this, PurchasesActivity.class);
        startActivity(intent);
    }

    public void menuDriverSettings(View view) {
        Intent intent = new Intent(PurchasesActivity.this, DriverSettingsActivity.class);
        startActivity(intent);
    }

    public void menuLogout(View view) {
        preferencesEdit = preferences.edit();
        preferencesEdit.putString("accessToken", "");
        preferencesEdit.commit();

        Intent intent = new Intent(PurchasesActivity.this, LoginActivity.class);
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

    //start purchase list
    public class MyListAdapter extends ArrayAdapter<DriverOrdersList> {

        //the list values in the List of type hero
        List<DriverOrdersList> purchaseList;

        //activity context
        Context context;

        //the layout resource file for the list items
        int resource;

        //constructor initializing the values
        public MyListAdapter(Context context, int resource, List<DriverOrdersList> purchaseList) {
            super(context, resource, purchaseList);
            this.context = context;
            this.resource = resource;
            this.purchaseList = purchaseList;
        }

        //this will return the ListView Item as a View
        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater layoutInflater = LayoutInflater.from(context);

            View view = layoutInflater.inflate(resource, null, false);

            final TextView purchaseDate = (TextView) view.findViewById(R.id.label_purchaseDate);
            final TextView purchaseFromPlace = (TextView) view.findViewById(R.id.label_purchaseFromPlace);
            final TextView purchaseToPlace = (TextView) view.findViewById(R.id.label_purchaseToPlace);
            final TextView purchaseStatus = (TextView) view.findViewById(R.id.label_purchaseStatus);

            DriverOrdersList purchase = purchaseList.get(position);

            String[] date = purchase.OrderDate.split("[T.]");

            try {
                purchaseDate.setText(date[0] + "\n" + date[1]);
                purchaseFromPlace.setText(purchase.FromPlace);
                purchaseToPlace.setText(purchase.ToPlace);
                purchaseStatus.setText(purchase.Status.Status);
            }
            catch(Exception e)
            {
                throw e;
            }

            if(purchase.Status.Id == 1){
                purchaseStatus.setTextColor(Color.RED);
            } else if(purchase.Status.Id == 2) {
                purchaseStatus.setTextColor(Color.rgb(255, 165, 0));
            } else if(purchase.Status.Id == 3) {
                purchaseStatus.setTextColor(Color.BLUE);
            } else {
                purchaseStatus.setTextColor(Color.GREEN);
            }

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final int purchaseId = purchaseList.get(position).Id;

                    Intent intent = new Intent(PurchasesActivity.this, SpecificPurchaseActivity.class);
                    intent.putExtra("specific", purchaseId);
                    startActivity(intent);
                }
            });

            //finally returning the view
            return view;
        }
    }
    //end purchase list
}

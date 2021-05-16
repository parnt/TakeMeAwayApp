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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import Helpers.AccountsService;
import Helpers.OnSwipeTouchListener;
import Helpers.OrdersService;
import Helpers.SortPatterns;
import Models.AccountsModels.AccountDetails;
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

public class OrdersActivity extends AppCompatActivity {
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
    ListView listView;
    View myView;
    MyListAdapter adapter;
    TextView specificOrderDate;
    TextView specificOrderDriver;
    TextView specificOrderPrice;
    TextView specificOrderStatus;
    TextView specificOrderDistance;
    TextView specificOrderDriverPhoneNumber;
    TextView specificOrderDuration;
    TextView specificOrderFromPlace;
    TextView specificOrderToPlace;
    //end elements

    //start model
    List<OrdersList> ordersList;
    //end model

    //start configurations
    boolean isUp;
    //end configurations

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

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

        findViewById(R.id.layoutContent).setOnTouchListener(new OnSwipeTouchListener(OrdersActivity.this) {
            public void onSwipeLeft() {
                if (isOpened) {
                    slideLeft(menu);
                    isOpened = false;
                }
            }
        });

        findViewById(R.id.list_orders).setOnTouchListener(new OnSwipeTouchListener(OrdersActivity.this) {
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
        listView = (ListView) findViewById(R.id.list_orders);
        myView = (View) findViewById(R.id.my_view);
        specificOrderDate = (TextView) findViewById(R.id.label_specifiedOrderDate);
        specificOrderDriver = (TextView) findViewById(R.id.label_specifiedOrderDriver);
        specificOrderPrice = (TextView) findViewById(R.id.label_specifiedOrderPrice);
        specificOrderStatus = (TextView) findViewById(R.id.label_specifiedOrderStatus);
        specificOrderDistance = (TextView) findViewById(R.id.label_specifiedOrderDistance);
        specificOrderDriverPhoneNumber = (TextView) findViewById(R.id.label_specifiedOrderDriverPhoneNumber);
        specificOrderDuration = (TextView) findViewById(R.id.label_specifiedOrderDuration);
        specificOrderFromPlace = (TextView) findViewById(R.id.label_specifiedOrderFromPlace);
        specificOrderToPlace = (TextView) findViewById(R.id.label_specifiedOrderToPlace);
        //end elements

        //start model
        ordersList = new ArrayList<>();
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
                        Toast.makeText(OrdersActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

                        Toast.makeText(OrdersActivity.this, errors, Toast.LENGTH_LONG).show();
                    } else {
                        //Jeżeli nie błędy walidacji to..
                        errorResponseAuthorization = gson.fromJson(responseJson, OperationError.class);

                        Toast.makeText(OrdersActivity.this, errorResponseAuthorization.ErrorDetails.Message, Toast.LENGTH_LONG).show();

                        if (errorResponseAuthorization.ErrorDetails.Code == "500") {
                            //jeżeli błąd Bazy danych
                            Toast.makeText(OrdersActivity.this, "Wysyłam raport", Toast.LENGTH_LONG).show();
                        } else {
                            //jeżeli błąd autoryzacji
                            Intent intent = new Intent(OrdersActivity.this, LoginActivity.class);
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
                    Toast.makeText(OrdersActivity.this, "Brak połączenia z Internetem", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<QueryResult<AccountDetails>> call, Throwable t) {
                Toast.makeText(OrdersActivity.this, "Coś poszło nie tak: " + t.getMessage(), Toast.LENGTH_LONG).show();
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

        //start http request - get Orders
        Retrofit.Builder builderOrders = new Retrofit.Builder().baseUrl(getString(R.string.baseUrl))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofitOrders = builderOrders.build();

        OrdersService ordersServiceOrders = retrofitOrders.create(OrdersService.class);
        Call<QueryResult<List<OrdersList>>> callOrders = ordersServiceOrders.GetOrders(accessToken);


        callOrders.enqueue(new Callback<QueryResult<List<OrdersList>>>() {
            @Override
            public void onResponse(Call<QueryResult<List<OrdersList>>> call, Response<QueryResult<List<OrdersList>>> response) {
                if (response.code() == 400) {
                    //wykonanie HTTP
                    String responseJson = null;
                    try {
                        responseJson = response.errorBody().string();
                    } catch (IOException e) {
                        Toast.makeText(OrdersActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

                        Toast.makeText(OrdersActivity.this, errors, Toast.LENGTH_LONG).show();
                    } else {
                        //Jeżeli nie błędy walidacji to..
                        errorResponseAuthorization = gson.fromJson(responseJson, OperationError.class);

                        Toast.makeText(OrdersActivity.this, errorResponseAuthorization.ErrorDetails.Message, Toast.LENGTH_LONG).show();

                        if (errorResponseAuthorization.ErrorDetails.Code == "500") {
                            //jeżeli błąd Bazy danych
                            Toast.makeText(OrdersActivity.this, "Wysyłam raport", Toast.LENGTH_LONG).show();
                        } else {
                            //jeżeli błąd autoryzacji
                            Intent intent = new Intent(OrdersActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                } else if (response.code() == 200){
                    //Jeżeli sukces
                    QueryResult<List<OrdersList>> result = response.body();

                    if (result != null) {
                        ordersList = result.Items;

                        //start order list
                        adapter = new MyListAdapter(OrdersActivity.this, R.layout.custom_order_item, ordersList);
                        listView.setAdapter(adapter);
                        //end order list
                    }
                } else {
                    //jeżeli brak internetu
                    Toast.makeText(OrdersActivity.this, "Brak połączenia z Internetem", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<QueryResult<List<OrdersList>>> call, Throwable t) {
                Toast.makeText(OrdersActivity.this, "Coś poszło nie tak: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        //end http request - get Orders

        //start order details slider
        myView.setVisibility(View.INVISIBLE);
        setClickable(myView, false);
        swipe(myView);
        isUp = false;

        findViewById(R.id.layoutContent).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event){
                //return gestureScanner.onTouchEvent(event);
                if (isUp) {
                    slideDown(myView);
                    isUp = false;
                }
                return false;
            }
        });
        //end order details slider
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
        Intent intent = new Intent(OrdersActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    public void menuOrders(View view) {
        Intent intent = new Intent(OrdersActivity.this, OrdersActivity.class);
        startActivity(intent);
    }

    public void menuSettings(View view) {
        Intent intent = new Intent(OrdersActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void menuBacomeDriver(View view) {
        Intent intent = new Intent(OrdersActivity.this, RegisterDriverActivity.class);
        startActivity(intent);
    }

    public void menuPurchases(View view) {
        Intent intent = new Intent(OrdersActivity.this, PurchasesActivity.class);
        startActivity(intent);
    }

    public void menuDriverSettings(View view) {
        Intent intent = new Intent(OrdersActivity.this, DriverSettingsActivity.class);
        startActivity(intent);
    }

    public void menuLogout(View view) {
        preferencesEdit = preferences.edit();
        preferencesEdit.putString("accessToken", "");
        preferencesEdit.commit();

        Intent intent = new Intent(OrdersActivity.this, LoginActivity.class);
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

    //start order details slider
    public void slideUp(View view){
        view.setVisibility(View.VISIBLE);
        setClickable(view, true);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public void slideDown(View view){
        view.setVisibility(View.INVISIBLE);
        setClickable(view, false);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public void swipe(View view) {
        if (view != null) {
            view.setOnTouchListener(new OnSwipeTouchListener(OrdersActivity.this){
                @Override
                public void onSwipeBottom() {
                    super.onSwipeBottom();
                    if (isUp) {
                        slideDown(myView);
                        isUp = false;
                    }
                }
            });
            if (view instanceof ViewGroup) {
                ViewGroup vg = ((ViewGroup) view);
                for (int i = 0; i < vg.getChildCount(); i++) {
                    swipe(vg.getChildAt(i));
                }
            }
        }
    }
    //end order details slider

    //start order list
    public class MyListAdapter extends ArrayAdapter<OrdersList> {

        //the list values in the List of type hero
        List<OrdersList> orderList;

        //activity context
        Context context;

        //the layout resource file for the list items
        int resource;

        //constructor initializing the values
        public MyListAdapter(Context context, int resource, List<OrdersList> orderList) {
            super(context, resource, orderList);
            this.context = context;
            this.resource = resource;
            this.orderList = orderList;
        }

        //this will return the ListView Item as a View
        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater layoutInflater = LayoutInflater.from(context);

            View view = layoutInflater.inflate(resource, null, false);

            final TextView orderDate = (TextView) view.findViewById(R.id.label_orderDate);
            final TextView orderDriver = (TextView) view.findViewById(R.id.label_orderDriver);
            final TextView orderPrice = (TextView) view.findViewById(R.id.label_orderPrice);
            final TextView orderStatus = (TextView) view.findViewById(R.id.label_orderStatus);

            final OrdersList order = orderList.get(position);

            String[] date = order.OrderDate.split("[T.]");

            orderDate.setText(date[0] + "\n" + date[1]);
            orderDriver.setText(order.Driver.Name);
            orderPrice.setText(order.Price.toString() + " zł");
            orderStatus.setText(order.Status.Status);

            if(order.Status.Id == 1){
                orderStatus.setTextColor(Color.RED);
            } else if(order.Status.Id == 2) {
                orderStatus.setTextColor(Color.rgb(255, 165, 0));
            } else if(order.Status.Id == 3) {
                orderStatus.setTextColor(Color.BLUE);
            } else {
                orderStatus.setTextColor(Color.GREEN);
            }

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if (isUp) {
                        slideDown(myView);
                        isUp = false;
                    } else {
                        slideUp(myView);
                        isUp = true;
                    }

                    int orderId = orderList.get(position).Id;

                    //start http request - get Order details
                    Retrofit.Builder builderOrder = new Retrofit.Builder().baseUrl(getString(R.string.baseUrl))
                            .addConverterFactory(GsonConverterFactory.create());
                    Retrofit retrofitOrder = builderOrder.build();

                    OrdersService ordersServiceOrder = retrofitOrder.create(OrdersService.class);
                    Call<QueryResult<OrderDetails>> callOrder = ordersServiceOrder.GetSpecificOrder(accessToken, orderId);


                    callOrder.enqueue(new Callback<QueryResult<OrderDetails>>() {
                        @Override
                        public void onResponse(Call<QueryResult<OrderDetails>> call, Response<QueryResult<OrderDetails>> response) {
                            if (response.code() == 400) {
                                //wykonanie HTTP
                                String responseJson = null;
                                try {
                                    responseJson = response.errorBody().string();
                                } catch (IOException e) {
                                    Toast.makeText(OrdersActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

                                    Toast.makeText(OrdersActivity.this, errors, Toast.LENGTH_LONG).show();
                                } else {
                                    //Jeżeli nie błędy walidacji to..
                                    errorResponseAuthorization = gson.fromJson(responseJson, OperationError.class);

                                    Toast.makeText(OrdersActivity.this, errorResponseAuthorization.ErrorDetails.Message, Toast.LENGTH_LONG).show();

                                    if (errorResponseAuthorization.ErrorDetails.Code == "500") {
                                        //jeżeli błąd Bazy danych
                                        Toast.makeText(OrdersActivity.this, "Wysyłam raport", Toast.LENGTH_LONG).show();
                                    } else {
                                        //jeżeli błąd autoryzacji
                                        Intent intent = new Intent(OrdersActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            } else if (response.code() == 200){
                                //Jeżeli sukces
                                QueryResult<OrderDetails> result = response.body();

                                if (result != null) {
                                    String[] date = result.Items.OrderDate.split("[T.]");
                                    String[] fromPlace = result.Items.FromPlace.split(",");
                                    String[] toPlace = result.Items.ToPlace.split(",");

                                    specificOrderDate.setText(date[0] + "\n" + date[1]);
                                    specificOrderDriver.setText(result.Items.Driver.Name);
                                    specificOrderPrice.setText(result.Items.Price.toString() + " zł");
                                    specificOrderStatus.setText(result.Items.Status.Status);
                                    specificOrderDistance.setText(result.Items.Distance.toString() + " km");
                                    specificOrderDriverPhoneNumber.setText(result.Items.Driver.PhoneNumber);
                                    specificOrderDuration.setText(result.Items.Duration);
                                    specificOrderFromPlace.setText(fromPlace[0] + ", " + (fromPlace[1].split(" "))[2]);
                                    specificOrderToPlace.setText(toPlace[0] + ", " + (toPlace[1].split(" "))[2]);

                                    specificOrderFromPlace.setMovementMethod(new ScrollingMovementMethod());
                                    specificOrderToPlace.setMovementMethod(new ScrollingMovementMethod());

                                    if(order.Status.Id == 1){
                                        specificOrderStatus.setTextColor(Color.RED);
                                    } else if(order.Status.Id == 2) {
                                        specificOrderStatus.setTextColor(Color.rgb(255, 165, 0));
                                    } else if(order.Status.Id == 3) {
                                        specificOrderStatus.setTextColor(Color.BLUE);
                                    } else {
                                        specificOrderStatus.setTextColor(Color.GREEN);
                                    }
                                }
                            } else {
                                //jeżeli brak internetu
                                Toast.makeText(OrdersActivity.this, "Brak połączenia z Internetem", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<QueryResult<OrderDetails>> call, Throwable t) {
                            Toast.makeText(OrdersActivity.this, "Coś poszło nie tak: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                    //end http request - get Order details
                }
            });

            //finally returning the view
            return view;
        }
    }
    //end order list
}

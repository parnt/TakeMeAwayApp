package com.app.takemeaway.takemeaway;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Helpers.AccountsService;
import Helpers.DirectionsParser;
import Helpers.DirectionsService;
import Helpers.OnSwipeTouchListener;
import Helpers.PlaceAutoCompleteAdapter;
import Models.AccountsModels.AccountDetails;
import Models.DirectionsModel.DirectionResult;
import Models.DirectionsModel.DirectionsInputModel;
import Models.ErrorCollection;
import Models.OperationError;
import Models.QueryResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener {
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
    View myView;
    TextView distance;
    TextView duration;
    TextView fromPlace;
    TextView toPlace;
    Button nextStep;
    Button cancelDirection;
    //end elements

    //start map
    private GoogleMap mMap;
    private static final int LOCATION_REQUEST = 500;
    ArrayList<LatLng> listPoints;
    private AutoCompleteTextView SearchText;
    private PlaceAutoCompleteAdapter placeAutoCompleteAdapter;
    //private GoogleApiClient googleApiClient;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(50.5,19), new LatLng(51,19.5));
    private Marker myMarker;
    //end map

    //start configurations
    boolean isUp;
    String routeId;
    //end configurations

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //start menu
        layoutContent = (View) findViewById(R.id.map); //wszystko po za menu i przyciskiem

        menu = (View) findViewById(R.id.menuViev);
        menu.setVisibility(View.INVISIBLE);
        setClickable(menu, false);
        isOpened = false;

        findViewById(R.id.map).setOnTouchListener(new View.OnTouchListener() {
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

        findViewById(R.id.map).setOnTouchListener(new OnSwipeTouchListener(HomeActivity.this) {
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
        myView = (View) findViewById(R.id.my_view);
        distance = (TextView) findViewById(R.id.label_distance);
        duration = (TextView) findViewById(R.id.label_duration);
        fromPlace = (TextView) findViewById(R.id.label_start);
        toPlace = (TextView) findViewById(R.id.label_target);
        nextStep = (Button) findViewById(R.id.nextStep);
        cancelDirection = (Button) findViewById(R.id.CancelDirection);
        //end elements

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
                        Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

                        Toast.makeText(HomeActivity.this, errors, Toast.LENGTH_LONG).show();
                    } else {
                        //Jeżeli nie błędy walidacji to..
                        errorResponseAuthorization = gson.fromJson(responseJson, OperationError.class);

                        Toast.makeText(HomeActivity.this, errorResponseAuthorization.ErrorDetails.Message, Toast.LENGTH_LONG).show();

                        if (errorResponseAuthorization.ErrorDetails.Code == "500") {
                            //jeżeli błąd Bazy danych
                            Toast.makeText(HomeActivity.this, "Wysyłam raport", Toast.LENGTH_LONG).show();
                        } else {
                            //jeżeli błąd autoryzacji
                            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
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
                    Toast.makeText(HomeActivity.this, "Brak połączenia z Internetem", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<QueryResult<AccountDetails>> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Coś poszło nie tak: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        //end http request - get Account Details

        //start map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        SearchText = (AutoCompleteTextView) findViewById(R.id.input_search);

        mapFragment.getMapAsync(this);
        listPoints = new ArrayList<>();

        init();
        //end map

        //start directions details slider
        myView.setVisibility(View.INVISIBLE);
        setClickable(myView, false);
        //TODO - przyciski wykluczyc
        //swipe(myView);
        isUp = false;
        //end directions details slider
    }

    public void cancelDirectionsButton(View view) {
        if (isUp) {
            slideDown(myView);
            isUp = false;
        }

        listPoints.clear();
        mMap.clear();
    }

    public void nextStepButton(View view){
        Intent intent = new Intent(HomeActivity.this, DriversActivity.class);
        intent.putExtra("routeId", routeId);
        startActivity(intent);
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
        Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    public void menuOrders(View view) {
        Intent intent = new Intent(HomeActivity.this, OrdersActivity.class);
        startActivity(intent);
    }

    public void menuSettings(View view) {
        Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void menuBacomeDriver(View view) {
        Intent intent = new Intent(HomeActivity.this, RegisterDriverActivity.class);
        startActivity(intent);
    }

    public void menuPurchases(View view) {
        Intent intent = new Intent(HomeActivity.this, PurchasesActivity.class);
        startActivity(intent);
    }

    public void menuDriverSettings(View view) {
        Intent intent = new Intent(HomeActivity.this, DriverSettingsActivity.class);
        startActivity(intent);
    }

    public void menuLogout(View view) {
        preferencesEdit = preferences.edit();
        preferencesEdit.putString("accessToken", "");
        preferencesEdit.commit();

        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
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

    //start directions details slider
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
            view.setOnTouchListener(new OnSwipeTouchListener(HomeActivity.this){
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
    //end directions details slider

    //start map
    private void init(){
        /*googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();*/

        //placeAutoCompleteAdapter = new PlaceAutoCompleteAdapter(this, googleApiClient, LAT_LNG_BOUNDS, null);

        placeAutoCompleteAdapter = new PlaceAutoCompleteAdapter(this, Places.getGeoDataClient(this),LAT_LNG_BOUNDS,null);

        SearchText.setAdapter(placeAutoCompleteAdapter);

        SearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == keyEvent.ACTION_DOWN
                        || keyEvent.getAction() == keyEvent.KEYCODE_ENTER){
                    geoLocate();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }

        mMap.setPadding(0,120,0,0);
        mMap.setMyLocationEnabled(true);

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = service.getBestProvider(criteria, false);
        Location location = service.getLastKnownLocation(provider);
        if(location != null) {
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15.0f));
        } else{
            LatLng userLocation = new LatLng(50.8118195, 19.1203094);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15.0f));
        }

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                //Reset marker when already 2
                if (listPoints.size() == 2) {
                    listPoints.clear();
                    mMap.clear();
                }
                //Save first point select
                listPoints.add(latLng);
                //Create marker
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

                if (listPoints.size() == 1) {
                    //Add first marker to the map
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else {
                    //Add second marker to the map
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }
                mMap.addMarker(markerOptions);

                if (listPoints.size() == 2) {
                    //start model
                    DirectionsInputModel directionsInputModel = new DirectionsInputModel();
                    directionsInputModel.Origin = (Double.toString(listPoints.get(0).latitude)) + "," + (Double.toString(listPoints.get(0).longitude));
                    directionsInputModel.Destination = (Double.toString(listPoints.get(1).latitude)) + "," + (Double.toString(listPoints.get(1).longitude));
                    directionsInputModel.WantFastestRoute = true;
                    //end model

                    //start http request - post directions
                    Retrofit.Builder builder = new Retrofit.Builder().baseUrl(getString(R.string.baseUrl))
                            .addConverterFactory(GsonConverterFactory.create());
                    Retrofit retrofit = builder.build();

                    DirectionsService directionsService = retrofit.create(DirectionsService.class);
                    Call<QueryResult<DirectionResult>> call = directionsService.CreateDirections(accessToken, directionsInputModel);


                    call.enqueue(new Callback<QueryResult<DirectionResult>>() {
                        @Override
                        public void onResponse(Call<QueryResult<DirectionResult>> call, Response<QueryResult<DirectionResult>> response) {
                            if (response.code() == 400) {
                                //wykonanie HTTP
                                String responseJson = null;
                                try {
                                    responseJson = response.errorBody().string();
                                } catch (IOException e) {
                                    Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

                                    Toast.makeText(HomeActivity.this, errors, Toast.LENGTH_LONG).show();
                                } else {
                                    //Jeżeli nie błędy walidacji to..
                                    errorResponseAuthorization = gson.fromJson(responseJson, OperationError.class);

                                    Toast.makeText(HomeActivity.this, errorResponseAuthorization.ErrorDetails.Message, Toast.LENGTH_LONG).show();

                                    if (errorResponseAuthorization.ErrorDetails.Code == "500") {
                                        //jeżeli błąd Bazy danych
                                        Toast.makeText(HomeActivity.this, "Wysyłam raport", Toast.LENGTH_LONG).show();
                                    } else {
                                        //jeżeli błąd autoryzacji
                                        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            } else if (response.code() == 200){
                                //Jeżeli sukces
                                QueryResult<DirectionResult> result = response.body();

                                if (result != null) {
                                    distance.setText(result.Items.Distance.Text);
                                    duration.setText(result.Items.Duration.Text);
                                    fromPlace.setText(result.Items.FromPlace);
                                    toPlace.setText(result.Items.ToPlace);

                                    if (isUp) {
                                        slideDown(myView);
                                        isUp = false;
                                    } else {
                                        slideUp(myView);
                                        isUp = true;
                                    }

                                    routeId = result.Items.Id;

                                    decodePoly(result.Items.Polyline);
                                    //TODO - directions - linia
                                    //decodePoly("aq{tHsoxsBEdBCXM?mAGI@G@y@EoLq@eA]UOMOOWe@eAQe@m@{BiD_PuCyMi@uA]k@cAsAYYSKe@Gq@D}@T}C~AqGlDuFzC_AUkAgGI_@Kc@a@XMFo@^qItE_@TORGPmF~B}C~A{AfAkCfCm@r@qBrC{HtKyNjSq@p@yCtBaAl@aAf@w@Z{@R}@Hw@@uAIaB]cA_@i@Yg@_@}AmAyCkCiAw@}A_AaAg@s@Y{Bu@sA]{L_CgAOiTuDkHqAcNcC_MyBa@Ig@WYWg@gAYcAq@_CWe@[Ye@Ou@ASB]N_@Z_@l@m@bBUbAQpAShGGhCBdCZhH?`AFfARnDPnFVzJRjP^hTAzCI|NApCHpCj@fPV`Ha@?qJEWDe@fDMlAIxAv@_@\\KZAhBD");
                                }
                            } else {
                                //jeżeli brak internetu
                                Toast.makeText(HomeActivity.this, "Brak połączenia z Internetem", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<QueryResult<DirectionResult>> call, Throwable t) {
                            Toast.makeText(HomeActivity.this, "Coś poszło nie tak: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                    //end http request - post Directions

                    //Create the URL to get request from first marker to second marker
//                    String url = getRequestUrl(listPoints.get(0), listPoints.get(1));
//                    TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
//                    taskRequestDirections.execute(url);
                }
            }
        });
    }

    private void geoLocate(){
        String searchString = SearchText.getText().toString();

        Geocoder geocoder = new Geocoder(HomeActivity.this);
        List<Address> listAddress = new ArrayList<>();
        try{
            listAddress = geocoder.getFromLocationName(searchString, 1);
        } catch(IOException e){
        }

        if(listAddress.size()>0){
            Address address = listAddress.get(0);
            //.makeText(this,address.toString(), Toast.LENGTH_SHORT).show();

            LatLng latLng= new LatLng(address.getLatitude(), address.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));

            //do przemyślenia
            mMap.setOnMarkerClickListener(this);

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(address.getAddressLine(0));
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

            myMarker = mMap.addMarker(markerOptions);
            myMarker.showInfoWindow();
        }
    }

    private String getRequestUrl(LatLng origin, LatLng dest) {
        //Value of origin
        String str_org = "origin=" + origin.latitude +","+origin.longitude;
        //Value of destination
        String str_dest = "destination=" + dest.latitude+","+dest.longitude;
        //Set value enable the sensor
        String sensor = "sensor=false";
        //Mode for find direction
        String mode = "mode=driving";
        //Build the full param
        String param = str_org +"&" + str_dest + "&" +sensor+"&" +mode;
        //Output format
        String output = "json";
        //Create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        return url;
    }

    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get the response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
                break;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.equals(myMarker))
        {
            if (listPoints.size() == 2) {
                listPoints.clear();
                mMap.clear();
            }
            //Save first point select
            listPoints.add(marker.getPosition());
            //Create marker
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(marker.getPosition());

            marker.remove();

            if (listPoints.size() == 1) {
                //Add first marker to the map
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            } else {
                //Add second marker to the map
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            }
            mMap.addMarker(markerOptions);

            if (listPoints.size() == 2) {
                //start model
                    DirectionsInputModel directionsInputModel = new DirectionsInputModel();
                    directionsInputModel.Origin = (Double.toString(listPoints.get(0).latitude)) + "," + (Double.toString(listPoints.get(0).longitude));
                    directionsInputModel.Destination = (Double.toString(listPoints.get(1).latitude)) + "," + (Double.toString(listPoints.get(1).longitude));
                    directionsInputModel.WantFastestRoute = true;
                    //end model

                    //start http request - post directions
                    Retrofit.Builder builder = new Retrofit.Builder().baseUrl(getString(R.string.baseUrl))
                            .addConverterFactory(GsonConverterFactory.create());
                    Retrofit retrofit = builder.build();

                    DirectionsService directionsService = retrofit.create(DirectionsService.class);
                    Call<QueryResult<DirectionResult>> call = directionsService.CreateDirections(accessToken, directionsInputModel);


                    call.enqueue(new Callback<QueryResult<DirectionResult>>() {
                        @Override
                        public void onResponse(Call<QueryResult<DirectionResult>> call, Response<QueryResult<DirectionResult>> response) {
                            if (response.code() == 400) {
                                //wykonanie HTTP
                                String responseJson = null;
                                try {
                                    responseJson = response.errorBody().string();
                                } catch (IOException e) {
                                    Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

                                    Toast.makeText(HomeActivity.this, errors, Toast.LENGTH_LONG).show();
                                } else {
                                    //Jeżeli nie błędy walidacji to..
                                    errorResponseAuthorization = gson.fromJson(responseJson, OperationError.class);

                                    Toast.makeText(HomeActivity.this, errorResponseAuthorization.ErrorDetails.Message, Toast.LENGTH_LONG).show();

                                    if (errorResponseAuthorization.ErrorDetails.Code == "500") {
                                        //jeżeli błąd Bazy danych
                                        Toast.makeText(HomeActivity.this, "Wysyłam raport", Toast.LENGTH_LONG).show();
                                    } else {
                                        //jeżeli błąd autoryzacji
                                        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            } else if (response.code() == 200){
                                //Jeżeli sukces
                                QueryResult<DirectionResult> result = response.body();

                                if (result != null) {
                                    distance.setText(result.Items.Distance.Text);
                                    duration.setText(result.Items.Duration.Text);
                                    fromPlace.setText(result.Items.FromPlace);
                                    toPlace.setText(result.Items.ToPlace);

                                    if (isUp) {
                                        slideDown(myView);
                                        isUp = false;
                                    } else {
                                        slideUp(myView);
                                        isUp = true;
                                    }

                                    routeId = result.Items.Id;

                                    decodePoly(result.Items.Polyline);
                                    //TODO - directions - linia
                                    //decodePoly("aq{tHsoxsBEdBCXM?mAGI@G@y@EoLq@eA]UOMOOWe@eAQe@m@{BiD_PuCyMi@uA]k@cAsAYYSKe@Gq@D}@T}C~AqGlDuFzC_AUkAgGI_@Kc@a@XMFo@^qItE_@TORGPmF~B}C~A{AfAkCfCm@r@qBrC{HtKyNjSq@p@yCtBaAl@aAf@w@Z{@R}@Hw@@uAIaB]cA_@i@Yg@_@}AmAyCkCiAw@}A_AaAg@s@Y{Bu@sA]{L_CgAOiTuDkHqAcNcC_MyBa@Ig@WYWg@gAYcAq@_CWe@[Ye@Ou@ASB]N_@Z_@l@m@bBUbAQpAShGGhCBdCZhH?`AFfARnDPnFVzJRjP^hTAzCI|NApCHpCj@fPV`Ha@?qJEWDe@fDMlAIxAv@_@\\KZAhBD");
                                }
                            } else {
                                //jeżeli brak internetu
                                Toast.makeText(HomeActivity.this, "Brak połączenia z Internetem", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<QueryResult<DirectionResult>> call, Throwable t) {
                            Toast.makeText(HomeActivity.this, "Coś poszło nie tak: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                    //end http request - post Directions

//                String url = getRequestUrl(listPoints.get(0), listPoints.get(1));
//                TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
//                taskRequestDirections.execute(url);
            }
        }

        return false;
    }

    public class TaskRequestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>> > {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //Get list route and display it into the map

            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat,lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }

            if (polylineOptions!=null) {
                mMap.addPolyline(polylineOptions);


                //bedzie tylko jedna
                /*ArrayList<LatLng> puntos1 = decodePoly("ud{tHonxsBt@kF@a@EYGOc@e@Qo@Kg@FOPq@`@sCRgAJ[j@qA@QEScAcB`CgFPo@Lw@J{@DcA?uCAkVGiBGeAWqB]yBu@_EWk@m@kAy@iAm@mA]_AUaAg@gD{@oHQkAU}@Q[a@m@c@o@_BqCEe@@UHUPKd@GVAPHDFFN@TEZ[hAy@tAaExHaKjRqI~OqAzBmAfBwAfByBrBsDjCsExCeFlDoH|EqDrBaChAwNrG}C~A{AfAkCfCm@r@qBrC{HtKyNjSq@p@yCtBaAl@aAf@w@Z{@R}@Hw@@uAIaB]cA_@i@Yg@_@}AmAyCkCiAw@}A_AaAg@s@Y{Bu@sA]{L_CgAOiTuDkHqAcNcC_MyBa@Ig@WYWg@gAYcAq@_CWe@[Ye@Ou@ASB]N_@Z_@l@m@bBUbAQpAYdGA~DBrAXdITpEXfIVzJFzEf@x[BlCA`DK|N@xBJtC~@|WiB?iHEWDeExAsCz@_LdDyDnAp@lHpEwAhI}B");
                ArrayList<LatLng> puntos2 = decodePoly("ud{tHonxsBt@kF@a@EYGOc@e@Qo@Kg@SXc@x@_A|BUz@K\\}A?sA@W@MlAKrC_BGMBgG_@aFWQGc@K[QSOQ[c@cAIM_@sAe@iBqBiJoDwPe@}Ac@aAaAsAa@g@SO[IM?q@@i@Jm@VgHzDkKxFu@d@mAfAQXe@bAiG`LgMjUcUva@yGtL]`@sChCsKtJw@~@uBrCcJbMsHhLYXw@nAc@d@GDi@VOF_@JoNW_AAUMqAY_@EmA?U?Ad@C`OArH_@Hu@H}AJsJSoFI}N[kGK_LSeGSuA@iB?iHEWDeExAsCz@_LdDyDnAp@lHpEwAhI}B");

                PolylineOptions ruta=new PolylineOptions();
                for(int i=0;i<puntos1.size();i++){
                    ruta.add(new LatLng(puntos1.get(i).latitude, puntos1.get(i).longitude));
                }
                ruta.color(Color.RED).width(7);
                mMap.addPolyline(ruta);

                //druga
                PolylineOptions ruta2=new PolylineOptions();
                for(int i=0;i<puntos2.size();i++){
                    ruta2.add(new LatLng(puntos2.get(i).latitude, puntos2.get(i).longitude));
                }
                ruta.color(Color.rgb(220,220,220)).width(5);
                mMap.addPolyline(ruta2);*/

            } else {
                Toast.makeText(getApplicationContext(), "Direction not found!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private ArrayList<LatLng> decodePoly(String encoded) {

        //Log.i("Location", "String received: "+encoded);
        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),(((double) lng / 1E5)));
            poly.add(p);
        }

        for(int i=0;i<poly.size();i++){
            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
            for (int z = 0; z < poly.size(); z++) {
                LatLng point = poly.get(z);
                options.add(point);
            }
            mMap.addPolyline(options);
            //Log.i("Location", "Point sent: Latitude: "+poly.get(i).latitude+" Longitude: "+poly.get(i).longitude);
        }
        return poly;
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    //end map
}

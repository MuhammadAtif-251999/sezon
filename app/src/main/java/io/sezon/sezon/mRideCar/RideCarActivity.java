package io.sezon.sezon.mRideCar;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.sezon.sezon.MangJekApplication;
import io.sezon.sezon.R;
import io.sezon.sezon.api.MapDirectionAPI;
import io.sezon.sezon.api.ServiceGenerator;
import io.sezon.sezon.api.service.BookService;
import io.sezon.sezon.gmap.directions.Directions;
import io.sezon.sezon.gmap.directions.Route;
import io.sezon.sezon.home.MainActivity;
import io.sezon.sezon.home.submenu.TopUpActivity;
import io.sezon.sezon.mMart.PlaceAPI;
import io.sezon.sezon.mMart.PlaceApiAdapter;
import io.sezon.sezon.mMart.PlaceAutocompleteAdapter;
import io.sezon.sezon.mMart.PlaceModel;
import io.sezon.sezon.model.Driver;
import io.sezon.sezon.model.Fitur;
import io.sezon.sezon.model.User;
import io.sezon.sezon.model.json.book.GetNearRideCarRequestJson;
import io.sezon.sezon.model.json.book.GetNearRideCarResponseJson;
import io.sezon.sezon.model.json.book.RequestRideCarRequestJson;
import io.sezon.sezon.utils.ConnectivityUtils;
import io.sezon.sezon.utils.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static io.sezon.sezon.config.General.BOUNDS;

/**
 * Created by bradhawk on 10/26/2016.
 */

public class RideCarActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String FITUR_KEY = "FiturKey";
    private static final String TAG = "RideCarActivity";
    private static final int REQUEST_PERMISSION_LOCATION = 991;
    @BindView(R.id.rideCar_title)
    TextView title;
    @BindView(R.id.rideCar_pickUpContainer)
    LinearLayout setPickUpContainer;
    @BindView(R.id.rideCar_destinationContainer)
    LinearLayout setDestinationContainer;
    @BindView(R.id.rideCar_pickUpButton)
    Button setPickUpButton;
    @BindView(R.id.rideCar_destinationButton)
    Button setDestinationButton;
    @BindView(R.id.rideCar_pickUpText)
    AutoCompleteTextView pickUpText;
    @BindView(R.id.rideCar_destinationText)
    AutoCompleteTextView destinationText;
    @BindView(R.id.rideCar_detail)
    LinearLayout detail;
    @BindView(R.id.rideCar_distance)
    TextView distanceText;
    @BindView(R.id.rideCar_price)
    TextView priceText;
    @BindView(R.id.rideCar_paymentGroup)
    RadioGroup paymentGroup;
    @BindView(R.id.rideCar_mPayPayment)
    RadioButton mPayButton;
    @BindView(R.id.rideCar_cashPayment)
    RadioButton cashButton;


    @BindView(R.id.rideCar_topUp)
    Button topUpButton;
    @BindView(R.id.rideCar_order)
    Button orderButton;
    @BindView(R.id.rideCar_mPayBalance)
    TextView mPayBalanceText;
    @BindView(R.id.rideCar_select_car_container)
    RelativeLayout carSelectContainer;
    @BindView(R.id.rideCar_select_car)
    ImageView carSelect;
    @BindView(R.id.rideCar_select_car_text)
    TextView carSelectText;
    @BindView(R.id.rideCar_select_ride_container)
    RelativeLayout rideSelectContainer;
    @BindView(R.id.rideCar_select_ride)
    ImageView rideSelect;
    @BindView(R.id.ride_car_select_ride_text)
    TextView rideSelectText;
    @BindView(R.id.discountText)
    TextView discountText;

    @BindView(R.id.piliy_cash)
    RelativeLayout pakaikas;
    @BindView(R.id.piliy_pay)
    RelativeLayout pakaipay;


    @BindView(R.id.pakai_pay)
    RelativeLayout pay;
    @BindView(R.id.pakai_cash)
    RelativeLayout cash;
    @BindView(R.id.ova)
    TextView oval;


    private GoogleMap gMap;
    private GoogleApiClient googleApiClient;
    private Location lastKnownLocation;
    private LatLng pickUpLatLang;
    private LatLng destinationLatLang;
    private Polyline directionLine;
    private Marker pickUpMarker;
    private Marker destinationMarker;
    private List<Driver> driverAvailable;
    private List<Marker> driverMarkers;

    //private Realm realm;
    //private Fitur designedFitur;
    int fiturId;//above 2 line delete by Nitin and when Required Realm Data just get it directlly

    private double jarak;
    private double timeDistance = 0;
    private long harga;
    private long saldoMpay;
    private boolean isMapReady = false;
    //    private long minPrice = 0 ;
    private PlaceApiAdapter mAdapter;


    private okhttp3.Callback updateRouteCallback = new okhttp3.Callback() {
        @Override
        public void onFailure(okhttp3.Call call, IOException e) {

        }

        @Override
        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
            if (response.isSuccessful()) {
                final String json = response.body().string();
                final long distance = MapDirectionAPI.getDistance(RideCarActivity.this, json);
                final long time = MapDirectionAPI.getTimeDistance(RideCarActivity.this, json);
                if (distance >= 0) {
                    RideCarActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateLineDestination(json);
                            updateDistance(distance);
                            timeDistance = time / 60;
                        }
                    });
                }
            }
        }
    };

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_car);
        ButterKnife.bind(this);

        pakaikas.setBackgroundResource(R.drawable.bg_tidak_pilih);
        pakaikas.setClickable(true);

//        diskonMpay = MangJekApplication.getInstance(this).getDiskonMpay();
        setPickUpContainer.setVisibility(View.GONE);
        setDestinationContainer.setVisibility(View.GONE);
        detail.setVisibility(View.GONE);

        User userLogin = MangJekApplication.getInstance(this).getLoginUser();
        saldoMpay = userLogin.getmPaySaldo();

        pickUpText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                setPickUpContainer.setVisibility((b) ? View.VISIBLE : View.GONE);
            }
        });


        pakaikas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                cashButton.setChecked(true);
                mPayButton.setChecked(false);

                pakaikas.setBackgroundResource(R.drawable.bg_tidak_pilih);
                pakaipay.setBackgroundResource(R.color.bgpay);

                discountText.setTextColor(getResources().getColor(R.color.grey));
                priceText.setTextColor(getResources().getColor(R.color.black));

                cash.setVisibility(View.VISIBLE);
                pay.setVisibility(View.GONE);

            }
        });


        pakaipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mPayButton.setChecked(true);
                cashButton.setChecked(false);
                cash.setVisibility(View.GONE);
                pay.setVisibility(View.VISIBLE);

                priceText.setTextColor(getResources().getColor(R.color.grey));


                pakaikas.setBackgroundResource(R.color.bgpay);

                pakaipay.setBackgroundResource(R.drawable.bg_tidak_pilih);

                discountText.setTextColor(getResources().getColor(R.color.orange));


            }
        });
        paymentGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (paymentGroup.getCheckedRadioButtonId()) {
                    case R.id.rideCar_mPayPayment:
                        long biayaTotal = (long) (harga * getFiturObject().getBiayaAkhir());
                        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
                        String formattedText = String.format(Locale.US, "%s.00", formattedTotal);
                        String formattedText1 = String.format(Locale.US, "Pay Wallet $%s.00", formattedTotal);
                        discountText.setText(formattedText);
                        oval.setText(formattedText1);
                        break;

                    case R.id.rideCar_cashPayment:
                        biayaTotal = harga;
                        formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
                        formattedText = String.format(Locale.US, "%s.00", formattedTotal);
                        priceText.setText(formattedText);
                        break;

                }
            }
        });


        destinationText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                setDestinationContainer.setVisibility((b) ? View.VISIBLE : View.GONE);
            }
        });

        setPickUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickUpClick();


            }
        });

        setDestinationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDestinationClick();

            }
        });

        topUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TopUpActivity.class));
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.rideCar_mapView);
        mapFragment.getMapAsync(this);

        driverAvailable = new ArrayList<>();
        driverMarkers = new ArrayList<>();

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .build();
        }




        Intent intent = getIntent();
        fiturId = intent.getIntExtra(FITUR_KEY, -1);
        setupFitur();

        //   discountText.setText(designedFitur.getDiskon());
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOrderButtonClick();
            }
        });

        carSelectContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   Toast.makeText(RideCarActivity.this,"Maaf untuk sementara layanan tidak tersedia", Toast.LENGTH_SHORT).show();
                selectCar();
            }
        });

        rideSelectContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectRide();
            }
        });


//        Log.e("diskon", designedFitur.getDiskon() + "");
//        Log.e("fitur", designedFitur.getIdFitur() + "");

        setupAutocompleteTextView();

    }

    private void btn1() {

    }

    private void setupAutocompleteTextView() {
        int layout = android.R.layout.simple_list_item_1;
        mAdapter = new PlaceApiAdapter(this, layout);

        pickUpText.setAdapter(mAdapter);

        pickUpText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager inputManager =
                        (InputMethodManager) RideCarActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(pickUpText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                PlaceModel item = mAdapter.getPlaceObject(position);

                    try {
                        LatLng latLng = new GetLatLon().execute(item.place_name).get();
                        if (latLng != null) {
                            gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            onPickUpClick();
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

            }
        });


        destinationText.setAdapter(mAdapter);
        destinationText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager inputManager =
                        (InputMethodManager) RideCarActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(destinationText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                PlaceModel item = mAdapter.getPlaceObject(position);

                //LatLng latLng = getLocationFromAddress(item.getFullText(null).toString());
                //gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                //onDestinationClick();

                   try {
                       LatLng latLng = new GetLatLon().execute(item.place_name).get();
                       if (latLng != null) {
                           gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                           onDestinationClick();
                       }
                   }
                   catch (Exception e)
                   {
                       e.printStackTrace();
                   }

            }
        });

    }


    class GetLatLon extends AsyncTask<String, Void, LatLng> {

        @Override
        protected LatLng doInBackground(String... input) {
            HttpURLConnection conn = null;
            StringBuilder jsonResults = new StringBuilder();

            try {
                StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/geocode/json");
                sb.append("?key=AIzaSyB7E0P3S5ObRU4dtOqRm4hfqvYXB6t-3Ho");
                //sb.append("&types=address");
                sb.append("&address=" + URLEncoder.encode(input[0], "utf8"));

                URL url = new URL(sb.toString());
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());

                // Load the results into a StringBuilder
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
            } catch (MalformedURLException e) {
                android.util.Log.e(TAG, "Error processing Places API URL", e);
                return null;
            } catch (IOException e) {
                android.util.Log.e(TAG, "Error connecting to Places API", e);
                return null;
            }

            try {
                // Log.d(TAG, jsonResults.toString());

                // Create a JSON object hierarchy from the results
                JSONObject jsonObj = new JSONObject(jsonResults.toString());
                JSONArray resultJsonArray = jsonObj.getJSONArray("results");
                if (resultJsonArray.length() > 0) {
                    JSONObject jsonObject = resultJsonArray.getJSONObject(0);
                    JSONObject jobj_location = jsonObject.getJSONObject("geometry").getJSONObject("location");

                    double lat = jobj_location.optDouble("lat");
                    double lng = jobj_location.optDouble("lng");
                    return new LatLng(lat, lng);

                } else {
                    return null;
                }

            } catch (JSONException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(LatLng latLng) {
            super.onPostExecute(latLng);
        }
    }


    class GetLocationName extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... input) {
            HttpURLConnection conn = null;
            StringBuilder jsonResults = new StringBuilder();

            try {
                StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/geocode/json");
                sb.append("?key=AIzaSyB7E0P3S5ObRU4dtOqRm4hfqvYXB6t-3Ho");
                //sb.append("&types=address");
                sb.append("&latlng=" + URLEncoder.encode(input[0], "utf8"));

                URL url = new URL(sb.toString());
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());

                // Load the results into a StringBuilder
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
            } catch (MalformedURLException e) {
                android.util.Log.e(TAG, "Error processing Places API URL", e);
                return null;
            } catch (IOException e) {
                android.util.Log.e(TAG, "Error connecting to Places API", e);
                return null;
            }

            try {
                // Log.d(TAG, jsonResults.toString());

                // Create a JSON object hierarchy from the results
                JSONObject jsonObj = new JSONObject(jsonResults.toString());
                JSONArray resultJsonArray = jsonObj.getJSONArray("results");
                if (resultJsonArray.length() > 0) {
                    JSONObject jsonObject = resultJsonArray.getJSONObject(0);
                    return jsonObject.optString("formatted_address");

                } else {
                    return null;
                }

            } catch (JSONException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String locationname) {
            super.onPostExecute(locationname);
        }
    }

//    public LatLng getAddressFromLocation(String locationAddress) {
//        try {
//            Geocoder selected_place_geocoder = new Geocoder(RideCarActivity.this);
//            List<Address> address;
//
//            address = selected_place_geocoder.getFromLocationName(locationAddress, 10);
//
//            if (address == null) {
//                return null;
//            } else {
//                if (address.size() > 0) {
//                    Address location = address.get(0);
//                    double lat = location.getLatitude();
//                    double lng = location.getLongitude();
//                    return new LatLng(lat, lng);
//                } else {
//                    return null;
//                }
//
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//
//
//    }

//    private LatLng getLocationFromAddress(String strAddress) {
//
//        Geocoder coder = new Geocoder(this, getResources().getConfiguration().locale);
//        List<Address> address;
//        LatLng p1;
//
//        try {
//            address = coder.getFromLocationName(strAddress, 5);
//
//            Log.e("LOCATION", address.toString() + " Length : " + address.size());
//            if (address == null || address.isEmpty()) {
//                return null;
//            }
//
//            int tentatives = 0;
//            while (address.size() == 0 && (tentatives < 10)) {
//                address = coder.getFromLocationName(strAddress, 1);
//                tentatives++;
//            }
//
//            if (address.size() > 0) {
//                return new LatLng(address.get(0).getLatitude(), address.get(0).getLongitude());
//            } else {
//                return null;
//            }
//
//
////            Address location = address.get(0);
////            location.getLatitude();
////            location.getLongitude();
////
////            p1 = new LatLng(location.getLatitude(), location.getLongitude());
////            return p1;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        User userLogin = MangJekApplication.getInstance(this).getLoginUser();
        String formattedText = String.format(Locale.US, "USD %s.00",
                NumberFormat.getNumberInstance(Locale.US).format(userLogin.getmPaySaldo()));
        mPayBalanceText.setText(formattedText);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLastLocation(true);
            } else {
                // TODO: 10/15/2016 Tell user to use GPS
            }
        }
    }

    private void cancelOrder() {

    }


    private void selectCar() {
        carSelect.setSelected(true);
        carSelectText.setSelected(true);
        rideSelect.setSelected(false);
        rideSelectText.setSelected(false);

//        designedFitur = realm.where(Fitur.class).equalTo("idFitur", 2).findFirst();

//        minPrice = designedFitur.getBiaya_minimum();
        updateFitur();
    }

    private void selectRide() {
        rideSelect.setSelected(true);
        rideSelectText.setSelected(true);
        carSelect.setSelected(false);
        carSelectText.setSelected(false);

//        designedFitur = realm.where(Fitur.class).equalTo("idFitur", 1).findFirst();
//        minPrice = designedFitur.getBiaya_minimum();

        updateFitur();
    }

    private void updateFitur() {
        driverAvailable.clear();

        for (Marker m : driverMarkers) {
            m.remove();
        }
        driverMarkers.clear();

        if (getFiturObject().getIdFitur() == 1) {
            title.setText(R.string.home_mRide);
        } else if (getFiturObject().getIdFitur() == 2) {
            title.setText(R.string.home_mCar);
        }

        if (isMapReady) updateLastLocation(false);
    }

    private void setupFitur() {
        if (getFiturObject().getIdFitur() == 1) {
            title.setText(R.string.home_mRide);
            selectRide();
        } else if (getFiturObject().getIdFitur() == 2) {
            title.setText(R.string.home_mCar);
            selectCar();
        }
    }

    private Fitur getFiturObject() {
        return Realm.getDefaultInstance().where(Fitur.class).equalTo("idFitur", fiturId).findFirst();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        updateLastLocation(true);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.getUiSettings().setMyLocationButtonEnabled(true);

        isMapReady = true;

        updateLastLocation(true);
    }

    private void updateLastLocation(boolean move) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }
        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        gMap.setMyLocationEnabled(true);

        if (lastKnownLocation != null) {
            if (move) {
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), 17f)
                );

                gMap.animateCamera(CameraUpdateFactory.zoomTo(17f));
            }
            fetchNearDriver();
        }
    }

    private void fetchNearDriver(double latitude, double longitude) {
        if (lastKnownLocation != null) {
            User loginUser = MangJekApplication.getInstance(this).getLoginUser();

            BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
            GetNearRideCarRequestJson param = new GetNearRideCarRequestJson();
            param.setLatitude(latitude);
            param.setLongitude(longitude);

            if (getFiturObject().getIdFitur() == 1) {
                service.getNearRide(param).enqueue(new Callback<GetNearRideCarResponseJson>() {
                    @Override
                    public void onResponse(Call<GetNearRideCarResponseJson> call, Response<GetNearRideCarResponseJson> response) {
                        if (response.isSuccessful()) {
                            driverAvailable = response.body().getData();
                            createMarker();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<GetNearRideCarResponseJson> call, Throwable t) {

                    }
                });
            } else if (getFiturObject().getIdFitur() == 2) {
                service.getNearCar(param).enqueue(new Callback<GetNearRideCarResponseJson>() {
                    @Override
                    public void onResponse(Call<GetNearRideCarResponseJson> call, Response<GetNearRideCarResponseJson> response) {
                        if (response.isSuccessful()) {
                            driverAvailable = response.body().getData();
                            createMarker();
                        }
                    }

                    @Override
                    public void onFailure(Call<GetNearRideCarResponseJson> call, Throwable t) {

                    }
                });
            }
        }
    }

    private void fetchNearDriver() {
        if (lastKnownLocation != null) {
            User loginUser = MangJekApplication.getInstance(this).getLoginUser();

            BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
            GetNearRideCarRequestJson param = new GetNearRideCarRequestJson();
            param.setLatitude(lastKnownLocation.getLatitude());
            param.setLongitude(lastKnownLocation.getLongitude());

//            param.setLatitude(15.6093603);
//            param.setLongitude(32.5617515);

            if (getFiturObject().getIdFitur() == 1) {
                service.getNearRide(param).enqueue(new Callback<GetNearRideCarResponseJson>() {
                    @Override
                    public void onResponse(Call<GetNearRideCarResponseJson> call, Response<GetNearRideCarResponseJson> response) {
                        if (response.isSuccessful()) {
                            driverAvailable = response.body().getData();
                            createMarker();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<GetNearRideCarResponseJson> call, Throwable t) {

                    }
                });
            } else if (getFiturObject().getIdFitur() == 2) {
                service.getNearCar(param).enqueue(new Callback<GetNearRideCarResponseJson>() {
                    @Override
                    public void onResponse(Call<GetNearRideCarResponseJson> call, Response<GetNearRideCarResponseJson> response) {
                        if (response.isSuccessful()) {
                            driverAvailable = response.body().getData();
                            createMarker();
                        }
                    }

                    @Override
                    public void onFailure(Call<GetNearRideCarResponseJson> call, Throwable t) {

                    }
                });
            }
        }
    }

    private void createMarker() {
        if (!driverAvailable.isEmpty()) {
            for (Marker m : driverMarkers) {
                m.remove();
            }
            driverMarkers.clear();

            for (Driver driver : driverAvailable) {
                LatLng currentDriverPos = new LatLng(driver.getLatitude(), driver.getLongitude());
                if (getFiturObject().getIdFitur() == 1) {
                    driverMarkers.add(
                            gMap.addMarker(new MarkerOptions()
                                    .position(currentDriverPos)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_m_ride_pin)))
                    );
                } else {
                    driverMarkers.add(
                            gMap.addMarker(new MarkerOptions()
                                    .position(currentDriverPos)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_m_car_pin)))
                    );
                }
            }
        }
    }

    public void onClick(View v) {

        Drawable dr = getResources().getDrawable(R.drawable.bg_pilih_pay);
        dr.setColorFilter(Color.parseColor("#FF0000"), PorterDuff.Mode.SRC_ATOP);

        switch (v.getId()) {
            case R.id.pakai_cash:
                if (pakaipay == null) {
                    pakaipay = (RelativeLayout) findViewById(v.getId());
                } else {
                    pakaikas.setBackgroundResource(R.drawable.bg_pilih_pay);
                    pakaikas = (RelativeLayout) findViewById(v.getId());
                }
                pakaipay.setBackgroundDrawable(dr);

                break;


            default:
                break;
        }
    }

    private void onOrderButtonClick() {
        switch (paymentGroup.getCheckedRadioButtonId()) {
            case R.id.rideCar_mPayPayment:
                if (driverAvailable.isEmpty()) {
                    AlertDialog dialog = new AlertDialog.Builder(RideCarActivity.this)
                            .setMessage("Maaf, untuk sementara driver tidak tersedia.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .create();
                    dialog.show();
                } else {
                    RequestRideCarRequestJson param = new RequestRideCarRequestJson();
                    User userLogin = MangJekApplication.getInstance(this).getLoginUser();
                    param.setIdPelanggan(userLogin.getId());
                    param.setOrderFitur(String.valueOf(getFiturObject().getIdFitur()));
                    param.setStartLatitude(pickUpLatLang.latitude);
                    param.setStartLongitude(pickUpLatLang.longitude);
                    param.setEndLatitude(destinationLatLang.latitude);
                    param.setEndLongitude(destinationLatLang.longitude);
                    param.setJarak(this.jarak);
                    param.setHarga((long) (this.harga * getFiturObject().getBiayaAkhir()));
                    param.setAlamatAsal(pickUpText.getText().toString());
                    param.setAlamatTujuan(destinationText.getText().toString());

                    Log.e("AjoPay", "used");
                    param.setPakaiMpay(1);


                    Intent intent = new Intent(RideCarActivity.this, WaitingActivity.class);
                    intent.putExtra(WaitingActivity.REQUEST_PARAM, param);
                    intent.putExtra(WaitingActivity.DRIVER_LIST, (ArrayList) driverAvailable);
                    intent.putExtra("time_distance", timeDistance);
                    startActivity(intent);


                    if (saldoMpay < (harga * getFiturObject().getBiayaAkhir())) {
                        Toast.makeText(RideCarActivity.this, "Maaf saldo anda tidak mencukupi", Toast.LENGTH_SHORT).show();
                    } else {

                    }
                }


                break;
            case R.id.rideCar_cashPayment:
                if (driverAvailable.isEmpty()) {
                    AlertDialog dialog = new AlertDialog.Builder(RideCarActivity.this)
                            .setMessage("Sorry, no driver is available.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .create();
                    dialog.show();
                } else {
                    RequestRideCarRequestJson param = new RequestRideCarRequestJson();
                    User userLogin = MangJekApplication.getInstance(this).getLoginUser();
                    param.setIdPelanggan(userLogin.getId());
                    param.setOrderFitur(String.valueOf(getFiturObject().getIdFitur()));
                    param.setStartLatitude(pickUpLatLang.latitude);
                    param.setStartLongitude(pickUpLatLang.longitude);
                    param.setEndLatitude(destinationLatLang.latitude);
                    param.setEndLongitude(destinationLatLang.longitude);
                    param.setJarak(this.jarak);
                    param.setHarga(this.harga);
                    param.setAlamatAsal(pickUpText.getText().toString());
                    param.setAlamatTujuan(destinationText.getText().toString());


                    Log.e("PayPace", "Tidak dengan AjoPay");


                    Intent intent = new Intent(RideCarActivity.this, WaitingActivity.class);
                    intent.putExtra(WaitingActivity.REQUEST_PARAM, param);
                    intent.putExtra(WaitingActivity.DRIVER_LIST, (ArrayList) driverAvailable);
                    intent.putExtra("time_distance", timeDistance);
                    startActivity(intent);
                }
                break;
        }
    }

    private void onDestinationClick() {
        if (destinationMarker != null) destinationMarker.remove();
        LatLng centerPos = gMap.getCameraPosition().target;
        destinationMarker = gMap.addMarker(new MarkerOptions()
                .position(centerPos)
                .title("Destination")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_loc_antar)));
        destinationLatLang = centerPos;

        fillAddress(destinationText, destinationLatLang);

        requestRoute();
    }

    private void onPickUpClick() {
        if (pickUpMarker != null) pickUpMarker.remove();
        LatLng centerPos = gMap.getCameraPosition().target;
        pickUpMarker = gMap.addMarker(new MarkerOptions()
                .position(centerPos)
                .title("Pick Up")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_loc_jemput)));
        pickUpLatLang = centerPos;

        fillAddress(pickUpText, pickUpLatLang);

        destinationText.requestFocus();
        fetchNearDriver(pickUpLatLang.latitude, pickUpLatLang.longitude);
        requestRoute();
    }

    private void requestRoute() {
        if (pickUpLatLang != null && destinationLatLang != null) {
            MapDirectionAPI.getDirection(pickUpLatLang, destinationLatLang).enqueue(updateRouteCallback);
        }
    }

    private void fillAddress(final EditText editText, final LatLng latLng) {

        try {
            String address=new GetLocationName().execute(latLng.latitude+","+latLng.longitude).get();
            if(address!=null && !address.isEmpty())
                editText.setText(address);
            else
                editText.setText(R.string.text_addressNotAvailable);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void updateLineDestination(String json) {
        Directions directions = new Directions(RideCarActivity.this);
        try {
            List<Route> routes = directions.parse(json);

            if (directionLine != null) directionLine.remove();
            if (routes.size() > 0) {
                directionLine = gMap.addPolyline((new PolylineOptions())
                        .addAll(routes.get(0).getOverviewPolyLine())
                        .color(ContextCompat.getColor(RideCarActivity.this, R.color.greyText))
                        .width(15));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateDistance(long distance) {
        detail.setVisibility(View.VISIBLE);
        float km = ((float) distance) / 1000.0f;

        this.jarak = km;

        String format = String.format(Locale.US, "Distance (%.1f Km)", km);
        distanceText.setText(format);

        long biayaTotal = (long) (getFiturObject().getBiaya() * km);

        if (biayaTotal % 1 != 0) {
            biayaTotal += 1 - (biayaTotal % 1);
        }


        this.harga = biayaTotal;
        if (biayaTotal < getFiturObject().getBiaya_minimum()) {
            this.harga = getFiturObject().getBiaya_minimum();
            biayaTotal = getFiturObject().getBiaya_minimum();
        }

        if (mPayButton.isChecked()) {
            //Log.e("AjoPay", "Biaya total :" + biayaTotal + " kali " + getFiturObject().getBiayaAkhir());
            biayaTotal *= getFiturObject().getBiayaAkhir();

        }

        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
        String formattedText = String.format(Locale.US, "%s.00", formattedTotal);
        priceText.setText(formattedText);

        long biayaTotal1 = (long) (harga * getFiturObject().getBiayaAkhir());
        String formattedTotal1 = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal1);
        String formattedText1 = String.format(Locale.US, "%s.00", formattedTotal1);
        discountText.setText(formattedText1);

        if (saldoMpay < (harga * getFiturObject().getBiayaAkhir())) {
            pakaipay.setEnabled(false);
            mPayButton.setEnabled(false);
            cashButton.toggle();
        } else {
            mPayButton.setEnabled(true);
            pakaipay.setEnabled(true);
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

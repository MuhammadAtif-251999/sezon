package io.sezon.sezon.mSend;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import io.sezon.sezon.mMart.PlaceApiAdapter;
import io.sezon.sezon.mMart.PlaceAutocompleteAdapter;
import io.sezon.sezon.mMart.PlaceModel;
import io.sezon.sezon.model.Driver;
import io.sezon.sezon.model.Fitur;
import io.sezon.sezon.model.User;
import io.sezon.sezon.model.json.book.GetNearRideCarRequestJson;
import io.sezon.sezon.model.json.book.GetNearRideCarResponseJson;
import io.sezon.sezon.splash.SplashActivity;

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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static io.sezon.sezon.R.id.mSend_detail;
import static io.sezon.sezon.config.General.BOUNDS;
import static io.sezon.sezon.utils.Utility.round;

public class SendActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String FITUR_KEY = "mSendFiturKey";
    private static final String TAG = "mSendActivity";
    private static final int REQUEST_PERMISSION_LOCATION = 991;
    @BindView(R.id.mSend_title)
    TextView title;
    @BindView(R.id.mSend_pickUpContainer)
    LinearLayout setPickUpContainer;
    @BindView(R.id.mSend_destinationContainer)
    LinearLayout setDestinationContainer;
    @BindView(R.id.mSend_pickUpButton)
    Button setPickUpButton;
    @BindView(R.id.mSend_destinationButton)
    Button setDestinationButton;
    @BindView(R.id.mSend_pickUpText)
    AutoCompleteTextView pickUpText;
    @BindView(R.id.mSend_destinationText)
    AutoCompleteTextView destinationText;
    @BindView(mSend_detail)
    LinearLayout detail;
    @BindView(R.id.mSend_distance)
    TextView distanceText;
    @BindView(R.id.mSend_price)
    TextView priceText;
    @BindView(R.id.discountText)
    TextView discountText;
//    @BindView(R.id.mSend_paymentGroup)
//    RadioGroup paymentGroup;
//    @BindView(R.id.mSend_mPayPayment)
//    RadioButton mPayButton;
//    @BindView(R.id.mSend_cashPayment)
//    RadioButton cashButton;
//    @BindView(R.id.mSend_topUp)
//    Button topUpButton;
    @BindView(R.id.mSend_next)
    Button nextButton;
//    @BindView(R.id.mSend_mPayBalance)
//    TextView mPayBalanceText;
    private GoogleMap gMap;
    private GoogleApiClient googleApiClient;
    private Location lastKnownLocation;
    private LatLng pickUpLatLang;
    private LatLng destinationLatLang;
    private Polyline directionLine;
    private Marker pickUpMarker;
    private Marker destinationMarker;
    private ArrayList<Driver> driverAvailable;
    private List<Marker> driverMarkers;
    private Realm realm;
    private Fitur designedFitur;
    private long price;
    private double jarak;
    private double timeDistance = 0;
    private long harga;
    private long saldoMpay;
    private boolean isMapReady = false;
    private PlaceApiAdapter mAdapter;
    SupportMapFragment mapFragment;
    int fiturId;

//    private static final LatLngBounds BOUNDS = new LatLngBounds(
//            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    private okhttp3.Callback updateRouteCallback = new okhttp3.Callback() {
        @Override
        public void onFailure(okhttp3.Call call, IOException e) {

        }

        @Override
        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
            if (response.isSuccessful()) {
                final String json = response.body().string();
                final long distance = MapDirectionAPI.getDistance(SendActivity.this, json);
                final long time = MapDirectionAPI.getTimeDistance(SendActivity.this, json);
                if (distance >= 0) {
                    SendActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateLineDestination(json);
                            updateDistance(distance);
                            timeDistance = time / 60;
                            detail.setVisibility(View.VISIBLE);

                        }
                    });
                }else {
                    detail.setVisibility(View.GONE);
                }
            }
        }
    };

    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        ButterKnife.bind(this);

        detail.setVisibility(View.GONE);
        setPickUpContainer.setVisibility(View.GONE);
        setDestinationContainer.setVisibility(View.GONE);


        User userLogin = new User();
        if(MangJekApplication.getInstance(this).getLoginUser() != null){
            userLogin = MangJekApplication.getInstance(this).getLoginUser();
        }else{
            startActivity(new Intent(this, SplashActivity.class));
            finish();
        }
        saldoMpay = userLogin.getmPaySaldo();

        pickUpText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                setPickUpContainer.setVisibility((b) ? View.VISIBLE : View.GONE);
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

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mSend_mapView);
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

        realm = Realm.getDefaultInstance();

        Intent intent = getIntent();
        if(intent.hasExtra(FITUR_KEY)){
            fiturId = intent.getIntExtra(FITUR_KEY, -1);

            if (fiturId != -1)
                designedFitur = realm.where(Fitur.class).equalTo("idFitur", fiturId).findFirst();
        }else{
            designedFitur = realm.where(Fitur.class).equalTo("idFitur", 5).findFirst();
        }



        updateFitur();
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNextButtonClick();
            }
        });

        setupAutocompleteTextView();


    }

    private void setupAutocompleteTextView() {

        int layout = android.R.layout.simple_list_item_1;
        mAdapter = new PlaceApiAdapter(this, layout);
        pickUpText.setAdapter(mAdapter);

        pickUpText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager inputManager =
                        (InputMethodManager) SendActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
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
                        (InputMethodManager) SendActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(destinationText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                PlaceModel item = mAdapter.getPlaceObject(position);

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
                android.util.Log.e("LocationPickerActivity", "Error processing Places API URL", e);
                return null;
            } catch (IOException e) {
                android.util.Log.e("LocationPickerActivity", "Error connecting to Places API", e);
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


    private void getLocationFromPlaceId(String placeId, ResultCallback<PlaceBuffer> callback) {
        Places.GeoDataApi.getPlaceById(googleApiClient, placeId).setResultCallback(callback);
    }


    private LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
            return p1;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLastLocation(true);
            } else {

            }
        }
    }

    private void cancelOrder() {

    }


    private void updateFitur() {
        driverAvailable.clear();

        for (Marker m : driverMarkers) {
            m.remove();
        }
        driverMarkers.clear();

        if (isMapReady) updateLastLocation(false);
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
                        new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), 20f)
                );

                gMap.animateCamera(CameraUpdateFactory.zoomTo(20f));
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

//            if (designedFitur.getIdFitur() == 1) {
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

        }
    }

    private void fetchNearDriver() {
        if (lastKnownLocation != null) {
            User loginUser = MangJekApplication.getInstance(this).getLoginUser();

            BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
            GetNearRideCarRequestJson param = new GetNearRideCarRequestJson();
            param.setLatitude(lastKnownLocation.getLatitude());
            param.setLongitude(lastKnownLocation.getLongitude());

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
                driverMarkers.add(
                        gMap.addMarker(new MarkerOptions()
                                .position(currentDriverPos)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_m_ride_pin)))
                );

            }
        }
    }

    private void onNextButtonClick(){
        Intent intent = new Intent(this, AddDetailSendActivity.class);
        intent.putExtra("distance", jarak);//double
        intent.putExtra("price", harga);//long
        intent.putExtra("pickup_latlng", pickUpLatLang);
        intent.putExtra("destination_latlng", destinationLatLang);
        intent.putExtra("pickup", pickUpText.getText().toString());
        intent.putExtra("destination", destinationText.getText().toString());
        intent.putExtra("driver", driverAvailable);
        intent.putExtra("time_distance", timeDistance);
        intent.putExtra("driver", driverAvailable);
        intent.putExtra(FITUR_KEY, fiturId);

        startActivity(intent);
    }

    private void onDestinationClick() {
        if (destinationMarker != null) destinationMarker.remove();
        LatLng centerPos = gMap.getCameraPosition().target;
        destinationMarker = gMap.addMarker(new MarkerOptions()
                .position(centerPos)
                .title("Pengantaran")
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
                .title("Penjemputan")
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Geocoder geocoder = new Geocoder(SendActivity.this, Locale.getDefault());
                    final List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    SendActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!addresses.isEmpty()) {
                                if (addresses.size() > 0) {
                                    String address = addresses.get(0).getAddressLine(0);
                                    editText.setText(address);
                                }
                            } else {
                                editText.setText(R.string.text_addressNotAvailable);
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void updateLineDestination(String json) {
        Directions directions = new Directions(SendActivity.this);
        try {
            List<Route> routes = directions.parse(json);

            if (directionLine != null) directionLine.remove();
            if (routes.size() > 0) {
                directionLine = gMap.addPolyline((new PolylineOptions())
                        .addAll(routes.get(0).getOverviewPolyLine())
                        .color(ContextCompat.getColor(SendActivity.this, R.color.greyText))
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


        km = (float)round(km, 1);
        long biayaTotal = (long) (designedFitur.getBiaya() * Math.ceil(km));

        if (biayaTotal % 1 != 0) {
            biayaTotal += 1 - (biayaTotal % 1);
        }
        this.harga = biayaTotal;
        if(biayaTotal < designedFitur.getBiaya_minimum()){
            this.harga = designedFitur.getBiaya_minimum();
            biayaTotal = designedFitur.getBiaya_minimum();
        }

//        if(mPayButton.isChecked()){
//            biayaTotal /= 2;
//        }

        discountText.setText("Diskon "+designedFitur.getDiskon());

        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
        String formattedText = String.format(Locale.US, "$ %s.00", formattedTotal);
        priceText.setText(formattedText);


//        if(saldoMpay < (harga/2)){
//            mPayButton.setEnabled(false);
//            cashButton.toggle();
//        }else {
//            mPayButton.setEnabled(true);
//        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}

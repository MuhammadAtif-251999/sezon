package io.sezon.sezon.mMassage;

import android.Manifest;
import android.app.Activity;
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
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.sezon.sezon.MangJekApplication;
import io.sezon.sezon.R;
import io.sezon.sezon.api.ServiceGenerator;
import io.sezon.sezon.api.service.BookService;
import io.sezon.sezon.mMart.PlaceApiAdapter;
import io.sezon.sezon.mMart.PlaceAutocompleteAdapter;
import io.sezon.sezon.mMart.PlaceModel;
import io.sezon.sezon.model.Driver;
import io.sezon.sezon.model.User;
import io.sezon.sezon.model.json.book.GetNearRideCarRequestJson;
import io.sezon.sezon.model.json.book.GetNearRideCarResponseJson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static io.sezon.sezon.config.General.BOUNDS;

/**
 * Created by bradhawk on 12/4/2016.
 */

public class LocationPickerActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_PERMISSION_LOCATION = 991;

    public static final int LOCATION_PICKER_ID = 79;
    public static final String FORM_VIEW_INDICATOR = "FormToFill";

    public static final String LOCATION_NAME = "LocationName";
    public static final String LOCATION_LATLNG = "LocationLatLng";

    @BindView(R.id.locationPicker_autoCompleteText)
    AutoCompleteTextView autoCompleteTextView;
    @BindView(R.id.locationPicker_currentAddress)
    TextView currentAddress;
    @BindView(R.id.locationPicker_destinationButton)
    TextView selectLocation;

    private GoogleMap gMap;
    private GoogleApiClient googleApiClient;

    private Location lastKnownLocation;

    private PlaceApiAdapter mAdapter;

//    private static final LatLngBounds BOUNDS = new LatLngBounds(
//            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    private int formToFill;

    private User loginUser;
    private List<Driver> driversAvailable;
    private List<Marker> driverMarkers;
    private BookService service;

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_picker);
        ButterKnife.bind(this);

        driversAvailable = new ArrayList<>();
        driverMarkers = new ArrayList<>();

        setupGoogleApiClient();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.locationPicker_maps);
        mapFragment.getMapAsync(this);

        setupAutocompleteTextView();

        Intent intent = getIntent();
        formToFill = intent.getIntExtra(FORM_VIEW_INDICATOR, -1);

        selectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectLocation();
            }
        });

        Realm realm = Realm.getDefaultInstance();
        loginUser = realm.copyFromRealm(MangJekApplication.getInstance(this).getLoginUser());

        service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
    }

    private void selectLocation() {
        LatLng selectedLocation = gMap.getCameraPosition().target;
        String selectedAddress = currentAddress.getText().toString();

        Intent intent = new Intent();
        intent.putExtra(FORM_VIEW_INDICATOR, formToFill);
        intent.putExtra(LOCATION_NAME, selectedAddress);
        intent.putExtra(LOCATION_LATLNG, selectedLocation);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void setupGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .build();
        }
    }

    private void setupAutocompleteTextView() {

        int layout = android.R.layout.simple_list_item_1;
        mAdapter = new PlaceApiAdapter(this, layout);
        autoCompleteTextView.setAdapter(mAdapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                InputMethodManager inputManager =
                        (InputMethodManager) LocationPickerActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                PlaceModel item = mAdapter.getPlaceObject(position);

                try {
                    LatLng latLng = new GetLatLon().execute(item.place_name).get();
                    if (latLng != null) {
                        gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
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


    private void updateLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }

        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        gMap.setMyLocationEnabled(true);

        if (lastKnownLocation != null) {
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), 15f)
            );

            gMap.animateCamera(CameraUpdateFactory.zoomTo(15f));
            fetchDriverMassage();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.getUiSettings().setMyLocationButtonEnabled(true);
        updateLastLocation();
        setupMapOnCameraChange();
    }


    private void fetchDriverMassage() {
        GetNearRideCarRequestJson param = new GetNearRideCarRequestJson();
        param.setLatitude(lastKnownLocation.getLatitude());
        param.setLongitude(lastKnownLocation.getLongitude());
        service.getNearMassage(param).enqueue(new Callback<GetNearRideCarResponseJson>() {
            @Override
            public void onResponse(Call<GetNearRideCarResponseJson> call, Response<GetNearRideCarResponseJson> response) {
                if (response.isSuccessful()) {
                    driversAvailable = response.body().getData();
                    createMarker();
                }
            }

            @Override
            public void onFailure(Call<GetNearRideCarResponseJson> call, Throwable t) {

            }
        });
    }

    private void createMarker() {
        if (!driversAvailable.isEmpty()) {
            for (Marker m : driverMarkers) {
                m.remove();
            }
            driverMarkers.clear();

            for (Driver driver : driversAvailable) {
                LatLng currentDriverPos = new LatLng(driver.getLatitude(), driver.getLongitude());
                driverMarkers.add(
                        gMap.addMarker(new MarkerOptions()
                                .position(currentDriverPos)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_m_ride_pin)))
                );
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLastLocation();
            } else {
                // TODO: 10/15/2016 Tell user to use GPS
            }
        }
    }

    private void setupMapOnCameraChange() {
        gMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng center = gMap.getCameraPosition().target;
                fillAddress(currentAddress, center);
            }
        });
    }

    private void fillAddress(final TextView textView, final LatLng latLng) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Geocoder geocoder = new Geocoder(LocationPickerActivity.this, Locale.getDefault());
                    final List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    LocationPickerActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!addresses.isEmpty()) {
                                if (addresses.size() > 0) {
                                    String address = addresses.get(0).getAddressLine(0);
                                    textView.setText(address);
                                }
                            } else {
                                textView.setText(R.string.text_addressNotAvailable);
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        updateLastLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
}

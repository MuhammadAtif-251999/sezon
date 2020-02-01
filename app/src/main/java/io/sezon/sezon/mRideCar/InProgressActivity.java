package io.sezon.sezon.mRideCar;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
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
import com.makeramen.roundedimageview.RoundedImageView;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import io.sezon.sezon.MangJekApplication;
import io.sezon.sezon.R;
import io.sezon.sezon.api.FCMHelper;
import io.sezon.sezon.api.MapDirectionAPI;
import io.sezon.sezon.api.ServiceGenerator;
import io.sezon.sezon.api.service.BookService;
import io.sezon.sezon.api.service.UserService;
import io.sezon.sezon.gmap.directions.Directions;
import io.sezon.sezon.gmap.directions.Route;
import io.sezon.sezon.home.ChatActivity;
import io.sezon.sezon.home.MainActivity;
import io.sezon.sezon.model.Driver;
import io.sezon.sezon.model.Fitur;
import io.sezon.sezon.model.ItemHistory;
import io.sezon.sezon.model.LokasiDriver;
import io.sezon.sezon.model.Transaksi;
import io.sezon.sezon.model.User;
import io.sezon.sezon.model.json.book.LiatLokasiDriverResponse;
import io.sezon.sezon.model.json.fcm.CancelBookRequestJson;
import io.sezon.sezon.model.json.fcm.CancelBookResponseJson;
import io.sezon.sezon.model.json.fcm.DriverRequest;
import io.sezon.sezon.model.json.fcm.DriverResponse;
import io.sezon.sezon.model.json.fcm.FCMMessage;
import io.sezon.sezon.utils.Log;
import io.sezon.sezon.utils.db.DBHandler;
import io.sezon.sezon.utils.db.Queries;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static io.sezon.sezon.config.General.FCM_KEY;
import static io.sezon.sezon.model.FCMType.ORDER;
import static io.sezon.sezon.model.ResponseCode.ACCEPT;
import static io.sezon.sezon.model.ResponseCode.CANCEL;
import static io.sezon.sezon.model.ResponseCode.FINISH;
import static io.sezon.sezon.model.ResponseCode.REJECT;
import static io.sezon.sezon.model.ResponseCode.START;
import static io.sezon.sezon.service.MangJekMessagingService.BROADCAST_ORDER;


public class InProgressActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private static final String TAG = "InProgressActivity";
    @BindView(R.id.rideCar_pickUpText)
    EditText pickUpText;
    @BindView(R.id.rideCar_destinationText)
    EditText destinationText;
    @BindView(R.id.rideCar_distance)
    TextView distanceText;
    @BindView(R.id.rideCar_price)
    TextView priceText;
    @BindView(R.id.driver_image)
    RoundedImageView driverImage;
    @BindView(R.id.driver_image1)
    RoundedImageView driverImage1;
    @BindView(R.id.driver_name)
    TextView driverName;
    @BindView(R.id.driver_name1)
    TextView driverName1;
    @BindView(R.id.order_number)
    TextView orderNumber;
    @BindView(R.id.driver_police_number)
    TextView driverPoliceNumber;
    @BindView(R.id.driver_car)
    TextView driverCar;
    @BindView(R.id.driver_arriving_time)
    TextView driverArrivingTime;
    @BindView(R.id.chat_driver)
    ImageView chatDriver;
    @BindView(R.id.call_driver)
    ImageView callDriver;
    @BindView(R.id.cancelBook)
    CardView cancelBook;
    @BindView(R.id.rideCar_title)
    TextView title;
    @BindView(R.id.pop)
    Button popi;

    @BindView(R.id.detail_progres)
    RelativeLayout tampilDetal;
    @BindView(R.id.naik)
    View turunNaik;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.bac)
    ImageView bac;


    Dialog myDialog;
    boolean klik;
    private GoogleMap gMap;
    private boolean isMapReady = false;
    private Location lastKnownLocation;
    private static final int REQUEST_PERMISSION_LOCATION = 991;
    private static final int REQUEST_PERMISSION_CALL = 992;
    private GoogleApiClient googleApiClient;
    private LatLng pickUpLatLng;
    private LatLng destinationLatLng;
    private Polyline directionLine;
    private Marker pickUpMarker;
    private Marker destinationMarker;
    private Marker driverMarker;
    private Context context;
    Bundle orderBundle;
    private boolean isCancelable = true;
    Driver driver;
    DriverRequest request;
    ItemHistory transaction;
    User loginUser;
    InProgressActivity activity;
    private Fitur designedFitur;
    Driver trans;
    @BindView(R.id.checkBox)
    CheckBox cek;
    public boolean ordering = false;
    Realm realm;
    private long delayInMillis = 1000;
    private boolean isCompleted;
    private BookService bookService;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private Handler handler;
    private Runnable updateDriverRunnable = new Runnable() {
        @Override
        public void run() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Response<LiatLokasiDriverResponse> response = bookService.liatLokasiDriver(trans.getId()).execute();
                        if (!response.isSuccessful()) throw new Exception("Connection failed.");
                        if (response.body().getData().isEmpty())
                            throw new Exception("No Driver found.");
                        LokasiDriver lokasi = response.body().getData().get(0);
                        final LatLng latLng = new LatLng(lokasi.getLatitude(), lokasi.getLongitude());
                        Log.d(TAG, "Latitude = " + latLng.latitude + " Longitude = " + latLng.longitude);
                        Log.e("lokasi", "Latitude = " + latLng.latitude + " Longitude = " + latLng.longitude);
                        InProgressActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateDriverMarker(latLng);
                            }
                        });
                    } catch (Exception e) {
                        //   Log.d(TAG, "Error = " + e.getLocalizedMessage());
                        e.printStackTrace();
                    } finally {
                        handler.postDelayed(updateDriverRunnable, delayInMillis);
                    }
                }
            }).start();
        }
    };
    private okhttp3.Callback updateRouteCallback = new okhttp3.Callback() {
        @Override
        public void onFailure(okhttp3.Call call, IOException e) {

        }

        @Override
        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
            if (response.isSuccessful()) {
                final String json = response.body().string();
                final long distance = MapDirectionAPI.getDistance(InProgressActivity.this, json);
                if (distance >= 0) {
                    InProgressActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateLineDestination(json);

//                            updateDistance(distance);
                        }
                    });
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_progress);
        ButterKnife.bind(this);
        myDialog = new Dialog(this);
        activity = InProgressActivity.this;


        context = getApplicationContext();
        realm = Realm.getDefaultInstance();
        turunNaik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.naik:
                        if (klik == false) {
                            tampilDetal.setVisibility(View.VISIBLE);
                        } else {
                            tampilDetal.setVisibility(View.GONE);
                        }
                        klik = !klik;
                        break;
                    default:
                        break;
                }

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tampilDetal.setVisibility(View.GONE);

            }
        });
        bac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InProgressActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();

            }
        });

        //     cek.setChecked(true);
        if (cek.isChecked()) {

            final User user = MangJekApplication.getInstance(this).getLoginUser();
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            String text = "Driver di temukan";
            driver = (Driver) getIntent().getSerializableExtra("driver");

            request = (DriverRequest) getIntent().getSerializableExtra("request");
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getBaseContext());
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setContentText(driver.getNamaDepan() + " " + driver.getNamaBelakang() + "\n" + driver.getDriverJob());
            mBuilder.setSound(soundUri);
            mBuilder.setContentTitle("Sezon");
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());
            //      Intent intent = new Intent(context, HistoryDetailActivity.class);
            //    intent.putExtra("driver", driver.getId());
            stackBuilder.addParentStack(InProgressActivity.class);
            stackBuilder.addNextIntent(getIntent());
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.setAutoCancel(true);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Random rand = new Random();
            notificationManager.notify(rand.nextInt(130000), mBuilder.build());
        }

        popi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowPopup(view);
            }
        });
//        readTransaction();

        loginUser = MangJekApplication.getInstance(InProgressActivity.this).getLoginUser();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.rideCar_mapView);
        mapFragment.getMapAsync(this);
        trans = (Driver) getIntent().getSerializableExtra("driver");
        driver = (Driver) getIntent().getSerializableExtra("driver");
        Log.e("transiksi", trans.getId() + "");
//        param = (RequestFoodRequestJson) intent.getSerializableExtra(REQUEST_PARAM);

        request = (DriverRequest) getIntent().getSerializableExtra("request");

        switch (request.getOrderFitur()) {
            case "1": {
                title.setText("Ride");
                title.setTextColor(Color.parseColor("#ffffff"));
                pickUpText.setText(request.getAlamatTujuan());
                destinationText.setText(request.getAlamatAsal());
                break;
            }
            case "3": {
                title.setText("Food");
                title.setTextColor(Color.parseColor("#ffffff"));
                pickUpText.setText(request.getAlamatTujuan());
                destinationText.setText(request.getAlamatAsal());
                break;
            }
            case "9": {
                title.setText("Jo Lapak");
                title.setTextColor(Color.parseColor("#ffffff"));
                pickUpText.setText(request.getAlamatTujuan());
                destinationText.setText(request.getAlamatAsal());
                break;

            }
            case "10": {
                title.setText("Grocery");
                title.setTextColor(Color.parseColor("#ffffff"));
                pickUpText.setText(request.getAlamatAsal());
                destinationText.setText(request.getAlamatTujuan());
                break;

            }

            default: {
                pickUpText.setText(request.getAlamatAsal());
                destinationText.setText(request.getAlamatTujuan());
                title.setTextColor(Color.parseColor("#ffffff"));


            }
        }


        String format = String.format(Locale.US, "(%.1f Km)", request.getJarak());
        distanceText.setText(format);

        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(request.getHarga());
        String formattedText = String.format(Locale.US, "$ %s.00", formattedTotal);
        priceText.setText(formattedText);

        Glide.with(getApplicationContext())
                .load(driver.getFoto())
                .into(driverImage);
        Glide.with(getApplicationContext()).load(driver.getFoto()).into(driverImage1);
        driverName.setText(driver.getNamaDepan() + " " + driver.getNamaBelakang());
        driverName1.setText(driver.getNamaDepan() + " " + driver.getNamaBelakang());
        orderNumber.setText("Order ID. " + request.getIdTransaksi());
        driverPoliceNumber.setText(driver.getNomor_kendaraan());
        driverCar.setText("-" + driver.getTipe());
        driverArrivingTime.setText("Waktu Perjalanan " + getIntent().getLongExtra("time_distance", 0) + " min");
        Log.d("TRIP DURATION : ", "" + getIntent().getLongExtra("time_distance", 0) + " min");
        Log.e("TRIP DURATION : ", "" + getIntent().getLongExtra("time_distance", 0) + " min");

        pickUpLatLng = new LatLng(request.getStartLatitude(), request.getStartLongitude());
        destinationLatLng = new LatLng(request.getEndLatitude(), request.getEndLongitude());

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


        chatDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "Chat with driver", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("reg_id", driver.getRegId());
                startActivity(intent);
            }
        });

        callDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InProgressActivity.this);
                alertDialogBuilder.setTitle("Call Driver!");
                alertDialogBuilder.setMessage("Do you want to contact the driver" + driver.getNoTelepon() + "?");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                if (ActivityCompat.checkSelfPermission(InProgressActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(InProgressActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PERMISSION_CALL);
                                    return;
                                }

                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:" + driver.getNoTelepon()));
                                startActivity(callIntent);
                            }
                        });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


            }
        });

        cancelBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCancelable) {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InProgressActivity.this);
                    alertDialogBuilder.setTitle("Cancel Order");
                    alertDialogBuilder.setMessage("Do you want to cancel the order?");
                    alertDialogBuilder.setPositiveButton("yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    cancelOrder();
                                }
                            });

                    alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    Toast.makeText(getApplicationContext(), "You can not cancel the order, the journey has already begun!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        bookService = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
        handler = new Handler();
    }

    public void ShowPopup(View v) {
        TextView txtclose;
        Button btnFollow;
        myDialog.setContentView(R.layout.custompopup);
        txtclose = (TextView) myDialog.findViewById(R.id.txtclose);
        txtclose.setText("M");
        btnFollow = (Button) myDialog.findViewById(R.id.btnfollow);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void cancelOrder() {
        User loginUser = MangJekApplication.getInstance(InProgressActivity.this).getLoginUser();
        CancelBookRequestJson request = new CancelBookRequestJson();
        request.id = loginUser.getId();
        request.id_transaksi = this.request.getIdTransaksi();
        request.id_driver = driver.getId();

        UserService service = ServiceGenerator.createService(UserService.class, loginUser.getEmail(), loginUser.getPassword());
        service.cancelOrder(request).enqueue(new Callback<CancelBookResponseJson>() {
            @Override
            public void onResponse(Call<CancelBookResponseJson> call, Response<CancelBookResponseJson> response) {
                if (response.isSuccessful()) {
                    if (response.body().mesage.equals("order canceled")) {
                        Toast.makeText(InProgressActivity.this, "Order Canceled!", Toast.LENGTH_SHORT).show();
                        new Queries(new DBHandler(getApplicationContext())).truncate(DBHandler.TABLE_CHAT);
                        startActivity(new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    } else {
                        Toast.makeText(InProgressActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CancelBookResponseJson> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(InProgressActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

        DriverResponse response = new DriverResponse();
        response.type = ORDER;
        response.setIdTransaksi(this.request.getIdTransaksi());
        response.setResponse(DriverResponse.REJECT);

        FCMMessage message = new FCMMessage();
        message.setTo(driver.getRegId());
        message.setData(response);


        FCMHelper.sendMessage(FCM_KEY, message).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                Log.e("CANCEL REQUEST", "sent");
            }

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                Log.e("CANCEL REQUEST", "failed");
            }
        });
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

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
        requestRoute();

        if (pickUpMarker != null) pickUpMarker.remove();
        pickUpMarker = gMap.addMarker(new MarkerOptions()
                .position(pickUpLatLng)
                .title("Pick Up")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_loc_jemput)));


        if (destinationMarker != null) destinationMarker.remove();
        destinationMarker = gMap.addMarker(new MarkerOptions()
                .position(destinationLatLng)
                .title("Destination")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_loc_antar)));

        if (!isCompleted) startDriverLocationUpdate();
    }


    private void updateLastLocation(boolean move) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }
        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        gMap.setMyLocationEnabled(true);

        if (pickUpLatLng != null) {
            if (move) {
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        pickUpLatLng, 15f)
                );

                gMap.animateCamera(CameraUpdateFactory.zoomTo(15f));
            }
//            fetchNearDriver();
        }
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

        if (requestCode == REQUEST_PERMISSION_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(context, "Izin panggilan diberikan", Toast.LENGTH_SHORT).show();
            } else {
                // Toast.makeText(context, "Izin panggilan dibatasi", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestRoute() {
        if (pickUpLatLng != null && destinationLatLng != null) {
            MapDirectionAPI.getDirection(pickUpLatLng, destinationLatLng).enqueue(updateRouteCallback);
        }
    }

    private void updateLineDestination(String json) {
        Directions directions = new Directions(InProgressActivity.this);
        try {
            List<Route> routes = directions.parse(json);

            if (directionLine != null) directionLine.remove();
            if (routes.size() > 0) {
                directionLine = gMap.addPolyline((new PolylineOptions())
                        .addAll(routes.get(0).getOverviewPolyLine())
                        .color(ContextCompat.getColor(InProgressActivity.this, R.color.greyText))
                        .width(15));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(BROADCAST_ORDER));
    }

    private void startDriverLocationUpdate() {
        handler.postDelayed(updateDriverRunnable, 0);
    }

    private void stopDriverLocationUpdate() {
        handler.removeCallbacks(updateDriverRunnable);
    }

    private void updateDriverMarker(LatLng latLng) {
        String fitur = request.getOrderFitur();
        int iconRes = R.drawable.ic_m_ride_pin;
        if (fitur.equalsIgnoreCase("Ojek") || fitur.equalsIgnoreCase("Box")
                || fitur.equalsIgnoreCase("Sembako")
                || fitur.equalsIgnoreCase("Food")) {
            iconRes = R.drawable.ic_m_ride_pin;
            Log.e("lokasi tidak ditemkan", "saas");
        } else if (fitur.equalsIgnoreCase("Taxi")) {
            iconRes = R.drawable.ic_m_car_pin;
            Log.e("lokasi tidak ditemkan", "saas");
        } else if (fitur.equalsIgnoreCase("Pijat")) {
            iconRes = R.drawable.ic_massage_pin;
            Log.e("lokasi tidak ditemkan", "saas");
        } else if (fitur.equalsIgnoreCase("Cargo")) {
            iconRes = R.drawable.ic_mbox_pin;
        } else if (fitur.equalsIgnoreCase("Servis")) {
            iconRes = R.drawable.ic_service_pin;
        }
        if (driverMarker != null) driverMarker.remove();
        driverMarker = gMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(driver.getNamaDepan())
                .icon(BitmapDescriptorFactory.fromResource(iconRes)));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            orderBundle = intent.getExtras();
            orderHandler(orderBundle.getInt("code"));
        }
    };

    private void orderHandler(int code) {
        Log.e("orderHandler", "Code : " + code);
        switch (code) {
            case REJECT:
                Log.e("DRIVER RESPONSE", "reject");
                isCancelable = false;
                break;
            case ACCEPT:
                Log.e("DRIVER RESPONSE", "diterima");

                cek.setChecked(true);
                Toast.makeText(getApplicationContext(), "Driver ditemukan", Toast.LENGTH_SHORT).show();
                break;
            case CANCEL:
                Log.e("DRIVER RESPONSE", "batal");
                finish();
                break;

            case START:
                Log.e("inrpgres", "Perjalanan mulai");


                isCancelable = false;
                Toast.makeText(getApplicationContext(), "You Trip Started", Toast.LENGTH_SHORT).show();
                break;
            case FINISH:
                Log.e("inprogres", "Perjalanan selesai");
                isCancelable = false;
//                new Queries(new DBHandler(getApplicationContext())).truncate(DBHandler.TABLE_CHAT);

                Toast.makeText(getApplicationContext(), "You Trip Finish", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), RateDriverActivity.class);
                intent.putExtra("id_transaksi", request.getIdTransaksi());
                intent.putExtra("id_pelanggan", loginUser.getId());
                intent.putExtra("driver_photo", driver.getFoto());
                intent.putExtra("id_driver", driver.getId());
                intent.putExtra("harga", request.getHarga()+"");
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    intent.putExtra("waktu_selesai", simpleDateFormat.format(request.getWaktuSelesai()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startActivity(intent);
                finish();
                break;
        }
    }


    @SuppressWarnings("unused")
    @Subscribe
    public void onMessageEvent(final DriverResponse response) {
        Log.e("IN PROGRESS", response.getResponse() + " " + response.getId() + " " + response.getIdTransaksi());

    }

    private void readTransaction() {
        RealmResults<Transaksi> results = realm.where(Transaksi.class).findAll();

        Log.e("Semua Transaksi", results.toString());
        Log.e("TRANSACTION SIZE", results.size() + "");
        for (int i = 0; i < results.size(); i++) {
            Log.e("TRANSACTION ID", results.get(i).getId() + "");
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (klik = true) {
            tampilDetal.setVisibility(View.GONE);
        }
        startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
        if (!isCompleted) stopDriverLocationUpdate();
    }

    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}

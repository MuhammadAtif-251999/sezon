package io.sezon.sezon.mRideCar;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import io.sezon.sezon.MangJekApplication;
import io.sezon.sezon.R;
import io.sezon.sezon.api.FCMHelper;
import io.sezon.sezon.api.ServiceGenerator;
import io.sezon.sezon.api.service.BookService;
import io.sezon.sezon.api.service.UserService;
import io.sezon.sezon.model.Driver;
import io.sezon.sezon.model.Transaksi;
import io.sezon.sezon.model.User;
import io.sezon.sezon.model.json.book.CheckStatusTransaksiRequest;
import io.sezon.sezon.model.json.book.CheckStatusTransaksiResponse;
import io.sezon.sezon.model.json.book.RequestRideCarRequestJson;
import io.sezon.sezon.model.json.book.RequestRideCarResponseJson;
import io.sezon.sezon.model.json.fcm.CancelBookRequestJson;
import io.sezon.sezon.model.json.fcm.CancelBookResponseJson;
import io.sezon.sezon.model.json.fcm.DriverRequest;
import io.sezon.sezon.model.json.fcm.DriverResponse;
import io.sezon.sezon.model.json.fcm.FCMMessage;
import io.sezon.sezon.utils.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static io.sezon.sezon.config.General.FCM_KEY;
import static io.sezon.sezon.model.FCMType.ORDER;
import static io.sezon.sezon.model.json.fcm.DriverResponse.REJECT;

/**
 * Created by bradhawk on 10/17/2016.
 */

public class WaitingActivity extends AppCompatActivity {

    public static final String REQUEST_PARAM = "RequestParam";
    public static final String DRIVER_LIST = "DriverList";

    private List<Driver> driverList;
    private RequestRideCarRequestJson param;

    private DriverRequest request;
    private DriverResponse respon;

    private int currentLoop;

    AppCompatActivity activity;

    private Driver driver;

    private double timeDistance;
    Transaksi transaksi;
    Thread thread;
    boolean threadRun = true;

    @BindView(R.id.waiting_cancel)
    Button cancelButton;

    @BindView(R.id.waiting_logo)
    ImageView gifku;

    @BindView(R.id.checkBox)
    CheckBox cek;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);
        ButterKnife.bind(this);

        activity = this;

        param = (RequestRideCarRequestJson) getIntent().getSerializableExtra(REQUEST_PARAM);
        driverList = (List<Driver>) getIntent().getSerializableExtra(DRIVER_LIST);

        timeDistance = getIntent().getDoubleExtra("time_distance", 0);
        currentLoop = 0;

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(request != null){
                    cancelOrder();
                }


            }
        });
        if(cek.isChecked()){
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            String text = "Driver di temukan";
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getBaseContext());
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setContentText(text);
            mBuilder.setSound(soundUri);
            mBuilder.setContentTitle("Example");
            Intent resultIntent = new Intent(activity, InProgressActivity.class);
            resultIntent.putExtra("request", request);
            resultIntent.putExtra("time_distance", timeDistance);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());
            stackBuilder.addParentStack(InProgressActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.setAutoCancel(true);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Random rand = new Random();
            notificationManager.notify(rand.nextInt(130000),mBuilder.build());
        }



        Glide.with(WaitingActivity.this)
                // LOAD URL DARI LOKAL DRAWABLE
                .load(R.drawable.sa)
                .asGif()
                //PENGATURAN CACHE
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(gifku);

        sendRequestTransaksi();

    }

    private void sendRequestTransaksi() {
        User loginUser = MangJekApplication.getInstance(this).getLoginUser();
        final BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());

        service.requestTransaksi(param).enqueue(new Callback<RequestRideCarResponseJson>() {
            @Override
            public void onResponse(Call<RequestRideCarResponseJson> call, Response<RequestRideCarResponseJson> response) {
                if (response.isSuccessful()) {
                    buildDriverRequest(response.body());
//                    fcmBroadcast(currentLoop);



                    thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i<driverList.size();i++){
//                                if(threadRun){
                                fcmBroadcast(i);
//                                }

                            }

                            try {
                                Thread.sleep(20000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            if(threadRun){
                                CheckStatusTransaksiRequest param = new CheckStatusTransaksiRequest();
                                param.setIdTransaksi(transaksi.getId());
                                service.checkStatusTransaksi(param).enqueue(new Callback<CheckStatusTransaksiResponse>() {
                                    @Override
                                    public void onResponse(Call<CheckStatusTransaksiResponse> call, Response<CheckStatusTransaksiResponse> response) {
                                        if(response.isSuccessful()) {
                                            CheckStatusTransaksiResponse checkStatus = response.body();
                                            if(checkStatus.isStatus()) {
                                                cek.setChecked(true);
                                                Log.e("diterima","sss");

                                                Log.e("diterimawaotong",DriverResponse.ACCEPT+"");
                                                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                                String text = "Driver found";
                                                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getBaseContext());
                                                mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                                                mBuilder.setContentText(text);
                                                mBuilder.setSound(soundUri);
                                                mBuilder.setContentTitle("Go-Pickme");
                                                Intent resultIntent = new Intent(activity, InProgressActivity.class);
                                                resultIntent.putExtra("driver", checkStatus.getListDriver().get(0));
                                                resultIntent.putExtra("request", request);
                                                resultIntent.putExtra("time_distance", timeDistance);
                                                TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());
                                                stackBuilder.addParentStack(InProgressActivity.class);
                                                stackBuilder.addNextIntent(resultIntent);
                                                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
                                                mBuilder.setContentIntent(resultPendingIntent);
                                                mBuilder.setAutoCancel(true);
                                                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                                Random rand = new Random();
                                                notificationManager.notify(rand.nextInt(1000),mBuilder.build());


                                                Intent intent = new Intent(activity, InProgressActivity.class);
                                                intent.putExtra("driver", checkStatus.getListDriver().get(0));
                                                intent.putExtra("request", request);
                                                intent.putExtra("time_distance", timeDistance);
                                                startActivity(intent);
                                            } else {
                                                Log.e("DRIVER STATUS", "Driver tidak ada!");
                                                activity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(WaitingActivity.this, "Your drivers look busy! Please try again.", Toast.LENGTH_LONG).show();
                                                    }
                                                });

                                                finish();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<CheckStatusTransaksiResponse> call, Throwable t) {
                                        Log.e("DRIVER STATUS", "Driver tidak ada!");
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(WaitingActivity.this, "Your drivers look busy! Please try again.", Toast.LENGTH_LONG).show();
                                            }
                                        });

                                        finish();

                                    }
                                });
                            }

                        }
                    });
                    thread.start();



                }
            }

            @Override
            public void onFailure(Call<RequestRideCarResponseJson> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(WaitingActivity.this, "You have been banned!\n please contact our customer service.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }


    private void cancelOrder(){
        User loginUser = MangJekApplication.getInstance(WaitingActivity.this).getLoginUser();
        CancelBookRequestJson request = new CancelBookRequestJson();

        request.id = loginUser.getId();
        request.id_transaksi = this.request.getIdTransaksi();
        request.id_driver = "D0";

        Log.d("id_transaksi_cancel", this.request.getIdTransaksi());
        UserService service = ServiceGenerator.createService(UserService.class, loginUser.getEmail(), loginUser.getPassword());
        service.cancelOrder(request).enqueue(new Callback<CancelBookResponseJson>() {
            @Override
            public void onResponse(Call<CancelBookResponseJson> call, Response<CancelBookResponseJson> response) {
                if (response.isSuccessful()) {
                    if (response.body().mesage.equals("order canceled")) {
                        Toast.makeText(WaitingActivity.this, "Order Canceled!", Toast.LENGTH_SHORT).show();
                        threadRun = false;
                        finish();
                    } else {
                        Toast.makeText(WaitingActivity.this, "Gagal!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CancelBookResponseJson> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(WaitingActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

        DriverResponse response = new DriverResponse();
        response.type = ORDER;
        response.setIdTransaksi(this.request.getIdTransaksi());
        response.setResponse(REJECT);

        FCMMessage message = new FCMMessage();
        message.setTo(driverList.get(currentLoop-1).getRegId());
        message.setData(response);


        FCMHelper.sendMessage(FCM_KEY, message).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                Log.e("CANCEL REQUEST", "sent");
                threadRun = false;

            }

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                Log.e("CANCEL REQUEST", "failed");
            }
        });
    }

    private void buildDriverRequest(RequestRideCarResponseJson response) {
        transaksi = response.getData().get(0);
        User loginUser = MangJekApplication.getInstance(this).getLoginUser();
        if (request == null) {
            request = new DriverRequest();
            request.setIdTransaksi(transaksi.getId());
            request.setIdPelanggan(transaksi.getIdPelanggan());
            request.setRegIdPelanggan(loginUser.getRegId());
            request.setOrderFitur(transaksi.getOrderFitur());
            request.setStartLatitude(transaksi.getStartLatitude());
            request.setStartLongitude(transaksi.getStartLongitude());
            request.setEndLatitude(transaksi.getEndLatitude());
            request.setEndLongitude(transaksi.getEndLongitude());
            request.setJarak(transaksi.getJarak());
            request.setHarga(transaksi.getHarga());
            request.setWaktuOrder(transaksi.getWaktuOrder());
            request.setAlamatAsal(transaksi.getAlamatAsal());
            request.setAlamatTujuan(transaksi.getAlamatTujuan());
            request.setKodePromo(transaksi.getKodePromo());
            request.setKreditPromo(transaksi.getKreditPromo());
            request.setPakaiMPay(transaksi.isPakaiMpay());


            String namaLengkap = String.format("%s %s", loginUser.getNamaDepan(), loginUser.getNamaBelakang());
            request.setNamaPelanggan(namaLengkap);
            request.setTelepon(loginUser.getNoTelepon());
            request.setType(ORDER);



        }
    }

    private void fcmBroadcast(int index) {
        Driver driverToSend = driverList.get(index);
        currentLoop++;
        request.setTime_accept(new Date().getTime()+"");
        FCMMessage message = new FCMMessage();
        message.setTo(driverToSend.getRegId());
        message.setData(request);

//        Log.e("REQUEST TO DRIVER", message.getData().toString());
        driver = driverToSend;

        FCMHelper.sendMessage(FCM_KEY, message).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

            }

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onBackPressed() {

    }


    @SuppressWarnings("unused")
    @Subscribe
    public void onMessageEvent(final DriverResponse response) {
        Log.e("DRIVER RESPONSE (W)", response.getResponse()+" "+response.getId()+" "+response.getIdTransaksi());
//        if (currentLoop < driverList.size()) {
        if (response.getResponse().equalsIgnoreCase(DriverResponse.ACCEPT)) {
            Log.d("DRIVER RESPONSE", "Terima");
//            cek.setChecked(true);

            activity.runOnUiThread(new Runnable() {


                public void run() {
                    threadRun = false;
                    for(Driver cDriver : driverList){
                        if(cDriver.getId().equals(response.getId())){
                            driver = cDriver;
                        }
                    }
                    Log.e("diterimawaotong",DriverResponse.ACCEPT+"");
                    Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    String text = "Driver found";
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getBaseContext());
                    mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                    mBuilder.setContentText(text);
                    mBuilder.setSound(soundUri);
                    mBuilder.setContentTitle("Go-Pickme");
                    Intent resultIntent = new Intent(activity, InProgressActivity.class);
                    resultIntent.putExtra("driver", driver);
                    resultIntent.putExtra("request", request);
                    resultIntent.putExtra("time_distance", timeDistance);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());
                    stackBuilder.addParentStack(InProgressActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);
                    mBuilder.setAutoCancel(true);
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    Random rand = new Random();
                    notificationManager.notify(rand.nextInt(1000),mBuilder.build());
                   Intent intent = new Intent(activity, InProgressActivity.class);
                   intent.putExtra("driver", driver);
                   intent.putExtra("request", request);
                   intent.putExtra("time_distance", timeDistance);
                    startActivity(intent);
                    finish();
                }
            });
        }

    }

    private void saveTransaction(Transaksi transaksi) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insert(transaksi);
        realm.commitTransaction();
    }

    private void saveDriver(Driver driver) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(Driver.class);
        realm.insert(driver);
        realm.commitTransaction();
    }


}

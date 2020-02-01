package io.sezon.sezon.mMart;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import io.sezon.sezon.MangJekApplication;
import io.sezon.sezon.R;
import io.sezon.sezon.api.FCMHelper;
import io.sezon.sezon.api.ServiceGenerator;
import io.sezon.sezon.api.service.BookService;
import io.sezon.sezon.api.service.UserService;
import io.sezon.sezon.mRideCar.InProgressActivity;
import io.sezon.sezon.model.Driver;
import io.sezon.sezon.model.Transaksi;
import io.sezon.sezon.model.User;
import io.sezon.sezon.model.json.book.CheckStatusTransaksiRequest;
import io.sezon.sezon.model.json.book.CheckStatusTransaksiResponse;
import io.sezon.sezon.model.json.book.RequestMartRequestJson;
import io.sezon.sezon.model.json.book.RequestMartResponseJson;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static io.sezon.sezon.config.General.FCM_KEY;
import static io.sezon.sezon.model.FCMType.ORDER;
import static io.sezon.sezon.model.json.fcm.DriverResponse.REJECT;

/**
 * Created by bradhawk on 10/17/2016.
 */

public class MartWaitingActivity extends AppCompatActivity {

    public static final String REQUEST_PARAM = "RequestParam";
    public static final String DRIVER_LIST = "DriverList";

    private List<Driver> driverList;
    private RequestMartRequestJson param;

    private DriverRequest request;
    private int currentLoop;

    private Thread driverRequestThread;

    private RequestMartResponseJson responseJson;
    private User loginUser;
    AppCompatActivity activity;
    boolean threadRun = true;
    Thread thread;
    private Driver driver;
    private double timeDistance;
    Transaksi transaksi;

    @BindView(R.id.waiting_cancel)
    Button cancelButton;

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

        param = (RequestMartRequestJson) getIntent().getSerializableExtra(REQUEST_PARAM);
        driverList = (List<Driver>) getIntent().getSerializableExtra(DRIVER_LIST);

        currentLoop = 0;
        timeDistance = getIntent().getDoubleExtra("time_distance", 0);

        activity = this;
        request = new DriverRequest();

        loginUser = MangJekApplication.getInstance(this).getLoginUser();

        sendRequestTransaksi();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(request != null){
                    cancelOrder();
                }
            }
        });
    }

    private void sendRequestTransaksi() {
        User loginUser = MangJekApplication.getInstance(this).getLoginUser();
        final BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());

        service.requestTransaksiMMart(param).enqueue(new Callback<RequestMartResponseJson>() {
            @Override
            public void onResponse(Call<RequestMartResponseJson> call, Response<RequestMartResponseJson> response) {
                if (response.isSuccessful()) {
//                    Toast.makeText(MartWaitingActivity.this, "Berhasil kirim data ke server", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(MartWaitingActivity.this, "RESPONSE : "+response.body().toString(), Toast.LENGTH_SHORT).show();
//                    Log.e("RESPONSE",response.body().toString() );
//                    responseJson = response.body();
//                    request = buildDriverRequest(response.body());
                    buildDriverRequest(response.body());
//                    driverRequestThread = new Thread(new Broadcaster());
//                    driverRequestThread.start();
                    thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < driverList.size(); i++) {
                                if (threadRun) {
                                    fcmBroadcast(currentLoop);
                                }
                            }
                            try {
                                Thread.sleep(30000);
//                                    if(!threadRun){
//                                        thread.stop();
//                                    }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (threadRun) {
                                CheckStatusTransaksiRequest param = new CheckStatusTransaksiRequest();
                                param.setIdTransaksi(transaksi.getId());
                                service.checkStatusTransaksi(param).enqueue(new Callback<CheckStatusTransaksiResponse>() {
                                    @Override
                                    public void onResponse(Call<CheckStatusTransaksiResponse> call, Response<CheckStatusTransaksiResponse> response) {
                                        if(response.isSuccessful()) {
                                            CheckStatusTransaksiResponse checkStatus = response.body();
                                            if(checkStatus.isStatus()) {
                                                Intent intent = new Intent(activity, InProgressActivity.class);
                                                intent.putExtra("driver", checkStatus.getListDriver().get(0));
                                                intent.putExtra("request", request);
                                                intent.putExtra("time_distance", timeDistance);
                                                startActivity(intent);
                                            } else {
                                                Log.e("DRIVER STATUS", "Driver tidak ditemukan!");
                                                activity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(MartWaitingActivity.this, "Driver Anda tampak sibuk! Silahkan coba lagi.", Toast.LENGTH_LONG).show();
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
                                                Toast.makeText(MartWaitingActivity.this, "Driver Anda tampak sibuk! Silahkan coba lagi.", Toast.LENGTH_LONG).show();
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
            public void onFailure(Call<RequestMartResponseJson> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

//    private MartDriverRequest buildDriverRequest(RequestMartResponseJson response) {
//        TransaksiMart transaksi = response.getData();
//        MartDriverRequest request = new MartDriverRequest();
//        request.setIdTransaksi(transaksi.getIdTransaksi());
//        request.setKodePromo(transaksi.getKodePromo());
//        request.setKreditPromo(transaksi.getKreditPromo());
//        request.setPakaiMpay(transaksi.isPakaiMpay());
//
//        request.setOrderFitur("4");
//
//        String namaLengkap = String.format("%s %s", loginUser.getNamaDepan(), loginUser.getNamaBelakang());
//        request.setNamaPelanggan(namaLengkap);
//        request.setTelepon(loginUser.getNoTelepon());
//
//        request.setType(1);
//        request.setRegIdPelanggan(loginUser.getRegId());
//        request.setTimeAccept(new Date().getTime());
//        return request;
//    }

    private void buildDriverRequest(RequestMartResponseJson response) {
//        transaksi = response.getData();
        transaksi = response.data.get(0);
        User loginUser = MangJekApplication.getInstance(this).getLoginUser();
        request.setIdTransaksi(transaksi.getId());
        request.setKodePromo(transaksi.getKodePromo());
        request.setKreditPromo(transaksi.getKreditPromo()+"");
        request.setPakaiMPay(transaksi.isPakaiMpay());

        request.setOrderFitur("4");

        String namaLengkap = String.format("%s %s", loginUser.getNamaDepan(), loginUser.getNamaBelakang());
        request.setNamaPelanggan(namaLengkap);
        request.setTelepon(loginUser.getNoTelepon());

        request.setType(1);
        request.setRegIdPelanggan(loginUser.getRegId());
        request.setTime_accept(new Date().getTime()+"");

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

        request.setType(ORDER);



    }


    private void fcmBroadcast(int index) {
        Driver driverToSend = driverList.get(index);
        currentLoop++;
        FCMMessage message = new FCMMessage();
        message.setTo(driverToSend.getRegId());
        message.setData(request);

        driver = driverToSend;

        FCMHelper.sendMessage(FCM_KEY, message)
                .enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(okhttp3.Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                        MartWaitingActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                Toast.makeText(MartWaitingActivity.this, "Berhasil kirim data ke driver", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }

    @Override
    public void onBackPressed() {
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onMessageEvent(final DriverResponse response) {
        Log.e("MartWaiting", "Anda mendapat siaran "+response.getResponse());
//        if (currentLoop < driverList.size()) {
        if (response.getResponse().equalsIgnoreCase(DriverResponse.ACCEPT)) {
            Log.d("DRIVER RESPONSE", "Terima");
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    threadRun = false;

                    for(Driver cDriver : driverList){
                        if(cDriver.getId().equals(response.getId())){
                            driver = cDriver;
                        }
                    }
//                        saveTransaction(transaksi);
//                        saveDriver(driver);
//                        Toast.makeText(getApplicationContext(), "Transaksi " + response.getIdTransaksi() + " ada yang mau!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(activity, InProgressActivity.class);
                    intent.putExtra("driver", driver);
                    intent.putExtra("request", request);
                    intent.putExtra("time_distance", timeDistance);
                    startActivity(intent);
                    finish();
                }
            });
        }
//        else {
//            Log.d("DRIVER RESPONSE", "Tolak");
//            if(currentLoop == (driverList.size()-1)){
//                Intent intent = new Intent(activity, InProgressActivity.class);
//                intent.putExtra("driver", driver);
//                intent.putExtra("request", request);
//                intent.putExtra("time_distance", timeDistance);
//                threadRun = false;
//                startActivity(intent);
//                finish();
//            }else{
////                    fcmBroadcast(++currentLoop);
//                currentLoop++;
//            }
//
//        }

//            if (response.getResponse().equalsIgnoreCase(DriverResponse.ACCEPT)) {
//                Toast.makeText(this, "Transaksi " + response.getIdTransaksi() + " ada yang mau!", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "Transaksi " + response.getIdTransaksi() + " respon tidak dikenal!", Toast.LENGTH_SHORT).show();
//            }
//        }
    }

//    private void onNoDriverAccepting() {
//
//    }

    private void cancelOrder(){
        User loginUser = MangJekApplication.getInstance(MartWaitingActivity.this).getLoginUser();
        CancelBookRequestJson request = new CancelBookRequestJson();

        request.id = loginUser.getId();
        request.id_transaksi = this.request.getIdTransaksi();
        request.id_driver = "D0";

        UserService service = ServiceGenerator.createService(UserService.class, loginUser.getEmail(), loginUser.getPassword());
        service.cancelOrder(request).enqueue(new Callback<CancelBookResponseJson>() {
            @Override
            public void onResponse(Call<CancelBookResponseJson> call, Response<CancelBookResponseJson> response) {
                if (response.isSuccessful()) {
                    if (response.body().mesage.equals("order canceled")) {
                        Toast.makeText(MartWaitingActivity.this, "Pesanan Dibatalkan!", Toast.LENGTH_SHORT).show();
                        threadRun = false;
                        finish();
                    } else {
                        Toast.makeText(MartWaitingActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CancelBookResponseJson> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(MartWaitingActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
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

//    private class Broadcaster implements Runnable {
//
//        @Override
//        public void run() {
//            for (int i = 0; i < driverList.size(); i++) {
//                try {
//                    fcmBroadcast(i);
//                    Thread.sleep(20000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            onNoDriverAccepting();
//        }
//    }
}

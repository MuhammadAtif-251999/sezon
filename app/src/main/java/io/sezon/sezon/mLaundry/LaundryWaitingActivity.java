package io.sezon.sezon.mLaundry;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import io.sezon.sezon.MangJekApplication;
import io.sezon.sezon.R;
import io.sezon.sezon.api.FCMHelper;
import io.sezon.sezon.api.ServiceGenerator;
import io.sezon.sezon.api.service.BookService;
import io.sezon.sezon.api.service.UserService;
import io.sezon.sezon.home.MainActivity;
import io.sezon.sezon.mRideCar.InProgressActivity;
import io.sezon.sezon.model.Driver;
import io.sezon.sezon.model.TransaksiLaundry;
import io.sezon.sezon.model.User;
import io.sezon.sezon.model.json.book.CheckStatusTransaksiRequest;
import io.sezon.sezon.model.json.book.CheckStatusTransaksiResponse;
import io.sezon.sezon.model.json.book.GetNearRideCarRequestJson;
import io.sezon.sezon.model.json.book.GetNearRideCarResponseJson;
import io.sezon.sezon.model.json.book.RequestLaundryRequestJson;
import io.sezon.sezon.model.json.book.RequestLaundryResponseJson;
import io.sezon.sezon.model.json.fcm.CancelBookRequestJson;
import io.sezon.sezon.model.json.fcm.CancelBookResponseJson;
import io.sezon.sezon.model.json.fcm.DriverRequest;
import io.sezon.sezon.model.json.fcm.DriverResponse;
import io.sezon.sezon.model.json.fcm.FCMMessage;
import io.sezon.sezon.model.json.fcm.LaundryDriverRequest;
import io.sezon.sezon.utils.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static io.sezon.sezon.config.General.FCM_KEY;
import static io.sezon.sezon.model.FCMType.ORDER;
import static io.sezon.sezon.model.json.fcm.DriverResponse.REJECT;

/**
 * Created by fathony on 1/26/2017.
 */

public class LaundryWaitingActivity extends AppCompatActivity {

    public static final String REQUEST_PARAM = "RequestParam";
    public static final String TIME_DISTANCE = "TimeDistance";

    private List<Driver> driverList;
    private RequestLaundryRequestJson param;

    private Realm realm;
    private User loginUser;
    private BookService bookService;

    private int minIndex;
    private int maxIndex;

    private boolean isDriverFound;

    private boolean shouldStopping;

    private long timeDistance;

    private LaundryDriverRequest foodDriverRequest;
    TextView title;
    Button waitingCancel;
    boolean threadRun = true;
    boolean cancelClicked =false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);
        title = (TextView) findViewById(R.id.waiting_tipeLayanan);
        waitingCancel = (Button) findViewById(R.id.waiting_cancel) ;


        Intent intent = getIntent();
        param = (RequestLaundryRequestJson) intent.getSerializableExtra(REQUEST_PARAM);
        timeDistance = intent.getLongExtra(TIME_DISTANCE, -1);

        if(param.getOrderFitur().equals("10")){
            title.setText("E-LAUNDRY");

        }

        realm = Realm.getDefaultInstance();
        loginUser = MangJekApplication.getInstance(this).getLoginUser();
//        loginUser = realm.copyFromRealm(MangJekApplication.getInstance(this).getLoginUser());
        bookService = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());

        GetNearRideCarRequestJson getRideParam = new GetNearRideCarRequestJson();
        getRideParam.setLatitude(param.getStartLatitude());
        getRideParam.setLongitude(param.getStartLongitude());

        bookService.getNearRide(getRideParam).enqueue(new Callback<GetNearRideCarResponseJson>() {
            @Override
            public void onResponse(Call<GetNearRideCarResponseJson> call, Response<GetNearRideCarResponseJson> response) {
                if(response.isSuccessful()) {
                    driverList = response.body().getData();
                    if(driverList.isEmpty()) {
                        Toast.makeText(LaundryWaitingActivity.this, "No driver available near you.", Toast.LENGTH_SHORT).show();
                        LaundryWaitingActivity.this.finish();
                    } else {
                        sendToServer();
                    }
                } else {
                    onFailure(call, new RuntimeException("Check your internet connection."));
                }
            }

            @Override
            public void onFailure(Call<GetNearRideCarResponseJson> call, Throwable t) {
                Toast.makeText(LaundryWaitingActivity.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                LaundryWaitingActivity.this.finish();
            }
        });
        waitingCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(foodDriverRequest != null){
                    cancelOrder();
                    cancelClicked = true;
                }
            }
        });

    }


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


    private void sendToServer() {
        bookService.requestTransaksiMLaundry(param).enqueue(new Callback<RequestLaundryResponseJson>() {
            @Override
            public void onResponse(Call<RequestLaundryResponseJson> call, Response<RequestLaundryResponseJson> response) {
                if(response.isSuccessful()) {
                    broadcastFcm(response.body().getData().get(0));
                    Log.d("Broadcast : ", "fcm " + response.body().getData().get(0));
                } else {
                    onFailure(call, new RuntimeException("Check your internet connection."));
                }
            }

            @Override
            public void onFailure(Call<RequestLaundryResponseJson> call, Throwable t) {
                Toast.makeText(LaundryWaitingActivity.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                LaundryWaitingActivity.this.finish();
            }
        });
    }

    private void broadcastFcm(TransaksiLaundry transaksiFood) {
        final LaundryDriverRequest requestToSend = generateFcmRequest(transaksiFood);
        foodDriverRequest = requestToSend;
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendMessage(requestToSend);
            }
        }).start();
    }

    private void sendMessage(LaundryDriverRequest request) {
        for(int i = 0; i < driverList.size(); i++) {
            request.setTimeAccept(new Date().getTime());

            FCMMessage message = new FCMMessage();
            message.setTo(driverList.get(i).getRegId());
            message.setData(request);


            Log.e("pesan",request+"");
            Log.d("FoodWaiting", "driver " + driverList.get(i).getNamaDepan());


            Log.d("FoodWaiting", ServiceGenerator.gson.toJson(request));

            Log.d("FoodWaiting", "onHandleIntent: To = " + message.getTo() + " Data = " + message.getData());

            FCMHelper.sendMessage(FCM_KEY, message).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    Log.d("FoodWaiting", "onResponse: ");
                    Log.e("rspon terima",response+"");


                }
            });
        }

        try {
            Thread.sleep(20 * 1000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        CheckStatusTransaksiRequest parameter = new CheckStatusTransaksiRequest();
        parameter.setIdTransaksi(foodDriverRequest.getIdTransaksi());
        bookService.checkStatusTransaksi(parameter).enqueue(new Callback<CheckStatusTransaksiResponse>() {
            @Override
            public void onResponse(Call<CheckStatusTransaksiResponse> call, Response<CheckStatusTransaksiResponse> response) {
                if(response.isSuccessful()) {
                    CheckStatusTransaksiResponse checkStatus = response.body();
                    Log.d("Response","Cek status : " + response.body().getMessage());
                    Log.d("Response","Cek status : " + response.body().isStatus());

                    int listDriver = 0;
                    listDriver = response.body().getListDriver().size();

                    if(checkStatus.isStatus() && listDriver > 0) {
                        DriverRequest request = new DriverRequest();
                        request.setAlamatAsal(foodDriverRequest.getAlamatAsal());
                        request.setAlamatTujuan(foodDriverRequest.getAlamatTujuan());
                        request.setJarak(foodDriverRequest.getJarak());
                        request.setHarga(foodDriverRequest.getHarga());
                        request.setIdTransaksi(foodDriverRequest.getIdTransaksi());
                        request.setStartLatitude(param.getStartLatitude());
                        request.setStartLongitude(param.getStartLongitude());
                        request.setEndLatitude(param.getLaundryLatitude());
                        request.setEndLongitude(param.getStartLongitude());
                        request.setOrderFitur(param.getOrderFitur());


                        Intent intent = new Intent(LaundryWaitingActivity.this, InProgressActivity.class);
                        intent.putExtra("driver", checkStatus.getListDriver().get(0));
                        intent.putExtra("request", request);
                        intent.putExtra("time_distance", timeDistance);
                        startActivity(intent);
                    } else {
                        Log.e("DRIVER STATUS", "Driver Busy!");
                        if(cancelClicked){

                        }else {
                            LaundryWaitingActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LaundryWaitingActivity.this, "Your driver seem busy!\npleas try again.", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<CheckStatusTransaksiResponse> call, Throwable t) {
                Log.e("DRIVER STATUS", "Driver not found!");
                LaundryWaitingActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LaundryWaitingActivity.this, "Your driver seem busy!\npleas try again.", Toast.LENGTH_LONG).show();
                    }
                });

                finish();
            }
        });
    }

    private void sendMessageLegacy(LaundryDriverRequest request) {
        int maxLoopLoading = (driverList.size() % 10 == 0) ? driverList.size() / 10 : (driverList.size() / 10) + 1;
        for (int i = 0; i < maxLoopLoading; i++) {
            int startIndex = i * 10;
            int maxIndex = (i + 1) * 10;

            minIndex = startIndex;
            this.maxIndex = maxIndex;

            if(driverList.size() < maxIndex) maxIndex = driverList.size();

            for(int j = startIndex; j < maxIndex; j++) {

                request.setTimeAccept(new Date().getTime());

                FCMMessage message = new FCMMessage();
                message.setTo(driverList.get(i).getRegId());
                message.setData(request);

                Log.d("FoodWaiting", ServiceGenerator.gson.toJson(request));

                Log.d("FoodWaiting", "onHandleIntent: To = " + message.getTo() + " Data = " + message.getData());

                FCMHelper.sendMessage(FCM_KEY, message).enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(okhttp3.Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                        Log.d("Laundry Waiting", "onResponse: ");
                    }
                });
            }

            try {
                Thread.sleep(20 * 1000);

                if(shouldStopping) break;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if(!isDriverFound) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LaundryWaitingActivity.this, "No driver available.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LaundryWaitingActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    LaundryWaitingActivity.this.startActivity(intent);
                }
            });
        }
    }

    private LaundryDriverRequest generateFcmRequest(TransaksiLaundry transaksiFood) {
        LaundryDriverRequest request = new LaundryDriverRequest();
        request.setIdTransaksi(transaksiFood.getId());
        request.setIdPelanggan(transaksiFood.getIdPelanggan());
        request.setRegIdPelanggan(loginUser.getRegId());
        request.setOrderFitur(transaksiFood.getOrderFitur());
        request.setHarga(transaksiFood.getHarga());
        request.setJarak(transaksiFood.getJarak());
        request.setAlamatAsal(transaksiFood.getAlamatAsal());
        request.setKreditPromo(transaksiFood.getKreditPromo());
        request.setAlamatTujuan(transaksiFood.getAlamatTujuan());
        request.setKodePromo(transaksiFood.getKodePromo());
        request.setPakaiMPay(transaksiFood.isPakaiMPay());
        request.setType("1");

        String namaPelanggan = String.format("%s %s", loginUser.getNamaDepan(), loginUser.getNamaBelakang());
        request.setNamaPelanggan(namaPelanggan);
        request.setTelepon(loginUser.getNoTelepon());
        request.setTimeAccept(new Date().getTime());

        return request;
    }

    @Override
    public void onBackPressed() {

    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DriverResponse response) {
        Log.e("MartWaiting", "you got a broadcast " + response.getResponse());
        if (response.getResponse().equalsIgnoreCase(DriverResponse.ACCEPT)) {

            Log.e("Pesanan Diterima",response+"");
            shouldStopping = true;
            isDriverFound = true;

            Driver driver = null;
            for(Driver d : driverList) {
                if(d.getId().equalsIgnoreCase(response.getId())) driver = d;
                break;
            }

            DriverRequest request = new DriverRequest();
            request.setAlamatAsal(foodDriverRequest.getAlamatAsal());
            request.setAlamatTujuan(foodDriverRequest.getAlamatTujuan());
            request.setJarak(foodDriverRequest.getJarak());
            request.setHarga(foodDriverRequest.getHarga());
            request.setIdTransaksi(foodDriverRequest.getIdTransaksi());
            request.setStartLatitude(param.getStartLatitude());
            request.setStartLongitude(param.getStartLongitude());
            request.setEndLatitude(param.getLaundryLatitude());
            request.setEndLongitude(param.getLaundryLongitude());
            request.setOrderFitur(param.getOrderFitur());

            Log.d("Laundry Waiting","" + request.getOrderFitur());


            Intent intent = new Intent(this, InProgressActivity.class);
            intent.putExtra("driver", driver);
            intent.putExtra("request", request);
            intent.putExtra("time_distance", timeDistance);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void cancelOrder(){
        User loginUser = MangJekApplication.getInstance(LaundryWaitingActivity.this).getLoginUser();
        CancelBookRequestJson request = new CancelBookRequestJson();

        request.id = loginUser.getId();
        request.id_transaksi = this.foodDriverRequest.getIdTransaksi();
        request.id_driver = "D0";

        Log.d("id_transaksi_cancel", this.foodDriverRequest.getIdTransaksi());
        UserService service = ServiceGenerator.createService(UserService.class, loginUser.getEmail(), loginUser.getPassword());
        service.cancelOrder(request).enqueue(new Callback<CancelBookResponseJson>() {
            @Override
            public void onResponse(Call<CancelBookResponseJson> call, Response<CancelBookResponseJson> response) {
                if (response.isSuccessful()) {
                    if (response.body().mesage.equals("order canceled")) {
                        Toast.makeText(LaundryWaitingActivity.this, "Order Canceled!", Toast.LENGTH_SHORT).show();
                        threadRun = false;
                        finish();
                    } else {
                        Toast.makeText(LaundryWaitingActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CancelBookResponseJson> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(LaundryWaitingActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

        DriverResponse response = new DriverResponse();
        response.type = ORDER;
        response.setIdTransaksi(this.foodDriverRequest.getIdTransaksi());
        response.setResponse(REJECT);

        for(int i=0; i< driverList.size();i++){
            FCMMessage message = new FCMMessage();
            message.setTo(driverList.get(i).getRegId());
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




    }
}

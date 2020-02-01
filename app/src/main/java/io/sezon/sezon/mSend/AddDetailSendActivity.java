package io.sezon.sezon.mSend;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import io.sezon.sezon.MangJekApplication;
import io.sezon.sezon.R;
import io.sezon.sezon.home.submenu.TopUpActivity;
import io.sezon.sezon.model.Driver;
import io.sezon.sezon.model.Fitur;
import io.sezon.sezon.model.User;
import io.sezon.sezon.model.json.book.RequestSendRequestJson;
import io.sezon.sezon.utils.Log;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

import static io.sezon.sezon.mSend.SendActivity.FITUR_KEY;

public class AddDetailSendActivity extends AppCompatActivity {
    @BindView(R.id.mSend_distance)
    TextView distanceText;
    @BindView(R.id.mSend_price)
    TextView priceText;
    @BindView(R.id.mSend_paymentGroup)
    RadioGroup paymentGroup;
    @BindView(R.id.mSend_mPayPayment)
    RadioButton mPayButton;
    @BindView(R.id.mSend_cashPayment)
    RadioButton cashButton;
    @BindView(R.id.mSend_topUp)
    RelativeLayout topUpButton;
    @BindView(R.id.mSend_mPayBalance)
    TextView mPayBalanceText;
    @BindView(R.id.mSend_order)
    Button orderButton;
    @BindView(R.id.mSend_goods_description)
    EditText goodsDescription;
    @BindView(R.id.mSend_sender_name)
    EditText senderName;
    @BindView(R.id.mSend_sender_phone)
    EditText senderPhone;
    @BindView(R.id.mSend_receiver_name)
    EditText receiverName;
    @BindView(R.id.mSend_receiver_phone)
    EditText receiverPhone;
    @BindView(R.id.discountText)
    TextView discountText;

    private double distance;
    private long price;
    private LatLng pickUpLatLang;
    private LatLng destinationLatLang;
    private String pickup;
    private String destination;
    private ArrayList<Driver> driverAvailable;
    private double timeDistance = 0;
//    DiskonMpay diskonMpay;
    private long mpayBalance;
    private Fitur fitur;
    Realm realm;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_add_detail);
        ButterKnife.bind(this);




        pakaikas.setBackgroundResource(R.drawable.bg_tidak_pilih);
        pakaikas.setClickable(true);


        realm = Realm.getDefaultInstance();
        User userLogin = MangJekApplication.getInstance(this).getLoginUser();
        mpayBalance = userLogin.getmPaySaldo();
//        diskonMpay = MangJekApplication.getInstance(this).getDiskonMpay();
        Intent intent = getIntent();
        if(intent.hasExtra("distance")){
            distance = intent.getDoubleExtra("distance", 0);
            price = intent.getLongExtra("price", 0);
            pickUpLatLang = intent.getParcelableExtra("pickup_latlng");
            destinationLatLang = intent.getParcelableExtra("destination_latlng");
            pickup = intent.getStringExtra("pickup");
            destination = intent.getStringExtra("destination");
            timeDistance = intent.getDoubleExtra("time_distance", 0);
            driverAvailable = (ArrayList<Driver>)intent.getSerializableExtra("driver");
            int selectedFitur = intent.getIntExtra(FITUR_KEY, -1);

            if (selectedFitur != -1)
                fitur = realm.where(Fitur.class).equalTo("idFitur", selectedFitur).findFirst();

            String disko = fitur.getDiskon();
            Log.e("Diskon kurir",disko+"");
        }

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


        long biayaTotal1 =  (long)(price * fitur.getBiayaAkhir());
        String formattedTotal1 = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal1);
        String formattedText1 = String.format(Locale.US, "$ %s.00", formattedTotal1);
        discountText.setText(formattedText1);


        long biayaTotal = price;
        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
        String formattedText = String.format(Locale.US, "$ %s.00", formattedTotal);
        priceText.setText(formattedText);


        float km = ((float) distance);
        String format = String.format(Locale.US, "Distance (%.1f Km)", km);
        distanceText.setText(format);

        if(mpayBalance < (price * fitur.getBiayaAkhir())){
            pakaipay.setEnabled(false);
            mPayButton.setEnabled(false);
            cashButton.toggle();
        }else {
            mPayButton.setEnabled(true);
            pakaipay.setEnabled(true);
        }


     //   discountText.setText(fitur.getDiskon());
        cashButton.setChecked(true);
        paymentGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (paymentGroup.getCheckedRadioButtonId()) {
                    case R.id.mSend_mPayPayment:
                        Toast.makeText(AddDetailSendActivity.this, "Pay Online", Toast.LENGTH_SHORT).show();

                        long biayaTotal = (long)(price * fitur.getBiayaAkhir());
                        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
                        String formattedText = String.format(Locale.US, "$ %s.00", formattedTotal);
                        String formattedText1 = String.format(Locale.US, "Pay Wallet $ %s.00", formattedTotal);
                        discountText.setText(formattedText);
                        oval.setText(formattedText1);
                        break;
                    case R.id.mSend_cashPayment:
                        Toast.makeText(AddDetailSendActivity.this, "Cash Online", Toast.LENGTH_SHORT).show();
                        biayaTotal = price;
                        formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
                        formattedText = String.format(Locale.US, "$ %s.00", formattedTotal);
                        priceText.setText(formattedText);
                        break;
                }
            }
        });


        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderButtonClick();
            }
        });

        topUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TopUpActivity.class));
            }
        });

    }

        private void onOrderButtonClick() {
        switch (paymentGroup.getCheckedRadioButtonId()) {
            case R.id.mSend_mPayPayment:
                if (driverAvailable.isEmpty()) {
                    AlertDialog dialog = new AlertDialog.Builder(AddDetailSendActivity.this)
                            .setMessage("Mohon maaf, tidak ada driver disekitar.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .create();
                    dialog.show();
                } else {
                    RequestSendRequestJson param = new RequestSendRequestJson();
                    User userLogin = MangJekApplication.getInstance(this).getLoginUser();
                    param.idPelanggan = userLogin.getId();
                    param.orderFitur = "5";
                    param.startLatitude = pickUpLatLang.latitude;
                    param.startLongitude = pickUpLatLang.longitude;
                    param.endLatitude = destinationLatLang.latitude;
                    param.endLongitude = destinationLatLang.longitude;
                    param.jarak = this.distance;
                    param.harga = (long)(this.price * fitur.getBiayaAkhir());
                    param.alamatAsal = pickup;
                    param.alamatTujuan = destination;
                    param.namaPengirim = senderName.getText().toString();
                    param.teleponPengirim = senderPhone.getText().toString();
                    param.namaPenerima = receiverName.getText().toString();
                    param.teleponPenerima = receiverPhone.getText().toString();
                    param.namaBarang = goodsDescription.getText().toString();



                    Log.e("AjoPay", "used");
                    param.pakaiMpay = 1;

                    Intent intent = new Intent(AddDetailSendActivity.this, SendWaitingActivity.class);
                    intent.putExtra(SendWaitingActivity.REQUEST_PARAM, param);
                    intent.putExtra(SendWaitingActivity.DRIVER_LIST, (ArrayList) driverAvailable);
                    intent.putExtra("time_distance", timeDistance);
                    startActivity(intent);
                }


                break;
            case R.id.mSend_cashPayment:
                if (driverAvailable.isEmpty()) {
                    AlertDialog dialog = new AlertDialog.Builder(AddDetailSendActivity.this)
                            .setMessage("Mohon maaf, tidak ada driver disekitar.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .create();
                    dialog.show();
                } else {
                    RequestSendRequestJson param = new RequestSendRequestJson();
                    User userLogin = MangJekApplication.getInstance(this).getLoginUser();
                    param.idPelanggan = userLogin.getId();
                    param.orderFitur = "5";
                    param.startLatitude = pickUpLatLang.latitude;
                    param.startLongitude = pickUpLatLang.longitude;
                    param.endLatitude = destinationLatLang.latitude;
                    param.endLongitude = destinationLatLang.longitude;
                    param.jarak = this.distance;
                    param.harga = this.price;
                    param.alamatAsal = pickup;
                    param.alamatTujuan = destination;
                    param.namaPengirim = senderName.getText().toString();
                    param.teleponPengirim = senderPhone.getText().toString();
                    param.namaPenerima = receiverName.getText().toString();
                    param.teleponPenerima = receiverPhone.getText().toString();
                    param.namaBarang = goodsDescription.getText().toString();

                    Log.e("PayPace", "Tidak Dengan AjoPay");
                    param.pakaiMpay = 0;


                    Intent intent = new Intent(AddDetailSendActivity.this, SendWaitingActivity.class);
                    intent.putExtra(SendWaitingActivity.REQUEST_PARAM, param);
                    intent.putExtra(SendWaitingActivity.DRIVER_LIST, (ArrayList) driverAvailable);
                    intent.putExtra("time_distance", timeDistance);
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        User userLogin = MangJekApplication.getInstance(this).getLoginUser();
        String formattedText = String.format(Locale.US, "$ %s.00",
                NumberFormat.getNumberInstance(Locale.US).format(userLogin.getmPaySaldo()));

        mPayBalanceText.setText(formattedText);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}

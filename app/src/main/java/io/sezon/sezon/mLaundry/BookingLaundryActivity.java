package io.sezon.sezon.mLaundry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;



import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.sezon.sezon.MangJekApplication;
import io.sezon.sezon.R;
import io.sezon.sezon.api.MapDirectionAPI;
import io.sezon.sezon.api.ServiceGenerator;
import io.sezon.sezon.home.submenu.TopUpActivity;
import io.sezon.sezon.model.Fitur;
import io.sezon.sezon.model.ItemLaundry;
import io.sezon.sezon.model.Laundry;
import io.sezon.sezon.model.MlaundryMitra;
import io.sezon.sezon.model.PesananLaundry;
import io.sezon.sezon.model.User;
import io.sezon.sezon.model.json.book.RequestLaundryRequestJson;

public class BookingLaundryActivity extends AppCompatActivity implements MenuBookingItem.OnCalculatePrice {

    @BindView(R.id.btn_home)
    ImageView backButton;

    @BindView(R.id.laundry_orders)
    RecyclerView laundryOrders;

    @BindView(R.id.booking_location)
    LinearLayout locationButton;
    @BindView(R.id.laundryAmbil_destination)
    TextView locationText;
    @BindView(R.id.laundry_addNotes)
    EditText addNotes;

    @BindView(R.id.laundry_cost)
    TextView laundryCost;
    @BindView(R.id.delivery_cost)
    TextView deliveryCost;
    @BindView(R.id.value_service)
    TextView totalCost;
    @BindView(R.id.laundry_cash)
    TextView cashCost;


    @BindView(R.id.food_cash1)
    TextView cash;

    @BindView(R.id.service_paymentgroup)
    RadioGroup paymentGroup;
    @BindView(R.id.radio_mpay)
    RadioButton mpayPayment;
    @BindView(R.id.radio_cash)
    RadioButton cashPayment;
    @BindView(R.id.mpay_balance)
    TextView mPayBalance;
    @BindView(R.id.mpay_discount)
    TextView mPayDiscount;
    @BindView(R.id.mpay_topup)
    RelativeLayout topupButton;

    @BindView(R.id.order_btn)
    Button orderButton;

    private FastItemAdapter<MenuBookingItem> pesananAdapter;

    private Realm realm;
    private User loginUser;
    private LatLng destinationLocation;

    private boolean isRestoranMitra;

    private Laundry restoran;
    private Fitur designedFitur;
    private MlaundryMitra mitraHarga;

    private long foodCostLong = 0;
    private long deliveryCostBeforeDiscLong = 0;
    private long deliveryCostLong = 0;
    private long totalLong = 0;

    private long timeDistance;

    private double jarak = -99.0;

    private final int DESTINATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_laundry);
        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();
        loginUser = MangJekApplication.getInstance(this).getLoginUser();


        restoran = realm.copyFromRealm(realm.where(Laundry.class).findFirst());

        isRestoranMitra = restoran.isPartner();

        designedFitur = realm.copyFromRealm(realm.where(Fitur.class).equalTo("idFitur", 10).findFirst());
        mitraHarga = MangJekApplication.getInstance(this).getMLaundryMitra();

        pesananAdapter = new FastItemAdapter<>();
        laundryOrders.setLayoutManager(new LinearLayoutManager(this));
        laundryOrders.setAdapter(pesananAdapter);
        loadMakanan();

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BookingLaundryActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();
                showLocationPicker();
            }
        });

        updateCostPrice();
        updateEstimatedFoodCost();

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (readyToOrder()) sendOrder();
            }
        });

        topupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BookingLaundryActivity.this, TopUpActivity.class));
            }
        });

        if (isRestoranMitra) {
            mPayDiscount.setText("Diskon " + mitraHarga.getDiskon());
        } else {
            mPayDiscount.setText("Diskon " + designedFitur.getDiskon());
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        cashPayment.setChecked(true);
        cashPayment.setBackgroundResource(R.drawable.bg_tidak_pilih);

        paymentGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (mpayPayment.isChecked()) {
                    //Toast.makeText(getBaseContext(), "Dklik paay", Toast.LENGTH_SHORT).show();
                    mpayPayment.setBackgroundResource(R.drawable.bg_tidak_pilih);
                    cashPayment.setBackgroundResource(R.color.bgpay);
                    updateCostPrice();
                } else if (cashPayment.isChecked()) {
                    //  Toast.makeText(getBaseContext(), "Dklik kas", Toast.LENGTH_SHORT).show();
                    cashPayment.setBackgroundResource(R.drawable.bg_tidak_pilih);
                    mpayPayment.setBackgroundResource(R.color.bgpay);
                    updateCostPrice();
                }
            }
        });


}

    private void sendOrder() {
        List<PesananLaundry> existingFood = realm.copyFromRealm(realm.where(PesananLaundry.class).findAll());

        for(PesananLaundry pesanan : existingFood) {
            if(pesanan.getCatatan() == null || pesanan.getCatatan().trim().equals("")) pesanan.setCatatan("");
        }

        RequestLaundryRequestJson param = new RequestLaundryRequestJson();
        param.setIdPelanggan(loginUser.getId());
        param.setOrderFitur("10");
        param.setStartLatitude(destinationLocation.latitude);
        param.setStartLongitude(destinationLocation.longitude);
        param.setLaundryLatitude(restoran.getLatitude());
        param.setLaundryLongitude(restoran.getLongitude());
        param.setAlamatAsal(locationText.getText().toString());
        param.setAlamatLaundry(restoran.getAlamat());
        param.setJarak(jarak);
        param.setHarga(deliveryCostLong);
        param.setPakaiMPay(paymentGroup.getCheckedRadioButtonId() == R.id.radio_mpay);
        param.setIdResto(String.valueOf(restoran.getId()));
        param.setTotalBiayaBelanja(foodCostLong);
        param.setCatatan(addNotes.getText().toString());
        param.setPesanan(existingFood);

        Log.d("BookingAct", ServiceGenerator.gson.toJson(param));

        Intent intent = new Intent(this, LaundryWaitingActivity.class);
        intent.putExtra(LaundryWaitingActivity.REQUEST_PARAM, param);
        intent.putExtra(LaundryWaitingActivity.TIME_DISTANCE, timeDistance);
        startActivity(intent);
    }

    private void showLocationPicker() {
        Intent intent = new Intent(this, LocationPickerLaundryActivity.class);
        intent.putExtra(LocationPickerLaundryActivity.FORM_VIEW_INDICATOR, DESTINATION_ID);
        startActivityForResult(intent, LocationPickerLaundryActivity.LOCATION_PICKER_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LocationPickerLaundryActivity.LOCATION_PICKER_ID) {
            if (resultCode == Activity.RESULT_OK) {
                String address = data.getStringExtra(LocationPickerLaundryActivity.LOCATION_NAME);
                LatLng latLng = data.getParcelableExtra(LocationPickerLaundryActivity.LOCATION_LATLNG);
                locationText.setText(address);
                destinationLocation = latLng;
            }
        }
        requestRoute();
    }

    @Override
    protected void onResume() {
        super.onResume();

        User userLogin = MangJekApplication.getInstance(this).getLoginUser();
        String formattedText = String.format(Locale.US, "Rp. %s ,-",
                NumberFormat.getNumberInstance(Locale.US).format(userLogin.getmPaySaldo()));
        mPayBalance.setText(formattedText);

        long biayaAkhir = 0;
        if (isRestoranMitra) {
            biayaAkhir = (long) (totalLong * mitraHarga.getBiayaAkhir());
        } else {
            biayaAkhir = (long) (totalLong * designedFitur.getBiayaAkhir());
        }

        if (userLogin.getmPaySaldo() < biayaAkhir) {
            mpayPayment.setEnabled(false);
            cashPayment.toggle();
        } else {
            mpayPayment.setEnabled(true);
        }
    }

    private boolean readyToOrder() {
        if (destinationLocation == null) {
            Toast.makeText(this, "Silakan pilih lokasi Anda.", Toast.LENGTH_SHORT).show();
            return false;
        }

        List<PesananLaundry> existingFood = realm.copyFromRealm(realm.where(PesananLaundry.class).findAll());

        int quantity = 0;
        for (int p = 0; p < existingFood.size(); p++) {
            quantity += existingFood.get(p).getQty();
        }

        if (quantity == 0) {
            Toast.makeText(this, "Please order at least 1 item.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(jarak == -99.0) {
            Toast.makeText(this, "Please wait a moment...", Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    private void updateEstimatedFoodCost() {
        List<PesananLaundry> existingFood = realm.copyFromRealm(realm.where(PesananLaundry.class).findAll());

        long cost = 0;
        for (int p = 0; p < existingFood.size(); p++) {
            cost += existingFood.get(p).getTotalHarga();
        }

        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(cost);
        String formattedText = String.format(Locale.US, "$ %s.00", formattedTotal);

        foodCostLong = cost;

        laundryCost.setText(formattedText);

        updateTotal();
    }

    private void updateCostPrice() {
        if (isRestoranMitra) {
            deliveryCostBeforeDiscLong = mitraHarga.getBiaya();
        } else {
            deliveryCostBeforeDiscLong = designedFitur.getBiaya();
        }

        double biayaAkhir = 1.0;

        if(paymentGroup.getCheckedRadioButtonId() == R.id.radio_mpay) {
            if(isRestoranMitra) {
                biayaAkhir = mitraHarga.getBiayaAkhir();
            } else {
                biayaAkhir = designedFitur.getBiayaAkhir();
            }
        }

        deliveryCostLong = (long) (deliveryCostBeforeDiscLong * biayaAkhir);

        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(deliveryCostLong);
        String formattedText = String.format(Locale.US, "$ %s.00", formattedTotal);
        deliveryCost.setText(formattedText);

        updateTotal();
    }

    private void updateTotal() {
        totalLong = foodCostLong + deliveryCostLong;

        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(totalLong);
        String formattedText = String.format(Locale.US, "$ %s.00", formattedTotal);

        totalCost.setText(formattedText);
        cashCost.setText(formattedText);
        cash.setText(formattedText);
    }

    private void loadMakanan() {
        List<ItemLaundry> makananList = realm.copyFromRealm(realm.where(ItemLaundry.class).findAll());
        List<PesananLaundry> pesananLaundry = realm.copyFromRealm(realm.where(PesananLaundry.class).findAll());
        pesananAdapter.clear();
        for (PesananLaundry pesanan : pesananLaundry) {
            MenuBookingItem makananItem = new MenuBookingItem(this, this);
            makananItem.quantity = 0;
            for (ItemLaundry makanan : makananList) {
                if (makanan.getId() == pesanan.getIdLaundry()) {
                    makananItem.quantity = pesanan.getQty();
                    makananItem.id = makanan.getId();
                    makananItem.foto=makanan.getFoto();
                    makananItem.namaMenu = makanan.getNamaLayanan();
                    makananItem.deskripsiMenu = makanan.getDeskripsiLayanan();
                    makananItem.harga = makanan.getHarga();
                    makananItem.cost = makanan.getHarga();
                    makananItem.foto = makanan.getFoto();
                    makananItem.catatan = pesanan.getCatatan();

                    break;
                }
            }

            pesananAdapter.add(makananItem);
        }

        pesananAdapter.notifyDataSetChanged();
    }

    public void calculatePrice() {
        updateEstimatedFoodCost();
    }

    private okhttp3.Callback updateRouteCallback = new okhttp3.Callback() {
        @Override
        public void onFailure(okhttp3.Call call, IOException e) {

        }

        @Override
        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
            if (response.isSuccessful()) {
                final String json = response.body().string();
                final long distance = MapDirectionAPI.getDistance(BookingLaundryActivity.this, json);
                final long time = MapDirectionAPI.getTimeDistance(BookingLaundryActivity.this, json);
                if (distance >= 0) {
                    BookingLaundryActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateDistance(distance);
                            timeDistance = time/60;
                        }
                    });
                }
            }
        }
    };

    private void updateDistance(long distance) {
        float km = ((float) distance) / 1000f;

        this.jarak = km;
    }

    private void requestRoute() {
        if(destinationLocation != null) {
            LatLng restoranLocation = new LatLng(restoran.getLatitude(), restoran.getLongitude());
            MapDirectionAPI.getDirection(restoranLocation, destinationLocation).enqueue(updateRouteCallback);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}

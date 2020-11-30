package io.sezon.sezon.mLaundry;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import io.sezon.sezon.MangJekApplication;
import io.sezon.sezon.R;
import io.sezon.sezon.api.ServiceGenerator;
import io.sezon.sezon.api.service.BookService;
import io.sezon.sezon.model.ItemLaundry;
import io.sezon.sezon.model.Laundry;
import io.sezon.sezon.model.PesananLaundry;
import io.sezon.sezon.model.TempatLaundry;
import io.sezon.sezon.model.User;
import io.sezon.sezon.model.json.book.GetLayananLaundryRequestJson;
import io.sezon.sezon.model.json.book.GetLayananLaundryResponseJson;
import io.sezon.sezon.utils.Log;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LaundryMenuActivity extends AppCompatActivity  implements MenuLaundryItem.OnCalculatePrice{

    @BindView(R.id.btn_home)
    ImageView btnHome;

    @BindView(R.id.laundry_title)
    TextView titleLaundry;

  @BindView(R.id.laundry_foto)
  ImageView fotoLaundry;

    @BindView(R.id.laundry_address)
    TextView addressLaundry;

    @BindView(R.id.laundry_info)
   TextView infoLaundry;

    @BindView(R.id.layanan_laundry_recycler)
    RecyclerView menuRecycler;

    @BindView(R.id.estimasiHarga_bottom)
    RelativeLayout priceContainer;

    @BindView(R.id.qty_text)
    TextView qtyText;

    @BindView(R.id.cost_text)
    TextView costText;


//
//    @BindView(R.id.laundry_closed)
//    TextView closed;
   @BindView(R.id.jam_buka)
    TextView jambuka;
    @BindView(R.id.jam_tutup)
    TextView jamtutup;
  //  @BindView(R.id.telepon)
 //   TextView telepone;

    public static final String ID_LAUNDRY = "idLaundry";
    public static final String NAMA_LAUNDRY = "namaLaundry";
    public static final String ALAMAT_LAUNDRY= "alamatLaundry";
    public static final String DISTANCE_LAUNDRY = "distanceLaundry";
    public static final String JAM_BUKA = "jamBuka";
    public static final String JAM_TUTUP = "jamTutup";
    public static final String IS_MITRA = "IsMitra";
    public static final String IS_OPEN = "IsOpen";
    public static final String PICTURE_URL = "PicUrl";

    private int idLaundry;
    private String namaLaundry;
    private String alamatLaundry;
    private double distanceLaundry;
    private String jamBuka;
    private String jamTutup;
    private boolean isOpenFromParent;
    private boolean isMitra;
    private String picUrl;
    public String foto;
    private boolean isOpen;

    private Realm realm;
    private List<ItemLaundry> menuLaundry;
//    private RealmResults<ItemLaundry> menuLaundry;

    //    private List<LayananLaundry> makanan;
    private FastItemAdapter<MenuLaundryItem> menuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry_menu);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        idLaundry = intent.getIntExtra(ID_LAUNDRY, -1);

        Log.e("ADD ID Laundary", String.valueOf(idLaundry));

        namaLaundry = intent.getStringExtra(NAMA_LAUNDRY);
        alamatLaundry = intent.getStringExtra(ALAMAT_LAUNDRY);
        distanceLaundry = intent.getDoubleExtra(DISTANCE_LAUNDRY, -8);
        jamBuka = intent.getStringExtra(JAM_BUKA);
        jamTutup = intent.getStringExtra(JAM_TUTUP);
        isOpenFromParent = intent.getBooleanExtra(IS_OPEN, false);
        isMitra = intent.getBooleanExtra(IS_MITRA, false);
        picUrl = intent.getStringExtra(PICTURE_URL);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        realm = Realm.getDefaultInstance();

        Glide.with(this)
                .load(picUrl)
                .error(R.drawable.ic_restoran)
                .into(fotoLaundry);
        if(isOpenFromParent) {
            isOpen = calculateJamBukaTutup();
        } else {
            isOpen = false;
        }



        titleLaundry.setText(namaLaundry);
        addressLaundry.setText(alamatLaundry);
        NumberFormat formatter = new DecimalFormat("#0.00");
        infoLaundry.setText(formatter.format(distanceLaundry)+" Km") ;
        //+ " KM | OPEN " + jamBuka + " - " + jamTutup)
        jambuka.setText(jamBuka);
        jamtutup.setText(jamTutup);
        menuAdapter = new FastItemAdapter<>();
        realm.beginTransaction();
        realm.delete(PesananLaundry.class);
        realm.commitTransaction();
        menuRecycler.setHasFixedSize(false);

        MenuRecycler();
        getMenuResto();

        priceContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOpen) {
                    Intent intent = new Intent(LaundryMenuActivity.this, BookingLaundryActivity.class);
                    LaundryMenuActivity.this.startActivity(intent);
                }else{
                    Toast.makeText(LaundryMenuActivity.this, "This shop is closed, please come back tomorrow again. :(", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private boolean calculateJamBukaTutup() {
        String[] parsedJamBuka = jamBuka.split(":");
        String[] parsedJamTutup = jamTutup.split(":");

        int jamBuka = Integer.parseInt(parsedJamBuka[0]), menitBuka = Integer.parseInt(parsedJamBuka[1]);
        int jamTutup = Integer.parseInt(parsedJamTutup[0]), menitTutup = Integer.parseInt(parsedJamTutup[1]);

        int totalMenitBuka = (jamBuka * 60) + menitBuka;
        int totalMenitTutup = (jamTutup * 60) + menitTutup;

        Calendar now = Calendar.getInstance();
        int totalMenitNow = (now.get(Calendar.HOUR_OF_DAY) * 60) + now.get(Calendar.MINUTE);

        return totalMenitNow <= totalMenitTutup && totalMenitNow >= totalMenitBuka;
    }

    private void MenuRecycler() {
        menuRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        menuRecycler.setAdapter(menuAdapter);

    }

    @Override
    public void onBackPressed() {
        List<PesananLaundry> existingFood = realm.where(PesananLaundry.class).findAll();

        int quantity = 0;
        for (int p = 0; p < existingFood.size(); p++) {
            quantity += existingFood.get(p).getQty();
        }

        if(quantity > 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete current order(s)?")
                    .setMessage("Leaving this page will cause you lose all the order you've made. Continue?")
                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LaundryMenuActivity.this.finish();
                        }
                    })
                    .setNegativeButton("Back", null)
                    .show();
        } else {
            finish();
        }
    }

    private void getMenuResto() {
        User loginUser = MangJekApplication.getInstance(this).getLoginUser();
        BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
        GetLayananLaundryRequestJson param = new GetLayananLaundryRequestJson();
        param.setIdLaundry(idLaundry);

        service.getMenuLaundry(param).enqueue(new Callback<GetLayananLaundryResponseJson>() {
            @Override
            public void onResponse(Call<GetLayananLaundryResponseJson> call, Response<GetLayananLaundryResponseJson> response) {
                if (response.isSuccessful()) {
                    TempatLaundry foodResto = response.body().getTempatLaundry();
                    Laundry restoranList = foodResto.getDetailLaundry().get(0);

                    menuLaundry = foodResto.getMenuLaundryList();
//                    makanan = foodResto.getLayananList();
                    Log.d(LaundryMenuActivity.class.getSimpleName(), "Number of layanan: " + menuLaundry.size());
//                    Log.d(LaundryMenuActivity.class.getSimpleName(), "Number of restoran: " + makanan.size());
                    Realm realm = MangJekApplication.getInstance(LaundryMenuActivity.this).getRealmInstance();
                    realm.beginTransaction();
//                    realm.delete(LayananLaundry.class);
                    realm.delete(ItemLaundry.class);
                    realm.copyToRealm(menuLaundry);
                    realm.commitTransaction();

                    realm.beginTransaction();
                    realm.delete(Laundry.class);
                    realm.copyToRealm(restoranList);
                    realm.commitTransaction();



                    menuAdapter.clear();

                    MenuLaundryItem menuItem;
                    for (int i = 0; i < menuLaundry.size(); i++) {
                        menuItem = new MenuLaundryItem(getBaseContext(), LaundryMenuActivity.this);
                        menuItem.id = menuLaundry.get(i).getId();
                        menuItem.namaMenu = menuLaundry.get(i).getNamaLayanan();
                        menuItem.deskripsiMenu = menuLaundry.get(i).getDeskripsiLayanan();
                        menuItem.foto = menuLaundry.get(i).getFoto();
                        menuItem.harga = menuLaundry.get(i).getHarga();

                        menuAdapter.add(menuItem);

                        Log.e("ADD LAUNDRY", menuItem.id + "");
                        Log.e("ADD lAUNDRY", menuItem.namaMenu + "");
                        Log.e("ADD LAUNDRY", menuItem.deskripsiMenu + "");
                        Log.e("ADD LAUNDRY", menuItem.harga + "");
                        Log.e("ADD LAUNDRY Foto", menuItem.foto + "");

                    }
                }
            }

            @Override
            public void onFailure(Call<GetLayananLaundryResponseJson> call, Throwable t) {
               // Toast.makeText(getApplicationContext(), "Connection to server lost, check your internet connection.",
                     //   Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        calculatePrice();
    }

    @Override
    public void calculatePrice() {
        List<PesananLaundry> existingFood = realm.where(PesananLaundry.class).findAll();
        Log.d("Calculate : ", "running...");
        int quantity = 0;
        long cost = 0;
        for (int p = 0; p < existingFood.size(); p++) {
            quantity += existingFood.get(p).getQty();
            cost += existingFood.get(p).getTotalHarga();
        }
        Log.d("Quantity total : ","" + quantity);
        Log.d("Cost total : ","" + cost);




        qtyText.setText("" + quantity);
        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(cost);
        String formattedText = String.format(Locale.US, "$ %s.00", formattedTotal);
        costText.setText(formattedText);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }



}

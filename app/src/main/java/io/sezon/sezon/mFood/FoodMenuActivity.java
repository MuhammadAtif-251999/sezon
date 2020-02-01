package io.sezon.sezon.mFood;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import io.sezon.sezon.MangJekApplication;
import io.sezon.sezon.R;
import io.sezon.sezon.api.ServiceGenerator;
import io.sezon.sezon.api.service.BookService;
import io.sezon.sezon.model.FoodResto;
import io.sezon.sezon.model.Makanan;
import io.sezon.sezon.model.MenuMakanan;
import io.sezon.sezon.model.PesananFood;
import io.sezon.sezon.model.Restoran;
import io.sezon.sezon.model.User;
import io.sezon.sezon.model.json.book.GetFoodRestoRequestJson;
import io.sezon.sezon.model.json.book.GetFoodRestoResponseJson;
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

public class FoodMenuActivity extends AppCompatActivity implements MakananItem.OnCalculatePrice{

    @BindView(R.id.btn_home)
    ImageView btnHome;

    @BindView(R.id.resto_title)
    TextView titleResto;

    @BindView(R.id.food_img)
    ImageView foodImage;
    @BindView(R.id.food_address)
    TextView foodAddress;

    @BindView(R.id.food_info)
    TextView foodInfo;

    @BindView(R.id.menu_recycler)
    RecyclerView menuRecycler;

    @BindView(R.id.foodMenu_bottom)
    FrameLayout priceContainer;


    @BindView(R.id.prc)
    RelativeLayout panelprice;

    @BindView(R.id.qty_text)
    TextView qtyText;

    @BindView(R.id.qty_text1)
    TextView qtyText1;

    @BindView(R.id.cost_text)
    TextView costText;

    @BindView(R.id.foodMenu_mitra)
    TextView mitra;

    @BindView(R.id.foodMenu_closed)
    TextView closed;

    @BindView(R.id.notelp)
    TextView telp;
    @BindView(R.id.whatsaap)
  TextView what;
    @BindView(R.id.jam_buka)
    TextView jambuka;
    @BindView(R.id.jam_tutup)
    TextView jamtutup;
    @BindView(R.id.telepon)
    TextView telepone;



    @BindView(R.id.makanan_recycler)
   RecyclerView makanan_recycler;
    public static final String ID_RESTO = "idResto";
    public static final String NAMA_RESTO = "namaResto";
    public static final String ALAMAT_RESTO = "alamatResto";
    public static final String DISTANCE_RESTO = "distanceResto";
    public static final String JAM_BUKA = "jamBuka";
    public static final String JAM_TUTUP = "jamTutup";
    public static final String IS_MITRA = "IsMitra";
    public static final String IS_OPEN = "IsOpen";
    public static final String PICTURE_URL = "PicUrl";
    public static final String KONTAK = "kontak_telepon";

    private int idResto;
    private String namaResto;
    private String alamatResto;
    private double distanceResto;
    private String jamBuka;
    private String jamTutup;
    private boolean isOpenFromParent;
    private boolean isMitra;
    private String picUrl;
    private String Kontak;

    private boolean isOpen;


    Restoran resto;
    private Realm realm;
    private List<MenuMakanan> menuMakanan;
    private List<Makanan> makanan;
    private FastItemAdapter<MakananItem> makananAdapter;
    private FastItemAdapter<MenuItem> menuAdapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_menu);
        ButterKnife.bind(this);


        makananAdapter = new FastItemAdapter<>();
        makanan_recycler.setLayoutManager(new GridLayoutManager(this, 2));
        makanan_recycler.setAdapter(makananAdapter);



        Intent intent = getIntent();
        idResto = intent.getIntExtra(ID_RESTO, -1);
        namaResto = intent.getStringExtra(NAMA_RESTO);
        alamatResto = intent.getStringExtra(ALAMAT_RESTO);
        distanceResto = intent.getDoubleExtra(DISTANCE_RESTO, -8);
        jamBuka = intent.getStringExtra(JAM_BUKA);
        jamTutup = intent.getStringExtra(JAM_TUTUP);
        Kontak = intent.getStringExtra(KONTAK);
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

        Glide.with(this).load(picUrl).centerCrop().into(foodImage);
        if(isOpenFromParent) {
            isOpen = calculateJamBukaTutup();
        } else {
            isOpen = false;
        }


        closed.setVisibility(isOpen ? View.GONE : View.VISIBLE);
        mitra.setVisibility(isMitra? View.VISIBLE : View.GONE);
        telp.setText("Fax:"+Kontak);
        telepone.setText(Kontak);
        titleResto.setText(namaResto);
        foodAddress.setText(alamatResto);
        NumberFormat formatter = new DecimalFormat("#0.00");
        foodInfo.setText(formatter.format(distanceResto)+" Km");
        jambuka.setText("Open "+jamBuka);
        jamtutup.setText("Closed "+ jamTutup);



        menuAdapter = new FastItemAdapter<>();
        MenuRecycler();
        getMenuResto();

        panelprice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOpen) {
                    Log.e("BUTTON", "CLICKED");

                    Intent intent = new Intent(FoodMenuActivity.this, BookingActivity.class);
                    FoodMenuActivity.this.startActivity(intent);
                } else {
                    Toast.makeText(FoodMenuActivity.this, "Your restaurant is still closed. :(", Toast.LENGTH_SHORT).show();
                }

            }
        });

        what.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String text = "Hai "+namaResto;// Replace with your message.

                    String toNumber = ""; // Replace with mobile phone number without +Sign or leading zeros.


                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=62"+Kontak+"&text="+text));
                    startActivity(intent);
                }
                catch (Exception e){
                    e.printStackTrace();
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
    public void openWhatsApp(View view){
        try {
            String text = "This is a test";// Replace with your message.

            String toNumber = "085316547777"; // Replace with mobile phone number without +Sign or leading zeros.


            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=62"+KONTAK +"&text="+text));
            startActivity(intent);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private void MenuRecycler() {
        menuRecycler.setLayoutManager(new LinearLayoutManager(FoodMenuActivity.this, LinearLayout.HORIZONTAL,false));
        menuRecycler.setAdapter(menuAdapter);

        menuAdapter.withOnClickListener(new FastAdapter.OnClickListener<MenuItem>() {
            @Override
            public boolean onClick(View v, IAdapter<MenuItem> adapter, MenuItem item, int position) {
                if(isOpen) {
                    Log.e("BUTTON", "CLICKED");
                    MenuMakanan selectedMenu = menuMakanan.get(position);
                    int idMenu = selectedMenu.getIdMenu();
                    Intent intent = new Intent(FoodMenuActivity.this, MakananActivity.class);
                    intent.putExtra(MakananActivity.ID_MENU, selectedMenu.getIdMenu());
                    intent.putExtra(MakananActivity.NAMA_RESTO, selectedMenu.getMenuMakanan());
                    Log.e("ID RESTO", idMenu + "");
                    startActivity(intent);
                } else {
                    Toast.makeText(FoodMenuActivity.this, "Your restaurant is still closed. :(", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        List<PesananFood> existingFood = realm.where(PesananFood.class).findAll();

        int quantity = 0;
        for (int p = 0; p < existingFood.size(); p++) {
            quantity += existingFood.get(p).getQty();
        }

        if(quantity > 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Want to delete this order(s)?")
                    .setMessage("Leaving this page will cause you to lose all the orders you made. Do You Want to Cancel?")
                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FoodMenuActivity.this.finish();
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
        GetFoodRestoRequestJson param = new GetFoodRestoRequestJson();
        param.setIdResto(idResto);
        Log.d("Request ID resto food : ",""+ idResto);

        service.getFoodResto(param).enqueue(new Callback<GetFoodRestoResponseJson>() {
            @Override
            public void onResponse(Call<GetFoodRestoResponseJson> call, Response<GetFoodRestoResponseJson> response) {
                if (response.isSuccessful()) {
                    FoodResto foodResto = response.body().getFoodResto();
                    Restoran restoranList = foodResto.getDetailRestoran().get(0);

                    menuMakanan = foodResto.getMenuMakananList();
                    makanan = foodResto.getMakananList();
                    Log.d(FoodMenuActivity.class.getSimpleName(), "Number of kategori: " + menuMakanan.size());
                  //  Log.d(FoodMenuActivity.class.getSimpleName(), "Number of restoran: " + makanan.size());
                    Realm realm = MangJekApplication.getInstance(FoodMenuActivity.this).getRealmInstance();
                    realm.beginTransaction();
                    realm.delete(Makanan.class);
                    realm.copyToRealm(makanan);
                    realm.commitTransaction();

                    realm.beginTransaction();
                    realm.delete(Restoran.class);
                    realm.copyToRealm(restoranList);
                    realm.commitTransaction();

                    makananAdapter.clear();
                    MenuItem menuItem;
                    for (int i = 0; i < menuMakanan.size(); i++) {
                        menuItem = new MenuItem(FoodMenuActivity.this);
                        menuItem.idMenu = menuMakanan.get(i).getIdMenu();
                        menuItem.menuMakanan = menuMakanan.get(i).getMenuMakanan();
                        menuAdapter.add(menuItem);
                        Log.e("ADD RESTO", menuItem.idMenu + "");
                        Log.e("ADD RESTO", menuItem.menuMakanan + "");
                    }
                    MakananItem makananItem;
                    for (int i = 0; i < makanan.size(); i++) {
                        makananItem = new MakananItem(getBaseContext(),FoodMenuActivity.this);
                        makananItem.id = makanan.get(i).getId();
                        makananItem.namaMenu = makanan.get(i).getNamaMenu();
                        makananItem.deskripsiMenu = makanan.get(i).getDeskripsiMenu();
                        makananItem.harga = makanan.get(i).getHarga();
                        makananItem.foto=makanan.get(i).getFoto();
                        makananAdapter.add(makananItem);
                        Log.e("Nid manann", makananItem.id + "");
                        Log.e("Nama makanan", makananItem.namaMenu + "");
                    }
                }
            }

            @Override
            public void onFailure(Call<GetFoodRestoResponseJson> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Connection to server lost, check your internet connection.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        CalculatePrice();
    }

    public void CalculatePrice() {
        List<PesananFood> existingFood = realm.where(PesananFood.class).findAll();

        int quantity = 0;
        long cost = 0;
        for (int p = 0; p < existingFood.size(); p++) {
            quantity += existingFood.get(p).getQty();
            cost += existingFood.get(p).getTotalHarga();
        }

        if(quantity > 0)
            panelprice.setVisibility(View.VISIBLE);
        else
            panelprice.setVisibility(View.GONE);

        qtyText.setText("" + quantity);
        qtyText1.setText(quantity+" Items");
        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(cost);
        String formattedText = String.format(Locale.US, "$ %s.00", formattedTotal);
        costText.setText(formattedText);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void calculatePrice() {
        List<PesananFood> existingFood = realm.where(PesananFood.class).findAll();

        int quantity = 0;
        long cost = 0;
        for (int p = 0; p < existingFood.size(); p++) {
            quantity += existingFood.get(p).getQty();
            cost += existingFood.get(p).getTotalHarga();
        }

        if(quantity > 0)
            panelprice.setVisibility(View.VISIBLE);
        else
            panelprice.setVisibility(View.GONE);

        qtyText.setText("" + quantity);
        qtyText1.setText(quantity+" Items");
        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(cost);
        String formattedText = String.format(Locale.US, "$ %s.00", formattedTotal);
        costText.setText(formattedText);
    }
}

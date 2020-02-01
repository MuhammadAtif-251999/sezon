package io.sezon.sezon.mFood;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import io.sezon.sezon.MangJekApplication;
import io.sezon.sezon.R;
import io.sezon.sezon.adapter.ItemOffsetDecoration;
import io.sezon.sezon.api.ServiceGenerator;
import io.sezon.sezon.api.service.BookService;
import io.sezon.sezon.mLaundry.LaundryActivity;
import io.sezon.sezon.model.DataRestoran;
import io.sezon.sezon.model.DataRestoranall;
import io.sezon.sezon.model.KategoriRestoran;
import io.sezon.sezon.model.PesananFood;
import io.sezon.sezon.model.PromosiMFood;
import io.sezon.sezon.model.Restoran;
import io.sezon.sezon.model.RestoranNearMeDB;
import io.sezon.sezon.model.User;
import io.sezon.sezon.model.json.book.GetDataRestoRequestJson;
import io.sezon.sezon.model.json.book.GetDataRestoResponseJson;
import io.sezon.sezon.model.json.book.GetDataRestoranRequestJson;
import io.sezon.sezon.model.json.book.GetDataRestoranResponseJson;
import io.sezon.sezon.utils.Log;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import io.realm.Realm;
import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import io.sezon.sezon.utils.SnackbarController;

public class FoodActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.btn_home)
    ImageView btnHome;

    @BindView(R.id.food_search)
    LinearLayout foodSearch;

    @BindView(R.id.food_nearme)
    RelativeLayout foodNearme;

    @BindView(R.id.food_explore)
    RelativeLayout foodExplore;

    @BindView(R.id.text_explore)
    TextView textExplore;

    @BindView(R.id.slide_viewPager)
    AutoScrollViewPager autoScrollViewPager;

    @BindView(R.id.slide_viewPager_indicator)
    CircleIndicator circleIndicator;

    @BindView(R.id.kategori_recycler)
    RecyclerView kategoriRecyler;

    @BindView(R.id.nearme_recycler1)
    RecyclerView nearmeRecycler;

    @BindView(R.id.nearme_noRecord)
    TextView noRecord;

    @BindView(R.id.nearme_progress)
    ProgressBar progress;

    public static final String FITUR_KEY = "FiturKey";
    private static final int REQUEST_PERMISSION_LOCATION = 991;
    private GoogleApiClient googleApiClient;

    private Realm realm;
    private Realm realm1;
    private Location lastKnownLocation;
    private List<KategoriRestoran> kategoriRestoran;
    private FastItemAdapter<KategoriItem> kategoriAdapter;
    private boolean requestUpdate = true;
    private SnackbarController snackbarController;

    private List<Restoran> laundryAll;
    private List<RestoranNearMeDB> laundryByLocation;
    private FastItemAdapter<RestoranItem> adapterListLaundry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);
        ButterKnife.bind(this);



        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .build();
        }

        realm = Realm.getDefaultInstance();
        realm1 = Realm.getDefaultInstance();
        kategoriAdapter = new FastItemAdapter<>();

        foodNearme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodActivity.this, NearmeActivity.class);
                startActivity(intent);
            }
        });

        foodSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodActivity.this, SearchRestoranActivity.class);
                FoodActivity.this.startActivity(intent);
            }
        });

      //  showProgress();
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        requestUpdate = true;
        adapterListLaundry = new FastItemAdapter<>();

        realm.beginTransaction();
        realm.delete(PesananFood.class);
        realm.commitTransaction();

        getDataLaundry();
        resotall();






    }

    private void resotall(){
        nearmeRecycler.setLayoutManager(new LinearLayoutManager(this));
        nearmeRecycler.setAdapter(adapterListLaundry);
        adapterListLaundry.withOnClickListener(new FastAdapter.OnClickListener<RestoranItem>() {
            @Override
            public boolean onClick(View v, IAdapter<RestoranItem> adapter, RestoranItem item, int position) {
                Log.e("BUTTON", "CLICKED");
                Intent intent = new Intent(FoodActivity.this, FoodMenuActivity.class);
                intent.putExtra(FoodMenuActivity.ID_RESTO, item.id);
                intent.putExtra(FoodMenuActivity.NAMA_RESTO, item.namaResto);
                intent.putExtra(FoodMenuActivity.ALAMAT_RESTO, item.alamat);
                intent.putExtra(FoodMenuActivity.DISTANCE_RESTO, item.distance);
                intent.putExtra(FoodMenuActivity.JAM_BUKA, item.jamBuka);
                intent.putExtra(FoodMenuActivity.JAM_TUTUP, item.jamTutup);
                intent.putExtra(FoodMenuActivity.IS_OPEN, item.isOpen);
                intent.putExtra(FoodMenuActivity.PICTURE_URL, item.pictureUrl);
                intent.putExtra(FoodMenuActivity.IS_MITRA, item.isMitra);
                intent.putExtra(FoodMenuActivity.KONTAK, item.kontak);
                startActivity(intent);


                return true;
            }
        });
    }
    private void getDataLaundry() {
        if (lastKnownLocation != null) {
            User loginUser = MangJekApplication.getInstance(this).getLoginUser();
            BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
            GetDataRestoranRequestJson param = new GetDataRestoranRequestJson();
            param.setLatitude(lastKnownLocation.getLatitude());
            param.setLongitude(lastKnownLocation.getLongitude());

            service.getDataRestoran1(param).enqueue(new Callback<GetDataRestoranResponseJson>() {
                @Override
                public void onResponse(Call<GetDataRestoranResponseJson> call, Response<GetDataRestoranResponseJson> response) {
                    if (response.isSuccessful()) {
                        DataRestoranall dataLaundry = response.body().getDataLaundry();
                        laundryAll = dataLaundry.getLaundryAll();
                        laundryByLocation = dataLaundry.getLaundryList();
                        Log.d(LaundryActivity.class.getSimpleName(), "Number of laundry: " + laundryAll.size());
                        Log.d(Restoran.class.getSimpleName(), "Number of laundry by location: " + laundryByLocation.size());
                        Realm realm = MangJekApplication.getInstance(FoodActivity.this).getRealmInstance();
                        realm.beginTransaction();
                        realm.delete(Restoran.class);
                        realm.copyToRealm(laundryAll);
                        realm.commitTransaction();

                        realm.beginTransaction();
                        realm.delete(RestoranNearMeDB.class);
                        realm.copyToRealm(laundryByLocation);
                        realm.commitTransaction();
                        adapterListLaundry.clear();
                        RestoranItem laundryItem;
                        for (int i = 0; i < laundryAll.size(); i++) {
                            laundryItem = new RestoranItem(FoodActivity.this);
                            laundryItem.id = laundryAll.get(i).getId();
                            laundryItem.namaResto = laundryAll.get(i).getNamaResto();
                            laundryItem.alamat = laundryAll.get(i).getAlamat();
                            laundryItem.distance = laundryAll.get(i).getDistance();
                            laundryItem.jamBuka = laundryAll.get(i).getJamBuka();
                            laundryItem.jamTutup = laundryAll.get(i).getJamTutup();
                            laundryItem.fotoResto = laundryAll.get(i).getFotoResto();
                            laundryItem.isOpen = laundryAll.get(i).isOpen();
                            laundryItem.pictureUrl = laundryAll.get(i).getFotoResto();
                            laundryItem.isMitra = laundryAll.get(i).isPartner();
                            adapterListLaundry.add(laundryItem);
                            Log.e("LAUNDRY", laundryItem.namaResto + "");
                            Log.e("LAUNDRY", laundryItem.alamat + "");
                            Log.e("LAUNDRY", laundryItem.jamBuka + "");
                            Log.e("LAUNDRY", laundryItem.jamTutup + "");
                            Log.e("LAUNDRY", laundryItem.fotoResto + "");

                        }

                    }
                }

                @Override
                public void onFailure(Call<GetDataRestoranResponseJson> call, Throwable t) {
//                    Toast.makeText(getApplicationContext(), "Connection to server lost, check your internet connection.",
//                            Toast.LENGTH_SHORT).show();
                    Log.e("kesalahan",t+"");
                }
            });
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
    public void onConnected(@Nullable Bundle bundle) {
        updateLastLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void updateLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }
        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if(lastKnownLocation != null) {
            if(requestUpdate){
                getDataRestoran();
                getDataLaundry();
                KategoriRecycler();
                resotall();
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

    private void KategoriRecycler() {
        kategoriRecyler.setLayoutManager(new LinearLayoutManager(FoodActivity.this, LinearLayout.HORIZONTAL,false));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);
        kategoriRecyler.addItemDecoration(itemDecoration);
        kategoriRecyler.setNestedScrollingEnabled(false);
        kategoriRecyler.setAdapter(kategoriAdapter);
        kategoriAdapter.withOnClickListener(new FastAdapter.OnClickListener<KategoriItem>() {
            @Override
            public boolean onClick(View v, IAdapter<KategoriItem> adapter, KategoriItem item, int position) {
                Log.e("BUTTON", "CLICKED");
                Intent intent = new Intent(FoodActivity.this, KategoriSelectActivity.class);
                intent.putExtra(KategoriSelectActivity.KATEGORI_ID, String.valueOf(item.idKategori));
                startActivity(intent);
                return true;
            }
        });
    }


    private void getDataRestoran() {
        if (lastKnownLocation != null) {
            Log.d("Location : ","" + lastKnownLocation.getLatitude() + " " + lastKnownLocation.getLongitude());
            User loginUser = MangJekApplication.getInstance(this).getLoginUser();
            BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
            GetDataRestoRequestJson param = new GetDataRestoRequestJson();
            param.setLatitude(lastKnownLocation.getLatitude());
            param.setLongitude(lastKnownLocation.getLongitude());

            service.getDataRestoran(param).enqueue(new Callback<GetDataRestoResponseJson>() {
                @Override
                public void onResponse(Call<GetDataRestoResponseJson> call, Response<GetDataRestoResponseJson> response) {
                    if (response.isSuccessful()) {
                        DataRestoran dataRestoran = response.body().getDataRestoran();
                        kategoriRestoran = dataRestoran.getKategoriRestoranList();
                        laundryByLocation = dataRestoran.getRestoranList();
                        Realm realm = MangJekApplication.getInstance(FoodActivity.this).getRealmInstance();
                        realm.beginTransaction();
                        realm.delete(KategoriRestoran.class);
                        realm.copyToRealm(kategoriRestoran);
                        realm.commitTransaction();

                        realm.beginTransaction();
                        realm.delete(RestoranNearMeDB.class);
                        realm.copyToRealm(laundryByLocation);
                        realm.commitTransaction();

                        kategoriAdapter.clear();
                        KategoriItem kategoriItem;
                        for (KategoriRestoran kategori : kategoriRestoran) {
                            kategoriItem = new KategoriItem(FoodActivity.this);
                            kategoriItem.idKategori = kategori.getIdKategori();
                            kategoriItem.kategori = kategori.getKategori();
                            kategoriItem.image = kategori.getFotoKategori();
                            kategoriAdapter.add(kategoriItem);
                            Log.e("ADD KATEGORI", kategori.getIdKategori() + "");
                        }

                        List<PromosiMFood> promosiMFoods = dataRestoran.getPromosiMFood();
                        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), promosiMFoods);
                        autoScrollViewPager.setAdapter(pagerAdapter);
                        circleIndicator.setViewPager(autoScrollViewPager);
                        autoScrollViewPager.setInterval(20000);
                        autoScrollViewPager.startAutoScroll(20000);
                    }
                }

                @Override
                public void onFailure(Call<GetDataRestoResponseJson> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Connection to server lost, check your internet connection.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 5;
        public List<PromosiMFood> banners = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fragmentManager, List<PromosiMFood> banners) {
            super(fragmentManager);
            this.banners = banners;
        }

        @Override
        public int getCount() {
            return banners.size();
        }

        @Override
        public Fragment getItem(int position) {
            return SlideRestoFragment.newInstance(banners.get(position).getId(),
                    banners.get(position).getFoto(),
                    banners.get(position).getIdResto());

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }
    private void showRecycler() {
        nearmeRecycler.setVisibility(View.VISIBLE);
        noRecord.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
    }

    private void showNoRecord() {
        nearmeRecycler.setVisibility(View.GONE);
        noRecord.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
    }

    private void showProgress() {
        nearmeRecycler.setVisibility(View.GONE);
        noRecord.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
        realm1.close();
    }

    @Override
    protected void onPause() {
        super.onPause();
        requestUpdate = false;
    }
}

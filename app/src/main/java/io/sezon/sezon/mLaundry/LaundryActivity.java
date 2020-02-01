package io.sezon.sezon.mLaundry;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import io.sezon.sezon.MangJekApplication;
import io.sezon.sezon.R;
import io.sezon.sezon.api.ServiceGenerator;
import io.sezon.sezon.api.service.BookService;
import io.sezon.sezon.model.DataLaundry;
import io.sezon.sezon.model.Laundry;
import io.sezon.sezon.model.LaundryNearMeDB;
import io.sezon.sezon.model.PromosiMLaundry;
import io.sezon.sezon.model.User;
import io.sezon.sezon.model.json.book.GetDataLaundryRequestJson;
import io.sezon.sezon.model.json.book.GetDataLaundryResponseJson;
import io.sezon.sezon.utils.Log;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LaundryActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.btn_home)
    ImageView btnHome;

    @BindView(R.id.laundry_search)
    LinearLayout laundrySearch;

    @BindView(R.id.laundry_nearme)
    RelativeLayout laundryNearme;

    @BindView(R.id.laundry_explore)
    RelativeLayout foodExplore;

    @BindView(R.id.text_explore)
    TextView textExplore;




    @BindView(R.id.listLaundry_recycler)
    RecyclerView daftarLaundry;

    public static final String FITUR_KEY = "FiturKey";
    private static final int REQUEST_PERMISSION_LOCATION = 991;
    private GoogleApiClient googleApiClient;
    private Location lastKnownLocation;
    private Realm realm;

    private List<Laundry> laundryAll;
    private List<LaundryNearMeDB> laundryByLocation;

    private FastItemAdapter<LaundryItem> adapterListLaundry;
    private boolean requestUpdate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry);
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
        adapterListLaundry = new FastItemAdapter<>();

        laundryNearme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaundryActivity.this, NearmeLaundryActivity.class);
                startActivity(intent);
            }
        });

        laundrySearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaundryActivity.this, SearchLaundryActivity.class);
                LaundryActivity.this.startActivity(intent);
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        requestUpdate = true;
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
                getDataLaundry();
                DaftarLaundryRecycler();
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

    private void DaftarLaundryRecycler() {

        daftarLaundry.setLayoutManager(new LinearLayoutManager(this));
        daftarLaundry.setAdapter(adapterListLaundry);
        adapterListLaundry.withOnClickListener(new FastAdapter.OnClickListener<LaundryItem>() {
            @Override
            public boolean onClick(View v, IAdapter<LaundryItem> adapter, LaundryItem item, int position) {
                Log.e("BUTTON", "CLICKED");
                Intent intent = new Intent(LaundryActivity.this, LaundryMenuActivity.class);
                intent.putExtra(LaundryMenuActivity.ID_LAUNDRY, item.id);
                intent.putExtra(LaundryMenuActivity.NAMA_LAUNDRY, item.namaLaundry);
                intent.putExtra(LaundryMenuActivity.ALAMAT_LAUNDRY, item.alamat);
                intent.putExtra(LaundryMenuActivity.DISTANCE_LAUNDRY, item.distance);
                intent.putExtra(LaundryMenuActivity.JAM_BUKA, item.jamBuka);
                intent.putExtra(LaundryMenuActivity.JAM_TUTUP, item.jamTutup);
                intent.putExtra(LaundryMenuActivity.IS_OPEN, item.isOpen);
                intent.putExtra(LaundryMenuActivity.PICTURE_URL, item.pictureUrl);
                intent.putExtra(LaundryMenuActivity.IS_MITRA, item.isMitra);
                startActivity(intent);
                return true;
            }
        });




    }

    private void getDataLaundry() {
        if (lastKnownLocation != null) {
            User loginUser = MangJekApplication.getInstance(this).getLoginUser();
            BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
            GetDataLaundryRequestJson param = new GetDataLaundryRequestJson();
            param.setLatitude(lastKnownLocation.getLatitude());
            param.setLongitude(lastKnownLocation.getLongitude());

            service.getDataLaundry(param).enqueue(new Callback<GetDataLaundryResponseJson>() {
                @Override
                public void onResponse(Call<GetDataLaundryResponseJson> call, Response<GetDataLaundryResponseJson> response) {
                    if (response.isSuccessful()) {
                        DataLaundry dataLaundry = response.body().getDataLaundry();
                        laundryAll = dataLaundry.getLaundryAll();
                        laundryByLocation = dataLaundry.getLaundryList();
                        Log.d(LaundryActivity.class.getSimpleName(), "Number of laundry: " + laundryAll.size());
                        Log.d(Laundry.class.getSimpleName(), "Number of laundry by location: " + laundryByLocation.size());
                        Realm realm = MangJekApplication.getInstance(LaundryActivity.this).getRealmInstance();
                        realm.beginTransaction();
                        realm.delete(Laundry.class);
                        realm.copyToRealm(laundryAll);
                        realm.commitTransaction();

                        realm.beginTransaction();
                        realm.delete(LaundryNearMeDB.class);
                        realm.copyToRealm(laundryByLocation);
                        realm.commitTransaction();
                        adapterListLaundry.clear();
                        LaundryItem laundryItem;
                        for (int i = 0; i < laundryAll.size(); i++) {
                            laundryItem = new LaundryItem(LaundryActivity.this);
                            laundryItem.id = laundryAll.get(i).getId();
                            laundryItem.namaLaundry = laundryAll.get(i).getNamaLaundry();
                            laundryItem.alamat = laundryAll.get(i).getAlamat();
                            laundryItem.distance = laundryAll.get(i).getDistance();
                            laundryItem.jamBuka = laundryAll.get(i).getJamBuka();
                            laundryItem.jamTutup = laundryAll.get(i).getJamTutup();
                            laundryItem.fotoLaundry = laundryAll.get(i).getFotoLaundry();
                            laundryItem.isOpen = laundryAll.get(i).isOpen();
                            laundryItem.pictureUrl = laundryAll.get(i).getFotoLaundry();
                            laundryItem.isMitra = laundryAll.get(i).isPartner();
                            adapterListLaundry.add(laundryItem);
                            Log.e("LAUNDRY", laundryItem.namaLaundry + "");
                            Log.e("LAUNDRY", laundryItem.alamat + "");
                            Log.e("LAUNDRY", laundryItem.jamBuka + "");
                            Log.e("LAUNDRY", laundryItem.jamTutup + "");
                            Log.e("LAUNDRY", laundryItem.fotoLaundry + "");

                        }
                        List<PromosiMLaundry> promosiMLaundry = dataLaundry.getPromosiMLaundry();
//                        Toast.makeText(FoodActivity.this, "size = "+promosiMFoods.size(), Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onFailure(Call<GetDataLaundryResponseJson> call, Throwable t) {
//                    Toast.makeText(getApplicationContext(), "Connection to server lost, check your internet connection.",
//                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 5;
        public List<PromosiMLaundry> banners = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fragmentManager, List<PromosiMLaundry> banners) {
            super(fragmentManager);
            this.banners = banners;
        }

        @Override
        public int getCount() {
            return banners.size();
        }

        @Override
        public Fragment getItem(int position) {
            return SlideLaundryFragment.newInstance(banners.get(position).getId(),
                    banners.get(position).getFoto(),
                    banners.get(position).getIdLaundry());
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    protected void onPause() {
        super.onPause();
        requestUpdate = false;
    }
}

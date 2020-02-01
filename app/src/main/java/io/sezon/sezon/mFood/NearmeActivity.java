package io.sezon.sezon.mFood;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import java.util.Calendar;

import io.sezon.sezon.R;
import io.sezon.sezon.model.PesananFood;
import io.sezon.sezon.model.RestoranNearMeDB;
import io.sezon.sezon.utils.Log;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class NearmeActivity extends AppCompatActivity {

    @BindView(R.id.btn_home)
    ImageView btnHome;



    @BindView(R.id.gambar1)
    ImageView imgHari;

    @BindView(R.id.food_search)
    LinearLayout foodSearch;

    @BindView(R.id.nearme_recycler)
    RecyclerView nearmeRecycler;

    @BindView(R.id.nearme_noRecord)
    TextView noRecord;

    @BindView(R.id.text_ucap1)
    TextView selamat;

    Context context;
    @BindView(R.id.nearme_progress)
    ProgressBar progress;
    @BindView(R.id.bg_nearme)
    ProgressBar background;
    private Realm realm;
    public String image;

    private RealmResults<RestoranNearMeDB> restoranRealmResults;
    private FastItemAdapter<RestoranItem> restoranAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearme);
        ButterKnife.bind(this);


        ucapan();
        showRecycler();
        showNoRecord();

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        realm = Realm.getDefaultInstance();

        foodSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NearmeActivity.this, SearchRestoranActivity.class);
                NearmeActivity.this.startActivity(intent);
            }
        });

        restoranAdapter = new FastItemAdapter<>();
        nearmeRecycler.setLayoutManager(new LinearLayoutManager(this));
        nearmeRecycler.setAdapter(restoranAdapter);
        restoranAdapter.withOnClickListener(new FastAdapter.OnClickListener<RestoranItem>() {
            @Override
            public boolean onClick(View v, IAdapter<RestoranItem> adapter, RestoranItem item, int position) {
                Log.e("BUTTON", "CLICKED");
                RestoranNearMeDB selectedResto = realm.where(RestoranNearMeDB.class).
                        equalTo("id", restoranAdapter.getAdapterItem(position).id).findFirst();
                Intent intent = new Intent(NearmeActivity.this, FoodMenuActivity.class);
                intent.putExtra(FoodMenuActivity.ID_RESTO, selectedResto.getId());
                intent.putExtra(FoodMenuActivity.NAMA_RESTO, selectedResto.getNamaResto());
                intent.putExtra(FoodMenuActivity.ALAMAT_RESTO, selectedResto.getAlamat());
                intent.putExtra(FoodMenuActivity.DISTANCE_RESTO, selectedResto.getDistance());
                intent.putExtra(FoodMenuActivity.JAM_BUKA, selectedResto.getJamBuka());
                intent.putExtra(FoodMenuActivity.JAM_TUTUP, selectedResto.getJamTutup());
                intent.putExtra(FoodMenuActivity.IS_OPEN, selectedResto.isOpen());
                intent.putExtra(FoodMenuActivity.KONTAK, selectedResto.getKontakTelepon());
                intent.putExtra(FoodMenuActivity.PICTURE_URL, selectedResto.getFotoResto());
                intent.putExtra(FoodMenuActivity.IS_MITRA, selectedResto.isPartner());
                startActivity(intent);
                return true;
            }
        });

        //TODO: Status restoran kerja sama dengan m-food, jam buka - tutup
        restoranRealmResults = realm.where(RestoranNearMeDB.class).findAll();
        RestoranItem restoranItem;
        for (int i = 0; i < restoranRealmResults.size(); i++) {
            restoranItem = new RestoranItem(NearmeActivity.this);
            restoranItem.id = restoranRealmResults.get(i).getId();
            restoranItem.namaResto = restoranRealmResults.get(i).getNamaResto();
            restoranItem.alamat = restoranRealmResults.get(i).getAlamat();
            restoranItem.distance = restoranRealmResults.get(i).getDistance();
            restoranItem.jamBuka = restoranRealmResults.get(i).getJamBuka();
            restoranItem.jamTutup = restoranRealmResults.get(i).getJamTutup();
            restoranItem.fotoResto = restoranRealmResults.get(i).getFotoResto();
            restoranItem.isOpen = restoranRealmResults.get(i).isOpen();
            restoranItem.kontak = restoranRealmResults.get(i).getKontakTelepon();
            restoranItem.pictureUrl = restoranRealmResults.get(i).getFotoResto();
            restoranItem.isMitra = restoranRealmResults.get(i).isPartner();
            restoranAdapter.add(restoranItem);
            Log.e("RESTO", restoranItem.namaResto + "");
            Log.e("RESTO", restoranItem.alamat + "");
            Log.e("RESTO", restoranItem.jamBuka + "");
            Log.e("RESTO", restoranItem.jamTutup + "");
            Log.e("RESTO", restoranItem.fotoResto + "");
        }



    }

    @Override
    protected void onResume() {
        super.onResume();
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(PesananFood.class);
        realm.commitTransaction();
    }

    private void ucapan(){

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay >= 0 && timeOfDay < 12){
            selamat.setText("Good Morning");
            selamat.setTextColor(this.getResources().getColor(R.color.white));
            imgHari.setImageResource(R.drawable.resto_siang);
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            selamat.setText("Good Day");
            selamat.setTextColor(this.getResources().getColor(R.color.white));
            imgHari.setImageResource(R.drawable.resto_pagi);
        }else if(timeOfDay >= 16 && timeOfDay < 21){
            selamat.setText("Good Faternoon " );
            selamat.setTextColor(this.getResources().getColor(R.color.white));
            imgHari.setImageResource(R.drawable.resto_sore);
        }else if(timeOfDay >= 21 && timeOfDay < 24){
            selamat.setText("Good Night");
            selamat.setTextColor(this.getResources().getColor(R.color.white));
            imgHari.setImageResource(R.drawable.resto_malam);
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
    }
}

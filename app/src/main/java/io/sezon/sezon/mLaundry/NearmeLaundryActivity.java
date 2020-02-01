package io.sezon.sezon.mLaundry;

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

import io.sezon.sezon.R;
import io.sezon.sezon.model.LaundryNearMeDB;
import io.sezon.sezon.model.PesananLaundry;
import io.sezon.sezon.utils.Log;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class NearmeLaundryActivity extends AppCompatActivity {

    @BindView(R.id.btn_home)
    ImageView btnHome;

    @BindView(R.id.laundry_search)
    LinearLayout laundrySearch;

    @BindView(R.id.nearme_recycler)
    RecyclerView nearmeRecycler;

    @BindView(R.id.nearme_noRecord)
    TextView noRecord;

    @BindView(R.id.nearme_progress)
    ProgressBar progress;

    private Realm realm;
    private RealmResults<LaundryNearMeDB> laundryRealmResults;
    private FastItemAdapter<LaundryItem> laundryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearme_laundry);
        ButterKnife.bind(this);

        showRecycler();
        showNoRecord();
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        realm = Realm.getDefaultInstance();

        laundrySearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NearmeLaundryActivity.this, SearchLaundryActivity.class);
                NearmeLaundryActivity.this.startActivity(intent);
            }
        });

        laundryAdapter = new FastItemAdapter<>();
        nearmeRecycler.setLayoutManager(new LinearLayoutManager(this));
        nearmeRecycler.setAdapter(laundryAdapter);
        laundryAdapter.withOnClickListener(new FastAdapter.OnClickListener<LaundryItem>() {
            @Override
            public boolean onClick(View v, IAdapter<LaundryItem> adapter, LaundryItem item, int position) {
                Log.e("BUTTON", "CLICKED");
                LaundryNearMeDB selectedResto = realm.where(LaundryNearMeDB.class).
                        equalTo("id", laundryAdapter.getAdapterItem(position).id).findFirst();
                Intent intent = new Intent(NearmeLaundryActivity.this, LaundryMenuActivity.class);
                intent.putExtra(LaundryMenuActivity.ID_LAUNDRY, selectedResto.getId());
                intent.putExtra(LaundryMenuActivity.NAMA_LAUNDRY, selectedResto.getNamaLaundry());
                intent.putExtra(LaundryMenuActivity.ALAMAT_LAUNDRY, selectedResto.getAlamat());
                intent.putExtra(LaundryMenuActivity.DISTANCE_LAUNDRY, selectedResto.getDistance());
                intent.putExtra(LaundryMenuActivity.JAM_BUKA, selectedResto.getJamBuka());
                intent.putExtra(LaundryMenuActivity.JAM_TUTUP, selectedResto.getJamTutup());
                intent.putExtra(LaundryMenuActivity.IS_OPEN, selectedResto.isOpen());
                intent.putExtra(LaundryMenuActivity.PICTURE_URL, selectedResto.getFotoLaundry());
                intent.putExtra(LaundryMenuActivity.IS_MITRA, selectedResto.isPartner());
                startActivity(intent);
                return true;
            }
        });

        //TODO: Status restoran kerja sama dengan m-food, jam buka - tutup
        laundryRealmResults = realm.where(LaundryNearMeDB.class).findAll();
        LaundryItem restoranItem;
        for (int i = 0; i < laundryRealmResults.size(); i++) {
            restoranItem = new LaundryItem(NearmeLaundryActivity.this);
            restoranItem.id = laundryRealmResults.get(i).getId();
            restoranItem.namaLaundry = laundryRealmResults.get(i).getNamaLaundry();
            restoranItem.alamat = laundryRealmResults.get(i).getAlamat();
            restoranItem.distance = laundryRealmResults.get(i).getDistance();
            restoranItem.jamBuka = laundryRealmResults.get(i).getJamBuka();
            restoranItem.jamTutup = laundryRealmResults.get(i).getJamTutup();
            restoranItem.fotoLaundry = laundryRealmResults.get(i).getFotoLaundry();
            restoranItem.isOpen = laundryRealmResults.get(i).isOpen();
            restoranItem.pictureUrl = laundryRealmResults.get(i).getFotoLaundry();
            restoranItem.isMitra = laundryRealmResults.get(i).isPartner();
            laundryAdapter.add(restoranItem);
            Log.e("LAUNDRY", restoranItem.namaLaundry + "");
            Log.e("LAUNDRY", restoranItem.alamat + "");
            Log.e("LAUNDRY", restoranItem.jamBuka + "");
            Log.e("LAUNDRY", restoranItem.jamTutup + "");
            Log.e("LAUNDRY", restoranItem.fotoLaundry + "");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(PesananLaundry.class);
        realm.commitTransaction();
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

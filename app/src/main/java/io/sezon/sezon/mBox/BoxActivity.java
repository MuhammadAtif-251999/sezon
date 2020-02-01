package io.sezon.sezon.mBox;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import io.sezon.sezon.MangJekApplication;
import io.sezon.sezon.R;
import io.sezon.sezon.api.ServiceGenerator;
import io.sezon.sezon.api.service.BookService;
import io.sezon.sezon.model.KendaraanAngkut;
import io.sezon.sezon.model.User;
import io.sezon.sezon.model.json.book.GetKendaraanAngkutResponseJson;
import io.sezon.sezon.utils.Log;

public class BoxActivity extends AppCompatActivity {

//    @BindView(R.cargoId.button_pickup)
//    LinearLayout buttonPickup;
//
//    @BindView(R.cargoId.button_truck)
//    LinearLayout buttonTruck;

    @BindView(R.id.cargo_type_recyclerView)
    RecyclerView cargoTypeRecyclerView;

    public static final String FITUR_KEY = "FiturKey";
    public static final String CARGO = "cargo";

    private Realm realm;
    int featureId;
    FastItemAdapter<CargoItemActivity> itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mbox);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        featureId = intent.getIntExtra(FITUR_KEY, -1);

        realm = Realm.getDefaultInstance();
        LoadKendaraan();
        itemAdapter = new FastItemAdapter<>();
        cargoTypeRecyclerView.setLayoutManager(new GridLayoutManager(BoxActivity.this,2));
        cargoTypeRecyclerView.setAdapter(itemAdapter);

        itemAdapter.withItemEvent(new ClickEventHook<CargoItemActivity>() {
            @Nullable
            @Override
            public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof CargoItemActivity.ViewHolder) {
                    return ((CargoItemActivity.ViewHolder) viewHolder).itemView;
                }
                return null;
            }

            @Override
            public void onClick(View v, int position, FastAdapter<CargoItemActivity> fastAdapter, CargoItemActivity item) {
                KendaraanAngkut selectedCargo = realm.where(KendaraanAngkut.class).equalTo("idKendaraan", itemAdapter.getAdapterItem(position).id).findFirst();
                Log.e("BUTTON","CLICKED, ID : "+selectedCargo.getIdKendaraan());
                Intent intent = new Intent(BoxActivity.this, BoxOrder.class);
                intent.putExtra(BoxOrder.FITUR_KEY, featureId);
                intent.putExtra(BoxOrder.KENDARAAN_KEY, selectedCargo.getIdKendaraan());
                startActivity(intent);
            }
        });


    }

    private void LoadKendaraan() {
        User loginUser = MangJekApplication.getInstance(this).getLoginUser();
        BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
        service.getKendaraanAngkut().enqueue(new Callback<GetKendaraanAngkutResponseJson>() {
            @Override
            public void onResponse(Call<GetKendaraanAngkutResponseJson> call, Response<GetKendaraanAngkutResponseJson> response) {
                if(response.isSuccessful()) {

                    Realm realm = MangJekApplication.getInstance(BoxActivity.this).getRealmInstance();
                    realm.beginTransaction();
                    realm.delete(KendaraanAngkut.class);
                    realm.copyToRealm(response.body().getData());
                    realm.commitTransaction();


                    CargoItemActivity cargoItem;
                    for (KendaraanAngkut cargo:response.body().getData() ){
                        cargoItem = new CargoItemActivity(BoxActivity.this);
                        cargoItem.featureId = featureId;
                        cargoItem.id = cargo.getIdKendaraan();
                        cargoItem.type = cargo.getKendaraanAngkut();
                        cargoItem.price = cargo.getHarga();
                        cargoItem.image = cargo.getFotoKendaraan();
                        cargoItem.dimension = cargo.getDimensiKendaraan();
                        cargoItem.maxWeight = cargo.getMaxweightKendaraan();
                        itemAdapter.add(cargoItem);
                        Log.e("ADD CARGO", cargo.getIdKendaraan()+"");
                    }
                }
            }

            @Override
            public void onFailure(Call<GetKendaraanAngkutResponseJson> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}


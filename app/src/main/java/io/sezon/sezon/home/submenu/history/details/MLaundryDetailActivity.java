package io.sezon.sezon.home.submenu.history.details;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import io.sezon.sezon.MangJekApplication;
import io.sezon.sezon.R;
import io.sezon.sezon.api.ServiceGenerator;
import io.sezon.sezon.api.service.BookService;
import io.sezon.sezon.home.submenu.history.details.items.MLaundryItem;
import io.sezon.sezon.model.MLaundryDetailTransaksi;
import io.sezon.sezon.model.MLaundryItemRemote;
import io.sezon.sezon.model.User;
import io.sezon.sezon.model.json.book.detailTransaksi.GetDataTransaksiMLaundryResponse;
import io.sezon.sezon.model.json.book.detailTransaksi.GetDataTransaksiRequest;
import io.sezon.sezon.utils.Log;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static io.sezon.sezon.home.submenu.history.details.MFoodDetailStrukActivity.HARGA_AKHIR;

/**
 * Created by fathony on 24/02/2017.
 */

public class MLaundryDetailActivity extends AppCompatActivity {

    public static final String ID_TRANSAKSI = "IDTransaksi";

    @BindView(R.id.title_detail)
    TextView title;
    @BindView(R.id.mLaundryDetail_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.mLaundryDetail_total)
    TextView totalField;
    @BindView(R.id.button_struk)
    Button foto;

    FastItemAdapter<MLaundryItem> adapter;

    private String idTransaksi, fotoStruk, detailFitur;
    private long hargaAkhir;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mlaundry_detail);
        ButterKnife.bind(this);

        idTransaksi = getIntent().getStringExtra(ID_TRANSAKSI);
        Realm realm = MangJekApplication.getInstance(this).getRealmInstance();
        User loginUser = realm.copyFromRealm(MangJekApplication.getInstance(this).getLoginUser());
        BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());


        title.setText("Grocery Billing");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FastItemAdapter<>();
        recyclerView.setAdapter(adapter);

        GetDataTransaksiRequest param = new GetDataTransaksiRequest();
        param.setIdTransaksi(idTransaksi);
        service.getDataTransaksiMLaundryDetail(param).enqueue(new Callback<GetDataTransaksiMLaundryResponse>() {
            @Override
            public void onResponse(Call<GetDataTransaksiMLaundryResponse> call, Response<GetDataTransaksiMLaundryResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getDataTransaksi().isEmpty()) {
                        onFailure(call, new Exception());
                    } else {
                        MLaundryDetailTransaksi detail = response.body().getDataTransaksi().get(0);
                        fotoStruk =  response.body().getDataTransaksi().get(0).getFotoStruk();
                        detailFitur = response.body().getDataTransaksi().get(0).getOrderFitur();
                        updateUI(detail, response.body().getListBarang());
                    }
                } else {
                    onFailure(call, new Exception());
                }
            }

            @Override
            public void onFailure(Call<GetDataTransaksiMLaundryResponse> call, Throwable t) {
                Toast.makeText(MLaundryDetailActivity.this, "Silahkan coba lagi lain waktu.", Toast.LENGTH_SHORT).show();
            }
        });

        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent struk = new Intent (MLaundryDetailActivity.this, MFoodDetailStrukActivity.class);
                struk.putExtra(MFoodDetailStrukActivity.URL_FOTO, fotoStruk);
                struk.putExtra(HARGA_AKHIR, hargaAkhir);
                struk.putExtra(MFoodDetailStrukActivity.ORDER_FITUR, detailFitur);


                Log.d("FOTO : "," "+ fotoStruk);
                Log.d("HARGA AKHIR : "," "+ hargaAkhir);

                startActivity(struk);
            }
        });

    }

    private void updateUI(MLaundryDetailTransaksi transaksi, List<MLaundryItemRemote> items) {
        List<MLaundryItem> mMartItems = new ArrayList<>();
        for (MLaundryItemRemote item : items) {
            mMartItems.add(new MLaundryItem(item.getNamaBarang(), item.getJumlah(), item.getCatatan()));
        }
        adapter.clear();
        adapter.set(mMartItems);
        adapter.notifyDataSetChanged();
        String total = "Total : " + String.format(Locale.US, "Rp. %s ,-",
//                NumberFormat.getNumberInstance(Locale.US).format(transaksi.getEstimasiBiaya()));
                NumberFormat.getNumberInstance(Locale.US).format(transaksi.getTotalBiaya()));
        hargaAkhir = transaksi.getHargaAkhir();
        totalField.setText(total);
    }
}

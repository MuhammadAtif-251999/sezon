package io.sezon.sezon.home.submenu.history.details;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import io.sezon.sezon.MangJekApplication;
import io.sezon.sezon.R;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by fathony on 24/02/2017.
 */

public class MFoodDetailStrukActivity extends AppCompatActivity {

    public static final String URL_FOTO = "foto";
    public static final String HARGA_AKHIR = "hargaAkhir";
    public static final String ORDER_FITUR = "fitur";



    @BindView(R.id.title_detail)
    TextView title;
    @BindView(R.id.foto_struk)
    ImageView fotoStruk;
    @BindView(R.id.mFoodDetail_total)
    TextView totalField;


    private String urlFoto, orderFitur;
    private  long  harga;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mfood_detail_struk);
        ButterKnife.bind(this);

        urlFoto = getIntent().getStringExtra(URL_FOTO);
        harga =  getIntent().getLongExtra(HARGA_AKHIR,0);
        orderFitur = getIntent().getStringExtra(ORDER_FITUR);

        Glide.with(this)
                .load(urlFoto)
                .centerCrop()
                .error(R.drawable.ic_kamera)
                .into(fotoStruk);
        Realm realm = MangJekApplication.getInstance(this).getRealmInstance();

        if(orderFitur.equalsIgnoreCase("3")){
            title.setText("Detail Sturk Pembelian");

        }else if (orderFitur.equalsIgnoreCase("9")){
            title.setText("Detail Sturk Pembelian");

        }else if (orderFitur.equalsIgnoreCase("10")){
            title.setText("Detail Sturk Pembelian");

        }

        String total = "Total : " + String.format(Locale.US, "Rp. %s ,-",
//                NumberFormat.getNumberInstance(Locale.US).format(transaksi.getEstimasiBiaya()));
                NumberFormat.getNumberInstance(Locale.US).format(harga));


        totalField.setText(total);
    }


}

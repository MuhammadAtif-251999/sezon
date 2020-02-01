package io.sezon.sezon.mFood;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.sezon.sezon.R;

/**
 * Created by Afif on 12/28/2016.
 */

public class KategoriItemHome extends AbstractItem<KategoriItemHome, KategoriItemHome.ViewHolder> {

    Context context;
    public int idKategori;
    public String kategori;
    public String image;

    public KategoriItemHome(Context context) {
        this.context = context;
    }

    @Override
    public int getType() {
        return R.id.kategori_itemhome;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        holder.kategoriName.setText(kategori);
        Glide.with(context).load(image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(holder.kategoriIMG);
//        Picasso.with(context).load(image).fit().centerCrop().into(holder.kategoriIMG);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_kategorihome;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.kategori_img1)
        RoundedImageView kategoriIMG;

        @BindView(R.id.kategori_name1)
        TextView kategoriName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}

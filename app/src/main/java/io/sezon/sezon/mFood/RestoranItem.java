package io.sezon.sezon.mFood;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import io.sezon.sezon.R;
import io.sezon.sezon.utils.Log;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Afif on 12/29/2016.
 */

public class RestoranItem extends AbstractItem<RestoranItem, RestoranItem.ViewHolder> {

    Context context;
    public int id;
    public String namaResto;
    public String alamat;
    public double distance;
    public String jamBuka;
    public String jamTutup;
    public String fotoResto;
    public boolean isOpen;
    public boolean isMitra;
    public String pictureUrl;
    public String kontak;

    public RestoranItem(Context context) {
        this.context = context;
    }

    @Override
    public int getType() {
        return R.id.nearme_item;
    }

    @Override
    public void bindView(RestoranItem.ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        holder.nearmeResto.setText(namaResto);
        holder.nearmeAddress.setText(alamat);
        NumberFormat formatter = new DecimalFormat("#0.00");
        holder.nearmeInfo.setText(formatter.format(distance)+" Km" );
        holder.jamBuka.setText(jamBuka);
        holder.jamTutup.setText(jamTutup);
        //+ jamBuka + " - " + jamTutup
        Glide.with(context)
                .load(fotoResto)
                .error(R.drawable.ic_efood)
                .into(holder.nearmeIMG);
        boolean isActuallyOpen = false;
        holder.kon.setText(kontak);
        if(isOpen) {
            isActuallyOpen = calculateJamBukaTutup();
        } else {
            isActuallyOpen = false;
        }

        holder.closed.setVisibility(isActuallyOpen ? View.GONE : View.VISIBLE);
        holder.partner.setVisibility(isMitra ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_nearme;
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

        Log.d("RestoranItem", "NOW HOUR = " + now.get(Calendar.HOUR_OF_DAY) + " Now Minutes ");

        Log.d("RestoranItem", "totalMenitBuka = " + totalMenitBuka + ", totalMenitTutup = " + totalMenitTutup + ", totalMenitNow = " + totalMenitNow);

        return totalMenitNow <= totalMenitTutup && totalMenitNow >= totalMenitBuka;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.nearme_img_resto)
        RoundedImageView nearmeIMG;

        @BindView(R.id.nearme_resto)
        TextView nearmeResto;

        @BindView(R.id.nearme_address)
        TextView nearmeAddress;

        @BindView(R.id.nearme_info)
        TextView nearmeInfo;

        @BindView(R.id.nearme_img_restoran)
        ImageView nearmeIMGfull;

        @BindView(R.id.nearme_closed)
        TextView closed;

        @BindView(R.id.nearme_mitra)
        TextView partner;

        @BindView(R.id.notelp)
        TextView kon;
        @BindView(R.id.jam_buka)
        TextView jamBuka;
        @BindView(R.id.jam_tutup)
        TextView jamTutup;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    /**
     * our ItemFactory implementation which creates the ViewHolder for our adapter.
     * It is highly recommended to implement a ViewHolderFactory as it is 0-1ms faster for ViewHolder creation,
     * and it is also many many times more efficient if you define custom listeners on views within your item.
     */
    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    /**
     * return our ViewHolderFactory implementation here
     *
     * @return
     */
    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

}

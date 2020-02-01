package io.sezon.sezon.home.submenu.history.details.items;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import io.sezon.sezon.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fathony on 24/02/2017.
 */

public class MElectronicItem extends AbstractItem<MElectronicItem, MElectronicItem.ViewHolder> {

    private String namaBarang;
    private String jumlah;
    private  String catatan;

    public MElectronicItem(String namaBarang, String jumlah, String catatan) {
        this.namaBarang = namaBarang;
        this.jumlah = jumlah;
        this.catatan = catatan;
    }

    @Override
    public int getType() {
        return R.id.itemMElectronic;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_melectronic_detail;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        holder.namaBarang.setText(namaBarang);
        holder.qty.setText(String.valueOf(jumlah));
        holder.catatan.setText("Note : " +catatan);
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemMElectronic_nama)
        TextView namaBarang;
        @BindView(R.id.itemMElectronic_qty)
        TextView qty;
        @BindView(R.id.catatanElectronicDetail)
        TextView catatan;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(ViewHolder.this, itemView);
        }
    }
}

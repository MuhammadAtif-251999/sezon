package io.sezon.sezon.model;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mikepenz.fastadapter.items.AbstractItem;

import io.sezon.sezon.R;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fathony on 23/01/2017.
 */

public class ElectronicSearchResult extends AbstractItem<ElectronicSearchResult, ElectronicSearchResult.ViewHolder> {

    @Expose
    @SerializedName("id_toko")
    private int idToko;

    @Expose
    @SerializedName("nama")
    private String nama;

    @Expose
    @SerializedName("alamat")
    private String kategori;

    @Expose
    @SerializedName("distance")
    private double distance;

    public int getIdToko() {
        return idToko;
    }

    public void setIdToko(int idToko) {
        this.idToko = idToko;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public int getType() {
        return R.id.electronicSearchResult;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_electronic_search;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
//        holder.content.setText(toTitleCase(nama));
        holder.content.setText(nama);
//        holder.description.setText(toTitleCase(kategori));
        holder.description.setText(kategori);
        holder.distance.setText(String.format(Locale.US, "%.2f km", distance));
    }

    private static String toTitleCase(String input) {
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }

            titleCase.append(c);
        }

        return titleCase.toString();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.electronicSearchResult_content)
        TextView content;

        @BindView(R.id.electronicSearchResult_description)
        TextView description;

        @BindView(R.id.electronicSearchResult_distance)
        TextView distance;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}

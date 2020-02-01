package io.sezon.sezon.model;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mikepenz.fastadapter.items.AbstractItem;

import io.sezon.sezon.R;
import io.sezon.sezon.utils.Log;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fathony on 23/01/2017.
 */

public class LaundrySearchResult extends AbstractItem<LaundrySearchResult, LaundrySearchResult.ViewHolder> {

    @Expose
    @SerializedName("id_laundry")
    private int idLaundry;

    @Expose
    @SerializedName("nama")
    private String nama;

    @Expose
    @SerializedName("alamat")
    private String kategori;

    @Expose
    @SerializedName("distance")
    private double distance;

    public int getIdLaundry() {
        return idLaundry;
    }

    public void setIdLaundry(int idLaundry) {
        this.idLaundry = idLaundry;
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
        return R.id.laundrySearchResult;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_laundry_search_result;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        Log.d("id_laundry","" + idLaundry + "  nama laundry : " +nama );
        holder.content.setText(nama);
//        holder.content.setText(toTitleCase(nama));

        holder.description.setText(kategori);
//        holder.description.setText(toTitleCase(kategori));

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

        @BindView(R.id.laundrySearchResult_content)
        TextView content;

        @BindView(R.id.laundrySearchResult_description)
        TextView description;

        @BindView(R.id.laundrySearchResult_distance)
        TextView distance;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}

package io.sezon.sezon.mFood;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.sezon.sezon.MangJekApplication;
import io.sezon.sezon.R;
import io.sezon.sezon.model.PesananFood;
import io.sezon.sezon.utils.Log;

/**
 * Created by Afif on 1/3/2017.
 */

public class MakananBoking extends AbstractItem<MakananBoking, MakananBoking.ViewHolder> {

    Context context;
    OnCalculatePrice calculatePrice;
    public int id;
    public String foto;
    public String namaMenu;
    public String deskripsiMenu;
    public long harga;
    public long cost;
    public int quantity;

    public String catatan;

    private Realm realm;

    private TextWatcher catatanUpdater;

    public MakananBoking(Context context, OnCalculatePrice calculatePrice) {
        this.context = context;
        this.calculatePrice = calculatePrice;

        catatanUpdater = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                catatan = s.toString();
                if(quantity > 0) UpdatePesanan(id, cost, quantity, catatan);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    @Override
    public int getType() {
        return R.id.makanan_item_booking;
    }

    @Override
    public void bindView(final MakananBoking.ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        realm = MangJekApplication.getInstance(context).getRealmInstance();
        holder.makananText.setText(namaMenu);
        holder.deskripsiText.setText(deskripsiMenu);
        holder.hargaText.setText(getFormattedPrice(harga));
        holder.quantityText.setText(String.valueOf(quantity));
        holder.notesText.setEnabled(quantity > 0);
        holder.notesText.setText(catatan);
        //Picasso.with(context).load(edit_gambar).fit().centerCrop().into(holder.gambarMakanan);
      //  Glide.with(context).load(edit_gambar).into(holder.gambarMakanan);

        Glide.with(context).load(foto)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.drawable.ic_restoran)
                .into(holder.gambarMakanan);
        holder.notesText.addTextChangedListener(catatanUpdater);

        holder.addQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity++;
                holder.quantityText.setText("" + quantity);
                holder.notesText.setEnabled(true);
                CalculateCost();
                if (quantity == 1) {
                    AddPesanan(id, cost, quantity, catatan);
                } else if (quantity > 1) {
                    UpdatePesanan(id, cost, quantity, catatan);
                }

                if(calculatePrice != null) calculatePrice.calculatePrice();
            }
        });

        holder.removeQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity - 1 >= 0) {
                    quantity--;
                    holder.quantityText.setText(String.valueOf(quantity));
                    CalculateCost();
                    UpdatePesanan(id, cost, quantity, catatan);

                    if (quantity == 0) {
                        DeletePesanan(id);
                        holder.notesText.setText("");
                        holder.notesText.setEnabled(false);
                    }
                }

                if(calculatePrice != null) calculatePrice.calculatePrice();
            }
        });
    }

    private void CalculateCost() {
        cost = quantity * harga;
        //Log.e("Cost", cost+"");
    }

    private void AddPesanan(int idMakanan, long totalHarga, int qty, String notes) {
        PesananFood pesananfood = new PesananFood();
        pesananfood.setIdMakanan(idMakanan);
        pesananfood.setTotalHarga(totalHarga);
        pesananfood.setQty(qty);
        pesananfood.setCatatan(notes);
        realm.beginTransaction();
        realm.copyToRealm(pesananfood);
        realm.commitTransaction();

        Log.e("Added", idMakanan + "");
        Log.e("Added", qty + "");
    }

    private void UpdatePesanan(int idMakanan, long totalHarga, int qty, String notes) {
        realm.beginTransaction();
        PesananFood updateFood = realm.where(PesananFood.class).equalTo("idMakanan", idMakanan).findFirst();
        updateFood.setTotalHarga(totalHarga);
        updateFood.setQty(qty);
        updateFood.setCatatan(notes);
        realm.copyToRealm(updateFood);
        realm.commitTransaction();

        Log.e("Updated", qty + "");
    }

    private void DeletePesanan(int idMakanan) {
        realm.beginTransaction();
        PesananFood deleteFood = realm.where(PesananFood.class).equalTo("idMakanan", idMakanan).findFirst();
        deleteFood.deleteFromRealm();
        realm.commitTransaction();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_makanan_boking;
    }

    private String getFormattedPrice(long price) {
        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(price);
        return String.format(Locale.US, "$ %s.00", formattedTotal);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.makanan_text1)
        TextView makananText;
        @BindView(R.id.foto_makanan1)
        ImageView gambarMakanan;
        @BindView(R.id.deskripsi_text1)
        TextView deskripsiText;

        @BindView(R.id.harga_text1)
        TextView hargaText;

        @BindView(R.id.notes_text1)
        EditText notesText;

        @BindView(R.id.add_quantity1)
        TextView addQuantity;

        @BindView(R.id.quantity_text1)
        TextView quantityText;

        @BindView(R.id.remove_quantity1)
        TextView removeQuantity;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnCalculatePrice {
        void calculatePrice();
    }

}

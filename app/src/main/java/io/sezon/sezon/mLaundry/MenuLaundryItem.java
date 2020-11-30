package io.sezon.sezon.mLaundry;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import io.sezon.sezon.MangJekApplication;
import io.sezon.sezon.R;
import io.sezon.sezon.model.PesananLaundry;
import io.sezon.sezon.utils.Log;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by Afif on 12/31/2016.
 */

public class MenuLaundryItem extends AbstractItem<MenuLaundryItem, MenuLaundryItem.ViewHolder> {

    Context context;
    OnCalculatePrice calculatePrice;

    public int id;
    public String namaMenu;
    public String deskripsiMenu;
    public long harga;
    public String foto;
    public long cost;
    public int quantity=0;

    public String catatan;

    private Realm realm;

    private TextWatcher catatanUpdater;

    int[] sampleImages = {R.drawable.grocery, R.drawable.resto_malam, R.drawable.resto_pagi};

    public MenuLaundryItem(Context context, OnCalculatePrice calculatePrice) {
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
        return R.id.laundry_item;
    }

    @Override
    public void bindView(final MenuLaundryItem.ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        realm = MangJekApplication.getInstance(context).getRealmInstance();
        holder.menuText.setText(namaMenu);
        holder.deskripsiText.setText(deskripsiMenu);
        holder.hargaText.setText(getFormattedPrice(harga));
        holder.quantityText.setText(String.valueOf(quantity));
        holder.notesText.setEnabled(quantity > 0);
        holder.notesText.setText(catatan);

//        Glide.with(context)
//                .load(foto)
//                .placeholder(R.drawable.ic_restoran)
//                .into(holder.fotoItem);

        holder.carouselView.setPageCount(sampleImages.length);
        holder.carouselView.setImageListener(imageListener);



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

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(sampleImages[position]);
        }
    };

    private void CalculateCost() {
//        cost = 1 * harga;
        cost = quantity * harga;

        //Log.e("Cost", cost+"");
    }

    private void AddPesanan(int idLaundry, long totalHarga, int qty, String notes) {
        PesananLaundry pesananlaundry = new PesananLaundry();
        pesananlaundry.setIdLaundry(idLaundry);
        pesananlaundry.setTotalHarga(totalHarga);
        pesananlaundry.setQty(qty);
        pesananlaundry.setCatatan(notes);
        realm.beginTransaction();
        realm.copyToRealm(pesananlaundry);
        realm.commitTransaction();

        Log.e("Added", idLaundry + "");
        Log.e("Added", qty + "");
    }

    private void UpdatePesanan(int idLaundry, long totalHarga, int qty, String notes) {
        realm.beginTransaction();
        PesananLaundry updateLaundry = realm.where(PesananLaundry.class).equalTo("idLaundry", idLaundry).findFirst();
        updateLaundry.setTotalHarga(totalHarga);
        updateLaundry.setQty(qty);
        updateLaundry.setCatatan(notes);
        realm.copyToRealm(updateLaundry);
        realm.commitTransaction();

        Log.e("Updated", qty + "");
    }

    private void DeletePesanan(int idLaundry) {
        realm.beginTransaction();
        PesananLaundry deleteFood = realm.where(PesananLaundry.class).equalTo("idLaundry", idLaundry).findFirst();
        deleteFood.deleteFromRealm();
        realm.commitTransaction();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_list_laundry;
    }

    private String getFormattedPrice(long price) {
        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(price);
        return String.format(Locale.US, "$ %s.00", formattedTotal);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.laundry_text)
        TextView menuText;

        @BindView(R.id.deskripsi_text)
        TextView deskripsiText;

        @BindView(R.id.harga_text)
        TextView hargaText;

        @BindView(R.id.notes_text)
        EditText notesText;

//        @BindView(R.id.foto_makanan)
//        ImageView fotoItem;

        @BindView(R.id.add_quantity)
        TextView addQuantity;

        @BindView(R.id.quantity_text)
        TextView quantityText;

        @BindView(R.id.remove_quantity)
        TextView removeQuantity;

        @BindView(R.id.carouselView)
        CarouselView carouselView;



        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnCalculatePrice {
        void calculatePrice();
    }

}

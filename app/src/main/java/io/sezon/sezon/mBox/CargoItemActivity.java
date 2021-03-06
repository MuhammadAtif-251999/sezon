package io.sezon.sezon.mBox;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.sezon.sezon.R;

/**
 * Created by haris on 12/22/16.
 */

public class CargoItemActivity extends AbstractItem<CargoItemActivity, CargoItemActivity.ViewHolder> {

    Context context;
    public int id;
    public String type;
    public long price;
    public long hargaminimummbox;
    public String dimension;
    public String maxWeight;
    public int featureId;
    public String image;

    public CargoItemActivity(Context context){
        this.context = context;
    }

    @Override
    public int getType() {
        return R.id.cargo_type_activity;
    }

//    @Override
//    public void unbindView(ViewHolder holder) {
//        super.unbindView(holder);
//        holder.cargoTitle.setText(null);
//        holder.cargoDimension.setText(null);
//        holder.cargoMaxWeight.setText(null);
//        holder.cargoId = 0;
//        holder.featureId = 0;
//        holder.cargoImage.setImageDrawable(null);
//    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        holder.cargoTitle.setText(type);
        holder.cargoDimension.setText(dimension);
        holder.cargoMaxWeight.setText(maxWeight);
//        holder.context = context;
//        holder.cargoId = this.id;
//        holder.featureId = this.featureId;
        Glide.with(context).load(image).into(holder.cargoImage);

    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_mbox_cargo;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cargo_image)
        ImageView cargoImage;

        @BindView(R.id.cargo_title)
        TextView cargoTitle;

        @BindView(R.id.cargo_dimension)
        TextView cargoDimension;

        @BindView(R.id.cargo_max_weight)
        TextView cargoMaxWeight;

//        private Realm realm;
//        Context context;
//        int cargoId;
//        int featureId;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
//            realm = MangJekApplication.getInstance(context).getRealmInstance();
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.e("BUTTON","CLICKED");
////                    KendaraanAngkut selectedCargo = realm.where(KendaraanAngkut.class).equalTo("idKendaraan", cargoId).findFirst();
////                    Intent intent = new Intent(context, BoxOrder.class);
////                    intent.putExtra(BoxOrder.FITUR_KEY, featureId);
////                    intent.putExtra(BoxOrder.KENDARAAN_KEY, selectedCargo.getIdKendaraan());
////                    context.startActivity(intent);
//                }
//            });
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

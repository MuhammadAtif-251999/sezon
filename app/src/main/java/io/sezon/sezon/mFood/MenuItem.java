package io.sezon.sezon.mFood;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import io.sezon.sezon.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Afif on 12/31/2016.
 */

public class MenuItem extends AbstractItem<MenuItem, MenuItem.ViewHolder> {

    Context context;
    public int idMenu;
    public String menuMakanan;

    public MenuItem(Context context) {
        this.context = context;
    }

    @Override
    public int getType() {
        return R.id.menu_item;
    }

    @Override
    public void bindView(MenuItem.ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        holder.menuText.setText(menuMakanan);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_foodmenu;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.menu_text)
        TextView menuText;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}

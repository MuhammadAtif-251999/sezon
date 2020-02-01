package io.sezon.sezon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Afif on 12/31/2016.
 */

public class MenuLaundry implements Serializable {

    @Expose
    @SerializedName("id_menu")
    private int idMenu;

    @Expose
    @SerializedName("menu_laundry")
    private String menuLaundry;

    public int getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(int idMenu) {
        this.idMenu = idMenu;
    }

    public String getMenuLaundry() {
        return menuLaundry;
    }

    public void setMenuLaundry(String menuLaundry) {
        this.menuLaundry = menuLaundry;
    }
}

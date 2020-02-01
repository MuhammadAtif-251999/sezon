package io.sezon.sezon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Afif on 12/31/2016.
 */

public class TempatLaundry implements Serializable {

    @Expose
    @SerializedName("detail_laundry")
    private List<Laundry> detailLaundry = new ArrayList<>();

    @Expose
    @SerializedName("list_menu_layanan")
    private List<ItemLaundry> menuLaundryList = new ArrayList<>(); //MENU LAUNDRY (ID_MENU, MENU LAUNDRY)

//    @Expose
//    @SerializedName("list_layanan")
//    private List<LayananLaundry> layananList = new ArrayList<>(); //ID, NAMA LAYANAN, HARGA



    public List<Laundry> getDetailLaundry() {
        return detailLaundry;
    }

    public void setDetailLaundry(List<Laundry> detailLaundry) {
        this.detailLaundry = detailLaundry;
    }

    public List<ItemLaundry> getMenuLaundryList() {
        return menuLaundryList;
    }

    public void setMenuLaundryList(List<ItemLaundry> menuLaundryList) {
        this.menuLaundryList = menuLaundryList;
    }

//    public List<LayananLaundry> getLayananList() {
//        return layananList;
//    }
//
//    public void setLayananList(List<LayananLaundry> layananList) {
//        this.layananList = layananList;
//    }
}

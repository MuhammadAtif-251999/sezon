package io.sezon.sezon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Afif on 12/31/2016.
 */

public class LayananLaundry extends RealmObject implements Serializable {

    @PrimaryKey
    @Expose
    @SerializedName("id")
    private int id;

    @Expose
    @SerializedName("nama_menu")
    private String namaMenu;

    @Expose
    @SerializedName("deskripsi_menu")
    private String deskripsiMenu;

    @Expose
    @SerializedName("harga")
    private long harga;

    @Expose
    @SerializedName("cost")
    private long cost;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNamaMenu() {
        return namaMenu;
    }

    public void setNamaMenu(String namaMenu) {
        this.namaMenu = namaMenu;
    }

    public String getDeskripsiMenu() {
        return deskripsiMenu;
    }

    public void setDeskripsiMenu(String deskripsiMenu) {
        this.deskripsiMenu = deskripsiMenu;
    }

    public long getHarga() {
        return harga;
    }

    public void setHarga(long harga) {
        this.harga = harga;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }


}

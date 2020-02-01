package io.sezon.sezon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Afif on 12/31/2016.
 */

public class ListBarang implements Serializable {

    @Expose
//    @SerializedName("id_barang")
    @SerializedName("id_kategori")
    private int idBarang;

    @Expose
//    @SerializedName("list_barang")
    @SerializedName("kategori_barang")
    private String listBarang;

    public int getIdBarang() {
        return idBarang;
    }

    public void setIdBarang(int idBarang) {
        this.idBarang = idBarang;
    }

    public String getListBarang() {
        return listBarang;
    }

    public void setListBarang(String listBarang) {
        this.listBarang = listBarang;
    }
}

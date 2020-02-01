package io.sezon.sezon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Afif on 12/31/2016.
 */

public class BarangToko implements Serializable {

    @Expose
    @SerializedName("detail_toko")
    private List<TokoElektronik> detailToko = new ArrayList<>();

    @Expose
//    @SerializedName("list_barang")
    @SerializedName("list_kategori_barang")
    private List<ListBarang> listBarang = new ArrayList<>();

    @Expose
//    @SerializedName("detail_barang")
    @SerializedName("list_barang")
    private List<Barang> detailBarang = new ArrayList<>();


    public List<TokoElektronik> getDetailToko() {
        return detailToko;
    }

    public void setDetailToko(List<TokoElektronik> detailToko) {
        this.detailToko = detailToko;
    }

    public List<ListBarang> getListBarang() {
        return listBarang;
    }

    public void setListBarang(List<ListBarang> listBarang) {
        this.listBarang = listBarang;
    }

    public List<Barang> getDetailBarang() {
        return detailBarang;
    }

    public void setDetailBarang(List<Barang> detailBarang) {
        this.detailBarang = detailBarang;
    }


}

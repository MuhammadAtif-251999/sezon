package io.sezon.sezon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by fathony on 24/02/2017.
 */

public class MElectronicItemRemote {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("id_barang")
    @Expose
    private String idTransaksi;

    @SerializedName("nama_barang")
    @Expose
    private String namaBarang;

    @SerializedName("jumlah")
    @Expose
    private String jumlah;

    @SerializedName("harga")
    @Expose
    private String harga;

    @SerializedName("total_harga")
    @Expose
    private String totalHarga;

    @SerializedName("catatan")
    @Expose
    private String catatan;

    public String getId() {
        return id;
    }

    public String getIdTransaksi() {
        return idTransaksi;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public String getJumlah() {
        return jumlah;
    }

    public String getHarga() { return harga; }

    public String getCatatan() {
        return catatan;
    }

    public String getTotalHarga() {
        return totalHarga;
    }
}

package io.sezon.sezon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Afif on 12/31/2016.
 */

public class Barang extends RealmObject implements Serializable {

    @PrimaryKey
    @Expose
    @SerializedName("id")
    private int id;

    @Expose
    @SerializedName("nama_barang")
    private String namaBarang;

    @Expose
    @SerializedName("harga")
    private long harga;

//    @Expose
//    @SerializedName("kategori_barang")
//    private int kategoriBarang;

    @Expose
    @SerializedName("deskripsi_barang")
    private String deskripsiBarang;

    @Expose
    @SerializedName("edit_gambar")
    private String foto;

    @Expose
//    @SerializedName("id_list_barang")
    @SerializedName("id_kategori")
    private int idListBarang;

    @Expose
//    @SerializedName("list_barang")
    @SerializedName("kategori_barang")
    private String listBarang;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public long getHarga() {
        return harga;
    }

    public void setHarga(long harga) {
        this.harga = harga;
    }

//    public int getKategoriBarang() {
//        return kategoriBarang;
//    }
//
//    public void setKategoriBarang(int kategoriBarang) {
//        this.kategoriBarang = kategoriBarang;
//    }

    public String getDeskripsiBarang() {
        return deskripsiBarang;
    }

    public void setDeskripsiBarang(String deskripsiBarang) {
        this.deskripsiBarang = deskripsiBarang;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public int getIdListBarang() {
        return idListBarang;
    }

    public void setIdListBarang(int idListBarang) {
        this.idListBarang = idListBarang;
    }

    public String getListBarang() {
        return listBarang;
    }

    public void setListBarang(String listBarang) {
        this.listBarang = listBarang;
    }
}

package io.sezon.sezon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Afif on 12/31/2016.
 */

public class ItemLaundry extends RealmObject implements Serializable {

    @PrimaryKey
    @Expose
    @SerializedName("id")
    private int id;

    @Expose
    @SerializedName("nama_layanan")
    private String namaLayanan;

    @Expose
    @SerializedName("harga")
    private long harga;


    @Expose
    @SerializedName("deskripsi_layanan")
    private String deskripsiLayanan;

  @Expose
    @SerializedName("edit_gambar")
    private String foto;

    @Expose
    @SerializedName("id_daftar_layanan")
    private int idDaftarLayanan;
//
//    @Expose
//    @SerializedName("menu_makanan")
//    private String menuMakanan;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNamaLayanan() {
        return namaLayanan;
    }

    public void setNamaLayanan(String namaLayanan) {
        this.namaLayanan = namaLayanan;
    }

    public long getHarga() {
        return harga;
    }

    public void setHarga(long harga) {
        this.harga = harga;
    }

//    public int getKategoriMenuMakanan() {
//        return kategoriMenuMakanan;
//    }
//
//    public void setKategoriMenuMakanan(int kategoriMenuMakanan) {
//        this.kategoriMenuMakanan = kategoriMenuMakanan;
//    }

    public String getDeskripsiLayanan() {
        return deskripsiLayanan;
    }

    public void setDeskripsiLayanan(String deskripsiLayanan) {
        this.deskripsiLayanan = deskripsiLayanan;
    }

  public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
   }

    public int getIdDaftarLayanan() {
        return idDaftarLayanan;
    }

    public void setIdDaftarLayanan(int idDaftarLayanan) {
        this.idDaftarLayanan = idDaftarLayanan;
    }
//
//    public String getMenuMakanan() {
//        return menuMakanan;
//    }
//
//    public void setMenuMakanan(String menuMakanan) {
//        this.menuMakanan = menuMakanan;
//    }
}

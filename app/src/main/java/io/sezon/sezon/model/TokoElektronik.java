package io.sezon.sezon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Afif on 12/28/2016.
 */

public class TokoElektronik extends RealmObject implements Serializable {

    @PrimaryKey
    @Expose
    @SerializedName("id")
    private int id;

    @Expose
    @SerializedName("nama")
    private String namaToko;

    @Expose
    @SerializedName("alamat")
    private String alamat;

    @Expose
    @SerializedName("latitude")
    private double latitude;

    @Expose
    @SerializedName("longitude")
    private double longitude;

    @Expose
    @SerializedName("jam_buka")
    private String jamBuka;

    @Expose
    @SerializedName("jam_tutup")
    private String jamTutup;

    @Expose
    @SerializedName("deskripsi")
    private String deskripsiToko;

    @Expose
    @SerializedName("kategori_toko")
    private int kategoriToko;

    @Expose
    @SerializedName("foto_toko")
    private String fotoToko;

    @Expose
    @SerializedName("kontak_telepon")
    private String kontakTelepon;

    @Expose
    @SerializedName("status")
    private int status;

    @Expose
    @SerializedName("is_open")
    private boolean isOpen;

    @Expose
    @SerializedName("is_partner")
    private boolean isPartner;

    @Expose
    @SerializedName("kategori_tokoelektronik")
    private String kategoriTokoElektronik;

    @Expose
    @SerializedName("distance")
    private double distance;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNamaToko() {
        return namaToko;
    }

    public void setNamaToko(String namaToko) {
        this.namaToko = namaToko;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getJamBuka() {
        return jamBuka;
    }

    public void setJamBuka(String jamBuka) {
        this.jamBuka = jamBuka;
    }

    public String getJamTutup() {
        return jamTutup;
    }

    public void setJamTutup(String jamTutup) {
        this.jamTutup = jamTutup;
    }

    public String getDeskripsiResto() {
        return deskripsiToko;
    }

    public void setDeskripsiToko(String deskripsiToko) {
        this.deskripsiToko = deskripsiToko;
    }

    public int getKategoriToko() {
        return kategoriToko;
    }

    public void setKategoriResto(int kategoriToko) {
        this.kategoriToko = kategoriToko;
    }

    public String getFotoResto() {
        return fotoToko;
    }

    public void setFotoResto(String fotoToko) {
        this.fotoToko = fotoToko
        ;
    }

    public String getKontakTelepon() {
        return kontakTelepon;
    }

    public void setKontakTelepon(String kontakTelepon) {
        this.kontakTelepon = kontakTelepon;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getKategoriRestoran() {
        return kategoriTokoElektronik;
    }

    public void setKategoriTokoElektronik(String kategoriTokoElektronik) {
        this.kategoriTokoElektronik = kategoriTokoElektronik;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean isPartner() {
        return isPartner;
    }

    public void setPartner(boolean partner) {
        isPartner = partner;
    }
}

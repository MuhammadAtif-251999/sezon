package io.sezon.sezon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Afif on 12/28/2016.
 */

public class Laundry extends RealmObject implements Serializable {

    @PrimaryKey
    @Expose
    @SerializedName("id")
    private int id;

    @Expose
    @SerializedName("nama")
    private String namaLaundry;

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
    @SerializedName("deskripsi_laundry")
    private String deskripsiLaundry;

    @Expose
    @SerializedName("kategori_laundry")
    private int kategoriLaundry;

    @Expose
    @SerializedName("foto_laundry")
    private String fotoLaundry;

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
    @SerializedName("distance")
    private double distance;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNamaLaundry() {
        return namaLaundry;
    }

    public void setNamaLaundry(String namaLaundry) {
        this.namaLaundry = namaLaundry;
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

    public String getDeskripsiLaundry() {
        return deskripsiLaundry;
    }

    public void setDeskripsiLaundry(String deskripsiLaundry) {
        this.deskripsiLaundry = deskripsiLaundry;
    }

    public int getKategoriLaundry() {
        return kategoriLaundry;
    }

    public void setKategoriLaundry(int kategoriLaundry) {
        this.kategoriLaundry = kategoriLaundry;
    }

    public String getFotoLaundry() {
        return fotoLaundry;
    }

    public void setFotoLaundry(String fotoLaundry) {
        this.fotoLaundry = fotoLaundry;
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

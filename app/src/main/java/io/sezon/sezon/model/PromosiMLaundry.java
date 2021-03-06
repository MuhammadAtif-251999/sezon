package io.sezon.sezon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by fathony on 1/26/2017.
 */

public class PromosiMLaundry implements Serializable {

    @Expose
    @SerializedName("id")
    private int id;
    @Expose
    @SerializedName("keterangan")
    private String keterangan;
    @Expose
    @SerializedName("id_laundry")
    private int idLaundry;
    @Expose
    @SerializedName("foto")
    private String foto;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public int getIdLaundry() {
        return idLaundry;
    }

    public void setIdLaundry(int idLaundry) {
        this.idLaundry = idLaundry;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}

package io.sezon.sezon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by fathony on 1/26/2017.
 */

public class PromosiMElectronic implements Serializable {

    @Expose
    @SerializedName("id")
    private int id;
    @Expose
    @SerializedName("keterangan")
    private String keterangan;
    @Expose
    @SerializedName("id_toko")
    private int idToko;
    @Expose
    @SerializedName("edit_gambar")
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

    public int getIdToko() {
        return idToko;
    }

    public void setIdToko(int idToko) {
        this.idToko = idToko;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}

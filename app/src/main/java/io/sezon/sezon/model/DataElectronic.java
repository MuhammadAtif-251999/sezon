package io.sezon.sezon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Afif on 12/28/2016.
 */

public class DataElectronic implements Serializable {

    @Expose
    @SerializedName("kategori_toko")
    private List<KategoriElektronik> kategoriElektronikList = new ArrayList<>();

    @Expose
    @SerializedName("toko_by_location")
    private List<TokoElektronikNearMeDB> elektronikList = new ArrayList<>();

    @Expose
    @SerializedName("promosi_mElectronic")
    private List<PromosiMElectronic> promosiMElectronics = new ArrayList<>();

    public List<KategoriElektronik> getKategoriElektronikList() {
        return kategoriElektronikList;
    }

    public void setKategoriElektronikList(List<KategoriElektronik> kategoriElektronikList) {
        this.kategoriElektronikList = kategoriElektronikList;
    }

    public List<TokoElektronikNearMeDB> getElektronikList() {
        return elektronikList;
    }

    public void setElektronikList(List<TokoElektronikNearMeDB> elektronikList) {
        this.elektronikList = elektronikList;
    }

    public List<PromosiMElectronic> getPromosiMElectronics() {
        return promosiMElectronics;
    }

    public void setPromosiMElectronics(List<PromosiMElectronic> promosiMElectronics) {
        this.promosiMElectronics = promosiMElectronics;
    }
}

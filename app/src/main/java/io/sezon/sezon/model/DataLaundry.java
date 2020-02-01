package io.sezon.sezon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Afif on 12/28/2016.
 */

public class DataLaundry implements Serializable {

    @Expose
    @SerializedName("list_laundry_all")
    private List<Laundry> laundryAll = new ArrayList<>();

    @Expose
    @SerializedName("laundry_by_location")
    private List<LaundryNearMeDB> laundryList = new ArrayList<>();

    @Expose
    @SerializedName("promosi_mlaundry")
    private List<PromosiMLaundry> promosiMLaundry = new ArrayList<>();

    public List<Laundry> getLaundryAll() {
        return laundryAll;
    }

    public void setLaundryAll(List<Laundry> laundryAll) {
        this.laundryAll = laundryAll;
    }

    public List<LaundryNearMeDB> getLaundryList() {
        return laundryList;
    }

    public void setLaundryList(List<LaundryNearMeDB> laundryList) {
        this.laundryList = laundryList;
    }

    public List<PromosiMLaundry> getPromosiMLaundry() {
        return promosiMLaundry;
    }

    public void setPromosiMLaundry(List<PromosiMLaundry> promosiMLaundry) {
        this.promosiMLaundry = promosiMLaundry;
    }
}

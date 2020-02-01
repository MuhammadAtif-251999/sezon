package io.sezon.sezon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DataRestoranall {

    @Expose
    @SerializedName("list_resto_all")
    private List<Restoran> RestoAll = new ArrayList<>();

    @Expose
    @SerializedName("resto_by_location")
    private List<RestoranNearMeDB> laundryList = new ArrayList<>();


    public List<Restoran> getLaundryAll() {
        return RestoAll;
    }

    public void setLaundryAll(List<Restoran> RestoAll) {
        this.RestoAll = RestoAll;
    }

    public List<RestoranNearMeDB> getLaundryList() {
        return laundryList;
    }

    public void setLaundryList(List<RestoranNearMeDB> laundryList) {
        this.laundryList = laundryList;
    }




}

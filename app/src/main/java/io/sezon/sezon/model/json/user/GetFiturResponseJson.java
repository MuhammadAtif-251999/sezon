package io.sezon.sezon.model.json.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.sezon.sezon.model.DiskonMpay;
import io.sezon.sezon.model.Fitur;
import io.sezon.sezon.model.MfoodMitra;
import io.sezon.sezon.model.MlaundryMitra;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bradhawk on 10/17/2016.
 */

public class GetFiturResponseJson {

    @Expose
    @SerializedName("data")
    private List<Fitur> data = new ArrayList<>();

    @Expose
    @SerializedName("diskon_mpay")
    private DiskonMpay diskonMpay;

    @Expose
    @SerializedName("mfood_mitra")
    MfoodMitra mfoodMitra;

    @Expose
    @SerializedName("mlundry_mitra")
    MlaundryMitra mlaundryMitra;

    public List<Fitur> getData() {
        return data;
    }

    public void setData(List<Fitur> data) {
        this.data = data;
    }

    public DiskonMpay getDiskonMpay() {
        return diskonMpay;
    }

    public void setDiskonMpay(DiskonMpay diskonMpay) {
        this.diskonMpay = diskonMpay;
    }

    public MfoodMitra getMfoodMitra() {
        return mfoodMitra;
    }

    public void setMfoodMitra(MfoodMitra mfoodMitra) {
        this.mfoodMitra = mfoodMitra;
    }

///,laundry
    public MlaundryMitra getMlaundryMitra() {
        return mlaundryMitra;
    }

    public void setMlaundryMitra(MlaundryMitra mlaundryMitra) {
        this.mlaundryMitra = mlaundryMitra;
    }
}


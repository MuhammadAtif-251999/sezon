package io.sezon.sezon.model.json.book.massage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;



import java.util.ArrayList;
import java.util.List;

import io.sezon.sezon.model.DetailTransaksiMassage;

/**
 * Created by bradhawk on 1/12/2017.
 */

public class DetailTransaksiResponse {

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("data_transaksi")
    private List<DetailTransaksiMassage> dataTransaksi = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DetailTransaksiMassage> getDataTransaksi() {
        return dataTransaksi;
    }

    public void setDataTransaksi(List<DetailTransaksiMassage> dataTransaksi) {
        this.dataTransaksi = dataTransaksi;
    }
}
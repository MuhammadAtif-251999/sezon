package io.sezon.sezon.model.json.book.detailTransaksi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.sezon.sezon.model.MFoodDetailTransaksi;
import io.sezon.sezon.model.MFoodItemRemote;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fathony on 24/02/2017.
 */

public class GetDataTransaksiMFoodResponse {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data_transaksi")
    @Expose
    private List<MFoodDetailTransaksi> dataTransaksi = new ArrayList<>();
    @SerializedName("list_barang")
    @Expose
    private List<MFoodItemRemote> listBarang = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public List<MFoodDetailTransaksi> getDataTransaksi() {
        return dataTransaksi;
    }

    public List<MFoodItemRemote> getListBarang() {
        return listBarang;
    }
}

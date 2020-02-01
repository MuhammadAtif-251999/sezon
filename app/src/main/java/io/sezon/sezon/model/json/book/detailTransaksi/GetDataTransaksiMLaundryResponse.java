package io.sezon.sezon.model.json.book.detailTransaksi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.sezon.sezon.model.MLaundryDetailTransaksi;
import io.sezon.sezon.model.MLaundryItemRemote;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fathony on 24/02/2017.
 */

public class GetDataTransaksiMLaundryResponse {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data_transaksi")
    @Expose
    private List<MLaundryDetailTransaksi> dataTransaksi = new ArrayList<>();
    @SerializedName("list_barang")
    @Expose
    private List<MLaundryItemRemote> listBarang = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public List<MLaundryDetailTransaksi> getDataTransaksi() {
        return dataTransaksi;
    }

    public List<MLaundryItemRemote> getListBarang() {
        return listBarang;
    }
}

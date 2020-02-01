package io.sezon.sezon.model.json.book.detailTransaksi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.sezon.sezon.model.MElectronicDetailTransaksi;
import io.sezon.sezon.model.MElectronicItemRemote;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fathony on 24/02/2017.
 */

public class GetDataTransaksiMElectronicResponse {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data_transaksi")
    @Expose
    private List<MElectronicDetailTransaksi> dataTransaksi = new ArrayList<>();
    @SerializedName("list_barang")
    @Expose
    private List<MElectronicItemRemote> listBarang = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public List<MElectronicDetailTransaksi> getDataTransaksi() {
        return dataTransaksi;
    }

    public List<MElectronicItemRemote> getListBarang() {
        return listBarang;
    }
}

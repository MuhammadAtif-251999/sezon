package io.sezon.sezon.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.sezon.sezon.model.TokoElektronik;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Afif on 12/28/2016.
 */

public class GetDataTokoByKategoriResponseJson {

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("data")
    private List<TokoElektronik> electronicList = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<TokoElektronik> getElectronicList() {
        return electronicList;
    }

    public void setElectronicList(List<TokoElektronik> electronicList) {
        this.electronicList = electronicList;
    }
}

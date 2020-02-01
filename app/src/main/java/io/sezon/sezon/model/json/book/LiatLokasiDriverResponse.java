package io.sezon.sezon.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.sezon.sezon.model.LokasiDriver;

import java.util.ArrayList;
import java.util.List;


public class LiatLokasiDriverResponse {

    @SerializedName("data")
    @Expose
    private List<LokasiDriver> data = new ArrayList<>();

    public List<LokasiDriver> getData() {
        return data;
    }

}

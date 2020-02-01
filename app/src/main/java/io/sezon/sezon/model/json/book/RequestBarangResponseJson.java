package io.sezon.sezon.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.sezon.sezon.model.TransaksiBarang;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fathony on 1/26/2017.
 */

public class RequestBarangResponseJson implements Serializable {

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("data")
    private List<TransaksiBarang> data = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<TransaksiBarang> getData() {
        return data;
    }

    public void setData(List<TransaksiBarang> data) {
        this.data = data;
    }
}

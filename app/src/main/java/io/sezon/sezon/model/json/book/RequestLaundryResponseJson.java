package io.sezon.sezon.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.sezon.sezon.model.TransaksiLaundry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fathony on 1/26/2017.
 */

public class RequestLaundryResponseJson implements Serializable {

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("data")
    private List<TransaksiLaundry> data = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<TransaksiLaundry> getData() {
        return data;
    }

    public void setData(List<TransaksiLaundry> data) {
        this.data = data;
    }
}

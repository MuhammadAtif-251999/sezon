package io.sezon.sezon.model.json.book.massage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;



import java.util.ArrayList;
import java.util.List;

import io.sezon.sezon.model.DriverMassage;
import io.sezon.sezon.model.TransaksiMassage;

/**
 * Created by bradhawk on 12/23/2016.
 */

public class RequestMassageResponseJson {

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("data")
    private List<TransaksiMassage> data = new ArrayList<>();

    @Expose
    @SerializedName("list_driver")
    private List<DriverMassage> listDriver = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<TransaksiMassage> getData() {
        return data;
    }

    public void setData(List<TransaksiMassage> data) {
        this.data = data;
    }

    public List<DriverMassage> getListDriver() {
        return listDriver;
    }

    public void setListDriver(List<DriverMassage> listDriver) {
        this.listDriver = listDriver;
    }
}

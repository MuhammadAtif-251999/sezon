package io.sezon.sezon.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.sezon.sezon.model.DataRestoranall;

public class GetDataRestoranResponseJson {

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("data")
    private DataRestoranall dataLaundry;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataRestoranall getDataLaundry() {
        return dataLaundry;
    }

    public void setDataLaundry(DataRestoranall dataLaundry) {
        this.dataLaundry = dataLaundry;
    }
}

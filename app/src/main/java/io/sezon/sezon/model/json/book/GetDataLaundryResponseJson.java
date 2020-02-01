package io.sezon.sezon.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.sezon.sezon.model.DataLaundry;

/**
 * Created by Afif on 12/28/2016.
 */

public class GetDataLaundryResponseJson {

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("data")
    private DataLaundry dataLaundry;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataLaundry getDataLaundry() {
        return dataLaundry;
    }

    public void setDataLaundry(DataLaundry dataLaundry) {
        this.dataLaundry = dataLaundry;
    }
}

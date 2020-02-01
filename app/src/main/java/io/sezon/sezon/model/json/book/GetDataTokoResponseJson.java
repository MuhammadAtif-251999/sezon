package io.sezon.sezon.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.sezon.sezon.model.DataElectronic;

/**
 * Created by Afif on 12/28/2016.
 */

public class GetDataTokoResponseJson {

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("data")
    private DataElectronic dataElectronic;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataElectronic getDataElectronic() {
        return dataElectronic;
    }

    public void setDataElectronic(DataElectronic dataElectronic) {
        this.dataElectronic = dataElectronic;
    }
}

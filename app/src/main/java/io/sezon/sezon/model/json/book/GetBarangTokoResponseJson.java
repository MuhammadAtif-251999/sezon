package io.sezon.sezon.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.sezon.sezon.model.BarangToko;

/**
 * Created by Afif on 12/31/2016.
 */

public class GetBarangTokoResponseJson {

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("data")
    private BarangToko barangToko;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BarangToko getBarangToko() {
        return barangToko;
    }

    public void setBarangToko(BarangToko barangToko) {
        this.barangToko = barangToko;
    }
}

package io.sezon.sezon.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.sezon.sezon.model.TempatLaundry;

/**
 * Created by Afif on 12/31/2016.
 */

public class GetLayananLaundryResponseJson {

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("data")
    private TempatLaundry tempatLaundry;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TempatLaundry getTempatLaundry() {
        return tempatLaundry;
    }

    public void setTempatLaundry(TempatLaundry tempatLaundry) {
        this.tempatLaundry = tempatLaundry;
    }
}

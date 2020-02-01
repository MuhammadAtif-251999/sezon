package io.sezon.sezon.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Afif on 12/31/2016.
 */

public class GetLayananLaundryRequestJson {

    @Expose
    @SerializedName("id_laundry")
    private int idLaundry;

    public int getIdLaundry() {
        return idLaundry;
    }

    public void setIdLaundry(int idLaundry) {
        this.idLaundry = idLaundry;
    }
}

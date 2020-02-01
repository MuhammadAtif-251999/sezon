package io.sezon.sezon.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Afif on 12/31/2016.
 */

public class GetBarangTokoRequestJson {

    @Expose
    @SerializedName("id_toko")
    private int idToko;

    public int getIdToko() {
        return idToko;
    }

    public void setIdToko(int idToko) {
        this.idToko = idToko;
    }
}

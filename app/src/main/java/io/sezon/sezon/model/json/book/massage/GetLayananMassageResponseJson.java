package io.sezon.sezon.model.json.book.massage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;



import java.util.ArrayList;
import java.util.List;

import io.sezon.sezon.mMassage.MenuMassageItem;

/**
 * Created by bradhawk on 12/22/2016.
 */

public class GetLayananMassageResponseJson {

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("data")
    private List<MenuMassageItem> data = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<MenuMassageItem> getData() {
        return data;
    }

    public void setData(List<MenuMassageItem> data) {
        this.data = data;
    }
}

package io.sezon.sezon.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.sezon.sezon.model.ElectronicSearchResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fathony on 23/01/2017.
 */

public class SearchElectronicResponse {

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("data")
    private List<ElectronicSearchResult> data = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ElectronicSearchResult> getData() {
        return data;
    }

    public void setData(List<ElectronicSearchResult> data) {
        this.data = data;
    }
}

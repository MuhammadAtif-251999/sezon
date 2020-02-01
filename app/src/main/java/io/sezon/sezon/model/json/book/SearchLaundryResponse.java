package io.sezon.sezon.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.sezon.sezon.model.LaundrySearchResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fathony on 23/01/2017.
 */

public class SearchLaundryResponse {

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("data")
    private List<LaundrySearchResult> data = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<LaundrySearchResult> getData() {
        return data;
    }

    public void setData(List<LaundrySearchResult> data) {
        this.data = data;
    }
}

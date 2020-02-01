package io.sezon.sezon.model.json.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.sezon.sezon.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bradhawk on 10/13/2016.
 */

public class RegisterResponseJson {

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private List<User> data = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<User> getData() {
        return data;
    }

    public void setData(List<User> data) {
        this.data = data;
    }
}

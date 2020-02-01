package io.sezon.sezon.model.json.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.sezon.sezon.model.Banner;

import java.util.ArrayList;

/**
 * Created by bradhawk on 10/17/2016.
 */

public class GetBannerResponseJson {

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("data")
    @Expose
    public ArrayList<Banner> data;

}

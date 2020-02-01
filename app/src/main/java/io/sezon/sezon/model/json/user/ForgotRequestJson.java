package io.sezon.sezon.model.json.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bradhawk on 10/13/2016.
 */

public class ForgotRequestJson {

    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("email")
    @Expose
    public String email;


}

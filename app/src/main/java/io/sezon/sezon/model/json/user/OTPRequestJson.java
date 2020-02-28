package io.sezon.sezon.model.json.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OTPRequestJson {

    @Expose
    @SerializedName("no_telepon")
    public String no_telepon;

    @Expose
    @SerializedName("otp")
    public String otp;


    public String getNo_telepon() {
        return no_telepon;
    }

    public void setNo_telepon(String no_telepon) {
        this.no_telepon = no_telepon;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}

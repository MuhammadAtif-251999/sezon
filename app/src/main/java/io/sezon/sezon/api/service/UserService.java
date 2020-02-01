package io.sezon.sezon.api.service;

import io.sezon.sezon.model.json.book.RateDriverRequestJson;
import io.sezon.sezon.model.json.book.RateDriverResponseJson;
import io.sezon.sezon.model.json.fcm.CancelBookRequestJson;
import io.sezon.sezon.model.json.fcm.CancelBookResponseJson;
import io.sezon.sezon.model.json.menu.HelpRequestJson;
import io.sezon.sezon.model.json.menu.HelpResponseJson;
import io.sezon.sezon.model.json.menu.HistoryRequestJson;
import io.sezon.sezon.model.json.menu.HistoryResponseJson;
import io.sezon.sezon.model.json.menu.VersionRequestJson;
import io.sezon.sezon.model.json.menu.VersionResponseJson;
import io.sezon.sezon.model.json.user.ChangePasswordRequestJson;
import io.sezon.sezon.model.json.user.ChangePasswordResponseJson;
import io.sezon.sezon.model.json.user.GetBannerResponseJson;
import io.sezon.sezon.model.json.user.GetFiturResponseJson;
import io.sezon.sezon.model.json.user.GetSaldoRequestJson;
import io.sezon.sezon.model.json.user.GetSaldoResponseJson;
import io.sezon.sezon.model.json.user.LoginRequestJson;
import io.sezon.sezon.model.json.user.LoginResponseJson;
import io.sezon.sezon.model.json.user.RegisterRequestJson;
import io.sezon.sezon.model.json.user.RegisterResponseJson;
import io.sezon.sezon.model.json.user.TopupRequestJson;
import io.sezon.sezon.model.json.user.TopupResponseJson;
import io.sezon.sezon.model.json.user.UpdateProfileRequestJson;
import io.sezon.sezon.model.json.user.UpdateProfileResponseJson;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by bradhawk on 10/13/2016.
 */

public interface UserService {

    @POST("pelanggan/login")
    Call<LoginResponseJson> login(@Body LoginRequestJson param);

    @POST("pelanggan/register_user")
    Call<RegisterResponseJson> register(@Body RegisterRequestJson param);

    @POST("pelanggan/get_saldo")
    Call<GetSaldoResponseJson> getSaldo(@Body GetSaldoRequestJson param);

    @GET("pelanggan/detail_fitur")
    Call<GetFiturResponseJson> getFitur();

    @POST("pelanggan/user_send_help")
    Call<HelpResponseJson> sendHelp(@Body HelpRequestJson param);

    @POST("pelanggan/update_profile")
    Call<UpdateProfileResponseJson> updateProfile(@Body UpdateProfileRequestJson param);

    @POST("pelanggan/change_password")
    Call<ChangePasswordResponseJson> changePassword(@Body ChangePasswordRequestJson param);

    @POST("book/user_cancel_transaction")
    Call<CancelBookResponseJson> cancelOrder(@Body CancelBookRequestJson param);

    @POST("pelanggan/check_version")
    Call<VersionResponseJson> checkVersion(@Body VersionRequestJson param);

    @POST("book/user_rate_driver")
    Call<RateDriverResponseJson> rateDriver(@Body RateDriverRequestJson param);

    @POST("pelanggan/verifikasi_topup")
    Call<TopupResponseJson> topUp(@Body TopupRequestJson param);

    @POST("pelanggan/complete_transaksi")
    Call<HistoryResponseJson> getCompleteHistory(@Body HistoryRequestJson param);

    @POST("pelanggan/inprogress_transaksi")
    Call<HistoryResponseJson> getOnProgressHistory(@Body HistoryRequestJson param);

    @GET("pelanggan/banner_promosi")
    Call<GetBannerResponseJson> getBanner();



}

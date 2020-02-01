package io.sezon.sezon.api.service;

import io.sezon.sezon.model.json.book.CheckStatusTransaksiRequest;
import io.sezon.sezon.model.json.book.CheckStatusTransaksiResponse;
import io.sezon.sezon.model.json.book.GetAdditionalMboxResponseJson;
import io.sezon.sezon.model.json.book.GetBarangTokoRequestJson;
import io.sezon.sezon.model.json.book.GetBarangTokoResponseJson;
import io.sezon.sezon.model.json.book.GetDataLaundryRequestJson;
import io.sezon.sezon.model.json.book.GetDataLaundryResponseJson;
import io.sezon.sezon.model.json.book.GetDataMserviceResponseJson;
import io.sezon.sezon.model.json.book.GetDataRestoByKategoriRequestJson;
import io.sezon.sezon.model.json.book.GetDataRestoByKategoriResponseJson;
import io.sezon.sezon.model.json.book.GetDataRestoRequestJson;
import io.sezon.sezon.model.json.book.GetDataRestoResponseJson;
import io.sezon.sezon.model.json.book.GetDataRestoranRequestJson;
import io.sezon.sezon.model.json.book.GetDataRestoranResponseJson;
import io.sezon.sezon.model.json.book.GetDataTokoByKategoriRequestJson;
import io.sezon.sezon.model.json.book.GetDataTokoByKategoriResponseJson;
import io.sezon.sezon.model.json.book.GetDataTokoRequestJson;
import io.sezon.sezon.model.json.book.GetDataTokoResponseJson;
import io.sezon.sezon.model.json.book.GetLayananLaundryRequestJson;
import io.sezon.sezon.model.json.book.GetLayananLaundryResponseJson;
import io.sezon.sezon.model.json.book.LiatLokasiDriverResponse;
import io.sezon.sezon.model.json.book.RequestBarangRequestJson;
import io.sezon.sezon.model.json.book.RequestBarangResponseJson;
import io.sezon.sezon.model.json.book.RequestLaundryRequestJson;
import io.sezon.sezon.model.json.book.RequestLaundryResponseJson;
import io.sezon.sezon.model.json.book.SearchElectronicRequest;
import io.sezon.sezon.model.json.book.SearchElectronicResponse;
import io.sezon.sezon.model.json.book.SearchLaundryRequest;
import io.sezon.sezon.model.json.book.SearchLaundryResponse;
import io.sezon.sezon.model.json.book.detailTransaksi.GetDataTransaksiMElectronicResponse;
import io.sezon.sezon.model.json.book.detailTransaksi.GetDataTransaksiMFoodResponse;
import io.sezon.sezon.model.json.book.detailTransaksi.GetDataTransaksiMLaundryResponse;
import io.sezon.sezon.model.json.book.detailTransaksi.GetDataTransaksiMMartResponse;
import io.sezon.sezon.model.json.book.detailTransaksi.GetDataTransaksiMSendResponse;
import io.sezon.sezon.model.json.book.detailTransaksi.GetDataTransaksiRequest;
import io.sezon.sezon.model.json.book.GetFoodRestoRequestJson;
import io.sezon.sezon.model.json.book.GetFoodRestoResponseJson;
import io.sezon.sezon.model.json.book.GetKendaraanAngkutResponseJson;
import io.sezon.sezon.model.json.book.GetNearBoxRequestJson;
import io.sezon.sezon.model.json.book.GetNearBoxResponseJson;
import io.sezon.sezon.model.json.book.GetNearRideCarRequestJson;
import io.sezon.sezon.model.json.book.GetNearRideCarResponseJson;
import io.sezon.sezon.model.json.book.GetNearServiceRequestJson;
import io.sezon.sezon.model.json.book.GetNearServiceResponseJson;
import io.sezon.sezon.model.json.book.MboxRequestJson;
import io.sezon.sezon.model.json.book.MboxResponseJson;
import io.sezon.sezon.model.json.book.MserviceRequestJson;
import io.sezon.sezon.model.json.book.MserviceResponseJson;
import io.sezon.sezon.model.json.book.RequestFoodRequestJson;
import io.sezon.sezon.model.json.book.RequestFoodResponseJson;
import io.sezon.sezon.model.json.book.RequestMartRequestJson;
import io.sezon.sezon.model.json.book.RequestMartResponseJson;
import io.sezon.sezon.model.json.book.RequestRideCarRequestJson;
import io.sezon.sezon.model.json.book.RequestRideCarResponseJson;
import io.sezon.sezon.model.json.book.RequestSendRequestJson;
import io.sezon.sezon.model.json.book.RequestSendResponseJson;
import io.sezon.sezon.model.json.book.SearchRestoranFoodRequest;
import io.sezon.sezon.model.json.book.SearchRestoranFoodResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import io.sezon.sezon.model.json.book.massage.DetailTransaksiRequest;
import io.sezon.sezon.model.json.book.massage.DetailTransaksiResponse;
import io.sezon.sezon.model.json.book.massage.GetLayananMassageResponseJson;
import io.sezon.sezon.model.json.book.massage.RequestMassageRequestJson;
import io.sezon.sezon.model.json.book.massage.RequestMassageResponseJson;

public interface BookService {

    @POST("book/list_driver_mride")
    Call<GetNearRideCarResponseJson> getNearRide(@Body GetNearRideCarRequestJson param);

    @POST("book/list_driver_mcar")
    Call<GetNearRideCarResponseJson> getNearCar(@Body GetNearRideCarRequestJson param);

    @POST("book/request_transaksi")
    Call<RequestRideCarResponseJson> requestTransaksi(@Body RequestRideCarRequestJson param);

    @POST("book/request_transaksi_mmart")
    Call<RequestMartResponseJson> requestTransaksiMMart(@Body RequestMartRequestJson param);

    @POST("book/request_transaksi_msend")
    Call<RequestSendResponseJson> requestTransMSend(@Body RequestSendRequestJson param);

    @GET("book/get_kendaraan_angkut")
    Call<GetKendaraanAngkutResponseJson> getKendaraanAngkut();

    @POST("book/list_driver_mbox")
    Call<GetNearBoxResponseJson> getNearBox(@Body GetNearBoxRequestJson param);

    @POST("book/request_transaksi_mbox")
    Call<MboxResponseJson> requestTransaksiMbox(@Body MboxRequestJson param);

    @GET("book/get_additional_mbox")
    Call<GetAdditionalMboxResponseJson> getAdditionalMbox();

    @POST("book/list_driver_mservice")
    Call<GetNearServiceResponseJson> getNearService(@Body GetNearServiceRequestJson param);

    @POST("book/request_transaksi_mservice")
    Call<MserviceResponseJson> requestTransaksi(@Body MserviceRequestJson param);

    @GET("book/get_data_mservice_ac")
    Call<GetDataMserviceResponseJson> getDataMservice();

    @GET("book/get_layanan_massage")
    Call<GetLayananMassageResponseJson> getLayananMassage();


    @POST("book/request_transaksi_mmassage")
    Call<RequestMassageResponseJson> requestTransaksiMMassage(@Body RequestMassageRequestJson param);

    @POST("book/list_driver_mmassage")
    Call<GetNearRideCarResponseJson> getNearMassage(@Body GetNearRideCarRequestJson param);

    @POST("book/get_data_transaksi_mmassage")
    Call<DetailTransaksiResponse> getDetailTransaksiMassage(@Body DetailTransaksiRequest param);

    @POST("book/get_data_restoran")
    Call<GetDataRestoResponseJson> getDataRestoran(@Body GetDataRestoRequestJson param);

    @POST("book/get_data_resto")
    Call<GetDataRestoranResponseJson> getDataRestoran1(@Body GetDataRestoranRequestJson param);

    @POST("book/get_food_in_resto")
    Call<GetFoodRestoResponseJson> getFoodResto(@Body GetFoodRestoRequestJson param);

    //Search item resto
    @POST("book/get_item_in_resto")
    Call<GetFoodRestoResponseJson> getMenuItem(@Body GetFoodRestoRequestJson param);

    @POST("book/search_restoran_or_food")
    Call<SearchRestoranFoodResponse> searchRestoranOrFood(@Body SearchRestoranFoodRequest param);

    @POST("book/get_resto_by_kategori")
    Call<GetDataRestoByKategoriResponseJson> getDataRestoranByKategori(@Body GetDataRestoByKategoriRequestJson param);

    @POST("book/request_transaksi_mfood")
    Call<RequestFoodResponseJson> requestTransaksiMFood(@Body RequestFoodRequestJson param);

    @POST("book/check_status_transaksi")
    Call<CheckStatusTransaksiResponse> checkStatusTransaksi(@Body CheckStatusTransaksiRequest param);

    @GET("book/liat_lokasi_driver/{id}")
    Call<LiatLokasiDriverResponse> liatLokasiDriver(@Path("id") String idDriver);

    @POST("book/get_data_order_mmassage")
    Call<String> getDataOrderMMassage(@Body GetDataTransaksiRequest param);

    @POST("book/get_data_transaksi_mfood")
    Call<String> getDataTransaksiMFood(@Body GetDataTransaksiRequest param);

    @POST("book/get_data_transaksi_mfood")
    Call<GetDataTransaksiMFoodResponse> getDataTransaksiMFoodDetail(@Body GetDataTransaksiRequest param);

    @POST("book/get_data_transaksi_mservice")
    Call<String> getDataTransaksiMService(@Body GetDataTransaksiRequest param);

    @POST("book/get_data_transaksi_mmart")
    Call<GetDataTransaksiMMartResponse> getDataTransaksiMMart(@Body GetDataTransaksiRequest param);

    @POST("book/get_data_transaksi_mbox")
    Call<String> getDataTransaksiMBox(@Body GetDataTransaksiRequest param);

    @POST("book/get_data_transaksi_msend")
    Call<GetDataTransaksiMSendResponse> getDataTransaksiMSend(@Body GetDataTransaksiRequest param);


    //service e-electronic
    @POST("book/get_item_in_store")
    Call<GetBarangTokoResponseJson> getBarangToko(@Body GetBarangTokoRequestJson param);
    @POST("book/get_data_store")
    Call<GetDataTokoResponseJson> getDataToko(@Body GetDataTokoRequestJson param);
    @POST("book/get_store_by_kategori")
    Call<GetDataTokoByKategoriResponseJson> getDataTokoByKategori(@Body GetDataTokoByKategoriRequestJson param);
    @POST("book/request_transaksi_mstore")
    Call<RequestBarangResponseJson> requestTransaksiEletronik(@Body RequestBarangRequestJson param);
    @POST("book/search_store_or_item")
    Call<SearchElectronicResponse> searchTokoOrElektronik(@Body SearchElectronicRequest param);
    @POST("book/get_data_transaksi_mstore")
    Call<GetDataTransaksiMElectronicResponse> getDataTransaksiMElectronicDetail(@Body GetDataTransaksiRequest param);

    //service e-laundry
    @POST("book/get_data_laundry")
    Call<GetDataLaundryResponseJson> getDataLaundry(@Body GetDataLaundryRequestJson param);


    @POST("book/get_promo_in_laundry") //SLIDE PROMO LAUNDRY.. GET DATA
    Call<GetLayananLaundryResponseJson> getPromoLaundry(@Body GetLayananLaundryRequestJson param);
    @POST("book/get_laundry_in_laundry")
    Call<GetLayananLaundryResponseJson> getSearchLaundry(@Body GetLayananLaundryRequestJson param);
    @POST("book/search_laundry")
    Call<SearchLaundryResponse> searchLaundry(@Body SearchLaundryRequest param);
    @POST("book/get_item_in_laundry")
    Call<GetLayananLaundryResponseJson> getMenuLaundry(@Body GetLayananLaundryRequestJson param);
    @POST("book/request_transaksi_mlaundry")
    Call<RequestLaundryResponseJson> requestTransaksiMLaundry(@Body RequestLaundryRequestJson param);
    @POST("book/get_data_transaksi_mlaundry")
    Call<GetDataTransaksiMLaundryResponse> getDataTransaksiMLaundryDetail(@Body GetDataTransaksiRequest param);


}

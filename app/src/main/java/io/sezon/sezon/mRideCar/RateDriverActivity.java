package io.sezon.sezon.mRideCar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import io.sezon.sezon.MangJekApplication;
import io.sezon.sezon.R;
import io.sezon.sezon.api.ServiceGenerator;
import io.sezon.sezon.api.service.UserService;
import io.sezon.sezon.home.MainActivity;
import io.sezon.sezon.model.User;
import io.sezon.sezon.model.json.book.RateDriverRequestJson;
import io.sezon.sezon.model.json.book.RateDriverResponseJson;
import io.sezon.sezon.utils.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RateDriverActivity extends AppCompatActivity {

    RateDriverActivity activity;
    float nilai;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_driver);

        activity = RateDriverActivity.this;

        TextView Biaya = (TextView) findViewById(R.id.biaya);
        TextView Tanggal = (TextView) findViewById(R.id.tanggal);
        TextView butSubmit = (TextView) findViewById(R.id.butSubmit);
        final RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        final EditText addComment = (EditText) findViewById(R.id.addComment);
        RoundedImageView logoFitur = (RoundedImageView) findViewById(R.id.logoFitur);

        final String idTransaksi = getIntent().getStringExtra("id_transaksi");
        final String idPelanggan = getIntent().getStringExtra("id_pelanggan");
        String driverPhoto = getIntent().getStringExtra("driver_photo");
        final String idDriver = getIntent().getStringExtra("id_driver");

        Glide.with(getApplicationContext()).load(driverPhoto).into(logoFitur);
        Log.d("Image driver : ","" +driverPhoto);

//        selectionFitur(orderFitur, logoFitur);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                nilai = v;
            }
        });



        butSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RateDriverRequestJson request = new RateDriverRequestJson();
                request.id_transaksi = idTransaksi;
                request.id_pelanggan = idPelanggan;
                request.id_driver = idDriver;
                request.rating = nilai+"";
                request.catatan = addComment.getText().toString();

                ratingUser(request);
//                Toast.makeText(RatingUserActivity.this, "Rating : "+(int)nilai+"\nKomentar : "+addComment.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        getIncomingIntent();
    }
    private void getIncomingIntent(){
        //  Log.d(TAG,"getIncomingIntent: checking for incoming intents.");

        if(getIntent().hasExtra("id_transaksi")){

            // String imageUrl=getIntent().getStringExtra("image_url");

            final String cost = getIntent().getStringExtra("harga");
            final String end_time = getIntent().getStringExtra("waktu_selesai");

        setImage(cost,end_time);
        }
    }
    private void setImage(String cost,String end_time){

        TextView tanggal,tarif;
        tarif =(TextView)findViewById(R.id.biaya);
        tanggal=(TextView)findViewById(R.id.tanggal);
        tarif.setText("$"+cost+".00");
        tanggal.setText("Finish Time :"+end_time);
    }
    private void selectionFitur(String fitur, ImageView logo){

        switch (fitur){
            case "1":
                logo.setImageResource(R.drawable.ic_e_ride_on);
                break;
            case "2":
                logo.setImageResource(R.drawable.ic_mcar);
                break;
            default:
                break;
        }
    }

    private void ratingUser(RateDriverRequestJson request){

        User loginUser = MangJekApplication.getInstance(RateDriverActivity.this).getLoginUser();

        final ProgressDialog pd = showLoading();
        UserService service = ServiceGenerator.createService(UserService.class, loginUser.getEmail(), loginUser.getPassword());
        service.rateDriver(request).enqueue(new Callback<RateDriverResponseJson>() {
            @Override
            public void onResponse(Call<RateDriverResponseJson> call, Response<RateDriverResponseJson> response) {
                if (response.isSuccessful()) {
                    if (response.body().mesage.equals("success")) {
                        finishDialog();
                    }
                }
                pd.dismiss();
            }

            @Override
            public void onFailure(Call<RateDriverResponseJson> call, Throwable t) {
                t.printStackTrace();
                pd.dismiss();
                Toast.makeText(RateDriverActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });



    }


    private ProgressDialog showLoading(){
        ProgressDialog ad = ProgressDialog.show(activity, "", "Loading...", true);
        return ad;
    }


    private void finishDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle("Gopickme");
        alertDialogBuilder.setMessage("Thank you for using our services!");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.dismiss();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    }
                });

//        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//
//            }
//        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        }

        }

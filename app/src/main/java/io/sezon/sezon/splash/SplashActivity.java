package io.sezon.sezon.splash;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;

import java.util.Calendar;

import butterknife.BindView;
import io.sezon.sezon.MangJekApplication;
import io.sezon.sezon.R;
import io.sezon.sezon.api.ServiceGenerator;
import io.sezon.sezon.api.service.UserService;
import io.sezon.sezon.home.MainActivity;
import io.sezon.sezon.model.User;
import io.sezon.sezon.model.json.menu.VersionRequestJson;
import io.sezon.sezon.model.json.menu.VersionResponseJson;
import io.sezon.sezon.signIn.SignInActivity;
import io.sezon.sezon.utils.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by bradhawk on 10/12/2016.
 */

public class SplashActivity extends AppCompatActivity {



    @BindView(R.id.gambar)
    ImageView imghari;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);





        PackageInfo pInfo;
        VersionRequestJson request = new VersionRequestJson();
        request.version = "1";
        request.application = "0";
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            request.version = pInfo.versionCode+"";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        UserService service = ServiceGenerator.createService(UserService.class, null, null);
        service.checkVersion(request).enqueue(new Callback<VersionResponseJson>() {
            @Override
            public void onResponse(Call<VersionResponseJson> call, Response<VersionResponseJson> response) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SplashActivity.this);
                alertDialogBuilder.setTitle("AJO");
                alertDialogBuilder.setMessage("New version is available.");

                if (response.isSuccessful()) {
                    if (response.body().new_version.equals("yes")) {
                        alertDialogBuilder.setMessage(response.body().message);
                        alertDialogBuilder.setPositiveButton("yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        final String appPackageName = getPackageName();
//                              final String appPackageName = "user.pacekurir.drivermangjek";
                                        try {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                        } catch (android.content.ActivityNotFoundException anfe) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                        }
                                    }
                                });

                        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                    } else if(response.body().new_version.equals("no")) {
                        start();
                    }else if(response.body().new_version.equals("maintenance")){
                        Log.e("VERSION","Maintenance");
                        alertDialogBuilder.setPositiveButton("dismiss",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int arg1) {
                                        dialog.dismiss();
                                        finish();
//                                        start();
                                    }
                                });

//                        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                                start();
//                            }
//                        });
                        alertDialogBuilder.setMessage(response.body().message);
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }else{
                        alertDialogBuilder.setPositiveButton("yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int arg1) {
                                        dialog.dismiss();
                                        start();
                                    }
                                });

                        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                start();
                            }
                        });
                        alertDialogBuilder.setMessage(response.body().message);
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }


            }

            @Override
            public void onFailure(Call<VersionResponseJson> call, Throwable t) {
                t.printStackTrace();
//                Toast.makeText(SplashActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Log.e("System error:", t.getLocalizedMessage());
//                restartActivity();
                start();
            }
        });



    }
    private void ucapan(){

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay >= 0 && timeOfDay < 12){
            //  selamat.setTextColor(this.getResources().getColor(R.color.white));
            //  kamu.setTextColor(this.getResources().getColor(R.color.white));
            imghari.setImageResource(R.drawable.morning);
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            imghari.setImageResource(R.drawable.noon);
        }else if(timeOfDay >= 16 && timeOfDay < 21){
            imghari.setImageResource(R.drawable.afternoon);
        }else if(timeOfDay >= 21 && timeOfDay < 24){
            imghari.setImageResource(R.drawable.night);
        }
    }

    private void restartActivity(){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void start(){
      new CountDownTimer(3000, 3000) {

          @Override
           public void onTick(long l) {

          }

          @Override
            public void onFinish() {
        User user = MangJekApplication.getInstance(getBaseContext()).getLoginUser();
        Intent intent;
        if (user != null) {
            intent = new Intent(SplashActivity.this, MainActivity.class);

        } else {
            intent = new Intent(SplashActivity.this, SignInActivity.class);
        }
        startActivity(intent);
            }
        }.start();
    }

}

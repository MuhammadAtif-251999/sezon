package io.sezon.sezon.home;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;

import java.text.NumberFormat;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import io.sezon.sezon.MangJekApplication;
import io.sezon.sezon.R;
import io.sezon.sezon.api.ServiceGenerator;
import io.sezon.sezon.api.service.UserService;
import io.sezon.sezon.home.submenu.TopUpActivity;
import io.sezon.sezon.home.submenu.help.HelpActivity;
import io.sezon.sezon.home.submenu.history.HistoryFragment;
import io.sezon.sezon.home.submenu.home.HomeFragment;
import io.sezon.sezon.home.submenu.setting.FAQActivity;
import io.sezon.sezon.home.submenu.setting.SettingFragment;
import io.sezon.sezon.home.submenu.setting.TermOfServiceActivity;
import io.sezon.sezon.home.submenu.setting.UpdateProfileActivity;
import io.sezon.sezon.model.DiskonMpay;
import io.sezon.sezon.model.Fitur;
import io.sezon.sezon.model.MfoodMitra;
import io.sezon.sezon.model.MlaundryMitra;
import io.sezon.sezon.model.User;
import io.sezon.sezon.model.json.user.GetFiturResponseJson;
import io.sezon.sezon.model.json.user.GetSaldoRequestJson;
import io.sezon.sezon.model.json.user.GetSaldoResponseJson;
import io.sezon.sezon.model.m_user;
import io.sezon.sezon.utils.Log;
import io.sezon.sezon.utils.MenuSelector;
import io.sezon.sezon.utils.SnackbarController;

import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by bradhawk on 10/10/2016.
 */

public class MainActivity extends AppCompatActivity implements SnackbarController,NavigationView.OnNavigationItemSelectedListener {




    private Snackbar snackBar;


    private MenuSelector selector;
    private SmartTabLayout.TabProvider tabProvider;

    private FragmentPagerItemAdapter adapter;

    boolean doubleBackToExitPressedOnce = false;
    private static final int REQUEST_PERMISSION_LOCATION = 991;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        //toolbar.setTitleTextColor(getResources().getColor(R.color.greyText));

        Bundle recv = getIntent().getExtras();
        activity = MainActivity.this;


        // getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_profile);

//        up = new UserPreference(activity);

        drawerLayout();



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            drawer.openDrawer(GravityCompat.END);
            closeLeftDrawer();
            return true;
        }
//        if (id == R.id.action_refresh) {
//            syncronizingAccount();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }



    Toolbar toolbar;
    MainActivity activity;

    DrawerLayout drawer;
    //    UserPreference up;

    public boolean statusFragment, ordering = false;
    ImageView imageDriver, image_kembali,set_kembali;
    //ImageView imageDriver;
    public TextView saldo, textRating;
    Intent service;
    ProgressDialog pd;
    int maxRetry1 = 4;

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        //calling the method displayselectedscreen and passing the id of selected menu
        //   displaySelectedScreen(item.getItemId());
        //make this method blank
        displaySelectedScreen(item.getItemId());
        return true;
    }

    private void drawerLayout(){

    drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        imageDriver = (ImageView)findViewById(R.id.image);
        image_kembali =(ImageView)findViewById(R.id.image_kembali);
        set_kembali =(ImageView)findViewById(R.id.setting_back);

        ImageView hamMenu = findViewById(R.id.image);
        hamMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DrawerLayout navDrawer = findViewById(R.id.drawer_layout);
                // If navigation drawer is not open yet open it else close it.
                if(!navDrawer.isDrawerOpen(GravityCompat.START)) navDrawer.openDrawer(Gravity.START);
                else navDrawer.closeDrawer(Gravity.END);
            }
        });

        image_kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DrawerLayout navDrawer = findViewById(R.id.drawer_layout);
                // If navigation drawer is not open yet open it else close it.
                if(!navDrawer.isDrawerOpen(GravityCompat.START)) navDrawer.openDrawer(Gravity.START);
                else navDrawer.closeDrawer(Gravity.END);
            }
        });
        set_kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DrawerLayout navDrawer = findViewById(R.id.drawer_layout);
                // If navigation drawer is not open yet open it else close it.
                if(!navDrawer.isDrawerOpen(GravityCompat.START)) navDrawer.openDrawer(Gravity.START);


                else navDrawer.closeDrawer(Gravity.END);
            }
        });
        NavigationView navViewLeft = (NavigationView) findViewById(R.id.nav_view);
        navViewLeft.setNavigationItemSelectedListener(this);


        int width = getResources().getDisplayMetrics().widthPixels*9/10;
        View headerL = navViewLeft.getHeaderView(0);
        initMenuDrawer(headerL);

        displaySelectedScreen(R.id.beranda);

    }
    @Override
    public void onBackPressed() {

        if(!ordering){
            exitByBackKey();
        }
    }

    public void changeFragment(Fragment frag, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.replace(R.id.content_frame, frag);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    protected void exitByBackKey() {

        if(statusFragment){
            changeFragment(new HomeFragment(), false);
            imageDriver.setVisibility(View.VISIBLE);
            image_kembali.setVisibility(View.GONE);
            set_kembali.setVisibility(View.GONE);
            statusFragment = false;
        }else{
            if(drawer.isDrawerOpen(GravityCompat.START) || drawer.isDrawerOpen(GravityCompat.END)){
                closeLeftDrawer();

            }else {
                showWarnExit();
            }
        }
    }
    private MaterialDialog showWarnExit(){
        final MaterialDialog md = new  MaterialDialog.Builder(activity)
                .title("Warning")
                .content("Do you want to exit the application?")
                .icon(new IconicsDrawable(activity)
                        .icon(FontAwesome.Icon.faw_exclamation_triangle)
                        .color(Color.RED)
                        .sizeDp(24))
                .positiveText("Yes")
                .positiveColor(Color.BLUE)
                .negativeColor(Color.DKGRAY)
                .negativeText("No")
                .show();

        View positive = md.getActionButton(DialogAction.POSITIVE);
        View negative = md.getActionButton(DialogAction.NEGATIVE);

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                md.dismiss();
                finish();
            }
        });
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                md.dismiss();
            }
        });

        return md;
    }
    private void closeLeftDrawer(){
        drawer.closeDrawer(GravityCompat.START);
    }


    private void displaySelectedScreen(int itemId) {

        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.beranda:
                fragment = new HomeFragment();
                break;
        }
        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    m_user data;
    @Override
    protected void onResume() {
        super.onResume();



        updateFiturMangJek();
    }


  private User loginUser;
    private void initMenuDrawer(View headerL){
        CardView menu_beranda, menu_history, menu_tarik, menu_poin,
                menu_dompet, menu_akun, menu_bantuan, menu_pengaturan,nilai,term,faq;
        TextView nama,email,nomorhp;

        RoundedImageView gambar;
            mPayBalance =(TextView)headerL.findViewById(R.id.home_mPayBalance);
        nama = (TextView)headerL.findViewById(R.id.setting_name1);
        email= (TextView)headerL.findViewById(R.id.setting_email1);
        nomorhp = (TextView)headerL.findViewById(R.id.setting_phone1);
        gambar = (RoundedImageView) headerL.findViewById(R.id.driver_image);


        final User user = MangJekApplication.getInstance(this).getLoginUser();
        final String user1 = MangJekApplication.getInstance(this).getLoginUser().getId();
        nama.setText(String.format("%s %s", user.getNamaDepan(), user.getNamaBelakang()));
        email.setText(user.getEmail());
        nomorhp.setText(user.getNoTelepon());

        nilai = (CardView)headerL.findViewById(R.id.rate);
        menu_beranda = (CardView) headerL.findViewById(R.id.beranda);
        menu_history = (CardView) headerL.findViewById(R.id.History);
        menu_tarik = (CardView) headerL.findViewById(R.id.tarik_dana);
        menu_poin = (CardView) headerL.findViewById(R.id.poin);
        menu_dompet = (CardView) headerL.findViewById(R.id.dompet);
        menu_akun = (CardView) headerL.findViewById(R.id.akun_saya);
        menu_bantuan = (CardView) headerL.findViewById(R.id.bantuan);
        menu_pengaturan = (CardView) headerL.findViewById(R.id.penganturan);
        term = (CardView) headerL.findViewById(R.id.setting_termOfService);
        faq = (CardView) headerL.findViewById(R.id.setting_faq);

        updateMPayBalance();


        term.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent toRate = new Intent(activity, TermOfServiceActivity.class);
                startActivity(toRate);

                if(ordering){
                }else {
                }
                closeLeftDrawer();
                statusFragment = true;
            }
        });
        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent toRate = new Intent(activity, FAQActivity.class);
                startActivity(toRate);

                if(ordering){
                }else {
                }
                closeLeftDrawer();
                statusFragment = true;
            }
        });


        menu_beranda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ordering){
                }else{
                    changeFragment(new HomeFragment(), false);
                    statusFragment = true;
                }
                closeLeftDrawer();

            }
        });
        menu_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ordering){
                }else{
                    changeFragment(new HistoryFragment(), false);
                    imageDriver.setVisibility(View.GONE);
                    image_kembali.setVisibility(View.VISIBLE);
                    statusFragment = true;
                }
                closeLeftDrawer();

            }
        });

        menu_bantuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent toRate = new Intent(activity, HelpActivity.class);
                startActivity(toRate);
                if(ordering){
                }else {
                }
                closeLeftDrawer();
                statusFragment = true;

            }
        });
        menu_akun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toRate = new Intent(activity, UpdateProfileActivity.class);
                startActivity(toRate);
                if(ordering){
                }else {
                }
                closeLeftDrawer();
                statusFragment = true;
            }
        });
        menu_pengaturan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ordering){
                }else {
                    imageDriver.setVisibility(View.GONE);
                    set_kembali.setVisibility(View.VISIBLE);
                    changeFragment(new SettingFragment(), false);
                    statusFragment = true;
                }
                closeLeftDrawer();
            }
        });
        menu_dompet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent toRate = new Intent(activity, TopUpActivity.class);
                startActivity(toRate);

                if(ordering){
                }else {
                }
                closeLeftDrawer();
                statusFragment = true;
            }
        });






        nilai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = MainActivity.this.getPackageName();
//                final String appPackageName = "user.pacekurir.drivermangjek";
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });


    }
    TextView mPayBalance;
    private int successfulCall;
    private void updateMPayBalance() {
        User loginUser = MangJekApplication.getInstance(MainActivity.this).getLoginUser();
        UserService userService = ServiceGenerator.createService(
                UserService.class, loginUser.getEmail(), loginUser.getPassword());

        GetSaldoRequestJson param = new GetSaldoRequestJson();
        param.setId(loginUser.getId());
        userService.getSaldo(param).enqueue(new Callback<GetSaldoResponseJson>() {
            @Override
            public void onResponse(Call<GetSaldoResponseJson> call, Response<GetSaldoResponseJson> response) {
                if (response.isSuccessful()) {
                    String formattedText = String.format(Locale.US, "%s.00",
                            NumberFormat.getNumberInstance(Locale.US).format(response.body().getData()));
                    mPayBalance.setText(formattedText);
                    successfulCall++;

                    if(MainActivity.this != null) {
                        Realm realm = MangJekApplication.getInstance(MainActivity.this).getRealmInstance();
                        User loginUser = MangJekApplication.getInstance(MainActivity.this).getLoginUser();
                        realm.beginTransaction();
                        loginUser.setmPaySaldo(response.body().getData());
                        realm.commitTransaction();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetSaldoResponseJson> call, Throwable t) {

            }
        });
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    private void updateFiturMangJek() {
        User loginUser = MangJekApplication.getInstance(this).getLoginUser();
        UserService userService = ServiceGenerator.createService(UserService.class,
                loginUser.getEmail(), loginUser.getPassword());
        userService.getFitur().enqueue(new Callback<GetFiturResponseJson>() {
            @Override
            public void onResponse(Call<GetFiturResponseJson> call, Response<GetFiturResponseJson> response) {
                if (response.isSuccessful()) {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
                        return;
                    }
                    Realm realm = MangJekApplication.getInstance(MainActivity.this).getRealmInstance();
                    realm.beginTransaction();
                    realm.delete(Fitur.class);
                    realm.copyToRealm(response.body().getData());
                    realm.commitTransaction();

                    DiskonMpay diskonMpay = response.body().getDiskonMpay();
                    realm.beginTransaction();
                    realm.delete(DiskonMpay.class);
                    realm.copyToRealm(response.body().getDiskonMpay());
                    realm.commitTransaction();
                    MangJekApplication.getInstance(MainActivity.this).setDiskonMpay(diskonMpay);

                    MfoodMitra mfoodMitra = response.body().getMfoodMitra();
                    realm.beginTransaction();
                    realm.delete(MfoodMitra.class);
                    realm.copyToRealm(response.body().getMfoodMitra());
                    realm.commitTransaction();
                    MangJekApplication.getInstance(MainActivity.this).setMfoodMitra(mfoodMitra);

                    MlaundryMitra mlaundryMitra = response.body().getMlaundryMitra();
                    realm.beginTransaction();
                    realm.delete(MlaundryMitra.class);
                    realm.copyToRealm(response.body().getMlaundryMitra());
                    realm.commitTransaction();
                    MangJekApplication.getInstance(MainActivity.this).setMLaundrykMitra(mlaundryMitra);

                    Log.e("diskonlaundry",mlaundryMitra.getDiskon()+"");
                    Log.e("diskonfood",mfoodMitra.getDiskon()+"");
                }
            }

            @Override
            public void onFailure(Call<GetFiturResponseJson> call, Throwable t) {

            }
        });
    }

    @Override
    public void showSnackbar(int stringRes, int duration, int actionResText, View.OnClickListener onClickListener) {

    }
}

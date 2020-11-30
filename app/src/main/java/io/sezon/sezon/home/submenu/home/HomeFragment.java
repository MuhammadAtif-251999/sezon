package io.sezon.sezon.home.submenu.home;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;

import io.sezon.sezon.MangJekApplication;
import io.sezon.sezon.R;
import io.sezon.sezon.adapter.ItemOffsetDecoration;
import io.sezon.sezon.adapter.response;
import io.sezon.sezon.api.ServiceGenerator;
import io.sezon.sezon.api.service.BookService;
import io.sezon.sezon.api.service.UserService;
import io.sezon.sezon.api.service.jason;
import io.sezon.sezon.barcode.ScanActivity;
import io.sezon.sezon.home.submenu.TopUpActivity;
import io.sezon.sezon.home.submenu.setting.UpdateProfileActivity;
import io.sezon.sezon.mBox.BoxActivity;
import io.sezon.sezon.mBox.BoxOrder;
import io.sezon.sezon.mBox.CargoItem;
import io.sezon.sezon.mFood.FoodActivity;
import io.sezon.sezon.mFood.KategoriItemHome;
import io.sezon.sezon.mFood.KategoriSelectActivity;
import io.sezon.sezon.mFood.SlideRestoFragment;
import io.sezon.sezon.mLaundry.LaundryActivity;
import io.sezon.sezon.mLaundry.LaundryItemHome;
import io.sezon.sezon.mLaundry.LaundryMenuActivity;
import io.sezon.sezon.mLaundry.SlideLaundryFragment;
import io.sezon.sezon.mMart.MartActivity;
import io.sezon.sezon.mMassage.MassageActivity;
import io.sezon.sezon.mRideCar.RideCarActivity;
import io.sezon.sezon.mSend.SendActivity;
import io.sezon.sezon.mService.mServiceActivity;
import io.sezon.sezon.model.Banner;
import io.sezon.sezon.model.DataLaundry;
import io.sezon.sezon.model.DataRestoran;
import io.sezon.sezon.model.Fitur;
import io.sezon.sezon.model.KategoriRestoran;
import io.sezon.sezon.model.KendaraanAngkut;
import io.sezon.sezon.model.Laundry;
import io.sezon.sezon.model.LaundryNearMeDB;
import io.sezon.sezon.model.PromosiMFood;
import io.sezon.sezon.model.PromosiMLaundry;
import io.sezon.sezon.model.RestoranNearMeDB;
import io.sezon.sezon.model.User;
import io.sezon.sezon.model.data;
import io.sezon.sezon.model.json.book.GetDataLaundryRequestJson;
import io.sezon.sezon.model.json.book.GetDataLaundryResponseJson;
import io.sezon.sezon.model.json.book.GetDataRestoRequestJson;
import io.sezon.sezon.model.json.book.GetDataRestoResponseJson;
import io.sezon.sezon.model.json.book.GetKendaraanAngkutResponseJson;
import io.sezon.sezon.model.json.user.GetBannerResponseJson;
import io.sezon.sezon.model.json.user.GetSaldoRequestJson;
import io.sezon.sezon.model.json.user.GetSaldoResponseJson;
import io.sezon.sezon.response_final_ouutput;
import io.sezon.sezon.splash.SplashActivity;
import io.sezon.sezon.utils.ConnectivityUtils;
import io.sezon.sezon.utils.Log;
import io.sezon.sezon.utils.SnackbarController;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import io.realm.Realm;
import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class HomeFragment extends Fragment {

    @BindView(R.id.home_mCar)
    CardView buttonMangCar;

    @BindView(R.id.home_mRide)
    CardView buttonMangRide;

    @BindView(R.id.home_mSend)
    CardView buttonMangSend;

    @BindView(R.id.home_mBox)
    CardView buttonMangBox;

    @BindView(R.id.home_mMart)
    RelativeLayout buttonMangMart;
    @BindView(R.id.profil)
    RelativeLayout profil;

    @BindView(R.id.home_mMassage)
    RelativeLayout buttonMangMassage;

    @BindView(R.id.home_mFood)
    CardView buttonMangFood;


    @BindView(R.id.home_mPayBalance)
    TextView mPayBalance;

    @BindView(R.id.home_mPayBalance1)
    TextView mPayBalance1;


    @BindView(R.id.ucap)
    TextView selamat;

    @BindView(R.id.kamu)
    TextView kamu;
    @BindView(R.id.slide_viewPager1)
    AutoScrollViewPager autoScrollViewPager1;

    @BindView(R.id.slide_viewPager_indicator1)
    CircleIndicator circleIndicator1;

    @BindView(R.id.home_topUpButton)
    RelativeLayout topUpButton;

    @BindView(R.id.home_mLaundry)
    CardView buttonLaundry;

    @BindView(R.id.home_mElectronic)
    RelativeLayout buttonElektronic;

    @BindView(R.id.barcode)
    RelativeLayout barCode;

    @BindView(R.id.imghari)
    ImageView imgHari;

    @BindView(R.id.slide_viewPager)
    AutoScrollViewPager slideViewPager;

    @BindView(R.id.slide_viewPager_indicator)
    CircleIndicator slideIndicator;

    @BindView(R.id.kategori_recycler)
    RecyclerView kategoriRecyler;

    @BindView(R.id.listLaundry_recycler)
    RecyclerView daftarLaundry;

//    @BindView(R.id.cargo_type_recyclerView)
//    RecyclerView cargoTypeRecyclerView;

    @BindView(R.id.slide_viewPager2)
    AutoScrollViewPager autoScrollViewPager2;

    @BindView(R.id.slide_viewPager_indicator2)
    CircleIndicator circleIndicator2;


    private SnackbarController snackbarController;

    private Location lastKnownLocation;
    private List<KategoriRestoran> kategoriRestoran;
    private List<RestoranNearMeDB> restoran;
    private FastItemAdapter<KategoriItemHome> kategoriAdapter;

    RecyclerView recyclerView;
    jason jasonApi;
    response recyclerAdapter;
    GridLayoutManager layoutManager;
    ArrayList<data> list = new ArrayList<>();
    private List<Laundry> laundryAll;
    private List<LaundryNearMeDB> laundryByLocation;
    private FastItemAdapter<LaundryItemHome> adapterListLaundry;


    private boolean requestUpdate = true;

    private boolean connectionAvailable;
    private boolean isDataLoaded = false;

    private Realm realm;
    private Realm realm1;

    private int successfulCall;

    public ArrayList<Banner> banners = new ArrayList<>();
    int featureId;
    FastItemAdapter<CargoItem> cargodapter;




    private MassageActivity activity;

    public static final String FITUR_KEY = "FiturKey";
    public static final String CARGO = "cargo";
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SnackbarController) {
            snackbarController = (SnackbarController) context;
        }
    }

    private User loginUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);



    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        realm1 = Realm.getDefaultInstance();
        LoadKendaraan();
        cargodapter = new FastItemAdapter<>();
//        cargoTypeRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
//        cargoTypeRecyclerView.setAdapter(cargodapter);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://svz.sezon.live/grocery/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jasonApi = retrofit.create(jason.class);


        recyclerView = view.findViewById(R.id.recyclerview);
        layoutManager = new GridLayoutManager(getContext(),2,GridLayoutManager.HORIZONTAL,false);
        recyclerAdapter = new response(getContext(), list);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setHasFixedSize(true);
        getdata();

        cargodapter.withItemEvent(new ClickEventHook<CargoItem>() {
            @Nullable
            @Override
            public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof CargoItem.ViewHolder) {
                    return ((CargoItem.ViewHolder) viewHolder).itemView;
                }
                return null;
            }

            @Override
            public void onClick(View v, int position, FastAdapter<CargoItem> fastAdapter, CargoItem item) {
                KendaraanAngkut selectedCargo = realm1.where(KendaraanAngkut.class).equalTo("idKendaraan", cargodapter.getAdapterItem(position).id).findFirst();
                Log.e("BUTTON","CLICKED, ID : "+selectedCargo.getIdKendaraan());
                Intent intent = new Intent(getActivity(), BoxOrder.class);
                intent.putExtra(BoxOrder.FITUR_KEY, featureId);
                intent.putExtra(BoxOrder.KENDARAAN_KEY, selectedCargo.getIdKendaraan());
                startActivity(intent);
            }
        });

        kategoriAdapter = new FastItemAdapter<>();
        getDataRestoran();
        KategoriRecycler();

        adapterListLaundry = new FastItemAdapter<>();
        getDataLaundry();
        DaftarLaundryRecycler();

        buttonMangRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMangRideClick();
            }
        });
        profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profil();
            }
        });
        buttonMangCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMangCarClick();
            }
        });
        buttonMangSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMangSendClick();
            }
        });
        buttonMangBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMangBoxClick();
            }
        });
        connectionAvailable = false;
        topUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTopUpClick();
            }
        });
        barCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Barcode();
            }
        });
        buttonMangMart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMangMartClick();
            }
        });

        buttonMangFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMangFoodClick();
            }
        });
        buttonLaundry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLaundryClick();
            }
        });
        buttonElektronic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onElectronicClick();
            }
        });
        buttonMangMassage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMMassageClick();
            }
        });


        realm = MangJekApplication.getInstance(getActivity()).getRealmInstance();
        getImageBanner();

        Intent intent = getActivity().getIntent();
        featureId = intent.getIntExtra(FITUR_KEY, 7);


        ucapan();
    }

    private void getdata() {

        Call<response_final_ouutput> ouutputCall =jasonApi.getFinaloutput();

        ouutputCall.enqueue(new Callback<response_final_ouutput>() {
            @Override
            public void onResponse(Call<response_final_ouutput> call, Response<response_final_ouutput> res) {
                if (res.isSuccessful()) {

                    response_final_ouutput response = res.body();
                    if (res.body().getListData() == null){

                    }
                    else {
                        list = response.getListData();
                        recyclerAdapter = new response(getContext(), list);
                        recyclerAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(recyclerAdapter);
                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2,GridLayoutManager.HORIZONTAL,false));


                        Log.d("Home Fragment",response.getListData().toString());
                    }

                }

            }

            @Override
            public void onFailure(Call<response_final_ouutput> call, Throwable t) {

            }
        });
    }

    private void getImageBanner() {
        User loginUser = new User();
        if (MangJekApplication.getInstance(getActivity()).getLoginUser() != null) {
            loginUser = MangJekApplication.getInstance(getActivity()).getLoginUser();
        } else {
            startActivity(new Intent(getActivity(), SplashActivity.class));
            getActivity().finish();
        }

        UserService userService = ServiceGenerator.createService(UserService.class,
                loginUser.getEmail(), loginUser.getPassword());
        userService.getBanner().enqueue(new Callback<GetBannerResponseJson>() {
            @Override
            public void onResponse(Call<GetBannerResponseJson> call, Response<GetBannerResponseJson> response) {
                if (response.isSuccessful()) {
                    banners = response.body().data;
                    Log.e("Image", response.body().data.get(0).foto);
                    MyPagerAdapter pagerAdapter = new MyPagerAdapter(getChildFragmentManager(), banners);
                    slideViewPager.setAdapter(pagerAdapter);
                    slideIndicator.setViewPager(slideViewPager);
                    slideViewPager.setInterval(3000);
                    slideViewPager.startAutoScroll(3000);
                }
            }

            @Override
            public void onFailure(Call<GetBannerResponseJson> call, Throwable t) {

            }
        });
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 5;
        public ArrayList<Banner> banners = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fragmentManager, ArrayList<Banner> banners) {
            super(fragmentManager);
            this.banners = banners;
        }

        @Override
        public int getCount() {
            return banners.size();
        }

        @Override
        public Fragment getItem(int position) {
            return SlideFragment.newInstance(banners.get(position).id, banners.get(position).foto);

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        successfulCall = 0;
        connectionAvailable = ConnectivityUtils.isConnected(getActivity());
        if (!connectionAvailable) {
            if (snackbarController != null) snackbarController.showSnackbar(
                    R.string.text_noInternet, Snackbar.LENGTH_INDEFINITE, R.string.text_close,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            return;
                        }
                    });
        } else {
            updateMPayBalance();

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
    private void onMangSendClick() {
        //    Toast.makeText(getActivity().getApplicationContext(), "Maaf untuk sementara layanan tidak tersedia", Toast.LENGTH_SHORT).show();

        Fitur selectedFitur = realm.where(Fitur.class).equalTo("idFitur", 5).findFirst();
        Intent intent = new Intent(getActivity(), SendActivity.class);
        intent.putExtra(SendActivity.FITUR_KEY, selectedFitur.getIdFitur());
        getActivity().startActivity(intent);


    }

    private void onTopUpClick() {
        Intent intent = new Intent(getActivity(), TopUpActivity.class);
        startActivity(intent);
    }
    private void Barcode() {
            //Toast.makeText(getActivity().getApplicationContext(), "Maaf untuk sementara layanan tidak tersedia", Toast.LENGTH_SHORT).show();

       Intent intent = new Intent(getActivity(), ScanActivity.class);
       startActivity(intent);
    }
    private void profil() {
        //Toast.makeText(getActivity().getApplicationContext(), "Maaf untuk sementara layanan tidak tersedia", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getActivity(), UpdateProfileActivity.class);
        startActivity(intent);
    }
    private void onMangRideClick() {
//        Toast.makeText(getActivity().getApplicationContext(), "Maaf untuk sementara layanan tidak tersedia", Toast.LENGTH_SHORT).show();
        Fitur selectedFitur = realm.where(Fitur.class).equalTo("idFitur", 1).findFirst();
        Intent intent = new Intent(getActivity(), RideCarActivity.class);
        intent.putExtra(RideCarActivity.FITUR_KEY, selectedFitur.getIdFitur());
        getActivity().startActivity(intent);
    }

    private void onMangCarClick() {
        //Toast.makeText(getActivity().getApplicationContext(), "Maaf untuk sementara layanan tidak tersedia", Toast.LENGTH_SHORT).show();

         Fitur selectedFitur = realm.where(Fitur.class).equalTo("idFitur", 2).findFirst();
         Intent intent = new Intent(getActivity(), RideCarActivity.class);
         intent.putExtra(RideCarActivity.FITUR_KEY, selectedFitur.getIdFitur());
        getActivity().startActivity(intent);
    }

    private void onMangMartClick() {
        Fitur selectedFitur = realm.where(Fitur.class).equalTo("idFitur", 4).findFirst();
        Intent intent = new Intent(getActivity(), MartActivity.class);
        intent.putExtra(MartActivity.FITUR_KEY, selectedFitur.getIdFitur());
        getActivity().startActivity(intent);
    }

    private void onMangBoxClick() {
      //  Toast.makeText(getActivity().getApplicationContext(), "Maaf untuk sementara layanan tidak tersedia", Toast.LENGTH_SHORT).show();

        Fitur selectedFitur = realm.where(Fitur.class).equalTo("idFitur", 7).findFirst();
        Intent intent = new Intent(getActivity(), BoxActivity.class);
        intent.putExtra(BoxActivity.FITUR_KEY, selectedFitur.getIdFitur());
        getActivity().startActivity(intent);
    }

    private void onMMassageClick() {
      //  Toast.makeText(getActivity().getApplicationContext(), "Maaf untuk sementara layanan tidak tersedia", Toast.LENGTH_SHORT).show();
      Fitur selectedFitur = realm.where(Fitur.class).equalTo("idFitur", 6).findFirst();
      Intent intent = new Intent(getActivity(), MassageActivity.class);
       intent.putExtra(mServiceActivity.FITUR_KEY, selectedFitur.getIdFitur());
      getActivity().startActivity(intent);

    }

    private void onMangFoodClick() {
        Fitur selectedFitur = realm.where(Fitur.class).equalTo("idFitur", 3).findFirst();
        Intent intent = new Intent(getActivity(), FoodActivity.class);
        intent.putExtra(FoodActivity.FITUR_KEY, selectedFitur.getIdFitur());
        Log.d("Food","fitur selected : " + selectedFitur + " id fitur : " + selectedFitur.getIdFitur());
        getActivity().startActivity(intent);
    }

    private void onLaundryClick() {
//        Toast.makeText(getActivity().getApplicationContext(), "Maaf untuk sementara layanan tidak tersedia", Toast.LENGTH_SHORT).show();

        Fitur selectedFitur = realm.where(Fitur.class).equalTo("idFitur", 10).findFirst();
        Intent intent = new Intent(getActivity(), LaundryActivity.class);
        intent.putExtra(LaundryActivity.FITUR_KEY, selectedFitur.getIdFitur());
        Log.d("Food","fitur selected : " + selectedFitur + " id fitur : " + selectedFitur.getIdFitur());
        Log.e("cklik launfry",selectedFitur.getFitur()+"");
        getActivity().startActivity(intent);
    }

    private void onElectronicClick() {
        Toast.makeText(getActivity().getApplicationContext(), "Maaf untuk sementara layanan tidak tersedia", Toast.LENGTH_SHORT).show();


    }
    private void ucapan(){

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay >= 0 && timeOfDay < 12){
            selamat.setText("Good Morning");
            selamat.setTextColor(this.getResources().getColor(R.color.white));
            kamu.setTextColor(this.getResources().getColor(R.color.white));
            imgHari.setImageResource(R.drawable.siang);
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            selamat.setText("Good Afternoon");
            selamat.setTextColor(this.getResources().getColor(R.color.white));
            kamu.setTextColor(this.getResources().getColor(R.color.white));
            imgHari.setImageResource(R.drawable.pagi);
        }else if(timeOfDay >= 16 && timeOfDay < 21){
            selamat.setText("Good Afteroon " );
            selamat.setTextColor(this.getResources().getColor(R.color.white));
            kamu.setTextColor(this.getResources().getColor(R.color.white));
            imgHari.setImageResource(R.drawable.sore);
        }else if(timeOfDay >= 21 && timeOfDay < 24){
            selamat.setText("Good Night");
            selamat.setTextColor(this.getResources().getColor(R.color.white));
            kamu.setTextColor(this.getResources().getColor(R.color.white));
            imgHari.setImageResource(R.drawable.malam);
        }
    }
    private void updateMPayBalance() {
        User loginUser = MangJekApplication.getInstance(getActivity()).getLoginUser();
        UserService userService = ServiceGenerator.createService(
                UserService.class, loginUser.getEmail(), loginUser.getPassword());

        GetSaldoRequestJson param = new GetSaldoRequestJson();
        param.setId(loginUser.getId());
        userService.getSaldo(param).enqueue(new Callback<GetSaldoResponseJson>() {
            @Override
            public void onResponse(Call<GetSaldoResponseJson> call, Response<GetSaldoResponseJson> response) {
                if (response.isSuccessful()) {
                    String formattedText = String.format(Locale.US, "$%s.00",
                            NumberFormat.getNumberInstance(Locale.US).format(response.body().getData()));
                    String formattedText1 = String.format(Locale.US, "$%s.00",
                            NumberFormat.getNumberInstance(Locale.US).format(response.body().getData()));
                    mPayBalance.setText(formattedText);
                    mPayBalance1.setText(formattedText1);
                    successfulCall++;

                    if(HomeFragment.this.getActivity() != null) {
                        Realm realm = MangJekApplication.getInstance(HomeFragment.this.getActivity()).getRealmInstance();
                        User loginUser = MangJekApplication.getInstance(HomeFragment.this.getActivity()).getLoginUser();
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
    private void getDataRestoran() {
        if (snackbarController != null) {
            User loginUser = MangJekApplication.getInstance(getActivity()).getLoginUser();
            BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
            GetDataRestoRequestJson param = new GetDataRestoRequestJson();

            service.getDataRestoran(param).enqueue(new Callback<GetDataRestoResponseJson>() {
                @Override
                public void onResponse(Call<GetDataRestoResponseJson> call, Response<GetDataRestoResponseJson> response) {
                    if (response.isSuccessful()) {
                        DataRestoran dataRestoran = response.body().getDataRestoran();
                        kategoriRestoran = dataRestoran.getKategoriRestoranList();
                        restoran = dataRestoran.getRestoranList();
                        Realm realm = MangJekApplication.getInstance(HomeFragment.this.getActivity()).getRealmInstance();
                        realm.beginTransaction();
                        realm.delete(KategoriRestoran.class);
                        realm.copyToRealm(kategoriRestoran);
                        realm.commitTransaction();

                        realm.beginTransaction();
                        realm.delete(RestoranNearMeDB.class);
                        realm.copyToRealm(restoran);
                        realm.commitTransaction();

                        kategoriAdapter.clear();
                        KategoriItemHome kategoriItem;
                        for (KategoriRestoran kategori : kategoriRestoran) {
                            kategoriItem = new KategoriItemHome(HomeFragment.this.getActivity());
                            kategoriItem.idKategori = kategori.getIdKategori();
                            kategoriItem.kategori = kategori.getKategori();
                            kategoriItem.image = kategori.getFotoKategori();
                            kategoriAdapter.add(kategoriItem);
                            Log.e("ADD KATEGORI", kategori.getIdKategori() + "");
                        }

                        List<PromosiMFood> promosiMFoods = dataRestoran.getPromosiMFood();
//                        Toast.makeText(FoodActivity.this, "size = "+promosiMFoods.size(), Toast.LENGTH_SHORT).show();
                        MyPagerAdapter2 pagerAdapter = new MyPagerAdapter2(getChildFragmentManager(), promosiMFoods);
                        autoScrollViewPager2.setAdapter(pagerAdapter);
                        circleIndicator2.setViewPager(autoScrollViewPager2);
                        autoScrollViewPager2.setInterval(4000);
                        autoScrollViewPager2.startAutoScroll(4000);

                    }
                }

                @Override
                public void onFailure(Call<GetDataRestoResponseJson> call, Throwable t) {
                    Toast.makeText(getActivity(), "Connection to server lost, check your internet connection.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static class MyPagerAdapter2 extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 5;
        public List<PromosiMFood> banners = new ArrayList<>();

        public MyPagerAdapter2(FragmentManager fragmentManager, List<PromosiMFood> banners) {
            super(fragmentManager);
            this.banners = banners;
        }

        @Override
        public int getCount() {
            return banners.size();
        }

        @Override
        public Fragment getItem(int position) {
            return SlideRestoFragment.newInstance(banners.get(position).getId(),
                    banners.get(position).getFoto(),
                    banners.get(position).getIdResto());

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }

    private void KategoriRecycler() {
        kategoriRecyler.setLayoutManager(new GridLayoutManager(getActivity(),2, LinearLayout.HORIZONTAL,false));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(HomeFragment.this.getActivity(), R.dimen.item_offset);
        kategoriRecyler.addItemDecoration(itemDecoration);
        kategoriRecyler.setNestedScrollingEnabled(false);
        kategoriRecyler.setAdapter(kategoriAdapter);
        kategoriAdapter.withOnClickListener(new FastAdapter.OnClickListener<KategoriItemHome>() {
            @Override
            public boolean onClick(View v, IAdapter<KategoriItemHome> adapter, KategoriItemHome item, int position) {
                Log.e("BUTTON", "CLICKED");
//                KategoriRestoran selectedKategori = realm.where(KategoriRestoran.class).equalTo("idKategori", kategoriAdapter.getAdapterItem(position).idKategori).findFirst();
//                Intent intent = new Intent(FoodActivity.this, BoxOrder.class);
//                intent.putExtra(BoxOrder.KENDARAAN_KEY, selectedKategori.getIdKategori());
//                startActivity(intent);
                Intent intent = new Intent(HomeFragment.this.getActivity(), KategoriSelectActivity.class);
                intent.putExtra(KategoriSelectActivity.KATEGORI_ID, String.valueOf(item.idKategori));
                startActivity(intent);
                return true;
            }
        });
    }



    private void DaftarLaundryRecycler() {

        daftarLaundry.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayout.HORIZONTAL,false));
        daftarLaundry.setAdapter(adapterListLaundry);
        adapterListLaundry.withOnClickListener(new FastAdapter.OnClickListener<LaundryItemHome>() {
            @Override
            public boolean onClick(View v, IAdapter<LaundryItemHome> adapter, LaundryItemHome item, int position) {
                Log.e("BUTTON", "CLICKED");
                Intent intent = new Intent(HomeFragment.this.getActivity(), LaundryMenuActivity.class);
                intent.putExtra(LaundryMenuActivity.ID_LAUNDRY, item.id);
                intent.putExtra(LaundryMenuActivity.NAMA_LAUNDRY, item.namaLaundry);
                intent.putExtra(LaundryMenuActivity.ALAMAT_LAUNDRY, item.alamat);
                intent.putExtra(LaundryMenuActivity.DISTANCE_LAUNDRY, item.distance);
                intent.putExtra(LaundryMenuActivity.JAM_BUKA, item.jamBuka);
                intent.putExtra(LaundryMenuActivity.JAM_TUTUP, item.jamTutup);
                intent.putExtra(LaundryMenuActivity.IS_OPEN, item.isOpen);
                intent.putExtra(LaundryMenuActivity.PICTURE_URL, item.pictureUrl);
                intent.putExtra(LaundryMenuActivity.IS_MITRA, item.isMitra);
                startActivity(intent);
                return true;
            }
        });




    }

    private void getDataLaundry() {
        if (snackbarController != null) {
            User loginUser = MangJekApplication.getInstance(getActivity()).getLoginUser();
            BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
            GetDataLaundryRequestJson param = new GetDataLaundryRequestJson();

            service.getDataLaundry(param).enqueue(new Callback<GetDataLaundryResponseJson>() {
                @Override
                public void onResponse(Call<GetDataLaundryResponseJson> call, Response<GetDataLaundryResponseJson> response) {
                    if (response.isSuccessful()) {
                        DataLaundry dataLaundry = response.body().getDataLaundry();
                        laundryAll = dataLaundry.getLaundryAll();
                        laundryByLocation = dataLaundry.getLaundryList();
                        Log.d(LaundryActivity.class.getSimpleName(), "Number of laundry: " + laundryAll.size());
                        Log.d(Laundry.class.getSimpleName(), "Number of laundry by location: " + laundryByLocation.size());
                        Realm realm = MangJekApplication.getInstance(HomeFragment.this.getActivity()).getRealmInstance();
                        realm.beginTransaction();
                        realm.delete(Laundry.class);
                        realm.copyToRealm(laundryAll);
                        realm.commitTransaction();

                        realm.beginTransaction();
                        realm.delete(LaundryNearMeDB.class);
                        realm.copyToRealm(laundryByLocation);
                        realm.commitTransaction();

                        adapterListLaundry.clear();
                        LaundryItemHome laundryItem;
                        for (int i = 0; i < laundryAll.size(); i++) {
                            laundryItem = new LaundryItemHome(HomeFragment.this.getActivity());
                            laundryItem.id = laundryAll.get(i).getId();
                            laundryItem.namaLaundry = laundryAll.get(i).getNamaLaundry();
                            laundryItem.alamat = laundryAll.get(i).getAlamat();
                            laundryItem.distance = laundryAll.get(i).getDistance();
                            laundryItem.jarak = laundryAll.get(i).getDistance();
                            laundryItem.jamBuka = laundryAll.get(i).getJamBuka();
                            laundryItem.jamTutup = laundryAll.get(i).getJamTutup();
                            laundryItem.fotoLaundry = laundryAll.get(i).getFotoLaundry();
                            laundryItem.isOpen = laundryAll.get(i).isOpen();
                            laundryItem.pictureUrl = laundryAll.get(i).getFotoLaundry();
                            laundryItem.isMitra = laundryAll.get(i).isPartner();
                            adapterListLaundry.add(laundryItem);
                            Log.e("LAUNDRY", laundryItem.namaLaundry + "");
                            Log.e("LAUNDRY", laundryItem.alamat + "");
                            Log.e("LAUNDRY", laundryItem.jamBuka + "");
                            Log.e("LAUNDRY", laundryItem.jamTutup + "");
                            Log.e("LAUNDRY", laundryItem.fotoLaundry + "");

                        }
                        List<PromosiMLaundry> promosiMLaundry = dataLaundry.getPromosiMLaundry();
//                        Toast.makeText(FoodActivity.this, "size = "+promosiMFoods.size(), Toast.LENGTH_SHORT).show();
                      MyPagerAdapter1 pagerAdapter = new MyPagerAdapter1(getChildFragmentManager(), promosiMLaundry);
                        autoScrollViewPager1.setAdapter(pagerAdapter);
                        circleIndicator1.setViewPager(autoScrollViewPager1);
                        autoScrollViewPager1.setInterval(3000);
                        autoScrollViewPager1.startAutoScroll(3000);
                    }
                }

                @Override
                public void onFailure(Call<GetDataLaundryResponseJson> call, Throwable t) {
//                    Toast.makeText(getApplicationContext(), "Connection to server lost, check your internet connection.",
//                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public static class MyPagerAdapter1 extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 5;
        public List<PromosiMLaundry> banners = new ArrayList<>();

        public MyPagerAdapter1(FragmentManager fragmentManager, List<PromosiMLaundry> banners) {
            super(fragmentManager);
            this.banners = banners;
        }

        @Override
        public int getCount() {
            return banners.size();
        }

        @Override
        public Fragment getItem(int position) {
            return SlideLaundryFragment.newInstance(banners.get(position).getId(),
                    banners.get(position).getFoto(),
                    banners.get(position).getIdLaundry());

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }
    private void LoadKendaraan() {
        User loginUser = MangJekApplication.getInstance(getContext()).getLoginUser();
        BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
        service.getKendaraanAngkut().enqueue(new Callback<GetKendaraanAngkutResponseJson>() {
            @Override
            public void onResponse(Call<GetKendaraanAngkutResponseJson> call, Response<GetKendaraanAngkutResponseJson> response) {
                if(response.isSuccessful()) {

                    Realm realm = MangJekApplication.getInstance(getActivity()).getRealmInstance();
                    realm.beginTransaction();
                    realm.delete(KendaraanAngkut.class);
                    realm.copyToRealm(response.body().getData());
                    realm.commitTransaction();

                    CargoItem cargoItem;
                    for (KendaraanAngkut cargo:response.body().getData() ){
                        cargoItem = new CargoItem(getContext());
                        cargoItem.featureId = featureId;
                        cargoItem.id = cargo.getIdKendaraan();
                        cargoItem.type = cargo.getKendaraanAngkut();
                        cargoItem.price = cargo.getHarga();
                        cargoItem.image = cargo.getFotoKendaraan();
                        cargoItem.dimension = cargo.getDimensiKendaraan();
                        cargoItem.maxWeight = cargo.getMaxweightKendaraan();
                        cargodapter.add(cargoItem);
                        Log.e("ADD CARGO", cargo.getIdKendaraan()+"");
                    }
                }
            }

            @Override
            public void onFailure(Call<GetKendaraanAngkutResponseJson> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


}

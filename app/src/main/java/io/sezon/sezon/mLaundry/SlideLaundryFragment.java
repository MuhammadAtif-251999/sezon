package io.sezon.sezon.mLaundry;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import io.sezon.sezon.MangJekApplication;
import io.sezon.sezon.R;
import io.sezon.sezon.api.ServiceGenerator;
import io.sezon.sezon.api.service.BookService;
import io.sezon.sezon.model.Laundry;
import io.sezon.sezon.model.User;
import io.sezon.sezon.model.json.book.GetLayananLaundryRequestJson;
import io.sezon.sezon.model.json.book.GetLayananLaundryResponseJson;
import io.sezon.sezon.utils.Log;

import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SlideLaundryFragment extends Fragment {

    private String image;
    private int id;
    private int idLaundry;
    private List<Laundry> laundryAll;


    public static SlideLaundryFragment newInstance(int id, String image, int idLaundry) {
        SlideLaundryFragment fragmentFirst = new SlideLaundryFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", id);
        args.putString("someString", image);
        args.putInt("idLaundry", idLaundry);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getArguments().getInt("someInt", 0);
        image = getArguments().getString("someString");
        idLaundry = getArguments().getInt("idLaundry", -1);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slide, container, false);
        ImageView imageView = (ImageView)view.findViewById(R.id.slide_image);

//        Picasso.with(getContext()).load(image).into(imageView);
        Glide.with(getContext())
                .load(image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = v.getContext();
                final ProgressDialog progressDialog = ProgressDialog.show(context, "", "Please wait...", false, false);
                GetLayananLaundryRequestJson param = new GetLayananLaundryRequestJson();
                param.setIdLaundry(idLaundry);
                Realm realm = MangJekApplication.getInstance(context).getRealmInstance();
                User loginUser = realm.copyFromRealm(MangJekApplication.getInstance(context).getLoginUser());
                BookService bookService = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());

                bookService.getMenuLaundry(param).enqueue(new Callback<GetLayananLaundryResponseJson>() {
                    @Override
                    public void onResponse(Call<GetLayananLaundryResponseJson> call, Response<GetLayananLaundryResponseJson> response) {
                        if(response.isSuccessful()) {
                            progressDialog.dismiss();
                            Laundry restoran = response.body().getTempatLaundry().getDetailLaundry().get(0);


                            Intent intent = new Intent(context, LaundryMenuActivity.class);
                            intent.putExtra(LaundryMenuActivity.ID_LAUNDRY, restoran.getId());
                            intent.putExtra(LaundryMenuActivity.NAMA_LAUNDRY, restoran.getNamaLaundry());
                            intent.putExtra(LaundryMenuActivity.ALAMAT_LAUNDRY, restoran.getAlamat());
//                            intent.putExtra(LaundryMenuActivity.DISTANCE_LAUNDRY, 0);
                            intent.putExtra(LaundryMenuActivity.DISTANCE_LAUNDRY, restoran.getDistance());
                            Log.d("Distance : ",""+restoran.getDistance());

                            intent.putExtra(LaundryMenuActivity.JAM_BUKA, restoran.getJamBuka());
                            intent.putExtra(LaundryMenuActivity.JAM_TUTUP, restoran.getJamTutup());
                            intent.putExtra(LaundryMenuActivity.IS_OPEN, restoran.isOpen());
                            intent.putExtra(LaundryMenuActivity.PICTURE_URL, restoran.getFotoLaundry());
                            intent.putExtra(LaundryMenuActivity.IS_MITRA, restoran.isPartner());
                            startActivity(intent);
                        } else {
                            onFailure(call, new RuntimeException("Check internet connection."));
                        }
                    }

                    @Override
                    public void onFailure(Call<GetLayananLaundryResponseJson> call, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        return view;
    }

}

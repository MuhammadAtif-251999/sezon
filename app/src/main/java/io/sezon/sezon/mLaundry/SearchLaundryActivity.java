package io.sezon.sezon.mLaundry;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import io.sezon.sezon.MangJekApplication;
import io.sezon.sezon.R;
import io.sezon.sezon.api.ServiceGenerator;
import io.sezon.sezon.api.service.BookService;
import io.sezon.sezon.model.Laundry;
import io.sezon.sezon.model.LaundrySearchResult;
import io.sezon.sezon.model.PesananLaundry;
import io.sezon.sezon.model.User;
import io.sezon.sezon.model.json.book.GetLayananLaundryRequestJson;
import io.sezon.sezon.model.json.book.GetLayananLaundryResponseJson;
import io.sezon.sezon.model.json.book.SearchLaundryRequest;
import io.sezon.sezon.model.json.book.SearchLaundryResponse;
import io.sezon.sezon.utils.Log;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by fathony on 23/01/2017.
 */

public class SearchLaundryActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.btn_home)
    ImageView backButton;

    @BindView(R.id.searchLaundry_searchQuery)
    EditText searchQuery;

    @BindView(R.id.searchLaundry_searchResult)
    RecyclerView searchResult;

    @BindView(R.id.searchLaundry_requirement)
    TextView requirement;

    @BindView(R.id.searchLaundry_noResult)
    CardView noResult;

    @BindView(R.id.searchLaundry_progress)
    ProgressBar progress;

    private Realm realm;
    private User loginUser;

    private BookService bookService;
    private Call<SearchLaundryResponse> searchCall;
    private Callback<SearchLaundryResponse> callbackRequest;
    private FastItemAdapter<LaundrySearchResult> searchResultAdapter;

    private GoogleApiClient googleApiClient;
    private static final int REQUEST_PERMISSION_LOCATION = 991;
    private Location lastKnownLocation;

    private Dialog progressDialog;

    private Dialog LoadingSpinner(Context mContext){
        Dialog pd = new Dialog(mContext, android.R.style.Theme_Black);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_progress, null);
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pd.getWindow().setBackgroundDrawableResource(R.color.transparent);
        pd.setContentView(view);
        return pd;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_laundry);
        ButterKnife.bind(this);

        searchResultAdapter = new FastItemAdapter<>();
        progressDialog = LoadingSpinner(this);
        searchResult.setLayoutManager(new LinearLayoutManager(this));
        searchResult.setAdapter(searchResultAdapter);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        callbackRequest = new Callback<SearchLaundryResponse>() {
            @Override
            public void onResponse(Call<SearchLaundryResponse> call, Response<SearchLaundryResponse> response) {
                if(response.isSuccessful()) {
                    List<LaundrySearchResult> searchResult = response.body().getData();
                    if(searchResult.size() > 0) {
                        searchResultAdapter.clear();
                        searchResultAdapter.set(searchResult);
                        searchResultAdapter.notifyDataSetChanged();
                        showRecycler();
                    } else {
                        showNoResultFoundMessage();
                    }
                } else {
                    onFailure(call, new RuntimeException("Connection failed."));
                }
            }

            @Override
            public void onFailure(Call<SearchLaundryResponse> call, Throwable t) {
                if(!call.isCanceled()) {
                    showNoResultFoundMessage();
                    Toast.makeText(SearchLaundryActivity.this, "Check your internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .build();
        }

        realm = Realm.getDefaultInstance();
        loginUser = MangJekApplication.getInstance(this).getLoginUser();

//        loginUser = realm.copyFromRealm(MangJekApplication.getInstance(this).getLoginUser());
        bookService = ServiceGenerator.createService(BookService.class, loginUser.getEmail() , loginUser.getPassword());

        showInsertTextMessage();

        searchResultAdapter.withOnClickListener(new FastAdapter.OnClickListener<LaundrySearchResult>() {
            @Override
            public boolean onClick(View v, IAdapter<LaundrySearchResult> adapter, final LaundrySearchResult item, int position) {
                progressDialog.show();
                GetLayananLaundryRequestJson param = new GetLayananLaundryRequestJson();
                param.setIdLaundry(item.getIdLaundry());
                Log.d("ID LAUNDRY SEARCH : ",""+ item.getIdLaundry());

//                bookService.getSearchLaundry(param).enqueue(new Callback<GetLayananLaundryResponseJson>() {
                bookService.getMenuLaundry(param).enqueue(new Callback<GetLayananLaundryResponseJson>() {

                        @Override
                    public void onResponse(Call<GetLayananLaundryResponseJson> call, Response<GetLayananLaundryResponseJson> response) {
                        if(response.isSuccessful()) {
                            progressDialog.dismiss();
                            Laundry restoran = response.body().getTempatLaundry().getDetailLaundry().get(0);

                            Intent intent = new Intent(SearchLaundryActivity.this, LaundryMenuActivity.class);
                            intent.putExtra(LaundryMenuActivity.ID_LAUNDRY, restoran.getId());
                            intent.putExtra(LaundryMenuActivity.NAMA_LAUNDRY, restoran.getNamaLaundry());
                            intent.putExtra(LaundryMenuActivity.ALAMAT_LAUNDRY, restoran.getAlamat());
                            intent.putExtra(LaundryMenuActivity.DISTANCE_LAUNDRY, item.getDistance());
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
                        Toast.makeText(SearchLaundryActivity.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            }
        });

        searchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(searchCall != null) searchCall.cancel();
                String strings = s.toString();
                if(strings.length() >= 3) {
                    showProgress();
                    SearchLaundryRequest param = new SearchLaundryRequest();
                    param.setLatitude(lastKnownLocation.getLatitude());
                    param.setLongitude(lastKnownLocation.getLongitude());
                    param.setCari(strings);

                    searchCall = bookService.searchLaundry(param);
                    searchCall.enqueue(callbackRequest);
                } else {
                    showInsertTextMessage();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void showRecycler() {
        requirement.setVisibility(View.GONE);
        noResult.setVisibility(View.GONE);
        searchResult.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
    }

    private void showInsertTextMessage() {
        requirement.setVisibility(View.VISIBLE);
        noResult.setVisibility(View.GONE);
        searchResult.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
    }

    private void showNoResultFoundMessage() {
        requirement.setVisibility(View.GONE);
        noResult.setVisibility(View.VISIBLE);
        searchResult.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
    }

    private void showProgress() {
        requirement.setVisibility(View.GONE);
        noResult.setVisibility(View.GONE);
        searchResult.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    private void updateLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }
        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLastLocation();
            } else {
                // TODO: 10/15/2016 Tell user to use GPS
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(PesananLaundry.class);
        realm.commitTransaction();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        updateLastLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}

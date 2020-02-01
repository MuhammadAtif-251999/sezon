package io.sezon.sezon.home.submenu.history;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.sezon.sezon.MangJekApplication;
import io.sezon.sezon.R;
import io.sezon.sezon.adapter.HistoryAdapter;
import io.sezon.sezon.api.ServiceGenerator;
import io.sezon.sezon.api.service.UserService;
import io.sezon.sezon.model.ItemHistory;
import io.sezon.sezon.model.User;
import io.sezon.sezon.model.json.menu.HistoryRequestJson;
import io.sezon.sezon.model.json.menu.HistoryResponseJson;
import io.sezon.sezon.utils.Log;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CompletedHistoryFragment extends Fragment implements HistoryFragment.OnSwipeRefresh {

    @BindView(R.id.completed_recyclerView)
    RecyclerView recyclerView;
    HistoryAdapter historyAdapter;

    public CompletedHistoryFragment() {
    }

    public static CompletedHistoryFragment newInstance() {
        CompletedHistoryFragment fragment = new CompletedHistoryFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_completed_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        requestData();

    }

    private void requestData(){
        User user = MangJekApplication.getInstance(getActivity()).getLoginUser();
        HistoryRequestJson request = new HistoryRequestJson();
        request.id = user.getId();

        UserService service = ServiceGenerator.createService(UserService.class, user.getEmail(), user.getPassword());
        service.getCompleteHistory(request).enqueue(new Callback<HistoryResponseJson>() {
            @Override
            public void onResponse(Call<HistoryResponseJson> call, Response<HistoryResponseJson> response) {
                if (response.isSuccessful()) {
                    ArrayList<ItemHistory> data = response.body().data;

//                    Log.e("HISTORY", data.get(0).toString());

             for(int i = 0;i<data.size();i++){
                        switch (data.get(i).order_fitur){
                            case "Ojek":
                                data.get(i).image_id = R.drawable.ic_e_ride_on;
                                break;
                            case "Taxi":
                                data.get(i).image_id = R.drawable.ic_mcar;
                                break;
                            case "Box":
                                data.get(i).image_id = R.drawable.ic_msend;
                                break;
                            case "Sembako":
                                data.get(i).image_id = R.drawable.ic_emart;
                                break;
                            case "Cargo":
                                data.get(i).image_id = R.drawable.ic_mbox;
                                break;
                            case "Servis":
                                data.get(i).image_id = R.drawable.ic_mservice;
                                break;
                            case "Pijat":
                                data.get(i).image_id = R.drawable.ic_mmassage;
                                break;
                            case "Food":
                                data.get(i).image_id = R.drawable.ic_efood;
                                break;
                            case "Market":
                                data.get(i).image_id = R.drawable.ic_melectronic;
                                break;
                            case "Laundry":
                                data.get(i).image_id = R.drawable.ic_elaundry;
                                break;

                            default:
                                data.get(i).image_id = R.drawable.ic_e_ride_on;
                                break;
                        }
                    }

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

                    recyclerView.setLayoutManager(layoutManager);
                    historyAdapter = new HistoryAdapter(getContext(), data, true);
                    recyclerView.setAdapter(historyAdapter);
                    if (response.body().data.size() == 0) {
                        Log.d("HISTORY", "Empty");
                    }
                }
            }

            @Override
            public void onFailure(Call<HistoryResponseJson> call, Throwable t) {
                t.printStackTrace();
//                Toast.makeText(getActivity(), "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                Log.e("System error:",""+t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        requestData();
    }

    @Override
    public void onRefresh() {
        requestData();
    }
}

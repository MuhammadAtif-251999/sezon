package io.sezon.sezon.home.submenu.help;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import io.sezon.sezon.MangJekApplication;
import io.sezon.sezon.R;
import io.sezon.sezon.api.ServiceGenerator;
import io.sezon.sezon.api.service.UserService;
import io.sezon.sezon.model.User;
import io.sezon.sezon.model.json.menu.HelpRequestJson;
import io.sezon.sezon.model.json.menu.HelpResponseJson;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HelpActivity extends AppCompatActivity {

    @BindView(R.id.help_title)
    TextView helpTitle;
    @BindView(R.id.help_subject)
    TextView helpSubject;
    @BindView(R.id.help_description)
    TextView helpDescription;
    @BindView(R.id.send_help_request)
    Button sendHelpRequest;

    Context context;
    private int titleId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ButterKnife.bind(this);
        context = getApplicationContext();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setHelpTitle(getIntent().getIntExtra("id",1));

        sendHelpRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(helpSubject.getText().toString().length()>0&&helpDescription.getText().toString().length()>0){
                    Toast.makeText(context, "Dikirm...", Toast.LENGTH_SHORT).show();
                    User loginUser = MangJekApplication.getInstance(HelpActivity.this).getLoginUser();
                    HelpRequestJson request = new HelpRequestJson();
                    request.id_pelanggan = loginUser.getId();
                    request.subject = helpSubject.getText().toString();
                    request.description = helpDescription.getText().toString();
                    request.type = titleId+"";

                    UserService service = ServiceGenerator.createService(UserService.class, loginUser.getEmail(), loginUser.getPassword());
                    service.sendHelp(request).enqueue(new Callback<HelpResponseJson>() {
                        @Override
                        public void onResponse(Call<HelpResponseJson> call, Response<HelpResponseJson> response) {
                            if (response.isSuccessful()) {
                                if (response.body().mesage.equals("success")) {
                                    helpSubject.setText("");
                                    helpDescription.setText("");
                                    View view = HelpActivity.this.getCurrentFocus();
                                    if (view != null) {
                                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                    }
                                } else {
                                    Toast.makeText(HelpActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<HelpResponseJson> call, Throwable t) {
                            t.printStackTrace();
                            Toast.makeText(HelpActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        });
    }


    private void setHelpTitle(int id){
        String titile = "";
        titleId = id;
        switch (id){
            case 0:
                titile = "Klik Ride";
                break;
            case 1:
                titile = "customer support";
                break;
            case 2:
                titile = "customer support Food";
                break;
            case 3:
                titile = "customer support Taxicar";
                break;
            case 4:
                titile = "customer support Mart";
                break;
            default:
                titile = "customer support ";
                break;

        }
        helpTitle.setText(titile);
    }

}

package io.sezon.sezon.signIn;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import io.sezon.sezon.MangJekApplication;
import io.sezon.sezon.R;
import io.sezon.sezon.api.ServiceGenerator;
import io.sezon.sezon.api.service.UserService;
import io.sezon.sezon.home.MainActivity;
import io.sezon.sezon.model.FirebaseToken;
import io.sezon.sezon.model.User;
import io.sezon.sezon.model.json.user.LoginRequestJson;
import io.sezon.sezon.model.json.user.LoginResponseJson;
import io.sezon.sezon.signUp.SignUpActivity;
import io.sezon.sezon.utils.DialogActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by bradhawk on 10/12/2016.
 */

public class SignInActivity extends DialogActivity implements Validator.ValidationListener {

    private static final String TAG = "SignInActivity";

    @NotEmpty
    @Email
    @BindView(R.id.signIn_email)
    EditText textEmail;

    @NotEmpty
    @BindView(R.id.signIn_password)
    EditText textPassword;

    @BindView(R.id.signIn_signInButton)
    Button buttonSignIn;


    @BindView(R.id.gambar)
    ImageView imghari;

    @BindView(R.id.text_ucap)
    TextView txt_ucap;



    @BindView(R.id.jam)
    TextView jam;

    @BindView(R.id.signIn_signUpButton)
    LinearLayout buttonSignUp;



    Validator validator;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

        validator = new Validator(this);
        validator.setValidationListener(this);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivityForResult(intent, SignUpActivity.SIGNUP_ID);
            }
        });

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });

        ucapan();

        Date now = new Date();
        String nowFormatted2 = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(now);
        jam.setText(nowFormatted2);
    }
    private void ucapan(){

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay >= 0 && timeOfDay < 12){
       txt_ucap.setText("Good Moorning");
            txt_ucap.setTextColor(this.getResources().getColor(R.color.white));
          //  selamat.setTextColor(this.getResources().getColor(R.color.white));
          //  kamu.setTextColor(this.getResources().getColor(R.color.white));
            imghari.setImageResource(R.drawable.morning);
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            txt_ucap.setText("Good Day!");
            txt_ucap.setTextColor(this.getResources().getColor(R.color.white));
            imghari.setImageResource(R.drawable.noon);
        }else if(timeOfDay >= 16 && timeOfDay < 21){
            txt_ucap.setText("Good Afternoon");
            txt_ucap.setTextColor(this.getResources().getColor(R.color.white));
            imghari.setImageResource(R.drawable.afternoon);
        }else if(timeOfDay >= 21 && timeOfDay < 24){
            txt_ucap.setText("Good Night");
            txt_ucap.setTextColor(this.getResources().getColor(R.color.white));
            imghari.setImageResource(R.drawable.night);
        }
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    private void onSignInClick() {
        showProgressDialog(R.string.dialog_loading);
        LoginRequestJson request = new LoginRequestJson();
        request.setEmail(textEmail.getText().toString());
        request.setPassword(textPassword.getText().toString());

        Realm realm = Realm.getDefaultInstance();
        FirebaseToken token = realm.where(FirebaseToken.class).findFirst();
        if (token.getTokenId() != null) {
            request.setRegId(token.getTokenId());
        } else {
            Toast.makeText(this, R.string.waiting_pleaseWait, Toast.LENGTH_SHORT).show();
            hideProgressDialog();
            return;
        }

        UserService service = ServiceGenerator.createService(UserService.class, request.getEmail(), request.getPassword());
        service.login(request).enqueue(new Callback<LoginResponseJson>() {
            @Override
            public void onResponse(Call<LoginResponseJson> call, Response<LoginResponseJson> response) {
                hideProgressDialog();
                if (response.isSuccessful()) {
                    if (response.body().getMessage().equalsIgnoreCase("found")) {
                        User user = response.body().getData().get(0);

                        saveUser(user);

                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignInActivity.this, "Username atau Password salah", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponseJson> call, Throwable t) {
                hideProgressDialog();
                t.printStackTrace();
                Toast.makeText(SignInActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SignUpActivity.SIGNUP_ID) {
            if (resultCode == Activity.RESULT_OK) {
                User user = (User) data.getSerializableExtra(SignUpActivity.USER_KEY);

                saveUser(user);

                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void onValidationSucceeded() {
        onSignInClick();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveUser(User user) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(User.class);
        realm.copyToRealm(user);
        realm.commitTransaction();

        MangJekApplication.getInstance(SignInActivity.this).setLoginUser(user);
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FirebaseToken response) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(FirebaseToken.class);
        realm.copyToRealm(response);
        realm.commitTransaction();
    }
}

package io.sezon.sezon.signUp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.sezon.sezon.R;
import io.sezon.sezon.api.ServiceGenerator;
import io.sezon.sezon.api.service.UserService;
import io.sezon.sezon.model.User;
import io.sezon.sezon.model.json.user.OTPRequestJson;
import io.sezon.sezon.utils.DialogActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static io.sezon.sezon.signUp.SignUpActivity.USER_KEY;

public class OTPActivity extends DialogActivity {

    @BindView(R.id.edt1)
    EditText edt1;
    @BindView(R.id.edt2)
    EditText edt2;
    @BindView(R.id.edt3)
    EditText edt3;
    @BindView(R.id.edt4)
    EditText edt4;
    @BindView(R.id.edt5)
    EditText edt5;
    @BindView(R.id.edt6)
    EditText edt6;

    @BindView(R.id.txt_mobile_no)
    TextView txt_mobile_no;


    private EditText[] editTexts;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {

        user=(User)getIntent().getSerializableExtra("user_data");

        txt_mobile_no.setText(Html.fromHtml(getResources().getString(R.string.enterotp)+"<br> on <b><font color='#000000'>" + user.getNoTelepon() + "</font></b>"));

        editTexts = new EditText[]{edt1, edt2, edt3, edt4, edt5, edt6};

        edt1.addTextChangedListener(new PinTextWatcher(0));
        edt2.addTextChangedListener(new PinTextWatcher(1));
        edt3.addTextChangedListener(new PinTextWatcher(2));
        edt4.addTextChangedListener(new PinTextWatcher(3));
        edt5.addTextChangedListener(new PinTextWatcher(4));
        edt6.addTextChangedListener(new PinTextWatcher(5));

        edt1.setOnKeyListener(new PinOnKeyListener(0));
        edt2.setOnKeyListener(new PinOnKeyListener(1));
        edt3.setOnKeyListener(new PinOnKeyListener(2));
        edt4.setOnKeyListener(new PinOnKeyListener(3));
        edt5.setOnKeyListener(new PinOnKeyListener(4));
        edt6.setOnKeyListener(new PinOnKeyListener(5));
    }

    @OnClick(R.id.txt_resend_otp)
    public void onResendClick() {

    }

    @OnClick(R.id.btn_verify_otp)
    public void onVerifyClick()
    {
        showProgressDialog(R.string.dialog_loading);
        String otp=edt1.getText().toString().trim()+edt2.getText().toString().trim()+edt3.getText().toString().trim()+edt4.getText().toString().trim()+edt5.getText().toString().trim()+edt6.getText().toString().trim();
        OTPRequestJson otpRequestJson=new OTPRequestJson();
        otpRequestJson.setNo_telepon(user.getNoTelepon());
        otpRequestJson.setOtp("S-"+otp);

        UserService service = ServiceGenerator.createService(UserService.class, user.getEmail(), user.getPassword());
        service.verifyOTP(otpRequestJson).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                hideProgressDialog();
                try {
                    String res=response.body().string();
                    JSONObject jsonObject=new JSONObject(res);
                    if(jsonObject.optString("message").equals("success"))
                    {
                        user.setVstatus("1");
                        Intent retIntent = getIntent();
                        retIntent.putExtra(USER_KEY, user);
                        setResult(Activity.RESULT_OK, retIntent);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(OTPActivity.this,jsonObject.optString("message"),Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
               Log.d("failed","faile");
               hideProgressDialog();
            }
        });
    }

    public class PinTextWatcher implements TextWatcher {

        private int currentIndex;
        private boolean isFirst = false, isLast = false;
        private String newTypedString = "";

        PinTextWatcher(int currentIndex) {
            this.currentIndex = currentIndex;

            if (currentIndex == 0)
                this.isFirst = true;
            else if (currentIndex == editTexts.length - 1)
                this.isLast = true;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            newTypedString = s.subSequence(start, start + count).toString().trim();
        }

        @Override
        public void afterTextChanged(Editable s) {

            String text = newTypedString;

            /* Detect paste event and set first char */
            if (text.length() > 1)
                text = String.valueOf(text.charAt(0)); // TODO: We can fill out other EditTexts

            editTexts[currentIndex].removeTextChangedListener(this);
            editTexts[currentIndex].setText(text);
            editTexts[currentIndex].setSelection(text.length());
            editTexts[currentIndex].addTextChangedListener(this);

            if (text.length() == 1)
                moveToNext();
            else if (text.length() == 0)
                moveToPrevious();
        }

        private void moveToNext() {
            if (!isLast)
                editTexts[currentIndex + 1].requestFocus();

            if (isAllEditTextsFilled() && isLast) { // isLast is optional
                editTexts[currentIndex].clearFocus();
                hideKeyboard();
            }
        }

        private void moveToPrevious() {
            if (!isFirst)
                editTexts[currentIndex - 1].requestFocus();
        }

        private boolean isAllEditTextsFilled() {
            for (EditText editText : editTexts)
                if (editText.getText().toString().trim().length() == 0)
                    return false;
            return true;
        }

        private void hideKeyboard() {
            if (getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }

    }

    public class PinOnKeyListener implements View.OnKeyListener {

        private int currentIndex;

        PinOnKeyListener(int currentIndex) {
            this.currentIndex = currentIndex;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (editTexts[currentIndex].getText().toString().isEmpty() && currentIndex != 0)
                    editTexts[currentIndex - 1].requestFocus();
            }
            return false;
        }

    }

}

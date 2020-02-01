package io.sezon.sezon;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import io.sezon.sezon.model.DiskonMpay;
import io.sezon.sezon.model.FirebaseToken;
import io.sezon.sezon.model.MElektronikMitra;
import io.sezon.sezon.model.MfoodMitra;
import io.sezon.sezon.model.MlaundryMitra;
import io.sezon.sezon.model.User;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by bradhawk on 10/13/2016.
 */

public class MangJekApplication extends Application {

    public static final String TAG1 = MangJekApplication.class
            .getSimpleName();

    private RequestQueue mRequestQueue;

    private static MangJekApplication mInstance;


    private static final int SCHEMA_VERSION = 0;

    private static final String TAG = "MangJekApplication";

    private User loginUser;

    private Realm realmInstance;

    private DiskonMpay diskonMpay;

    private MfoodMitra mfoodMitra;

    private MElektronikMitra mElektronikMitra;

    private MlaundryMitra mLaundryMitra;



    public static MangJekApplication getInstance(Context context) {
        return (MangJekApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        Realm.init(this);

        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(SCHEMA_VERSION)
                .deleteRealmIfMigrationNeeded()
                .build();

        FirebaseToken token = new FirebaseToken(FirebaseInstanceId.getInstance().getToken());

        Realm.setDefaultConfiguration(config);

//        realmInstance = Realm.getInstance(config);
        realmInstance = Realm.getDefaultInstance();
        realmInstance.beginTransaction();
        realmInstance.delete(FirebaseToken.class);
        realmInstance.copyToRealm(token);
        realmInstance.commitTransaction();

        start();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    public static synchronized MangJekApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG1 : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG1);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public User getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(User loginUser) {
        this.loginUser = loginUser;
    }

    public final Realm getRealmInstance() {
        return realmInstance;
    }

    private void start(){
        Realm realm = getRealmInstance();
        User user = realm.where(User.class).findFirst();
        if (user != null) {
            setLoginUser(user);
        }
    }

    public DiskonMpay getDiskonMpay() {
        return diskonMpay;
    }

    public void setDiskonMpay(DiskonMpay diskonMpay) {
        this.diskonMpay = diskonMpay;
    }

    public MfoodMitra getMfoodMitra() { return mfoodMitra; }

    public void setMfoodMitra(MfoodMitra mfoodMitra) {
        this.mfoodMitra = mfoodMitra;
    }

    public MElektronikMitra getMElektronikMitra() {
        return mElektronikMitra;
    }

    public void setMElektronikMitra(MElektronikMitra mElektronikMitra) {
        this.mElektronikMitra = mElektronikMitra;
    }

    public MlaundryMitra getMLaundryMitra() {
        return mLaundryMitra;
    }

    public void setMLaundrykMitra(MlaundryMitra mLaundryMitra) {
        this.mLaundryMitra = mLaundryMitra;
    }



}

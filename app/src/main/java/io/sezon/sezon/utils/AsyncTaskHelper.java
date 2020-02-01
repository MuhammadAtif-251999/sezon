package io.sezon.sezon.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

/**
 * Created by A. Fauzi Harismawan on 10/2/2015.
 */

public class AsyncTaskHelper extends AsyncTask<Void, Void, String> {

    public ProgressDialog dialog;
    public Activity activity;
    public boolean isDialogShow;

    OnAsyncTaskListener mCallback;

    public interface OnAsyncTaskListener {
        void onAsyncTaskDoInBackground(AsyncTaskHelper asyncTask);

        void onAsyncTaskProgressUpdate(AsyncTaskHelper asyncTask);

        void onAsyncTaskPostExecute(AsyncTaskHelper asyncTask);

        void onAsyncTaskPreExecute(AsyncTaskHelper asyncTask);
    }

    public void setAsyncTaskListener(OnAsyncTaskListener listener) {
        try {
            mCallback = listener;
        } catch (ClassCastException e) {
            throw new ClassCastException(this.toString() + "Did not implement OnMGAsyncTaskListener");
        }
    }

    public AsyncTaskHelper(Activity activity, boolean isDialogShow) {
        this.activity = activity;
        this.isDialogShow = isDialogShow;
    }

    @Override
    protected void onPreExecute() {
        // Things to be done before execution of long running operation. For example showing ProgessDialog
        if (isDialogShow) {
            dialog = ProgressDialog.show(activity, "", "Loading...", true);
        }
        mCallback.onAsyncTaskPreExecute(this);
    }

    @Override
    protected String doInBackground(Void... params) {
        mCallback.onAsyncTaskDoInBackground(this);
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        // execution of result of Long time consuming operation. parse json data
        if (isDialogShow) {
            dialog.dismiss();
        }
        mCallback.onAsyncTaskPostExecute(this);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        // Things to be done while execution of long running operation is in progress. For example updating ProgessDialog
        mCallback.onAsyncTaskProgressUpdate(this);
    }
}

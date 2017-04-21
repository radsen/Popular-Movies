package com.kzlabs.popularmovies.activity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.kzlabs.popularmovies.R;
import com.kzlabs.popularmovies.util.ConnectivityReceiver;
import com.kzlabs.popularmovies.util.NetworkHelper;

/**
 * Created by radsen on 4/15/17.
 */

public class BaseActivity extends AppCompatActivity implements ConnectivityReceiver.OnConnectivityListener {

    private static final String TAG = BaseActivity.class.getSimpleName();

    private ProgressBar pbLoading;
    private ConnectivityReceiver connectivityReceiver;
    private IntentFilter intentFilter;
    private boolean connected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pbLoading = (ProgressBar) findViewById(R.id.pb_wait);
        intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        connectivityReceiver = new ConnectivityReceiver(this);
        connected = NetworkHelper.isNetworkAvailable(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(connectivityReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectivityReceiver);
    }

    public void showProgress() {
        Log.d(TAG, "showProgress");
        if(pbLoading.getVisibility() != View.VISIBLE){
            pbLoading.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgress() {
        pbLoading.setVisibility(View.GONE);
    }

    @Override
    public void onNetworkStatusChange(boolean isConnected) {

    }

    public boolean isConnected() {
        return connected;
    }
}

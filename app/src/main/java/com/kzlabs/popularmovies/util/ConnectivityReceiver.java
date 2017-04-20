package com.kzlabs.popularmovies.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.kzlabs.popularmovies.R;

/**
 * Created by radsen on 4/15/17.
 */

public class ConnectivityReceiver extends BroadcastReceiver {

    private final OnConnectivityListener listener;

    public interface OnConnectivityListener {
        void onNetworkStatusChange(boolean isConnected);
    }

    public ConnectivityReceiver(OnConnectivityListener listener){
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(NetworkHelper.isNetworkAvailable(context)){
            listener.onNetworkStatusChange(true);
        } else {
            Toast.makeText(context, context.getString(R.string.network_not_available_txt),
                    Toast.LENGTH_LONG).show();
        }
    }
}

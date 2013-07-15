package com.kodart.httpzoid;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * (c) Artur Sharipov
 */
public class NetworkImpl implements Network {
    private ConnectivityManager manager;

    public NetworkImpl(ConnectivityManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean isOffline() {
        return !isOnline();
    }

    @Override
    public boolean isOnline() {
        for(NetworkInfo info: manager.getAllNetworkInfo()) {
            Log.e("Httpzoid", info.getState().toString());
        }

        return true;

//        return manager.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED
//                || manager.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING;
    }
}

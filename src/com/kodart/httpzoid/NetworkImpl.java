package com.kodart.httpzoid;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
        return manager.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED
                || manager.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING;
    }
}

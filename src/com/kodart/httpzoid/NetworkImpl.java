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
        NetworkInfo info = manager.getActiveNetworkInfo();
        return (info != null && info.isConnectedOrConnecting());
    }
}

package com.kodart.httpzoid;

import android.net.ConnectivityManager;
import com.kodart.httpzoid.serializers.JsonHttpSerializer;

/**
 * (c) Artur Sharipov
 */
public class HttpFactory {
    public static Http create(ConnectivityManager connectivity) {
        return new HttpUrlConnection(new JsonHttpSerializer(), new NetworkImpl(connectivity));
    }
}

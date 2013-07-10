package com.kodart.httpzoid;

import com.kodart.httpzoid.serializers.JsonHttpSerializer;

/**
 * (c) Artur Sharipov
 */
public class HttpFactory {
    public static Http create() {
        return new HttpUrlConnection(new JsonHttpSerializer());
    }
}

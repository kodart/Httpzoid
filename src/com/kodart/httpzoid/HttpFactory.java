package com.kodart.httpzoid;

/**
 * (c) Artur Sharipov
 */
public class HttpFactory {
    public static Http create() {
        return new HttpUrlConnection();
    }
}

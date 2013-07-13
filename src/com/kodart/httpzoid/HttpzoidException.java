package com.kodart.httpzoid;

import java.io.IOException;

/**
 * (c) Artur Sharipov
 */
public class HttpzoidException extends IOException {
    private NetworkError error;

    public HttpzoidException(String message, NetworkError error) {
        super(message);
        this.error = error;
    }

    public NetworkError getNetworkError() {
        return error;
    }
}

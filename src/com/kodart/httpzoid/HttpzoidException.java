package com.kodart.httpzoid;

/**
 * (c) Artur Sharipov
 */
public class HttpzoidException extends Exception {
    private NetworkError error;

    public HttpzoidException(String message, NetworkError error) {
        super(message);
        this.error = error;
    }

    public NetworkError getNetworkError() {
        return error;
    }
}

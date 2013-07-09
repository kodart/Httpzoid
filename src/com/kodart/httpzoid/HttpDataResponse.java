package com.kodart.httpzoid;

import java.net.HttpURLConnection;

/**
 * (c) Artur Sharipov
 */
public class HttpDataResponse extends HttpResponse {
    private Object data;

    public HttpDataResponse(Object data, HttpURLConnection connection) {
        super(connection);
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}

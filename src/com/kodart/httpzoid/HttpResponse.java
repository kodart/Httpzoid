package com.kodart.httpzoid;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

/**
 * (c) Artur Sharipov
 */
public class HttpResponse {
    private HttpURLConnection connection;

    public HttpResponse(HttpURLConnection connection) {
        this.connection = connection;
    }

    public int getResponseCode() {
        try {
            return connection.getResponseCode();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, List<String>> getHeaders() {
        return connection.getHeaderFields();
    }

    public boolean isSuccess() {
        return getResponseCode() < 300;
    }
}

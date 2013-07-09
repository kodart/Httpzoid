package com.kodart.httpzoid;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * (c) Artur Sharipov
 */
public class HttpUrlConnection implements Http {

    private HttpRequest request(String url, String method) throws MalformedURLException {
        return new HttpRequestUrlConnection(new URL(url), method);
    }

    @Override
    public HttpRequest get(String url) throws MalformedURLException {
        return request(url, "GET");
    }

    @Override
    public HttpRequest post(String url) throws MalformedURLException {
        return request(url, "POST");
    }

    @Override
    public HttpRequest put(String url) throws MalformedURLException {
        return request(url, "PUT");
    }

    @Override
    public HttpRequest delete(String url) throws MalformedURLException {
        return request(url, "DELETE");
    }
}

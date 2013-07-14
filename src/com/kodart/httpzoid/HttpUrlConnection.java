package com.kodart.httpzoid;

import com.kodart.httpzoid.serializers.HttpSerializer;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * (c) Artur Sharipov
 */
public class HttpUrlConnection implements Http {

    private final HttpSerializer serializer;
    private final Network network;

    public HttpUrlConnection(HttpSerializer serializer, Network network) {
        this.serializer = serializer;
        this.network = network;
    }

    @Override
    public HttpRequest request(String url, String method) {
        try {
            return new HttpUrlConnectionRequest(new URL(url), method, serializer, network);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public HttpRequest get(String url) {
        return request(url, "GET");
    }

    @Override
    public HttpRequest post(String url) {
        return request(url, "POST");
    }

    @Override
    public HttpRequest put(String url) {
        return request(url, "PUT");
    }

    @Override
    public HttpRequest delete(String url) {
        return request(url, "DELETE");
    }
}

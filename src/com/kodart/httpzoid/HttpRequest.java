package com.kodart.httpzoid;

import java.net.Proxy;

/**
 * Http request
 *
 * (c) Artur Sharipov
 */
public interface HttpRequest {
    public HttpRequest data(Object data);
    public HttpRequest header(String key, String value);
    public HttpRequest timeout(int timeout);
    public HttpRequest proxy(Proxy proxy);
    public HttpRequest handler(ResponseHandler handler);
    public void execute();
}

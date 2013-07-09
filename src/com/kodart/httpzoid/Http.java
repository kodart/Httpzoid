package com.kodart.httpzoid;

import java.io.IOException;
import java.net.URL;

/**
 * (c) Artur Sharipov
 */
public interface Http {

    public HttpRequest get(String url) throws IOException;
    public HttpRequest post(String url) throws IOException;
    public HttpRequest put(String url) throws IOException;
    public HttpRequest delete(String url) throws IOException;

}


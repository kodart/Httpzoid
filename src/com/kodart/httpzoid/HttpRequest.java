package com.kodart.httpzoid;

import java.io.IOException;
import java.net.Proxy;
import java.util.Map;

public interface HttpRequest {
    public HttpRequest data(Object data) throws IOException;
    public HttpRequest headers(Map<String, String> headers);
    public HttpRequest handler(ResponseHandler handler);

    public HttpRequest timeout(int timeout);
    public HttpRequest proxy(Proxy proxy);

    public void execute() throws IOException;
}

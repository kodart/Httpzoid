package com.kodart.httpzoid;

import java.util.List;
import java.util.Map;

/**
 * (c) Artur Sharipov
 */
public class HttpResponse {

    private int code;
    private Map<String, List<String>> headers;

    public HttpResponse(int code, Map<String, List<String>> headers) {
        this.code = code;
        this.headers = headers;
    }

    public int getCode() {
        return code;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }
}

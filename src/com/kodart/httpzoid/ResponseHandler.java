package com.kodart.httpzoid;

/**
 * (c) Artur Sharipov
 */
public class ResponseHandler<T> {
    public void success(T data, HttpResponse response){}
    public void error(){}
    public void complete(){}
}

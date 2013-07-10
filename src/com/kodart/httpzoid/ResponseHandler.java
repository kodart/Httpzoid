package com.kodart.httpzoid;

/**
 * (c) Artur Sharipov
 */
public class ResponseHandler<T> {
    /**
     * Notifies about success
     * @param data returned data
     * @param response http response object
     */
    public void success(T data, HttpResponse response){}

    /**
     * Notifies about error (http response code >= 400)
     * @param message error message
     * @param response http response object
     */
    public void error(String message, HttpResponse response){}

    /**
     * Notifies about request complete (happens after success/error)
     */
    public void complete(){}
}
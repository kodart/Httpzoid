package com.kodart.httpzoid;

/**
 * Response callback handler
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
     * Notifies about network failure (offline, authentication error, etc.)
     * @param error
     */
    public void failure(NetworkError error){}

    /**
     * Notifies about request complete (happens after success/error/failure)
     */
    public void complete(){}
}
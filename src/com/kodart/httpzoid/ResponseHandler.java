package com.kodart.httpzoid;

/**
 * Response callback handler interface
 * (c) Artur Sharipov
 */
public interface ResponseHandler<T> {
    /**
     * Notifies about success
     * @param data returned data
     * @param response http response object
     */
    void success(T data, HttpResponse response);

    /**
     * Notifies about error (http response code >= 400)
     * @param message error message
     * @param response http response object
     */
    void error(String message, HttpResponse response);

    /**
     * Notifies about network failure (offline, authentication error, etc.)
     * @param error
     */
    void failure(NetworkError error);

    /**
     * Notifies about request complete (happens after success/error/failure)
     */
    void complete();
}
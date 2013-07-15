package com.kodart.httpzoid;

import java.net.Proxy;

/**
 * Http request
 *
 * (c) Artur Sharipov
 */
public interface HttpRequest {
    /**
     * Data object to be sent to server
     * @param data entity
     * @return current request for chaining
     */
    public HttpRequest data(Object data);

    /**
     * Set request header
     * @param key header key
     * @param value header value
     * @return current request for chaining
     */
    public HttpRequest header(String key, String value);

    /**
     * Set request custom content type. By default it uses content type of the provided serializer.
     * @param value content type
     * @return current request for chaining
     */
    public HttpRequest contentType(String value);

    /**
     * Set request timeout. Default value is 60 sec.
     * @param timeout timeout in ms, 0 defines infinite timeout.
     * @return current request for chaining
     */
    public HttpRequest timeout(int timeout);

    /**
     * Set request proxy. By default no proxy is used.
     * @param proxy proxy
     * @return current request for chaining
     */
    public HttpRequest proxy(Proxy proxy);

    /**
     * Set request callback handler. It is executed in UI thread.
     * @param handler callback handler
     * @return current request for chaining
     */
    public HttpRequest handler(ResponseHandler handler);

    /**
     * Execute request in background.
     */
    public Cancellable send();
}

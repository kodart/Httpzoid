package com.kodart.httpzoid;

/**
 * Network errors
 *
 * (c) Artur Sharipov
 */
public enum NetworkError {
    Offline,
    /**
     * Network authentication requested (web-form)
     */
    AuthenticationRequired,
    /**
     * Unsupported HTTP method requested
     */
    UnsupportedMethod,
    /**
     * Request timeout
     */
    Timeout, /**
     * Unknown error, post an issue with logs if you encounter it.
     */
    Unknown
}

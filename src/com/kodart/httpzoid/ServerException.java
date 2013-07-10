package com.kodart.httpzoid;

/**
 * (c) Artur Sharipov
 */
public class ServerException extends HttpzoidException {
    public ServerException(String response) {
        super("Server error: " + response);
    }
}

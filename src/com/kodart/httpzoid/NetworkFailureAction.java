package com.kodart.httpzoid;

/**
 * (c) Artur Sharipov
 */
public class NetworkFailureAction implements Action {
    private ResponseHandler handler;
    private NetworkError error;

    public NetworkFailureAction(ResponseHandler handler, NetworkError error) {
        this.handler = handler;
        this.error = error;
    }

    @Override
    public void call() {
        handler.failure(error);
    }
}

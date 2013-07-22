package com.kodart.httpzoid;

import android.os.AsyncTask;
import android.util.Log;
import com.google.gson.JsonParseException;
import com.kodart.httpzoid.serializers.HttpSerializer;

import java.io.*;
import java.lang.reflect.Method;
import java.net.*;
import java.util.*;


/**
 * (c) Artur Sharipov
 */
public class HttpUrlConnectionRequest implements HttpRequest {
    private static final String TAG = "Httpzoid";
    private static final int DEFAULT_TIMEOUT = 60000;
    private Proxy proxy = Proxy.NO_PROXY;
    private int timeout = DEFAULT_TIMEOUT;
    private String contentType;

    private ResponseHandler handler = new ResponseHandler();

    private Map<String, String> headers = new HashMap<String, String>();
    private Class type;
    private Object data;

    private URL url;
    private String method;
    private HttpSerializer serializer;
    private Network network;

    public HttpUrlConnectionRequest(URL url, String method, HttpSerializer serializer, Network network) {
        this.url = url;
        this.method = method;
        this.serializer = serializer;
        this.network = network;
    }

    @Override
    public HttpRequest data(Object data) {
        this.data = data;
        return this;
    }

    @Override
    public HttpRequest header(String key, String value) {
        headers.put(key, value);
        return this;
    }

    @Override
    public HttpRequest contentType(String value) {
        contentType = value;
        return this;
    }

    @Override
    public HttpRequest handler(ResponseHandler handler) {
        this.handler = handler;
        type = findType(handler);
        return this;
    }

    @Override
    public HttpRequest timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    @Override
    public HttpRequest proxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    @Override
    public Cancellable send() {
        if (network.isOffline()) {
            handler.failure(NetworkError.Offline);
            handler.complete();
            return Cancellable.Empty;
        }

        return new AsyncTaskCancellable(new AsyncTask<Void, Void, Action>() {
            @Override
            protected Action doInBackground(Void... params) {
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) url.openConnection(proxy);
                    init(connection);
                    sendData(connection);
                    final HttpDataResponse response = readData(connection);
                    return new Action() {
                        @Override
                        public void call() {
                            if (response.getCode() < 400)
                                handler.success(response.getData(), response);
                            else {
                                handler.error((String) response.getData(), response);
                            }
                        }
                    };

                } catch (HttpzoidException e) {
                    Log.e(TAG, e.getMessage());
                    return new NetworkFailureAction(handler, e.getNetworkError());
                } catch (SocketTimeoutException e) {
                    Log.e(TAG, e.getMessage());
                    return new NetworkFailureAction(handler, NetworkError.Timeout);
                } catch (ProtocolException e) {
                    Log.wtf(TAG, e.getMessage());
                    return new NetworkFailureAction(handler, NetworkError.UnsupportedMethod);
                } catch (Throwable e) {
                    Log.wtf(TAG, e);
                    return new NetworkFailureAction(handler, NetworkError.Unknown);
                } finally {
                    if (connection != null)
                        connection.disconnect();
                }
            }

            @Override
            protected void onPostExecute(Action action) {
                action.call();
                handler.complete();
            }

        }.execute());
    }

    private HttpDataResponse readData(HttpURLConnection connection) throws NetworkAuthenticationException, IOException {
        int responseCode = getResponseCode(connection);
        if (responseCode >= 500) {
            String response = getString(connection.getErrorStream());
            Log.e(TAG, response);
            return new HttpDataResponse(response, responseCode, connection.getHeaderFields());
        }

        if (responseCode >= 400) {
            return new HttpDataResponse(getString(connection.getErrorStream()), responseCode, connection.getHeaderFields());
        }

        InputStream input = new BufferedInputStream(connection.getInputStream());
        validate(connection);

        if (type.equals(Void.class))
            return new HttpDataResponse(null, responseCode, connection.getHeaderFields());

        if (type.equals(InputStream.class)) {
            ByteArrayOutputStream memory = new ByteArrayOutputStream();
            copyStream(input, memory);
            return new HttpDataResponse(new ByteArrayInputStream(memory.toByteArray()), responseCode, connection.getHeaderFields());
        }

        if (type.equals(String.class)) {
            return new HttpDataResponse(getString(input), responseCode, connection.getHeaderFields());
        }

        String value = getString(input);
        Log.d(TAG, "RECEIVED: " + value);
        return new HttpDataResponse(serializer.deserialize(value, type), responseCode, connection.getHeaderFields());
    }

    private int getResponseCode(HttpURLConnection connection) throws IOException {
        try {
            return connection.getResponseCode();
        } catch (IOException e) {
            if (e.getMessage().equals("Received authentication challenge is null"))
                return 401;
            throw e;
        }
    }

    private String getString(InputStream input) throws IOException {
        if (input == null)
            return null;

        StringBuilder builder = new StringBuilder();
        // todo need to find content encoding / getContentEncoding doesn't work
        InputStreamReader reader = new InputStreamReader(input, "UTF-8");
        int bytes;
        char[] buffer = new char[64 * 1024];
        while ((bytes = reader.read(buffer)) != -1) {
            builder.append(buffer, 0, bytes);
        }
        return builder.toString();
    }

    private void copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[64 * 1024];
        int bytes;
        while ((bytes = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytes);
        }
    }

    private void sendData(HttpURLConnection connection) throws IOException {
        if (data == null)
            return;

        connection.setDoOutput(true);
        OutputStream outputStream = new BufferedOutputStream(connection.getOutputStream());
        try {
            if (data instanceof InputStream) {
                copyStream((InputStream)data, outputStream);
            }
            else if (data instanceof String) {
                OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
                Log.d(TAG, "SENT: " + data);
                writer.write((String)data);
                writer.flush();
            }
            else {
                OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
                String output = serializer.serialize(data);
                Log.d(TAG, "SENT: " + output);
                writer.write(output);
                writer.flush();
            }
        }
        finally {
            outputStream.flush();
            outputStream.close();
        }
    }

    private void setContentType(Object data, HttpURLConnection connection) {
        if (headers.containsKey("Content-Type"))
            return;
        if (contentType != null) {
            connection.setRequestProperty("Content-Type", contentType);
            return;
        }

        if (data instanceof InputStream)
            connection.setRequestProperty("Content-Type", "application/octet-stream");
        else
            connection.setRequestProperty("Content-Type", serializer.getContentType());
    }

    private void init(HttpURLConnection connection) throws ProtocolException {
        connection.setRequestMethod(method);
        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }
        setContentType(data, connection);
    }

    private void validate(HttpURLConnection connection) throws NetworkAuthenticationException {
        if (!url.getHost().equals(connection.getURL().getHost())) {
            throw new NetworkAuthenticationException();
        }
    }

    private Class findType(ResponseHandler handler) {
        Method[] methods = handler.getClass().getMethods();
        for(Method method : methods) {
            if (method.getName().equals("success")) {
                Class param = method.getParameterTypes()[0];
                if (!param.equals(Object.class))
                    return param;
            }
        }
        return Object.class;
    }
}
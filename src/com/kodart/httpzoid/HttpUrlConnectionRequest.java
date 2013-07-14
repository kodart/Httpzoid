package com.kodart.httpzoid;

import android.os.AsyncTask;
import android.util.Log;
import com.kodart.httpzoid.serializers.HttpSerializer;

import java.io.*;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.util.*;


/**
 * (c) Artur Sharipov
 */
public class HttpUrlConnectionRequest implements HttpRequest {

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
    public void execute() {
        if (network.isOffline()) {
            handler.failure(NetworkError.Offline);
            handler.complete();
            return;
        }

        new AsyncTask<Void, Void, Action>() {
            @Override
            protected Action doInBackground(Void... params) {
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection)url.openConnection(proxy);
                    init(connection);
                    sendData(connection);
                    final HttpDataResponse response = new HttpDataResponse(readData(connection), connection);
                    return new Action() {
                        @Override
                        public void call() {
                            if (response.getResponseCode() < 400)
                                handler.success(response.getData(), response);
                            else {
                                handler.error((String)response.getData(), response);
                            }
                        }
                    };
                }
                catch (final HttpzoidException e) {
                    Log.e("Httpzoid", e.getMessage());
                    return new NetworkFailureAction(handler, e.getNetworkError());
                }
                catch (final ProtocolException e) {
                    Log.e("Httpzoid", e.getMessage());
                    return new NetworkFailureAction(handler, NetworkError.UnsupportedMethod);
                }
                catch (Throwable e) {
                    Log.wtf("Httpzoid", e);
                    return new NetworkFailureAction(handler, NetworkError.Unknown);
                }
                finally {
                    if (connection != null)
                        connection.disconnect();
                }
            }

            @Override
            protected void onPostExecute(Action action) {
                action.call();
                handler.complete();
            }

        }.execute();
    }

    private Object readData(HttpURLConnection connection) throws NetworkAuthenticationException, IOException {
        if (connection.getResponseCode() >= 500) {
            String response = getString(connection.getErrorStream());
            Log.wtf("Httpzoid", response);
            return response;
        }

        if (connection.getResponseCode() >= 400) {
            return getString(connection.getErrorStream());
        }

        InputStream input = new BufferedInputStream(connection.getInputStream());
        validate(connection);

        if (type.equals(Void.class))
            return null;

        if (InputStream.class.isAssignableFrom(type))
            return input;

        if (type.equals(String.class)) {
            return getString(input);
        }

        return serializer.deserialize(getString(input), type);
    }

    private String getString(InputStream input) throws IOException {
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

    private void sendData(HttpURLConnection connection) throws IOException {
        if (data == null)
            return;

        connection.setDoOutput(true);
        OutputStream outputStream = new BufferedOutputStream(connection.getOutputStream());
        try {
            if (data instanceof InputStream) {
                byte[] buffer = new byte[64 * 1024];
                int bytes;
                while ((bytes = ((InputStream)data).read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytes);
                }
            } else {
                OutputStreamWriter writer = new OutputStreamWriter(outputStream);
                writer.write(serializer.serialize(data));
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
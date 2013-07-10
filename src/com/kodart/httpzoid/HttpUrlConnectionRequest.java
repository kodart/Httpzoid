package com.kodart.httpzoid;

import android.os.AsyncTask;
import android.util.Log;
import com.kodart.httpzoid.serializers.HttpSerializer;

import java.io.*;
import java.lang.ref.WeakReference;
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

    private ResponseHandler handler = new ResponseHandler();
    private Map<String, String> headers = new HashMap<String, String>();
    private Class type;
    private Object data;

    private URL url;
    private String method;
    private HttpSerializer serializer;

    public HttpUrlConnectionRequest(URL url, String method, HttpSerializer serializer) {
        this.url = url;
        this.method = method;
        this.serializer = serializer;
    }

    @Override
    public HttpRequest data(Object data) {
        this.data = data;
        return this;
    }

    @Override
    public HttpRequest setHeader(String key, String value) {
        headers.put(key, value);
        return this;
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
        new AsyncTask<Void, Void, HttpDataResponse>() {
            @Override
            protected HttpDataResponse doInBackground(Void... params) {
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection)url.openConnection(proxy);
                    init(connection);
                    sendData(connection);
                    return new HttpDataResponse(readData(connection), connection);
                }
                catch (HttpzoidException e) {
                    Log.e("Httpzoid", e.getMessage());
                    return new HttpDataResponse(connection);
                }
                catch (IOException e) {
                    Log.e("Httpzoid", e.getMessage());
                    return new HttpDataResponse(connection);
                }
                catch (Throwable e) {
                    Log.e("Httpzoid", e.getMessage());
                    return new HttpDataResponse(connection);
                }
                finally {
                    if (connection != null)
                        connection.disconnect();
                }
            }

            @Override
            protected void onPostExecute(HttpDataResponse response) {
                if (response.getResponseCode() < 400)
                    handler.success(response.getData(), response);
                else {
                    handler.error(response);
                }
                handler.complete();
            }

        }.execute();
    }

    private Object readData(HttpURLConnection connection) throws IOException {
        InputStream input = new BufferedInputStream(connection.getInputStream());
        validate(connection);

        if (connection.getResponseCode() >= 500) {
            String response = getString(input);
            Log.e("Httpzoid", response);
            throw new ServerException(response);
        }

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
        int bytes;
        char[] buffer = new char[100 * 1024];
        StringBuilder builder = new StringBuilder();

        // todo need to find content encoding / getContentEncoding doesn't work
        InputStreamReader reader = new InputStreamReader(input, "UTF-8");
        while ((bytes = reader.read(buffer)) != -1) {
            builder.append(buffer, 0, bytes);
        }
        return builder.toString();
    }

    private void sendData(HttpURLConnection connection) throws IOException {
        if (data == null)
            return;

        OutputStream outputStream = null;
        try {
            if (data instanceof InputStream) {
                byte[] buffer = new byte[64 * 1024];
                int bytes;
                connection.setDoOutput(true);
                outputStream = connection.getOutputStream();
                while ((bytes = ((InputStream)data).read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytes);
                }
            } else {
                connection.setRequestProperty("Content-Type", serializer.getContentType());
                connection.setDoOutput(true);
                outputStream = connection.getOutputStream();
                OutputStreamWriter writer = new OutputStreamWriter(outputStream);
                writer.write(serializer.serialize(data));
                writer.flush();
            }
        }
        finally {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
        }
    }

    private void init(HttpURLConnection connection) throws ProtocolException {
        connection.setRequestMethod(method);
        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }
    }

    private void validate(HttpURLConnection connection) throws NetworkAuthenticationException {
        if (!url.getHost().equals(connection.getURL().getHost())) {
            throw new NetworkAuthenticationException();
        }
    }
}
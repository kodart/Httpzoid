package com.kodart.httpzoid.serializers;

import com.google.gson.Gson;

/**
 * (c) Artur Sharipov
 */
public class JsonHttpSerializer implements HttpSerializer {
    private Gson mapper = new Gson();

    @Override
    public String getContentType() {
        return "application/json";
    }

    @Override
    public String serialize(Object object) {
        return mapper.toJson(object);
    }

    @Override
    public Object deserialize(String value, Class type) {
        return mapper.fromJson(value, type);
    }
}


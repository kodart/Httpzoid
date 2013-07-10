package com.kodart.httpzoid.serializers;

public class XmlHttpSerializer implements HttpSerializer {

    @Override
    public String getContentType() {
        return "application/xml";
    }

    @Override
    public String serialize(Object object) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object deserialize(String value, Class type) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}

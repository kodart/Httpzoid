# Httpzoid - Android REST (JSON) Client
## Overview
Httpzoid is designed to provide a simple way to deal with REST requests.
Requests are asynchronous, callback handler runs in UI thread.

## Quick start
This sample will make a post request to the specified url and send User object in JSON format.
```java
Http http = HttpFactory.create(context);
http.post("http://example.com/users")
    .data(new User("John"))
    .send();
```

Request with callbacks.
```java
Http http = HttpFactory.create(context);
http.post("http://example.com/users")
    .data(new User("John"))
    .handler(new ResponseHandler<Void>() {
        @Override
        public void success(Void ignore, HttpResponse response) {
        }

        @Override
        public void error(String message, HttpResponse response) {
        }

        @Override
        public void failure(NetworkError error) {
        }

        @Override
        public void complete() {
        }
    }).send();
```

Httpzoid works with objects or stream directly
```java
Http http = HttpFactory.create(context);
http.get("http://example.com/users")
    .handler(new ResponseHandler<User[]>() {
        @Override
        public void success(User[] users, HttpResponse response) {
        }
    }).send();

InputStream input = new FileInputStream("avatar.jpg");
http.post("http://example.com/users/1/avatar")
    .data(input)
    .handler(new ResponseHandler<Void>() {
        @Override
        public void complete() {
            input.close();
        }
    }).send();
```
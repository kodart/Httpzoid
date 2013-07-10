## Overview
Httpzoid is designed to provide a simple way to deal with REST requests.
Requests are asynchronous, callback handler runs in UI thread.

## Quick start
This sample will make a post request to the specified url and send User object in JSON format.
```java
Http http = HttpFactory.create();
http.post("http://example.com/users")
    .data(new User("John"))
    .execute();
```

Request with callbacks.
```java
Http http = HttpFactory.create();
http.post("http://example.com/users")
    .data(new User("John"))
    .handler(new ResponseHandler<Void>() {
        @Override
        public void success(Void ignore, HttpResponse response) {
        }

        @Override
        public void error(HttpResponse response) {
        }

        @Override
        public void complete() {
        }
    }).execute();
```

Httpzoid works with objects or stream directly
```java
Http http = HttpFactory.create();
http.get("http://example.com/users")
    .handler(new ResponseHandler<User[]>() {
        @Override
        public void success(User[] users, HttpResponse response) {
        }
    }).execute();

InputStream input = new FileInputStream("avatar.jpg");
http.post("http://example.com/users/1/avatar")
    .data(input)
    .handler(new ResponseHandler<Void>() {
        @Override
        public void complete() {
            input.close();
        }
    }).execute();
```
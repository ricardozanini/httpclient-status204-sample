# HttpCommons Status 204

A simple reproducer to observe how HttpCommons 4.x handles HTTP 204 responses with a body.

## Lab Results

For this lab, I implemented three different servers to observe their behavior with [RFC 7230 (HTTP/1.1)](https://tools.ietf.org/html/rfc7230#section-3.3.3) when returning a Http Status 204 with a body (breaking the RFC).

The motivation behind this lab was to observe the response parse by HttpCommons in such scenarios. The problem is, neither servers return a response body when setting a 204 http status:

- [`HttpCommonsNastyServer`](src/main/java/samples/httpclient/status204/HttpCommonsNastyServer.java)
- [`JetttyNastyServer`](src/main/java/samples/httpclient/status204/JetttyNastyServer.java)
- [`TomcatNastyServer`](src/main/java/samples/httpclient/status204/TomcatNastyServer.java)

Still looking for a server which breaks the RFC.

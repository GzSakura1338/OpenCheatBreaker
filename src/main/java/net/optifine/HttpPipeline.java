package net.optifine;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpPipeline {
    public static Map mapConnections = new HashMap();
    public static final String HEADER_USER_AGENT = "User-Agent";
    public static final String HEADER_HOST = "Host";
    public static final String HEADER_ACCEPT = "Accept";
    public static final String HEADER_LOCATION = "Location";
    public static final String HEADER_KEEP_ALIVE = "Keep-Alive";
    public static final String HEADER_CONNECTION = "Connection";
    public static final String HEADER_VALUE_KEEP_ALIVE = "keep-alive";
    public static final String HEADER_TRANSFER_ENCODING = "Transfer-Encoding";
    public static final String HEADER_VALUE_CHUNKED = "chunked";

    public static void addRequest(String str, HttpListener httpListener) throws java.net.MalformedURLException, IOException {
        addRequest(str, httpListener, Proxy.NO_PROXY);
    }

    public static void addRequest(String str, HttpListener httpListener, Proxy proxy) throws java.net.MalformedURLException, IOException {
        HttpRequest makeRequest2 = makeRequest(str, proxy);
        HttpPipelineRequest httpPipelineRequest = new HttpPipelineRequest(makeRequest2, httpListener);
        addRequest(httpPipelineRequest);
    }

    public static HttpRequest makeRequest(String str, Proxy proxy) throws java.net.MalformedURLException, IOException {
        URL url = new URL(str);
        if (!url.getProtocol().equals("http")) {
            throw new IOException("Only protocol http is supported: " + url);
        }

        String file = url.getFile();
        String host = url.getHost();
        int port = url.getPort();
        if (port <= 0) {
            port = 80;
        }

        String str2 = "GET";
        String str3 = "HTTP/1.1";
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put("User-Agent", "Java/" + System.getProperty("java.version"));
        linkedHashMap.put("Host", host);
        linkedHashMap.put("Accept", "text/html, image/gif, image/png");
        linkedHashMap.put("Connection", "keep-alive");
        byte[] bytes = new byte[0];
        return new HttpRequest(host, port, proxy, str2, file, str3, linkedHashMap, bytes);
    }

    public static void addRequest(HttpPipelineRequest httpPipelineRequest) {
        HttpRequest httpRequest = httpPipelineRequest.getHttpRequest();

        for (HttpPipelineConnection connection = getConnection(httpRequest.getHost(), httpRequest.getPort(), httpRequest.getProxy());
            !connection.addRequest(httpPipelineRequest);
            connection = getConnection(httpRequest.getHost(), httpRequest.getPort(), httpRequest.getProxy())
        ) {
            removeConnection(httpRequest.getHost(), httpRequest.getPort(), httpRequest.getProxy(), connection);
        }
    }

    public static synchronized HttpPipelineConnection getConnection(String str, int n, Proxy proxy) {
        String makeConnectionKey2 = makeConnectionKey(str, n, proxy);
        HttpPipelineConnection httpPipelineConnection = (HttpPipelineConnection)mapConnections.get(makeConnectionKey2);
        if (httpPipelineConnection == null) {
            httpPipelineConnection = new HttpPipelineConnection(str, n, proxy);
            mapConnections.put(makeConnectionKey2, httpPipelineConnection);
        }

        return httpPipelineConnection;
    }

    public static synchronized void removeConnection(String str, int n, Proxy proxy, HttpPipelineConnection httpPipelineConnection) {
        String makeConnectionKey2 = makeConnectionKey(str, n, proxy);
        HttpPipelineConnection httpPipelineConnection2 = (HttpPipelineConnection)mapConnections.get(makeConnectionKey2);
        if (httpPipelineConnection2 == httpPipelineConnection) {
            mapConnections.remove(makeConnectionKey2);
        }
    }

    public static String makeConnectionKey(String str, int n, Proxy proxy) {
        return str + ":" + n + "-" + proxy;
    }

    public static byte[] get(String str) throws java.net.MalformedURLException, IOException {
        return get(str, Proxy.NO_PROXY);
    }

    public static byte[] get(String str, Proxy proxy) throws java.net.MalformedURLException, IOException {
        if (str.startsWith("file:")) {
            URL url = new URL(str);
            InputStream openStream = url.openStream();
            return Config.readAll(openStream);
        } else {
            HttpRequest makeRequest2 = makeRequest(str, proxy);
            HttpResponse executeRequest2 = executeRequest(makeRequest2);
            if (executeRequest2.getStatus() / 100 != 2) {
                throw new IOException("HTTP response: " + executeRequest2.getStatus());
            } else {
                return executeRequest2.getBody();
            }
        }
    }

    public static HttpResponse executeRequest(HttpRequest httpRequest) throws InterruptedIOException, IOException {
        final HashMap map = new HashMap();
        String str = "Response";
        String str2 = "Exception";
        HttpListener httpListener = new HttpListener() {
            @Override
            public void finished(HttpRequest httpRequest2, HttpResponse httpResponse) {
                HashMap map2 = map;
                synchronized (map) {
                    map.put("Response", httpResponse);
                    map.notifyAll();
                }
            }

            @Override
            public void failed(HttpRequest httpRequest2, Exception exception) {
                HashMap map2 = map;
                synchronized (map) {
                    map.put("Exception", exception);
                    map.notifyAll();
                }
            }
        };
        synchronized (map) {
            HttpPipelineRequest httpPipelineRequest = new HttpPipelineRequest(httpRequest, httpListener);
            addRequest(httpPipelineRequest);

            try {
                map.wait();
            } catch (InterruptedException e) {
                throw new InterruptedIOException("Interrupted");
            }

            Exception exception = (Exception)map.get("Exception");
            if (exception != null) {
                if (exception instanceof IOException) {
                    throw (IOException)exception;
                } else if (exception instanceof RuntimeException) {
                    throw (RuntimeException)exception;
                } else {
                    throw new RuntimeException(exception.getMessage(), exception);
                }
            } else {
                HttpResponse httpResponse = (HttpResponse)map.get("Response");
                if (httpResponse == null) {
                    throw new IOException("Response is null");
                } else {
                    return httpResponse;
                }
            }
        }
    }

    public static boolean hasActiveRequests() {
        for (HttpPipelineConnection httpPipelineConnection : (Iterable<HttpPipelineConnection>)mapConnections.values()) {
            if (httpPipelineConnection.hasActiveRequests()) {
                return true;
            }
        }

        return false;
    }
}

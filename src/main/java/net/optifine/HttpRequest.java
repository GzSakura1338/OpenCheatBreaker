package net.optifine;

import java.net.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequest {
    public String host = null;
    public int port = 0;
    public Proxy proxy = Proxy.NO_PROXY;
    public String method = null;
    public String file = null;
    public String http = null;
    public Map<String, String> headers = new LinkedHashMap<>();
    public byte[] body = null;
    public int redirects = 0;
    public static final String METHOD_GET = "GET";
    public static final String METHOD_HEAD = "HEAD";
    public static final String METHOD_POST = "POST";
    public static final String HTTP_1_0 = "HTTP/1.0";
    public static final String HTTP_1_1 = "HTTP/1.1";

    public HttpRequest(String str, int n, Proxy proxy, String str2, String str3, String str4, Map<String, String> map, byte[] bytes) {
        this.host = str;
        this.port = n;
        this.proxy = proxy;
        this.method = str2;
        this.file = str3;
        this.http = str4;
        this.headers = map;
        this.body = bytes;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public String getMethod() {
        return this.method;
    }

    public String getFile() {
        return this.file;
    }

    public String getHttp() {
        return this.http;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public byte[] getBody() {
        return this.body;
    }

    public int getRedirects() {
        return this.redirects;
    }

    public void setRedirects(int n) {
        this.redirects = n;
    }

    public Proxy getProxy() {
        return this.proxy;
    }
}

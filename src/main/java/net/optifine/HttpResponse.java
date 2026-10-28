package net.optifine;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {
    public int status = 0;
    public String statusLine = null;
    public Map<String, String> headers = new LinkedHashMap<>();
    public byte[] body = null;

    public HttpResponse(int n, String str, Map map, byte[] bytes) {
        this.status = n;
        this.statusLine = str;
        this.headers = map;
        this.body = bytes;
    }

    public int getStatus() {
        return this.status;
    }

    public String getStatusLine() {
        return this.statusLine;
    }

    public Map getHeaders() {
        return this.headers;
    }

    public String getHeader(String str) {
        return this.headers.get(str);
    }

    public byte[] getBody() {
        return this.body;
    }
}

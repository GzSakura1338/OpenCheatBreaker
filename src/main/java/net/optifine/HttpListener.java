package net.optifine;

public interface HttpListener {
    void finished(HttpRequest httpRequest, HttpResponse httpResponse);

    void failed(HttpRequest httpRequest, Exception exception);
}

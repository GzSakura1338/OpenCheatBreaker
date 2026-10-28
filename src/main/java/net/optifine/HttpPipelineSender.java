package net.optifine;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Map;

public class HttpPipelineSender extends Thread {
    public HttpPipelineConnection httpPipelineConnection = null;
    public static final String CRLF = "\r\n";
    public static Charset ASCII = Charset.forName("ASCII");

    public HttpPipelineSender(HttpPipelineConnection httpPipelineConnection) {
        super("HttpPipelineSender");
        this.httpPipelineConnection = httpPipelineConnection;
    }

    @Override
    public void run() {
        HttpPipelineRequest httpPipelineRequest = null;

        try {
            this.connect();

            while (!Thread.interrupted()) {
                httpPipelineRequest = this.httpPipelineConnection.getNextRequestSend();
                HttpRequest httpRequest = httpPipelineRequest.getHttpRequest();
                OutputStream outputStream = this.httpPipelineConnection.getOutputStream();
                this.writeRequest(httpRequest, outputStream);
                this.httpPipelineConnection.onRequestSent(httpPipelineRequest);
            }
        } catch (InterruptedException e) {
            return;
        } catch (Exception e) {
            this.httpPipelineConnection.onExceptionSend(httpPipelineRequest, e);
        }
    }

    public void connect() throws java.io.IOException {
        String host = this.httpPipelineConnection.getHost();
        int port = this.httpPipelineConnection.getPort();
        Proxy proxy = this.httpPipelineConnection.getProxy();
        Socket socket = new Socket(proxy);
        socket.connect(new InetSocketAddress(host, port), 5000);
        this.httpPipelineConnection.setSocket(socket);
    }

    public void writeRequest(HttpRequest httpRequest, OutputStream outputStream) throws java.io.IOException {
        this.write(outputStream, httpRequest.getMethod() + " " + httpRequest.getFile() + " " + httpRequest.getHttp() + "\r\n");
        Map headers = httpRequest.getHeaders();

        for (String str : (Iterable<String>)headers.keySet()) {
            String str2 = httpRequest.getHeaders().get(str);
            this.write(outputStream, str + ": " + str2 + "\r\n");
        }

        this.write(outputStream, "\r\n");
    }

    public void write(OutputStream outputStream, String str) throws java.io.IOException {
        byte[] bytes = str.getBytes(ASCII);
        outputStream.write(bytes);
    }
}

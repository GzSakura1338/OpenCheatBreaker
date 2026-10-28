package net.optifine;

import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class HttpPipelineConnection {
    public String host = null;
    public int port = 0;
    public Proxy proxy = Proxy.NO_PROXY;
    public List<HttpPipelineRequest> listRequests = new LinkedList<>();
    public List<HttpPipelineRequest> listRequestsSend = new LinkedList<>();
    public Socket socket = null;
    public InputStream inputStream = null;
    public OutputStream outputStream = null;
    public HttpPipelineSender httpPipelineSender = null;
    public HttpPipelineReceiver httpPipelineReceiver = null;
    public int countRequests = 0;
    public boolean responseReceived = false;
    public long keepaliveTimeoutMs = 5000L;
    public int keepaliveMaxCount = 1000;
    public long timeLastActivityMs = System.currentTimeMillis();
    public boolean terminated = false;
    public static final String EMPTY_TEXT = "\n";
    public static Pattern patternFullUrl = Pattern.compile("^[a-zA-Z]+://.*");
    public static final int CONSTANT_5000 = 5000;
    public static final int CONSTANT_5000_2 = 5000;

    public HttpPipelineConnection(String str, int n) {
        this(str, n, Proxy.NO_PROXY);
    }

    public HttpPipelineConnection(String str, int n, Proxy proxy) {
        this.host = str;
        this.port = n;
        this.proxy = proxy;
        this.httpPipelineSender = new HttpPipelineSender(this);
        this.httpPipelineSender.start();
        this.httpPipelineReceiver = new HttpPipelineReceiver(this);
        this.httpPipelineReceiver.start();
    }

    public synchronized boolean addRequest(HttpPipelineRequest httpPipelineRequest) {
        if (this.isClosed()) {
            return false;
        }

        this.addRequest(httpPipelineRequest, this.listRequests);
        this.addRequest(httpPipelineRequest, this.listRequestsSend);
        this.countRequests++;
        return true;
    }

    public void addRequest(HttpPipelineRequest httpPipelineRequest, List<HttpPipelineRequest> list) {
        list.add(httpPipelineRequest);
        this.notifyAll();
    }

    public synchronized void setSocket(Socket socket) throws java.net.SocketException, IOException {
        if (!this.terminated) {
            if (this.socket != null) {
                throw new IllegalArgumentException("Already connected");
            }

            this.socket = socket;
            this.socket.setTcpNoDelay(true);
            this.inputStream = this.socket.getInputStream();
            this.outputStream = new BufferedOutputStream(this.socket.getOutputStream());
            this.onActivity();
            this.notifyAll();
        }
    }

    public synchronized OutputStream getOutputStream() throws InterruptedException {
        while (this.outputStream == null) {
            this.checkTimeout();
            this.wait(1000L);
        }

        return this.outputStream;
    }

    public synchronized InputStream getInputStream() throws InterruptedException {
        while (this.inputStream == null) {
            this.checkTimeout();
            this.wait(1000L);
        }

        return this.inputStream;
    }

    public synchronized HttpPipelineRequest getNextRequestSend() throws IOException, InterruptedException {
        if (this.listRequestsSend.size() <= 0 && this.outputStream != null) {
            this.outputStream.flush();
        }

        return this.getNextRequest(this.listRequestsSend, true);
    }

    public synchronized HttpPipelineRequest getNextRequestReceive() throws InterruptedException {
        return this.getNextRequest(this.listRequests, false);
    }

    public HttpPipelineRequest getNextRequest(List<HttpPipelineRequest> list, boolean flag) throws InterruptedException {
        while (list.size() <= 0) {
            this.checkTimeout();
            this.wait(1000L);
        }

        this.onActivity();
        return flag ? (HttpPipelineRequest)list.remove(0) : (HttpPipelineRequest)list.get(0);
    }

    public void checkTimeout() {
        if (this.socket != null) {
            long keepaliveTimeoutMs = this.keepaliveTimeoutMs;
            if (this.listRequests.size() > 0) {
                keepaliveTimeoutMs = 5000L;
            }

            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis > this.timeLastActivityMs + keepaliveTimeoutMs) {
                this.terminate(new InterruptedException("Timeout " + keepaliveTimeoutMs));
            }
        }
    }

    public void onActivity() {
        this.timeLastActivityMs = System.currentTimeMillis();
    }

    public synchronized void onRequestSent(HttpPipelineRequest httpPipelineRequest) {
        if (!this.terminated) {
            this.onActivity();
        }
    }

    public synchronized void onResponseReceived(HttpPipelineRequest httpPipelineRequest, HttpResponse httpResponse) {
        if (!this.terminated) {
            this.responseReceived = true;
            this.onActivity();
            if (this.listRequests.size() <= 0 || this.listRequests.get(0) != httpPipelineRequest) {
                throw new IllegalArgumentException("Response out of order: " + httpPipelineRequest);
            }

            this.listRequests.remove(0);
            httpPipelineRequest.setClosed(true);
            String header = httpResponse.getHeader("Location");
            if (httpResponse.getStatus() / 100 == 3 && header != null && httpPipelineRequest.getHttpRequest().getRedirects() < 5) {
                try {
                    header = this.normalizeUrl(header, httpPipelineRequest.getHttpRequest());
                    HttpRequest makeRequest = HttpPipeline.makeRequest(header, httpPipelineRequest.getHttpRequest().getProxy());
                    makeRequest.setRedirects(httpPipelineRequest.getHttpRequest().getRedirects() + 1);
                    HttpPipelineRequest httpPipelineRequest2 = new HttpPipelineRequest(makeRequest, httpPipelineRequest.getHttpListener());
                    HttpPipeline.addRequest(httpPipelineRequest2);
                } catch (IOException e) {
                    httpPipelineRequest.getHttpListener().failed(httpPipelineRequest.getHttpRequest(), e);
                }
            } else {
                HttpListener httpListener = httpPipelineRequest.getHttpListener();
                httpListener.finished(httpPipelineRequest.getHttpRequest(), httpResponse);
            }

            this.checkResponseHeader(httpResponse);
        }
    }

    public String normalizeUrl(String str, HttpRequest httpRequest) {
        if (patternFullUrl.matcher(str).matches()) {
            return str;
        }

        if (str.startsWith("//")) {
            return "http:" + str;
        }

        String host = httpRequest.getHost();
        if (httpRequest.getPort() != 80) {
            host = host + ":" + httpRequest.getPort();
        }

        if (str.startsWith("/")) {
            return "http://" + host + str;
        }

        String file = httpRequest.getFile();
        int lastIndexOf = file.lastIndexOf("/");
        return lastIndexOf >= 0 ? "http://" + host + file.substring(0, lastIndexOf + 1) + str : "http://" + host + "/" + str;
    }

    public void checkResponseHeader(HttpResponse httpResponse) {
        String header = httpResponse.getHeader("Connection");
        if (header != null && !header.toLowerCase().equals("keep-alive")) {
            this.terminate(new EOFException("Connection not keep-alive"));
        }

        String header2 = httpResponse.getHeader("Keep-Alive");
        if (header2 != null) {
            String[] tokenize = Config.tokenize(header2, ",;");

            for (int i = 0; i < tokenize.length; i++) {
                String str = tokenize[i];
                String[] split = this.split(str, '=');
                if (split.length >= 2) {
                    if (split[0].equals("timeout")) {
                        int parseInt = Config.parseInt(split[1], -1);
                        if (parseInt > 0) {
                            this.keepaliveTimeoutMs = parseInt * 1000;
                        }
                    }

                    if (split[0].equals("max")) {
                        int parseInt = Config.parseInt(split[1], -1);
                        if (parseInt > 0) {
                            this.keepaliveMaxCount = parseInt;
                        }
                    }
                }
            }
        }
    }

    public String[] split(String str, char c) {
        int indexOf = str.indexOf(c);
        if (indexOf < 0) {
            return new String[]{str};
        }

        String substring = str.substring(0, indexOf);
        String substring2 = str.substring(indexOf + 1);
        return new String[]{substring, substring2};
    }

    public synchronized void onExceptionSend(HttpPipelineRequest httpPipelineRequest, Exception exception) {
        this.terminate(exception);
    }

    public synchronized void onExceptionReceive(HttpPipelineRequest httpPipelineRequest, Exception exception) {
        this.terminate(exception);
    }

    public synchronized void terminate(Exception exception) {
        if (!this.terminated) {
            this.terminated = true;
            this.terminateRequests(exception);
            if (this.httpPipelineSender != null) {
                this.httpPipelineSender.interrupt();
            }

            if (this.httpPipelineReceiver != null) {
                this.httpPipelineReceiver.interrupt();
            }

            try {
                if (this.socket != null) {
                    this.socket.close();
                }
            } catch (IOException e) {
            }

            this.socket = null;
            this.inputStream = null;
            this.outputStream = null;
        }
    }

    public void terminateRequests(Exception exception) {
        if (this.listRequests.size() > 0) {
            if (!this.responseReceived) {
                HttpPipelineRequest httpPipelineRequest = this.listRequests.remove(0);
                httpPipelineRequest.getHttpListener().failed(httpPipelineRequest.getHttpRequest(), exception);
                httpPipelineRequest.setClosed(true);
            }

            while (this.listRequests.size() > 0) {
                HttpPipelineRequest httpPipelineRequest = this.listRequests.remove(0);
                HttpPipeline.addRequest(httpPipelineRequest);
            }
        }
    }

    public synchronized boolean isClosed() {
        return this.terminated ? true : this.countRequests >= this.keepaliveMaxCount;
    }

    public int getCountRequests() {
        return this.countRequests;
    }

    public synchronized boolean hasActiveRequests() {
        return this.listRequests.size() > 0;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public Proxy getProxy() {
        return this.proxy;
    }
}

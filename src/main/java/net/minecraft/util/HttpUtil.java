package net.minecraft.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpUtil {
    public static AtomicInteger downloadThreadsStarted = new AtomicInteger(0);
    public static Logger logger = LogManager.getLogger();

    public static String buildPostString(Map data) {
        StringBuilder sb = new StringBuilder();

        for (Entry entry : (Iterable<Entry>)data.entrySet()) {
            if (sb.length() > 0) {
                sb.append('&');
            }

            try {
                sb.append(URLEncoder.encode((String)entry.getKey(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (entry.getValue() != null) {
                sb.append('=');

                try {
                    sb.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

    public static String postMap(URL url, Map data, boolean skipLoggingErrors) {
        return post(url, buildPostString(data), skipLoggingErrors);
    }

    public static String post(URL url, String content, boolean skipLoggingErrors) {
        try {
            Proxy proxy = MinecraftServer.getServer() == null ? null : MinecraftServer.getServer().getServerProxy();
            if (proxy == null) {
                proxy = Proxy.NO_PROXY;
            }

            HttpURLConnection openConnection = (HttpURLConnection)url.openConnection(proxy);
            openConnection.setRequestMethod("POST");
            openConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            openConnection.setRequestProperty("Content-Length", "" + content.getBytes().length);
            openConnection.setRequestProperty("Content-Language", "en-US");
            openConnection.setUseCaches(false);
            openConnection.setDoInput(true);
            openConnection.setDoOutput(true);
            DataOutputStream dataOutputStream = new DataOutputStream(openConnection.getOutputStream());
            dataOutputStream.writeBytes(content);
            dataOutputStream.flush();
            dataOutputStream.close();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openConnection.getInputStream()));
            StringBuffer sb = new StringBuffer();

            String str;
            while ((str = bufferedReader.readLine()) != null) {
                sb.append(str);
                sb.append('\r');
            }

            bufferedReader.close();
            return sb.toString();
        } catch (Exception e) {
            if (!skipLoggingErrors) {
                logger.error("Could not post to " + url, e);
            }

            return "";
        }
    }

    public static void downloadResourcePack(
        final File packFile,
        final String packName,
        final HttpUtil.DownloadListener completionListener,
        final Map requestData,
        final int maxSize,
        final IProgressUpdate loadingScreen,
        final Proxy proxy
    ) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() { try {
                URLConnection urlconnection = null;
                InputStream inputStream = null;
                DataOutputStream dataOutputStream = null;
                boolean flag = false;
                if (loadingScreen != null) {
                    loadingScreen.resetProgressAndMessage("Downloading Texture Pack");
                    loadingScreen.displayLoadingString("Making Request...");
                }

                try {
                    try {
                        byte[] bytes = new byte[4096];
                        URL url = new URL(packName);
                        urlconnection = url.openConnection(proxy);
                        urlconnection.setRequestProperty("User-Agent", "Minecraft/1.7.10 CheatBreaker");
                        float f = 0.0F;
                        float size = requestData.entrySet().size();

                        for (Entry entry : (Iterable<Entry>)requestData.entrySet()) {
                            urlconnection.setRequestProperty((String)entry.getKey(), (String)entry.getValue());
                            if (loadingScreen != null) {
                                loadingScreen.setLoadingProgress((int)(++f / size * 100.0F));
                            }
                        }

                        inputStream = urlconnection.getInputStream();
                        size = urlconnection.getContentLength();
                        int contentLength = urlconnection.getContentLength();
                        if (maxSize > 0 && contentLength > maxSize) {
                            throw new IOException("Filesize is bigger than maximum allowed (file is " + contentLength + ", limit is " + maxSize + ")");
                        }

                        if (loadingScreen != null) {
                            loadingScreen.displayLoadingString(String.format("Downloading file (%.2f MB)...", size / 1000.0F / 1000.0F));
                        }

                        if (packFile.exists()) {
                            long l = packFile.length();
                            if (l == contentLength) {
                                flag = true;
                                completionListener.onDownloadComplete(packFile);
                                return;
                            }

                            HttpUtil.logger.warn("Deleting " + packFile + " as it does not match what we currently have (" + contentLength + " vs our " + l + ").");
                            packFile.delete();
                        } else if (packFile.getParentFile() != null) {
                            packFile.getParentFile().mkdirs();
                        }

                        dataOutputStream = new DataOutputStream(new FileOutputStream(packFile));
                        long l = 0L;

                        int n;
                        while ((n = inputStream.read(bytes)) >= 0) {
                            l += n;
                            if (loadingScreen != null && contentLength > 0) {
                                loadingScreen.setLoadingProgress((int)(l * 100L / contentLength));
                            }

                            if (maxSize > 0 && l > maxSize) {
                                throw new IOException("Filesize was bigger than maximum allowed (got >= " + l + ", limit was " + maxSize + ")");
                            }

                            if (Thread.currentThread().isInterrupted()) {
                                throw new InterruptedIOException("Resource pack download interrupted");
                            }

                            dataOutputStream.write(bytes, 0, n);
                        }

                        if (contentLength >= 0 && l != contentLength) {
                            throw new IOException("Incomplete resource pack download (expected " + contentLength + " bytes, received " + l + ")");
                        }

                        dataOutputStream.close();
                        flag = true;
                        completionListener.onDownloadComplete(packFile);
                    } catch (Exception e) {
                        HttpUtil.logger.error("Couldn't download resource pack from " + packName, e);
                        completionListener.handleThrowable(e);
                    }
                } finally {
                    IOUtils.closeQuietly(inputStream);
                    IOUtils.closeQuietly(dataOutputStream);
                    if (dataOutputStream != null && !flag) {
                        FileUtils.deleteQuietly(packFile);
                    }

                    if (urlconnection instanceof HttpURLConnection) {
                        ((HttpURLConnection)urlconnection).disconnect();
                    }

                    if (loadingScreen != null) {
                        loadingScreen.setDoneWorking();
                    }
                }
            } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } }
        }, "File Downloader #" + downloadThreadsStarted.incrementAndGet());
        thread.setDaemon(true);
        thread.start();
    }

    public static int getSuitableLanPort() throws IOException {
        ServerSocket serverSocket = null;
        boolean flag = true;

        int n;
        try {
            serverSocket = new ServerSocket(0);
            n = serverSocket.getLocalPort();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
            }
        }

        return n;
    }

    public static String get(URL url) throws IOException {
        HttpURLConnection openConnection = (HttpURLConnection)url.openConnection();
        openConnection.setRequestMethod("GET");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openConnection.getInputStream()));
        StringBuilder sb = new StringBuilder();

        String str;
        while ((str = bufferedReader.readLine()) != null) {
            sb.append(str);
            sb.append('\r');
        }

        bufferedReader.close();
        return sb.toString();
    }

    public interface DownloadListener {
        void onDownloadComplete(File file);

        default void handleThrowable(Throwable throwable) {
        }
    }
}

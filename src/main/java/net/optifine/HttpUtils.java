package net.optifine;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import net.minecraft.client.Minecraft;

public class HttpUtils {
    public static String playerItemsUrl = null;
    public static final String SERVER_URL = "http://s.optifine.net";
    public static final String POST_URL = "http://optifine.net";

    public static byte[] get(String str) throws java.net.MalformedURLException, IOException {
        HttpURLConnection httpURLConnection = null;

        try {
            URL url = new URL(str);
            httpURLConnection = (HttpURLConnection)url.openConnection(Minecraft.getMinecraft().getProxy());
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(false);
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() / 100 != 2) {
                if (httpURLConnection.getErrorStream() != null) {
                    Config.readAll(httpURLConnection.getErrorStream());
                }

                throw new IOException("HTTP response: " + httpURLConnection.getResponseCode());
            } else {
                InputStream inputStream = httpURLConnection.getInputStream();
                byte[] bytes = new byte[httpURLConnection.getContentLength()];
                int n = 0;

                do {
                    int read = inputStream.read(bytes, n, bytes.length - n);
                    if (read < 0) {
                        throw new IOException("Input stream closed: " + str);
                    }

                    n += read;
                } while (n < bytes.length);

                return bytes;
            }
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }

    public static String post(String str, Map map, byte[] bytes) throws java.net.MalformedURLException, IOException, java.net.ProtocolException, java.io.UnsupportedEncodingException {
        HttpURLConnection httpURLConnection = null;

        try {
            URL url = new URL(str);
            httpURLConnection = (HttpURLConnection)url.openConnection(Minecraft.getMinecraft().getProxy());
            httpURLConnection.setRequestMethod("POST");
            if (map != null) {
                for (String str2 : (Iterable<String>)map.keySet()) {
                    String str3 = "" + map.get(str2);
                    httpURLConnection.setRequestProperty(str2, str3);
                }
            }

            httpURLConnection.setRequestProperty("Content-Type", "text/plain");
            httpURLConnection.setRequestProperty("Content-Length", "" + bytes.length);
            httpURLConnection.setRequestProperty("Content-Language", "en-US");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();
            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "ASCII");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer sb = new StringBuffer();

            String str2;
            while ((str2 = bufferedReader.readLine()) != null) {
                sb.append(str2);
                sb.append('\r');
            }

            bufferedReader.close();
            return sb.toString();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }

    public static synchronized String getPlayerItemsUrl() {
        if (playerItemsUrl == null) {
            try {
                boolean parseBoolean = Config.parseBoolean(System.getProperty("player.models.local"), false);
                if (parseBoolean) {
                    File mcDataDir = Minecraft.getMinecraft().mcDataDir;
                    File file = new File(mcDataDir, "playermodels");
                    playerItemsUrl = file.toURI().toURL().toExternalForm();
                }
            } catch (Exception e) {
                Config.warn("" + e.getClass().getName() + ": " + e.getMessage());
            }

            if (playerItemsUrl == null) {
                playerItemsUrl = "http://s.optifine.net";
            }
        }

        return playerItemsUrl;
    }
}

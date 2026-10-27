package net.minecraft.util;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
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
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpUtil {
   public static ListeningExecutorService downloadExecutor = MoreExecutors.listeningDecorator(
      Executors.newCachedThreadPool(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Downloader %d").build())
   );
   public static AtomicInteger downloadThreadsStarted = new AtomicInteger(0);
   public static Logger logger = LogManager.getLogger();

   public static String buildPostString(Map<String, Object> data) {
      StringBuilder sb = new StringBuilder();

      for (Entry entry : data.entrySet()) {
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

   public static String postMap(URL url, Map<String, Object> data, boolean skipLoggingErrors) {
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

   public static ListenableFuture<Object> downloadResourcePack(
      final File saveFile, final String packUrl, final Map<String, String> map, final int maxSize, final IProgressUpdate progressUpdate, final Proxy proxy
   ) {
      return downloadExecutor.submit(new Callable<Object>() {
         @Override
         public Object call() throws IOException {
            HttpURLConnection httpURLConnection = null;
            InputStream inputStream = null;
            DataOutputStream dataOutputStream = null;
            boolean flag = false;
            if (progressUpdate != null) {
               progressUpdate.resetProgressAndMessage("Downloading Resource Pack");
               progressUpdate.displayLoadingString("Making Request...");
            }

            try {
               byte[] bytes = new byte[4096];
               URL url = new URL(packUrl);
               httpURLConnection = (HttpURLConnection)url.openConnection(proxy);
               httpURLConnection.setRequestProperty("User-Agent", "Minecraft/1.8.9 CheatBreaker");
               float f = 0.0F;
               float size = map.entrySet().size();

               for (Entry entry : map.entrySet()) {
                  httpURLConnection.setRequestProperty((String)entry.getKey(), (String)entry.getValue());
                  if (progressUpdate != null) {
                     progressUpdate.setLoadingProgress((int)(++f / size * 100.0F));
                  }
               }

               inputStream = httpURLConnection.getInputStream();
               size = httpURLConnection.getContentLength();
               int contentLength = httpURLConnection.getContentLength();
               if (progressUpdate != null) {
                  progressUpdate.displayLoadingString(String.format("Downloading file (%.2f MB)...", size / 1000.0F / 1000.0F));
               }

               if (saveFile.exists()) {
                  long l = saveFile.length();
                  if (l == contentLength) {
                     flag = true;
                     return null;
                  }

                  HttpUtil.logger.warn("Deleting " + saveFile + " as it does not match what we currently have (" + contentLength + " vs our " + l + ").");
                  FileUtils.deleteQuietly(saveFile);
               } else if (saveFile.getParentFile() != null) {
                  saveFile.getParentFile().mkdirs();
               }

               dataOutputStream = new DataOutputStream(new FileOutputStream(saveFile));
               if (maxSize > 0 && size > maxSize) {
                  throw new IOException("Filesize is bigger than maximum allowed (file is " + contentLength + ", limit is " + maxSize + ")");
               }

               long l = 0L;
               int n = 0;

               while ((n = inputStream.read(bytes)) >= 0) {
                  l += n;
                  if (progressUpdate != null && contentLength > 0) {
                     progressUpdate.setLoadingProgress((int)(l * 100L / contentLength));
                  }

                  if (maxSize > 0 && l > maxSize) {
                     throw new IOException("Filesize was bigger than maximum allowed (got >= " + l + ", limit was " + maxSize + ")");
                  }

                  if (Thread.interrupted()) {
                     throw new InterruptedIOException("Resource pack download interrupted");
                  }

                  dataOutputStream.write(bytes, 0, n);
               }

               if (contentLength >= 0 && l != contentLength) {
                  throw new IOException("Incomplete resource pack download (expected " + contentLength + " bytes, received " + l + ")");
               }

               dataOutputStream.close();
               flag = true;
               return null;
            } catch (IOException e) {
               HttpUtil.logger.error("Couldn't download resource pack from " + packUrl, e);
               if (httpURLConnection != null) {
                  try (InputStream errorStream = httpURLConnection.getErrorStream()) {
                     if (errorStream != null) {
                        HttpUtil.logger.error(IOUtils.toString(errorStream, "UTF-8"));
                     }
                  } catch (IOException e2) {
                     e.addSuppressed(e2);
                  }
               }

               throw e;
            } finally {
               IOUtils.closeQuietly(inputStream);
               IOUtils.closeQuietly(dataOutputStream);
               if (dataOutputStream != null && !flag) {
                  FileUtils.deleteQuietly(saveFile);
               }

               if (httpURLConnection != null) {
                  httpURLConnection.disconnect();
               }

               if (progressUpdate != null) {
                  progressUpdate.setDoneWorking();
               }
            }
         }
      });
   }

   public static int getSuitableLanPort() throws IOException {
      ServerSocket serverSocket = null;
      int n = -1;

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
}

package net.optifine.http;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import net.optifine.Config;

public class HttpPipelineReceiver extends Thread {
   public HttpPipelineConnection httpPipelineConnection = null;
   public static Charset ASCII = Charset.forName("ASCII");
   public static final String HEADER_CONTENT_LENGTH = "Content-Length";
   public static final char CONSTANT_13 = '\r';
   public static final char CONSTANT_10 = '\n';

   public HttpPipelineReceiver(HttpPipelineConnection httpPipelineConnection) {
      super("HttpPipelineReceiver");
      this.httpPipelineConnection = httpPipelineConnection;
   }

   @Override
   public void run() {
      while (!Thread.interrupted()) {
         HttpPipelineRequest httpPipelineRequest = null;

         try {
            httpPipelineRequest = this.httpPipelineConnection.getNextRequestReceive();
            InputStream inputStream = this.httpPipelineConnection.getInputStream();
            HttpResponse readResponse = this.readResponse(inputStream);
            this.httpPipelineConnection.onResponseReceived(httpPipelineRequest, readResponse);
         } catch (InterruptedException e) {
            return;
         } catch (Exception e) {
            this.httpPipelineConnection.onExceptionReceive(httpPipelineRequest, e);
         }
      }
   }

   public HttpResponse readResponse(InputStream inputStream) throws IOException {
      String readLine = this.readLine(inputStream);
      String[] tokenize = Config.tokenize(readLine, " ");
      if (tokenize.length < 3) {
         throw new IOException("Invalid status line: " + readLine);
      }

      String str = tokenize[0];
      int parseInt = Config.parseInt(tokenize[1], 0);
      String str2 = tokenize[2];
      LinkedHashMap linkedHashMap = new LinkedHashMap();

      while (true) {
         String readLine2 = this.readLine(inputStream);
         if (readLine2.length() <= 0) {
            byte[] bytes = null;
            String str3 = (String)linkedHashMap.get("Content-Length");
            if (str3 != null) {
               int parseInt2 = Config.parseInt(str3, -1);
               if (parseInt2 > 0) {
                  bytes = new byte[parseInt2];
                  this.readFull(bytes, inputStream);
               }
            } else {
               String str4 = (String)linkedHashMap.get("Transfer-Encoding");
               if (Config.equals(str4, "chunked")) {
                  bytes = this.readContentChunked(inputStream);
               }
            }

            return new HttpResponse(parseInt, readLine, linkedHashMap, bytes);
         }

         int indexOf = readLine2.indexOf(":");
         if (indexOf > 0) {
            String trim = readLine2.substring(0, indexOf).trim();
            String trim2 = readLine2.substring(indexOf + 1).trim();
            linkedHashMap.put(trim, trim2);
         }
      }
   }

   public byte[] readContentChunked(InputStream inputStream) throws IOException {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

      int n;
      do {
         String readLine = this.readLine(inputStream);
         String[] tokenize = Config.tokenize(readLine, "; ");
         n = Integer.parseInt(tokenize[0], 16);
         byte[] bytes = new byte[n];
         this.readFull(bytes, inputStream);
         byteArrayOutputStream.write(bytes);
         this.readLine(inputStream);
      } while (n != 0);

      return byteArrayOutputStream.toByteArray();
   }

   public void readFull(byte[] bytes, InputStream inputStream) throws IOException {
      int n = 0;

      while (n < bytes.length) {
         int read = inputStream.read(bytes, n, bytes.length - n);
         if (read < 0) {
            throw new EOFException();
         }

         n += read;
      }
   }

   public String readLine(InputStream inputStream) throws IOException {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      int n = -1;
      boolean flag = false;

      while (true) {
         int read = inputStream.read();
         if (read < 0) {
            break;
         }

         byteArrayOutputStream.write(read);
         if (n == 13 && read == 10) {
            flag = true;
            break;
         }

         n = read;
      }

      byte[] toByteArray = byteArrayOutputStream.toByteArray();
      String str = new String(toByteArray, ASCII);
      if (flag) {
         str = str.substring(0, str.length() - 2);
      }

      return str;
   }
}

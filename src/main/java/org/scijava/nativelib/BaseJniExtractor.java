package org.scijava.nativelib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseJniExtractor implements JniExtractor {
   public static final String ALTR_TMPDIR = "./tmplib";
   public static Logger logger = LoggerFactory.getLogger("org.scijava.nativelib.BaseJniExtractor");
   public static final String LEFTOVER_MIN_AGE = "org.scijava.nativelib.leftoverMinAgeMs";
   public static final long LEFTOVER_MIN_AGE_DEFAULT = 300000L;
   public String[] nativeResourcePaths;
   public Class<?> libraryJarClass;
   public static final String JAVA_TMPDIR = "java.io.tmpdir";
   public static final String TMP_PREFIX = "nativelib-loader_";

   @Override
   public File extractJni(String str, String str2) throws IOException {
      String mapLibraryName = System.mapLibraryName(str2);
      debug("mappedLib is " + mapLibraryName);
      URL url = null;
      if (null == this.libraryJarClass) {
         this.libraryJarClass = this.getClass();
      }

      String str3 = (!str.equals("") && !str.endsWith("/") ? str + "/" : str) + mapLibraryName;
      url = this.libraryJarClass.getClassLoader().getResource(str3);
      if (null == url) {
         String str4;
         if (mapLibraryName.endsWith(".jnilib")) {
            str4 = mapLibraryName.substring(0, mapLibraryName.length() - 7) + ".dylib";
         } else if (mapLibraryName.endsWith(".dylib")) {
            str4 = mapLibraryName.substring(0, mapLibraryName.length() - 6) + ".jnilib";
         } else {
            str4 = null;
         }

         if (str4 != null) {
            url = this.getClass().getClassLoader().getResource(str + str4);
            if (url != null) {
               mapLibraryName = str4;
            }
         }
      }

      if (null != url) {
         debug("URL is " + url.toString());
         debug("URL path is " + url.getPath());
         return this.extractResource(this.getNativeDir(), url, mapLibraryName);
      } else {
         debug("Couldn't find resource " + str3);
         return null;
      }
   }

   public BaseJniExtractor() {
      this.init(null);
   }

   public void init(Class<?> clazz) {
      this.libraryJarClass = clazz;
      String mxSysInfo = MxSysInfo.getMxSysInfo();
      if (mxSysInfo != null) {
         this.nativeResourcePaths = new String[]{"natives/", "META-INF/lib/" + mxSysInfo + "/", "META-INF/lib/"};
      } else {
         this.nativeResourcePaths = new String[]{"natives/", "META-INF/lib/"};
      }

      this.deleteLeftoversFromPreviousSessions();
   }

   public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
      byte[] bytes = new byte[8192];
      int n = 0;

      while (true) {
         n = inputStream.read(bytes);
         if (n <= 0) {
            return;
         }

         outputStream.write(bytes, 0, n);
      }
   }

   @Override
   public void extractRegistered() throws IOException {
      debug("Extracting libraries registered in classloader " + this.getClass().getClassLoader());

      for (String str : this.nativeResourcePaths) {
         Enumeration resources = this.getClass().getClassLoader().getResources(str + "AUTOEXTRACT.LIST");

         while (resources.hasMoreElements()) {
            URL nextElement = (URL)resources.nextElement();
            this.extractLibrariesListedIn(nextElement);
         }
      }
   }

   public void deleteLeftoversFromPreviousSessions() {
      File file = new File(System.getProperty("java.io.tmpdir", "./tmplib"));
      File[] listFiles = file.listFiles(new FilenameFilter() {
         @Override
         public boolean accept(File file2, String str) {
            return str.startsWith("nativelib-loader_");
         }
      });
      if (listFiles != null) {
         long leftoverMinAge = this.getLeftoverMinAge();

         for (File file2 : listFiles) {
            long l = System.currentTimeMillis() - file2.lastModified();
            if (l < leftoverMinAge) {
               debug("Not deleting leftover folder " + file2 + ": is " + l + "ms old");
            } else {
               debug("Deleting leftover folder: " + file2);
               deleteRecursively(file2);
            }
         }
      }
   }

   public abstract File getJniDir();

   public static void debug(String str) {
      logger.debug(str);
   }

   public File extractResource(File file, URL url, String str) throws IOException, java.io.FileNotFoundException {
      InputStream inputStream = null;

      try {
         URLConnection openConnection = url.openConnection();
         openConnection.setUseCaches(false);
         inputStream = openConnection.getInputStream();
         File file2 = new File(this.getNativeDir(), str);
         debug("Extracting '" + url + "' to '" + file2.getAbsolutePath() + "'");
         FileOutputStream fileOutputStream = null;

         try {
            fileOutputStream = new FileOutputStream(file2);
            copy(inputStream, fileOutputStream);
         } finally {
            if (fileOutputStream != null) {
               fileOutputStream.close();
            }
         }

         file2.deleteOnExit();
         return file2;
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
      }
   }

   public static File createTempDir() throws IOException {
      File file = new File(System.getProperty("java.io.tmpdir", "./tmplib"));
      if (!file.isDirectory()) {
         file.mkdirs();
         if (!file.isDirectory()) {
            throw new IOException("Unable to create temporary directory " + file);
         }
      }

      File createTempFile = File.createTempFile("nativelib-loader_", "");
      createTempFile.delete();
      return createTempFile;
   }

   public static void error(String str, Throwable throwable) {
      logger.error(str, throwable);
   }

   public long getLeftoverMinAge() {
      try {
         return Long.parseLong(System.getProperty("org.scijava.nativelib.leftoverMinAgeMs", String.valueOf(300000L)));
      } catch (NumberFormatException e) {
         error("Cannot load leftover minimal age system property", e);
         return 300000L;
      }
   }

   public BaseJniExtractor(Class<?> clazz) {
      this.init(clazz);
   }

   public void extractLibrariesListedIn(URL url) throws IOException, java.io.UnsupportedEncodingException {
      debug("Extracting libraries listed in " + url);
      BufferedReader bufferedReader = null;

      try {
         URLConnection openConnection = url.openConnection();
         openConnection.setUseCaches(false);
         bufferedReader = new BufferedReader(new InputStreamReader(openConnection.getInputStream(), "UTF-8"));

         String str;
         while ((str = bufferedReader.readLine()) != null) {
            URL url2 = null;
            String[] nativeResourcePaths = this.nativeResourcePaths;
            int length = nativeResourcePaths.length;
            int n = 0;

            while (true) {
               if (n < length) {
                  String str2 = nativeResourcePaths[n];
                  url2 = this.getClass().getClassLoader().getResource(str2 + str);
                  if (url2 == null) {
                     n++;
                     continue;
                  }
               }

               if (url2 == null) {
                  throw new IOException("Couldn't find native library " + str + "on the classpath");
               }

               this.extractResource(this.getJniDir(), url2, str);
               break;
            }
         }
      } finally {
         if (bufferedReader != null) {
            bufferedReader.close();
         }
      }
   }

   public abstract File getNativeDir();

   public static boolean deleteRecursively(File file) {
      if (file == null) {
         return true;
      }

      File[] listFiles = file.listFiles();
      if (listFiles == null) {
         return true;
      }

      for (File file2 : listFiles) {
         if (file2.isFile()) {
            if (!file2.delete()) {
               return false;
            }
         } else if (file2.isDirectory() && !deleteRecursively(file2)) {
            return false;
         }
      }

      return file.delete();
   }
}

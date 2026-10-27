package org.scijava.nativelib;

import java.io.IOException;

public class NativeLoader {
   public static JniExtractor classloader = null;

   public static JniExtractor getClassloader() {
      return classloader;
   }

   public static void handleJniExtractor(JniExtractor jniExtractor) {
      classloader = jniExtractor;
   }

   static {
      try {
         if (NativeLoader.class.getClassLoader() == ClassLoader.getSystemClassLoader()) {
            classloader = new DefaultJniExtractor(null);
         } else {
            classloader = new WebappJniExtractor("Classloader");
         }
      } catch (IOException e) {
         throw new ExceptionInInitializerError(e);
      }
   }

   public static void extractRegistered() throws IOException {
      classloader.extractRegistered();
   }

   public static void handleString(String str, String... strs) throws IOException {
      try {
         System.loadLibrary(str);
      } catch (UnsatisfiedLinkError e) {
         if (!NativeLibraryUtil.loadNativeLibrary(classloader, str, strs)) {
            throw new IOException("Couldn't load library library " + str, e);
         }
      }
   }
}

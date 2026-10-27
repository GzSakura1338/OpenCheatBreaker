package org.scijava.nativelib;

import java.io.File;
import java.io.IOException;

public class WebappJniExtractor extends BaseJniExtractor {
   public File file = createTempDir();
   public File file2;

   @Override
   public File getNativeDir() {
      return this.file2;
   }

   @Override
   public File getJniDir() {
      return this.file;
   }

   public WebappJniExtractor(String str) throws IOException {
      this.file.mkdirs();
      if (!this.file.isDirectory()) {
         throw new IOException("Unable to create native library working directory " + this.file);
      }

      long currentTimeMillis = System.currentTimeMillis();
      int n = 0;

      while (true) {
         File file = new File(this.file, str + "." + currentTimeMillis + "." + n);
         if (file.mkdir()) {
            this.file2 = file;
            this.file2.deleteOnExit();
            return;
         }

         if (!file.exists()) {
            throw new IOException("Unable to create native library working directory " + file);
         }

         n++;
      }
   }

   @Override
   public void finalize() throws Throwable {
      super.finalize();
      File[] listFiles = this.file2.listFiles();

      for (File file : listFiles) {
         file.delete();
      }

      this.file2.delete();
   }
}

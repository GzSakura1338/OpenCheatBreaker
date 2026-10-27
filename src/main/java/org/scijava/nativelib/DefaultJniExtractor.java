package org.scijava.nativelib;

import java.io.File;
import java.io.IOException;

public class DefaultJniExtractor extends BaseJniExtractor {
   public File file = createTempDir();

   @Override
   public File getNativeDir() {
      return this.file;
   }

   public DefaultJniExtractor(Class<?> clazz) throws IOException {
      super(clazz);
      this.file.mkdirs();
      if (!this.file.isDirectory()) {
         throw new IOException("Unable to create native library working directory " + this.file);
      }

      this.file.deleteOnExit();
   }

   @Override
   public File getJniDir() {
      return this.file;
   }
}

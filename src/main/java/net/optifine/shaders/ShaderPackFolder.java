package net.optifine.shaders;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import net.optifine.util.StrUtils;

public class ShaderPackFolder implements IShaderPack {
   public File packFile;

   public ShaderPackFolder(String str, File file) {
      this.packFile = file;
   }

   @Override
   public void close() {
   }

   @Override
   public InputStream getResourceAsStream(String str) {
      try {
         String removePrefixSuffix = StrUtils.removePrefixSuffix(str, "/", "/");
         File file = new File(this.packFile, removePrefixSuffix);
         return !file.exists() ? null : new BufferedInputStream(new FileInputStream(file));
      } catch (Exception e) {
         return null;
      }
   }

   @Override
   public boolean hasDirectory(String str) {
      File file = new File(this.packFile, str.substring(1));
      return !file.exists() ? false : file.isDirectory();
   }

   @Override
   public String getName() {
      return this.packFile.getName();
   }
}

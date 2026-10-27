package net.optifine.shaders;

import com.google.common.base.Joiner;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.optifine.Config;
import net.optifine.util.StrUtils;

public class ShaderPackZip implements IShaderPack {
   public File packFile;
   public ZipFile packZipFile;
   public String baseFolder;

   public ShaderPackZip(String str, File file) {
      this.packFile = file;
      this.packZipFile = null;
      this.baseFolder = "";
   }

   @Override
   public void close() {
      if (this.packZipFile != null) {
         try {
            this.packZipFile.close();
         } catch (Exception e) {
         }

         this.packZipFile = null;
      }
   }

   @Override
   public InputStream getResourceAsStream(String str) {
      try {
         if (this.packZipFile == null) {
            this.packZipFile = new ZipFile(this.packFile);
            this.baseFolder = this.detectBaseFolder(this.packZipFile);
         }

         String removePrefix = StrUtils.removePrefix(str, "/");
         if (removePrefix.contains("..")) {
            removePrefix = this.resolveRelative(removePrefix);
         }

         ZipEntry entry = this.packZipFile.getEntry(this.baseFolder + removePrefix);
         return entry == null ? null : this.packZipFile.getInputStream(entry);
      } catch (Exception e) {
         return null;
      }
   }

   public String resolveRelative(String str) {
      ArrayDeque arrayDeque = new ArrayDeque();
      String[] tokenize = Config.tokenize(str, "/");

      for (int i = 0; i < tokenize.length; i++) {
         String str2 = tokenize[i];
         if (str2.equals("..")) {
            if (arrayDeque.isEmpty()) {
               return "";
            }

            arrayDeque.removeLast();
         } else {
            arrayDeque.add(str2);
         }
      }

      return Joiner.on('/').join(arrayDeque);
   }

   public String detectBaseFolder(ZipFile zipFile) {
      ZipEntry entry = zipFile.getEntry("shaders/");
      if (entry != null && entry.isDirectory()) {
         return "";
      }

      Pattern compile = Pattern.compile("([^/]+/)shaders/");
      Enumeration entries = zipFile.entries();

      while (entries.hasMoreElements()) {
         ZipEntry nextElement = (ZipEntry)entries.nextElement();
         String name = nextElement.getName();
         Matcher matcher = compile.matcher(name);
         if (matcher.matches()) {
            String group = matcher.group(1);
            if (group != null) {
               if (group.equals("shaders/")) {
                  return "";
               }

               return group;
            }
         }
      }

      return "";
   }

   @Override
   public boolean hasDirectory(String str) {
      try {
         if (this.packZipFile == null) {
            this.packZipFile = new ZipFile(this.packFile);
            this.baseFolder = this.detectBaseFolder(this.packZipFile);
         }

         String removePrefix = StrUtils.removePrefix(str, "/");
         ZipEntry entry = this.packZipFile.getEntry(this.baseFolder + removePrefix);
         return entry != null;
      } catch (IOException e) {
         return false;
      }
   }

   @Override
   public String getName() {
      return this.packFile.getName();
   }
}

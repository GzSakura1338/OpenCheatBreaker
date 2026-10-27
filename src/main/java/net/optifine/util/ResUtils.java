package net.optifine.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;

public class ResUtils {
   public static String[] collectFiles(String str, String str2) {
      return collectFiles(new String[]{str}, new String[]{str2});
   }

   public static String[] collectFiles(String[] strs, String[] strs2) {
      LinkedHashSet linkedHashSet = new LinkedHashSet();
      IResourcePack[] resourcePacks = Config.getResourcePacks();

      for (int i = 0; i < resourcePacks.length; i++) {
         IResourcePack resourcePack = resourcePacks[i];
         String[] collectFiles2 = collectFiles(resourcePack, strs, strs2, (String[])null);
         linkedHashSet.addAll(Arrays.asList(collectFiles2));
      }

      return (String[])linkedHashSet.toArray(new String[linkedHashSet.size()]);
   }

   public static String[] collectFiles(IResourcePack resourcePack, String str, String str2, String[] strs) {
      return collectFiles(resourcePack, new String[]{str}, new String[]{str2}, strs);
   }

   public static String[] collectFiles(IResourcePack resourcePack, String[] strs, String[] strs2) {
      return collectFiles(resourcePack, strs, strs2, (String[])null);
   }

   public static String[] collectFiles(IResourcePack resourcePack, String[] strs, String[] strs2, String[] strs3) {
      if (resourcePack instanceof DefaultResourcePack) {
         return collectFilesFixed(resourcePack, strs3);
      }

      if (!(resourcePack instanceof AbstractResourcePack)) {
         Config.warn("Unknown resource pack type: " + resourcePack);
         return new String[0];
      }

      AbstractResourcePack abstractResourcePack = (AbstractResourcePack)resourcePack;
      File resourcePackFile = abstractResourcePack.resourcePackFile;
      if (resourcePackFile == null) {
         return new String[0];
      }

      if (resourcePackFile.isDirectory()) {
         return collectFilesFolder(resourcePackFile, "", strs, strs2);
      }

      if (resourcePackFile.isFile()) {
         return collectFilesZIP(resourcePackFile, strs, strs2);
      }

      Config.warn("Unknown resource pack file: " + resourcePackFile);
      return new String[0];
   }

   public static String[] collectFilesFixed(IResourcePack resourcePack, String[] strs) {
      if (strs == null) {
         return new String[0];
      }

      ArrayList list = new ArrayList();

      for (int i = 0; i < strs.length; i++) {
         String str = strs[i];
         ResourceLocation resourceLocation = new ResourceLocation(str);
         if (resourcePack.resourceExists(resourceLocation)) {
            list.add(str);
         }
      }

      return (String[])list.toArray(new String[list.size()]);
   }

   public static String[] collectFilesFolder(File file, String str, String[] strs, String[] strs2) {
      ArrayList list = new ArrayList();
      String str2 = "assets/minecraft/";
      File[] listFiles = file.listFiles();
      if (listFiles == null) {
         return new String[0];
      }

      for (int i = 0; i < listFiles.length; i++) {
         File file2 = listFiles[i];
         if (file2.isFile()) {
            String str3 = str + file2.getName();
            if (str3.startsWith(str2)) {
               str3 = str3.substring(str2.length());
               if (StrUtils.startsWith(str3, strs) && StrUtils.endsWith(str3, strs2)) {
                  list.add(str3);
               }
            }
         } else if (file2.isDirectory()) {
            String str3 = str + file2.getName() + "/";
            String[] collectFilesFolder2 = collectFilesFolder(file2, str3, strs, strs2);

            for (int j = 0; j < collectFilesFolder2.length; j++) {
               String str4 = collectFilesFolder2[j];
               list.add(str4);
            }
         }
      }

      return (String[])list.toArray(new String[list.size()]);
   }

   public static String[] collectFilesZIP(File file, String[] strs, String[] strs2) {
      ArrayList list = new ArrayList();
      String str = "assets/minecraft/";

      try {
         ZipFile zipFile = new ZipFile(file);
         Enumeration entries = zipFile.entries();

         while (entries.hasMoreElements()) {
            ZipEntry nextElement = (ZipEntry)entries.nextElement();
            String name = nextElement.getName();
            if (name.startsWith(str)) {
               name = name.substring(str.length());
               if (StrUtils.startsWith(name, strs) && StrUtils.endsWith(name, strs2)) {
                  list.add(name);
               }
            }
         }

         zipFile.close();
         return (String[])list.toArray(new String[list.size()]);
      } catch (IOException e) {
         e.printStackTrace();
         return new String[0];
      }
   }

   public static boolean isLowercase(String str) {
      return str.equals(str.toLowerCase(Locale.ROOT));
   }

   public static Properties readProperties(String str, String str2) {
      ResourceLocation resourceLocation = new ResourceLocation(str);

      try {
         InputStream resourceStream = Config.getResourceStream(resourceLocation);
         if (resourceStream == null) {
            return null;
         }

         PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
         propertiesOrdered.load(resourceStream);
         resourceStream.close();
         Config.dbg("" + str2 + ": Loading " + str);
         return propertiesOrdered;
      } catch (FileNotFoundException e) {
         return null;
      } catch (IOException e) {
         Config.warn("" + str2 + ": Error reading " + str);
         return null;
      }
   }

   public static Properties readProperties(InputStream inputStream, String str) {
      if (inputStream == null) {
         return null;
      }

      try {
         PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
         propertiesOrdered.load(inputStream);
         inputStream.close();
         return propertiesOrdered;
      } catch (FileNotFoundException e) {
         return null;
      } catch (IOException e) {
         return null;
      }
   }
}

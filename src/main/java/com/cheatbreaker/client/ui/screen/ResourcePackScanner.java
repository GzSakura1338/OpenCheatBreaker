package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.util.Logging;
import com.cheatbreaker.client.util.SneakyThrow;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.ResourcePackRepository.Entry;

public class ResourcePackScanner {
   public static Minecraft mc = Minecraft.getMinecraft();

   public static List<Entry> createArrayList(File file, String str) {
      ArrayList list = new ArrayList();

      for (File file2 : Objects.requireNonNull(file.listFiles())) {
         if (checkFile2(file2)) {
            if (file2.getName().equals(str)) {
               findOptional(file2).ifPresent(list::add);
            }
         } else if (checkFile(file2)) {
            list.addAll(createArrayList(file2, str));
         }
      }

      return list;
   }

   public static boolean checkFile(File file) {
      return file.isDirectory() && !new File(file, "pack.mcmeta").isFile();
   }

   public static Optional<Entry> findOptional(File file) {
      Optional empty = Optional.empty();

      try {
         Constructor declaredConstructor = Entry.class.getDeclaredConstructor(ResourcePackRepository.class, File.class);
         declaredConstructor.setAccessible(true);
         empty = Optional.of(declaredConstructor.newInstance(mc.getResourcePackRepository(), file));
         ((Entry)empty.get()).updateResourcePack();
      } catch (NoSuchMethodException e) {
         CheatBreaker.getInstance().getLogger().error("Failed to find constructor", e);
         empty = Optional.empty();
      } catch (IllegalAccessException | InstantiationException | InvocationTargetException | IOException e) {
         CheatBreaker.getInstance().getLogger().error("Failed to create entry", e);
      }

      return empty;
   }

   public static List<Entry> createArrayList() {
      ArrayList list = new ArrayList();
      ResourcePackRepository resourcePackRepository = mc.getResourcePackRepository();

      for (String str : mc.gameSettings.resourcePacks) {
         for (Entry entry : resourcePackRepository.getRepositoryEntriesAll()) {
            if (!entry.getResourcePackName().equals(str)) {
            }
         }

         for (File file : Objects.requireNonNull(resourcePackRepository.getDirResourcepacks().listFiles(ResourcePackScanner::checkFile))) {
            list.addAll(createArrayList(file, str));
         }
      }

      return list;
   }

   public static boolean checkFile2(File file) {
      try {
         if (file.isFile() && file.getName().endsWith(".zip")) {
            try {
               ZipFile zipFile = new ZipFile(file);
               ZipEntry entry = zipFile.getEntry("pack.mcmeta");
               if (entry == null) {
                  Logging.debug("Not found in: " + file.getName());
                  return false;
               } else {
                  return true;
               }
            } catch (Exception e) {
               return false;
            }
         } else {
            return file.isDirectory() && new File(file, "pack.mcmeta").isFile();
         }
      } catch (Throwable e) {
         throw SneakyThrow.sneakyThrow(e);
      }
   }
}

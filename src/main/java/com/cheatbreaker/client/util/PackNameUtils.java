package com.cheatbreaker.client.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.ResourcePackRepository.Entry;

public class PackNameUtils {
   public static Minecraft mc = Minecraft.getMinecraft();
   public static DefaultResourcePack defaultResourcePack = null;

   public static String joinInts(int[] ints) {
      return joinIntsWithSeparator(ints, ", ");
   }

   public static String joinFloats(float[] floats, String str) {
      if (floats == null) {
         return "";
      }

      StringBuffer sb = new StringBuffer(floats.length * 5);

      for (int i = 0; i < floats.length; i++) {
         float f = floats[i];
         if (i > 0) {
            sb.append(str);
         }

         sb.append(f);
      }

      return sb.toString();
   }

   public static String getActivePackNames() {
      if (mc.getResourcePackRepository() == null) {
         return "";
      }

      IResourcePack[] activeResourcePacks = getActiveResourcePacks();
      if (activeResourcePacks.length <= 0) {
         return getDefaultResourcePack().getPackName();
      }

      String[] strs = new String[activeResourcePacks.length];

      for (int i = 0; i < activeResourcePacks.length; i++) {
         strs[i] = activeResourcePacks[i].getPackName();
      }

      return joinStrings(strs, ", ");
   }

   public static String joinFloatsDefault(float[] floats) {
      return joinFloats(floats, ", ");
   }

   public static IResourcePack getTopResourcePack() {
      IResourcePack[] activeResourcePacks = getActiveResourcePacks();
      if (activeResourcePacks.length <= 0) {
         return getDefaultResourcePack();
      }

      IResourcePack[] resourcePacks = new IResourcePack[activeResourcePacks.length];
      System.arraycopy(activeResourcePacks, 0, resourcePacks, 0, activeResourcePacks.length);
      return resourcePacks[resourcePacks.length - 1];
   }

   public static IResourcePack[] getActiveResourcePacks() {
      ResourcePackRepository resourcePackRepository = mc.getResourcePackRepository();
      List repositoryEntries = resourcePackRepository.getRepositoryEntries();
      ArrayList list = new ArrayList();

      for (Entry entry : (Iterable<Entry>)repositoryEntries) {
         list.add(entry.getResourcePack());
      }

      if (resourcePackRepository.getResourcePackInstance() != null) {
         list.add(resourcePackRepository.getResourcePackInstance());
      }

      return (IResourcePack[])list.toArray(new IResourcePack[list.size()]);
   }

   public static DefaultResourcePack getDefaultResourcePack() {
      if (defaultResourcePack == null) {
         defaultResourcePack = mc.getMcDefaultResourcePack();
         if (defaultResourcePack == null) {
            ResourcePackRepository resourcePackRepository = mc.getResourcePackRepository();
            if (resourcePackRepository != null) {
               defaultResourcePack = (DefaultResourcePack)resourcePackRepository.rprDefaultResourcePack;
            }
         }
      }

      return defaultResourcePack;
   }

   public static IResourcePack getResourcePack(int n) {
      IResourcePack[] activeResourcePacks = getActiveResourcePacks();
      if (activeResourcePacks.length <= 0) {
         return getDefaultResourcePack();
      }

      IResourcePack[] resourcePacks = new IResourcePack[n + 1];
      System.arraycopy(activeResourcePacks, 0, resourcePacks, 0, n + 1);
      return resourcePacks[n];
   }

   public static String joinStrings(String[] strs, String str) {
      if (strs == null) {
         return "";
      }

      StringBuffer sb = new StringBuffer(strs.length * 5);

      for (int i = 0; i < strs.length; i++) {
         String str2 = strs[i];
         if (i > 0) {
            sb.append(str);
         }

         sb.append(str2);
      }

      return sb.toString();
   }

   public static String joinIntsWithSeparator(int[] ints, String str) {
      if (ints == null) {
         return "";
      }

      StringBuffer sb = new StringBuffer(ints.length * 5);

      for (int i = 0; i < ints.length; i++) {
         int n = ints[i];
         if (i > 0) {
            sb.append(str);
         }

         sb.append(n);
      }

      return sb.toString();
   }
}

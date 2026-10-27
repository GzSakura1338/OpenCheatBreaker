package com.cheatbreaker.client.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.SimpleResource;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;

public class ResourceLocationUtils {
   public static ResourceLocation createResourceLocation(String str, String str2) {
      if (str.equals("blockbuster.actors")) {
         str = "b.a";
      }

      return new ResourceLocation(str, str2);
   }

   public static String formatText(ResourceLocation resourceLocation) {
      if (resourceLocation == null) {
         return "";
      } else if (resourceLocation.getResourceDomain().equals("b.a")) {
         String[] split = resourceLocation.getResourcePath().split("/");
         return split[split.length - 1];
      } else {
         return resourceLocation.toString();
      }
   }

   public static NBTBase findNBTBase(ResourceLocation resourceLocation) {
      if (!(resourceLocation instanceof CustomResourceLocation)) {
         return resourceLocation != null ? new NBTTagString(resourceLocation.toString()) : null;
      }

      CustomResourceLocation customResourceLocation = (CustomResourceLocation)resourceLocation;
      NBTTagList nbttagList = new NBTTagList();
      Iterator iterator = customResourceLocation.resourceLocationList.iterator();

      while (iterator.hasNext()) {
         nbttagList.appendTag(new NBTTagString(((ResourceLocation)iterator.next()).toString()));
      }

      return nbttagList;
   }

   public static ResourceLocation findResourceLocation(String str, String str2) {
      if (str.isEmpty()) {
         return null;
      } else {
         return str.indexOf(":") == -1 ? createResourceLocation("b.a", (str.indexOf("/") == -1 ? str2 + "/" : "") + str) : findResourceLocation(str);
      }
   }

   public static IResource findIResource(CustomResourceLocation customResourceLocation) throws IOException {
      if (customResourceLocation.resourceLocationList.isEmpty()) {
         throw new IOException("Multi-skin is empty!");
      }

      try {
         IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
         BufferedImage read = ImageIO.read(resourceManager.getResource(customResourceLocation.resourceLocationList.get(0)).getInputStream());
         Graphics graphics = read.getGraphics();

         for (int i = 1; i < customResourceLocation.resourceLocationList.size(); i++) {
            ResourceLocation resourceLocation = customResourceLocation.resourceLocationList.get(i);

            try {
               graphics.drawImage(ImageIO.read(resourceManager.getResource(resourceLocation).getInputStream()), 0, 0, null);
            } catch (Exception e) {
            }
         }

         graphics.dispose();
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         ImageIO.write(read, "png", byteArrayOutputStream);
         return new SimpleResource("McLib multiskin handler", customResourceLocation, new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), null, null);
      } catch (IOException e) {
         throw SneakyThrow.sneakyThrow(e);
      } catch (Exception e) {
         throw new IOException(e.getMessage());
      }
   }

   public static ResourceLocation createCustomResourceLocation(ResourceLocation resourceLocation) {
      if (resourceLocation instanceof CustomResourceLocation) {
         CustomResourceLocation customResourceLocation = (CustomResourceLocation)resourceLocation;
         CustomResourceLocation customResourceLocation2 = new CustomResourceLocation(customResourceLocation.toString());
         customResourceLocation2.resourceLocationList.clear();
         customResourceLocation2.resourceLocationList.addAll(customResourceLocation.resourceLocationList);
         return customResourceLocation2;
      } else {
         return resourceLocation != null ? findResourceLocation(resourceLocation.toString()) : null;
      }
   }

   public static ResourceLocation findResourceLocation(String str) {
      if (str.startsWith("blockbuster.actors:")) {
         str = "b.a" + str.substring(18);
      }

      return new ResourceLocation(str);
   }

   public static ResourceLocation createCustomResourceLocation(JsonElement jsonElement) {
      if (jsonElement.isJsonArray()) {
         JsonArray asJsonArray = jsonElement.getAsJsonArray();
         int size = asJsonArray.size();
         if (size > 0) {
            JsonElement jsonElement2 = asJsonArray.get(0);
            if (jsonElement2.isJsonPrimitive()) {
               CustomResourceLocation customResourceLocation = new CustomResourceLocation(jsonElement2.getAsString());

               for (int i = 1; i < size; i++) {
                  customResourceLocation.resourceLocationList.add(createCustomResourceLocation(asJsonArray.get(i)));
               }

               return customResourceLocation;
            }
         }
      } else if (jsonElement.isJsonPrimitive()) {
         return findResourceLocation(jsonElement.getAsString());
      }

      return null;
   }

   public static ResourceLocation createCustomResourceLocation(NBTBase nbtbase) {
      if (nbtbase instanceof NBTTagList) {
         NBTTagList nbttagList = (NBTTagList)nbtbase;
         if (!nbttagList.hasNoTags()) {
            CustomResourceLocation customResourceLocation = new CustomResourceLocation(nbttagList.getStringTagAt(0));

            for (int i = 1; i < nbttagList.tagCount(); i++) {
               customResourceLocation.resourceLocationList.add(findResourceLocation(nbttagList.getStringTagAt(i)));
            }

            return customResourceLocation;
         }
      } else if (nbtbase instanceof NBTTagString) {
         return findResourceLocation(((NBTTagString)nbtbase).getString());
      }

      return null;
   }

   public static JsonElement findJsonElement(ResourceLocation resourceLocation) {
      if (!(resourceLocation instanceof CustomResourceLocation)) {
         return (JsonElement)(resourceLocation != null ? new JsonPrimitive(resourceLocation.toString()) : JsonNull.INSTANCE);
      }

      CustomResourceLocation customResourceLocation = (CustomResourceLocation)resourceLocation;
      JsonArray jsonArray = new JsonArray();
      Iterator iterator = customResourceLocation.resourceLocationList.iterator();

      while (iterator.hasNext()) {
         jsonArray.add(new JsonPrimitive(((ResourceLocation)iterator.next()).toString()));
      }

      return jsonArray;
   }
}

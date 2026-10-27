package net.optifine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.optifine.util.PropertiesOrdered;

public class EmissiveTextures {
   public static String suffixEmissive = null;
   public static String suffixEmissivePng = null;
   public static boolean active = false;
   public static boolean render = false;
   public static boolean hasEmissive = false;
   public static boolean renderEmissive = false;
   public static float lightMapX;
   public static float lightMapY;
   public static final String SUFFIX_PNG = ".png";
   public static ResourceLocation LOCATION_EMPTY = new ResourceLocation("mcpatcher/ctm/default/empty.png");

   public static boolean isActive() {
      return active;
   }

   public static String getSuffixEmissive() {
      return suffixEmissive;
   }

   public static void beginRender() {
      render = true;
      hasEmissive = false;
   }

   public static ITextureObject getEmissiveTexture(ITextureObject textureObject, Map<ResourceLocation, ITextureObject> map) {
      if (!render) {
         return textureObject;
      }

      if (!(textureObject instanceof SimpleTexture)) {
         return textureObject;
      }

      SimpleTexture simpleTexture = (SimpleTexture)textureObject;
      ResourceLocation locationEmissive = simpleTexture.locationEmissive;
      if (!renderEmissive) {
         if (locationEmissive != null) {
            hasEmissive = true;
         }

         return textureObject;
      } else {
         if (locationEmissive == null) {
            locationEmissive = LOCATION_EMPTY;
         }

         ITextureObject textureObject2 = (ITextureObject)map.get(locationEmissive);
         if (textureObject2 == null) {
            textureObject2 = new SimpleTexture(locationEmissive);
            TextureManager textureManager = Config.getTextureManager();
            textureManager.loadTexture(locationEmissive, textureObject2);
         }

         return textureObject2;
      }
   }

   public static boolean hasEmissive() {
      return hasEmissive;
   }

   public static void beginRenderEmissive() {
      lightMapX = OpenGlHelper.lastBrightnessX;
      lightMapY = OpenGlHelper.lastBrightnessY;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, lightMapY);
      renderEmissive = true;
   }

   public static void endRenderEmissive() {
      renderEmissive = false;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightMapX, lightMapY);
   }

   public static void endRender() {
      render = false;
      hasEmissive = false;
   }

   public static void update() {
      active = false;
      suffixEmissive = null;
      suffixEmissivePng = null;
      if (Config.isEmissiveTextures()) {
         try {
            String str = "optifine/emissive.properties";
            ResourceLocation resourceLocation = new ResourceLocation(str);
            InputStream resourceStream = Config.getResourceStream(resourceLocation);
            if (resourceStream == null) {
               return;
            }

            dbg("Loading " + str);
            PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
            propertiesOrdered.load(resourceStream);
            resourceStream.close();
            suffixEmissive = propertiesOrdered.getProperty("suffix.emissive");
            if (suffixEmissive != null) {
               suffixEmissivePng = suffixEmissive + ".png";
            }

            active = suffixEmissive != null;
         } catch (FileNotFoundException e) {
            return;
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   public static void dbg(String str) {
      Config.dbg("EmissiveTextures: " + str);
   }

   public static void warn(String str) {
      Config.warn("EmissiveTextures: " + str);
   }

   public static boolean isEmissive(ResourceLocation resourceLocation) {
      return suffixEmissivePng == null ? false : resourceLocation.getResourcePath().endsWith(suffixEmissivePng);
   }

   public static void loadTexture(ResourceLocation resourceLocation, SimpleTexture simpleTexture) {
      if (resourceLocation != null && simpleTexture != null) {
         simpleTexture.isEmissive = false;
         simpleTexture.locationEmissive = null;
         if (suffixEmissivePng != null) {
            String resourcePath = resourceLocation.getResourcePath();
            if (resourcePath.endsWith(".png")) {
               if (resourcePath.endsWith(suffixEmissivePng)) {
                  simpleTexture.isEmissive = true;
               } else {
                  String str = resourcePath.substring(0, resourcePath.length() - ".png".length()) + suffixEmissivePng;
                  ResourceLocation resourceLocation2 = new ResourceLocation(resourceLocation.getResourceDomain(), str);
                  if (Config.hasResource(resourceLocation2)) {
                     simpleTexture.locationEmissive = resourceLocation2;
                  }
               }
            }
         }
      }
   }
}

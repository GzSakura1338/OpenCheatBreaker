package net.minecraft.client.resources;

import com.google.common.collect.ImmutableSet;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import net.optifine.reflect.ReflectorForge;

public class DefaultResourcePack implements IResourcePack {
   public static Set<String> defaultResourceDomains = ImmutableSet.of("minecraft", "realms", "emoticons");
   public Map<String, File> mapAssets;

   public DefaultResourcePack(Map<String, File> mapAssetsIn) {
      this.mapAssets = mapAssetsIn;
   }

   @Override
   public InputStream getInputStream(ResourceLocation location) throws IOException {
      InputStream resourceStream = this.getResourceStream(location);
      if (resourceStream != null) {
         return resourceStream;
      } else {
         InputStream inputStreamAssets = this.getInputStreamAssets(location);
         if (inputStreamAssets != null) {
            return inputStreamAssets;
         } else {
            throw new FileNotFoundException(location.getResourcePath());
         }
      }
   }

   public InputStream getInputStreamAssets(ResourceLocation location) throws FileNotFoundException, IOException {
      File file = this.mapAssets.get(location.toString());
      return file != null && file.isFile() ? new FileInputStream(file) : null;
   }

   public InputStream getResourceStream(ResourceLocation location) {
      String resourceDomain = location.getResourceDomain();
      String resourcePath = location.getResourcePath();
      if ("emoticons".equals(resourceDomain)) {
         resourceDomain = "minecraft";
         resourcePath = "emoticons/" + resourcePath;
      }

      String str = "/assets/" + resourceDomain + "/" + resourcePath;
      InputStream optiFineResourceStream = ReflectorForge.getOptiFineResourceStream(str);
      return optiFineResourceStream != null ? optiFineResourceStream : DefaultResourcePack.class.getResourceAsStream(str);
   }

   @Override
   public boolean resourceExists(ResourceLocation location) {
      return this.getResourceStream(location) != null || this.mapAssets.containsKey(location.toString());
   }

   @Override
   public Set<String> getResourceDomains() {
      return defaultResourceDomains;
   }

   @Override
   public <T extends IMetadataSection> T getPackMetadata(IMetadataSerializer metadataSerializer, String metadataSectionName) throws IOException {
      try {
         FileInputStream fileInputStream = new FileInputStream(this.mapAssets.get("pack.mcmeta"));
         return AbstractResourcePack.readMetadata(metadataSerializer, fileInputStream, metadataSectionName);
      } catch (RuntimeException e) {
         return null;
      } catch (FileNotFoundException e) {
         return null;
      }
   }

   @Override
   public BufferedImage getPackImage() throws IOException {
      return TextureUtil.readBufferedImage(DefaultResourcePack.class.getResourceAsStream("/" + new ResourceLocation("pack.png").getResourcePath()));
   }

   @Override
   public String getPackName() {
      return "Default";
   }
}

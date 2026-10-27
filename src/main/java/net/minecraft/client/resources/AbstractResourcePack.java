package net.minecraft.client.resources;

import com.cheatbreaker.client.util.ClientResourceFilter;
import com.google.common.base.Charsets;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractResourcePack implements IResourcePack {
   public static Logger resourceLog = LogManager.getLogger();
   public File resourcePackFile;
   public static final int CONSTANT_64 = 64;

   public AbstractResourcePack(File resourcePackFileIn) {
      this.resourcePackFile = resourcePackFileIn;
   }

   public static String locationToName(ResourceLocation location) {
      return String.format("%s/%s/%s", "assets", location.getResourceDomain(), location.getResourcePath());
   }

   public static String getRelativeName(File file, File file2) {
      return file.toURI().relativize(file2.toURI()).getPath();
   }

   @Override
   public InputStream getInputStream(ResourceLocation location) throws IOException {
      if (ClientResourceFilter.isBundledClientResource(location)) {
         throw new FileNotFoundException(location.toString());
      } else {
         return this.getInputStreamByName(locationToName(location));
      }
   }

   @Override
   public boolean resourceExists(ResourceLocation location) {
      return !ClientResourceFilter.isBundledClientResource(location) && this.hasResourceName(locationToName(location));
   }

   public abstract InputStream getInputStreamByName(String str) throws IOException;

   public abstract boolean hasResourceName(String str);

   public void logNameNotLowercase(String name) {
      resourceLog.warn("ResourcePack: ignored non-lowercase namespace: {} in {}", new Object[]{name, this.resourcePackFile});
   }

   @Override
   public <T extends IMetadataSection> T getPackMetadata(IMetadataSerializer metadataSerializer, String metadataSectionName) throws IOException {
      return readMetadata(metadataSerializer, this.getInputStreamByName("pack.mcmeta"), metadataSectionName);
   }

   public static <T extends IMetadataSection> T readMetadata(IMetadataSerializer metadataSerializer, InputStream inputStream, String str) {
      JsonObject jsonObject = null;
      BufferedReader bufferedReader = null;

      try {
         bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charsets.UTF_8));
         String str2 = bufferedReader.lines().collect(Collectors.joining());
         str2 = StringEscapeUtils.unescapeJson(str2);
         jsonObject = JsonParser.parseString(str2).getAsJsonObject();
      } catch (RuntimeException e) {
         throw new JsonParseException(e);
      } finally {
         IOUtils.closeQuietly(bufferedReader);
      }

      return metadataSerializer.parseMetadataSection(str, jsonObject);
   }

   @Override
   public BufferedImage getPackImage() throws IOException {
      try {
         return this.findBufferedImage(TextureUtil.readBufferedImage(this.getInputStreamByName("pack.png")));
      } catch (IOException e) {
         return Config.getDefaultResourcePack().getPackImage();
      }
   }

   @Override
   public String getPackName() {
      return this.resourcePackFile.getName();
   }

   public BufferedImage findBufferedImage(BufferedImage bufferedImage) {
      if (bufferedImage == null) {
         return null;
      } else if (bufferedImage.getWidth() <= 64 && bufferedImage.getHeight() <= 64) {
         resourceLog.info("[Icon Scaler] Retaining pack icon scale at " + bufferedImage.getWidth());
         return bufferedImage;
      } else {
         resourceLog.info("[Icon Scaler] Scaling resource pack icon from " + bufferedImage.getWidth() + " to " + 64);
         BufferedImage bufferedImage2 = new BufferedImage(64, 64, 2);
         Graphics graphics = bufferedImage2.getGraphics();
         graphics.drawImage(bufferedImage, 0, 0, 64, 64, null);
         graphics.dispose();
         return bufferedImage2;
      }
   }
}

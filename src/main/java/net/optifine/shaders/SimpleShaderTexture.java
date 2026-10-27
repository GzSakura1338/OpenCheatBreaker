package net.optifine.shaders;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.AnimationMetadataSectionSerializer;
import net.minecraft.client.resources.data.FontMetadataSection;
import net.minecraft.client.resources.data.FontMetadataSectionSerializer;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.client.resources.data.LanguageMetadataSectionSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.resources.data.PackMetadataSectionSerializer;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSectionSerializer;
import org.apache.commons.io.IOUtils;

public class SimpleShaderTexture extends AbstractTexture {
   public String texturePath;
   public static IMetadataSerializer METADATA_SERIALIZER = makeMetadataSerializer();

   public SimpleShaderTexture(String str) {
      this.texturePath = str;
   }

   @Override
   public void loadTexture(IResourceManager resourceManager) throws FileNotFoundException, java.io.IOException {
      this.deleteGlTexture();
      InputStream shaderPackResourceStream = Shaders.getShaderPackResourceStream(this.texturePath);
      if (shaderPackResourceStream == null) {
         throw new FileNotFoundException("Shader texture not found: " + this.texturePath);
      }

      try {
         BufferedImage readBufferedImage = TextureUtil.readBufferedImage(shaderPackResourceStream);
         TextureMetadataSection loadTextureMetadataSection2 = loadTextureMetadataSection(this.texturePath, new TextureMetadataSection(false, false, new ArrayList<>()));
         TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), readBufferedImage, loadTextureMetadataSection2.getTextureBlur(), loadTextureMetadataSection2.getTextureClamp());
      } finally {
         IOUtils.closeQuietly(shaderPackResourceStream);
      }
   }

   public static TextureMetadataSection loadTextureMetadataSection(String str, TextureMetadataSection textureMetadataSection) {
      String str2 = str + ".mcmeta";
      String str3 = "texture";
      InputStream shaderPackResourceStream = Shaders.getShaderPackResourceStream(str2);
      if (shaderPackResourceStream != null) {
         IMetadataSerializer metadataSerializer = METADATA_SERIALIZER;
         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(shaderPackResourceStream));

         TextureMetadataSection textureMetadataSection2;
         try {
            JsonObject asJsonObject = new JsonParser().parse(bufferedReader).getAsJsonObject();
            TextureMetadataSection parseMetadataSection = metadataSerializer.parseMetadataSection(str3, asJsonObject);
            if (parseMetadataSection == null) {
               return textureMetadataSection;
            }

            textureMetadataSection2 = parseMetadataSection;
         } catch (RuntimeException e) {
            SMCLog.warning("Error reading metadata: " + str2);
            SMCLog.warning("" + e.getClass().getName() + ": " + e.getMessage());
            return textureMetadataSection;
         } finally {
            IOUtils.closeQuietly(bufferedReader);
            IOUtils.closeQuietly(shaderPackResourceStream);
         }

         return textureMetadataSection2;
      } else {
         return textureMetadataSection;
      }
   }

   public static IMetadataSerializer makeMetadataSerializer() {
      IMetadataSerializer metadataSerializer = new IMetadataSerializer();
      metadataSerializer.registerMetadataSectionType(new TextureMetadataSectionSerializer(), TextureMetadataSection.class);
      metadataSerializer.registerMetadataSectionType(new FontMetadataSectionSerializer(), FontMetadataSection.class);
      metadataSerializer.registerMetadataSectionType(new AnimationMetadataSectionSerializer(), AnimationMetadataSection.class);
      metadataSerializer.registerMetadataSectionType(new PackMetadataSectionSerializer(), PackMetadataSection.class);
      metadataSerializer.registerMetadataSectionType(new LanguageMetadataSectionSerializer(), LanguageMetadataSection.class);
      return metadataSerializer;
   }
}

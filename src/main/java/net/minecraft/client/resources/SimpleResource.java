package net.minecraft.client.resources;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

public class SimpleResource implements IResource {
   public Map<String, IMetadataSection> mapMetadataSections = Maps.newHashMap();
   public String resourcePackName;
   public ResourceLocation srResourceLocation;
   public InputStream resourceInputStream;
   public InputStream mcmetaInputStream;
   public IMetadataSerializer srMetadataSerializer;
   public boolean mcmetaJsonChecked;
   public JsonObject mcmetaJson;

   public SimpleResource(
      String resourcePackNameIn,
      ResourceLocation srResourceLocationIn,
      InputStream resourceInputStreamIn,
      InputStream mcmetaInputStreamIn,
      IMetadataSerializer srMetadataSerializerIn
   ) {
      this.resourcePackName = resourcePackNameIn;
      this.srResourceLocation = srResourceLocationIn;
      this.resourceInputStream = resourceInputStreamIn;
      this.mcmetaInputStream = mcmetaInputStreamIn;
      this.srMetadataSerializer = srMetadataSerializerIn;
   }

   @Override
   public ResourceLocation getResourceLocation() {
      return this.srResourceLocation;
   }

   @Override
   public InputStream getInputStream() {
      return this.resourceInputStream;
   }

   @Override
   public boolean hasMetadata() {
      return this.mcmetaInputStream != null;
   }

   @Override
   public <T extends IMetadataSection> T getMetadata(String str) {
      if (!this.hasMetadata()) {
         return null;
      }

      if (this.mcmetaJson == null && !this.mcmetaJsonChecked) {
         this.mcmetaJsonChecked = true;
         BufferedReader bufferedReader = null;

         try {
            bufferedReader = new BufferedReader(new InputStreamReader(this.mcmetaInputStream));
            this.mcmetaJson = new JsonParser().parse(bufferedReader).getAsJsonObject();
         } finally {
            IOUtils.closeQuietly(bufferedReader);
         }
      }

      IMetadataSection metadataSection = this.mapMetadataSections.get(str);
      if (metadataSection == null) {
         metadataSection = this.srMetadataSerializer.parseMetadataSection(str, this.mcmetaJson);
      }

      return (T)metadataSection;
   }

   @Override
   public String getResourcePackName() {
      return this.resourcePackName;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof SimpleResource)) {
         return false;
      }

      SimpleResource simpleResource = (SimpleResource)obj;
      if (this.srResourceLocation != null) {
         if (!this.srResourceLocation.equals(simpleResource.srResourceLocation)) {
            return false;
         }
      } else if (simpleResource.srResourceLocation != null) {
         return false;
      }

      if (this.resourcePackName != null) {
         if (!this.resourcePackName.equals(simpleResource.resourcePackName)) {
            return false;
         }
      } else if (simpleResource.resourcePackName != null) {
         return false;
      }

      return true;
   }

   @Override
   public int hashCode() {
      int n = this.resourcePackName != null ? this.resourcePackName.hashCode() : 0;
      return 31 * n + (this.srResourceLocation != null ? this.srResourceLocation.hashCode() : 0);
   }
}

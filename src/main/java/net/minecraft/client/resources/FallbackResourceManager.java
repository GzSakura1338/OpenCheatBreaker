package net.minecraft.client.resources;

import com.cheatbreaker.client.util.ClientResourceFilter;
import com.google.common.collect.Lists;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FallbackResourceManager implements IResourceManager {
   public static Logger logger = LogManager.getLogger();
   public List<IResourcePack> resourcePacks = Lists.newArrayList();
   public IMetadataSerializer frmMetadataSerializer;

   public FallbackResourceManager(IMetadataSerializer frmMetadataSerializerIn) {
      this.frmMetadataSerializer = frmMetadataSerializerIn;
   }

   public void addResourcePack(IResourcePack resourcePack) {
      this.resourcePacks.add(resourcePack);
   }

   @Override
   public Set<String> getResourceDomains() {
      return null;
   }

   @Override
   public IResource getResource(ResourceLocation location) throws IOException {
      IResourcePack resourcePack = null;
      ResourceLocation locationMcmeta = getLocationMcmeta(location);

      for (int i = this.resourcePacks.size() - 1; i >= 0; i--) {
         IResourcePack resourcePack2 = this.resourcePacks.get(i);
         if (ClientResourceFilter.isAllowedInPack(location, resourcePack2)) {
            if (resourcePack == null && resourcePack2.resourceExists(locationMcmeta)) {
               resourcePack = resourcePack2;
            }

            if (resourcePack2.resourceExists(location)) {
               InputStream inputStream = null;
               if (resourcePack != null) {
                  inputStream = this.getInputStream(locationMcmeta, resourcePack);
               }

               return new SimpleResource(resourcePack2.getPackName(), location, this.getInputStream(location, resourcePack2), inputStream, this.frmMetadataSerializer);
            }
         }
      }

      throw new FileNotFoundException(location.toString());
   }

   public InputStream getInputStream(ResourceLocation location, IResourcePack resourcePack) throws IOException {
      InputStream inputStream = resourcePack.getInputStream(location);
      return logger.isDebugEnabled() ? new FallbackResourceManager.InputStreamLeakedResourceLogger(inputStream, location, resourcePack.getPackName()) : inputStream;
   }

   @Override
   public List<IResource> getAllResources(ResourceLocation location) throws IOException {
      ArrayList newArrayList = Lists.newArrayList();
      ResourceLocation locationMcmeta = getLocationMcmeta(location);

      for (IResourcePack resourcePack : this.resourcePacks) {
         if (ClientResourceFilter.isAllowedInPack(location, resourcePack) && resourcePack.resourceExists(location)) {
            InputStream inputStream = resourcePack.resourceExists(locationMcmeta) ? this.getInputStream(locationMcmeta, resourcePack) : null;
            newArrayList.add(new SimpleResource(resourcePack.getPackName(), location, this.getInputStream(location, resourcePack), inputStream, this.frmMetadataSerializer));
         }
      }

      if (newArrayList.isEmpty()) {
         throw new FileNotFoundException(location.toString());
      } else {
         return newArrayList;
      }
   }

   public static ResourceLocation getLocationMcmeta(ResourceLocation location) {
      return new ResourceLocation(location.getResourceDomain(), location.getResourcePath() + ".mcmeta");
   }

   public List<IResourcePack> getResourcePacks() {
      return this.resourcePacks;
   }

   static class InputStreamLeakedResourceLogger extends InputStream {
      public boolean isClosed = false;
      public InputStream inputStream;
      public String message;

      public InputStreamLeakedResourceLogger(InputStream inputStream, ResourceLocation location, String resourcePack) {
         this.inputStream = inputStream;
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         new Exception().printStackTrace(new PrintStream(byteArrayOutputStream));
         this.message = "Leaked resource: '" + location + "' loaded from pack: '" + resourcePack + "'\n" + byteArrayOutputStream.toString();
      }

      @Override
      public void close() throws IOException {
         this.inputStream.close();
         this.isClosed = true;
      }

      @Override
      public void finalize() throws Throwable {
         if (!this.isClosed) {
            FallbackResourceManager.logger.warn(this.message);
         }

         super.finalize();
      }

      @Override
      public int read() throws IOException {
         return this.inputStream.read();
      }
   }
}

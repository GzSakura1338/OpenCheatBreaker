package net.minecraft.client.resources;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.IResourcePackEvent;
import com.cheatbreaker.client.util.PackNameUtils;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleReloadableResourceManager implements IReloadableResourceManager {
   public static Logger logger = LogManager.getLogger();
   public static Joiner joinerResourcePacks = Joiner.on(", ");
   public Map<String, FallbackResourceManager> domainResourceManagers = Maps.newHashMap();
   public List<IResourceManagerReloadListener> reloadListeners = Lists.newArrayList();
   public Set<String> setResourceDomains = Sets.newLinkedHashSet();
   public IMetadataSerializer rmMetadataSerializer;

   public SimpleReloadableResourceManager(IMetadataSerializer rmMetadataSerializerIn) {
      this.rmMetadataSerializer = rmMetadataSerializerIn;
   }

   public void reloadResourcePack(IResourcePack resourcePack) {
      for (String str : resourcePack.getResourceDomains()) {
         this.setResourceDomains.add(str);
         FallbackResourceManager fallbackResourceManager = this.domainResourceManagers.get(str);
         if (fallbackResourceManager == null) {
            fallbackResourceManager = new FallbackResourceManager(this.rmMetadataSerializer);
            this.domainResourceManagers.put(str, fallbackResourceManager);
         }

         fallbackResourceManager.addResourcePack(resourcePack);
      }
   }

   @Override
   public Set<String> getResourceDomains() {
      return this.setResourceDomains;
   }

   @Override
   public IResource getResource(ResourceLocation location) throws IOException {
      IResourceManager resourceManager = this.domainResourceManagers.get(location.getResourceDomain());
      if (resourceManager != null) {
         return resourceManager.getResource(location);
      } else {
         throw new FileNotFoundException(location.toString());
      }
   }

   @Override
   public List<IResource> getAllResources(ResourceLocation location) throws IOException {
      IResourceManager resourceManager = this.domainResourceManagers.get(location.getResourceDomain());
      if (resourceManager != null) {
         return resourceManager.getAllResources(location);
      } else {
         throw new FileNotFoundException(location.toString());
      }
   }

   public void clearResources() {
      this.domainResourceManagers.clear();
      this.setResourceDomains.clear();
   }

   @Override
   public void reloadResources(List<IResourcePack> resourcesPacksList) {
      this.clearResources();
      logger.info("Reloading ResourceManager: " + joinerResourcePacks.join(Iterables.transform(resourcesPacksList, new Function<IResourcePack, String>() {
         public String apply(IResourcePack resourcePack) {
            return resourcePack.getPackName();
         }
      })));

      for (IResourcePack resourcePack : resourcesPacksList) {
         this.reloadResourcePack(resourcePack);
      }

      this.notifyReloadListeners();
      if (CheatBreaker.getInstance() != null) {
         CheatBreaker.getInstance().getEventBus().handleEvent(new IResourcePackEvent(PackNameUtils.getTopResourcePack()));
      }
   }

   @Override
   public void registerReloadListener(IResourceManagerReloadListener reloadListener) {
      this.reloadListeners.add(reloadListener);
      reloadListener.onResourceManagerReload(this);
   }

   public void notifyReloadListeners() {
      for (IResourceManagerReloadListener resourceManagerReloadListener : this.reloadListeners) {
         resourceManagerReloadListener.onResourceManagerReload(this);
      }
   }

   public Map<String, FallbackResourceManager> getDomainResourceManagers() {
      return this.domainResourceManagers;
   }
}

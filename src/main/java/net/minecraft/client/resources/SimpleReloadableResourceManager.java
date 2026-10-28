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
    public Map domainResourceManagers = Maps.newHashMap();
    public List reloadListeners = Lists.newArrayList();
    public Set setResourceDomains = Sets.newLinkedHashSet();
    public IMetadataSerializer rmMetadataSerializer;

    public SimpleReloadableResourceManager(IMetadataSerializer metadataSerializer) {
        this.rmMetadataSerializer = metadataSerializer;
    }

    public void reloadResourcePack(IResourcePack resourcePack) {
        for (String str : (Iterable<String>)resourcePack.getResourceDomains()) {
            this.setResourceDomains.add(str);
            FallbackResourceManager fallbackResourceManager = (FallbackResourceManager)this.domainResourceManagers.get(str);
            if (fallbackResourceManager == null) {
                fallbackResourceManager = new FallbackResourceManager(this.rmMetadataSerializer);
                this.domainResourceManagers.put(str, fallbackResourceManager);
            }

            fallbackResourceManager.addResourcePack(resourcePack);
        }
    }

    @Override
    public Set getResourceDomains() {
        return this.setResourceDomains;
    }

    @Override
    public IResource getResource(ResourceLocation resourceLocation) throws IOException {
        IResourceManager resourceManager = (IResourceManager)this.domainResourceManagers.get(resourceLocation.getResourceDomain());
        if (resourceManager != null) {
            return resourceManager.getResource(resourceLocation);
        } else {
            throw new FileNotFoundException(resourceLocation.toString());
        }
    }

    @Override
    public List getAllResources(ResourceLocation resourceLocation) throws IOException {
        IResourceManager resourceManager = (IResourceManager)this.domainResourceManagers.get(resourceLocation.getResourceDomain());
        if (resourceManager != null) {
            return resourceManager.getAllResources(resourceLocation);
        } else {
            throw new FileNotFoundException(resourceLocation.toString());
        }
    }

    public void clearResources() {
        this.domainResourceManagers.clear();
        this.setResourceDomains.clear();
    }

    @Override
    public void reloadResources(List list) {
        this.clearResources();
        logger.info("Reloading ResourceManager: " + joinerResourcePacks.join(Iterables.transform(list, new Function() {
            public String apply(IResourcePack resourcePack) {
                return resourcePack.getPackName();
            }

            public Object apply(Object obj) {
                return this.apply((IResourcePack)obj);
            }
        })));

        for (IResourcePack resourcePack : (Iterable<IResourcePack>)list) {
            this.reloadResourcePack(resourcePack);
        }

        this.notifyReloadListeners();
        if (CheatBreaker.getInstance() != null) {
            CheatBreaker.getInstance().getEventBus().handleEvent(new IResourcePackEvent(PackNameUtils.getTopResourcePack()));
        }
    }

    @Override
    public void registerReloadListener(IResourceManagerReloadListener resourceManagerReloadListener) {
        this.reloadListeners.add(resourceManagerReloadListener);
        resourceManagerReloadListener.onResourceManagerReload(this);
    }

    public void notifyReloadListeners() {
        for (IResourceManagerReloadListener resourceManagerReloadListener : (Iterable<IResourceManagerReloadListener>)this.reloadListeners) {
            resourceManagerReloadListener.onResourceManagerReload(this);
        }
    }
}

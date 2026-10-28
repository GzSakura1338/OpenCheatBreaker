package net.minecraft.client.resources;

import com.cheatbreaker.client.util.ClientResourceFilter;
import com.cheatbreaker.client.util.SneakyThrow;
import com.google.common.collect.Lists;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

public class FallbackResourceManager implements IResourceManager {
    public List<IResourcePack> resourcePacks = new ArrayList<>();
    public IMetadataSerializer frmMetadataSerializer;

    public FallbackResourceManager(IMetadataSerializer metadataSerializer) {
        this.frmMetadataSerializer = metadataSerializer;
    }

    public void addResourcePack(IResourcePack resourcePack) {
        this.resourcePacks.add(resourcePack);
    }

    @Override
    public Set getResourceDomains() {
        return null;
    }

    @Override
    public IResource getResource(ResourceLocation resourceLocation) throws IOException {
        try {
            IResourcePack resourcePack = null;
            ResourceLocation locationMcmeta = getLocationMcmeta(resourceLocation);

            for (int i = this.resourcePacks.size() - 1; i >= 0; i--) {
                IResourcePack resourcePack2 = this.resourcePacks.get(i);
                if (ClientResourceFilter.isAllowedInPack(resourceLocation, resourcePack2)) {
                    if (resourcePack == null && resourcePack2.resourceExists(locationMcmeta)) {
                        resourcePack = resourcePack2;
                    }

                    if (resourcePack2.resourceExists(resourceLocation)) {
                        InputStream inputStream = null;
                        if (resourcePack != null) {
                            inputStream = resourcePack.getInputStream(locationMcmeta);
                        }

                        return new SimpleResource(resourceLocation, resourcePack2.getInputStream(resourceLocation), inputStream, this.frmMetadataSerializer);
                    }
                }
            }

            throw new FileNotFoundException(resourceLocation.toString());
        } catch (Throwable e) {
            throw SneakyThrow.sneakyThrow(e);
        }
    }

    @Override
    public List<SimpleResource> getAllResources(ResourceLocation resourceLocation) throws IOException {
        if (ClientResourceFilter.isBundledClientResource(resourceLocation)) {
            return Lists.newArrayList(new SimpleResource[]{(SimpleResource)this.getResource(resourceLocation)});
        }

        ArrayList newArrayList = Lists.newArrayList();
        ResourceLocation locationMcmeta = getLocationMcmeta(resourceLocation);

        for (IResourcePack resourcePack : this.resourcePacks) {
            if (resourcePack.resourceExists(resourceLocation)) {
                InputStream inputStream = resourcePack.resourceExists(locationMcmeta) ? resourcePack.getInputStream(locationMcmeta) : null;
                newArrayList.add(new SimpleResource(resourceLocation, resourcePack.getInputStream(resourceLocation), inputStream, this.frmMetadataSerializer));
            }
        }

        if (newArrayList.isEmpty()) {
            throw new FileNotFoundException(resourceLocation.toString());
        } else {
            return newArrayList;
        }
    }

    public static ResourceLocation getLocationMcmeta(ResourceLocation resourceLocation) {
        return new ResourceLocation(resourceLocation.getResourceDomain(), resourceLocation.getResourcePath() + ".mcmeta");
    }
}

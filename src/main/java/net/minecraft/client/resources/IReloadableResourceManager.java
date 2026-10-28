package net.minecraft.client.resources;

import java.util.List;

public interface IReloadableResourceManager extends IResourceManager {
    void reloadResources(List list);

    void registerReloadListener(IResourceManagerReloadListener resourceManagerReloadListener);
}

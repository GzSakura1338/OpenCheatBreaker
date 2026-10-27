package net.minecraft.client.resources;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import net.minecraft.util.ResourceLocation;

public interface IResourceManager {
   Set<String> getResourceDomains();

   IResource getResource(ResourceLocation resourceLocation) throws IOException;

   List<IResource> getAllResources(ResourceLocation resourceLocation) throws IOException;
}

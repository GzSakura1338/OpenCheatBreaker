package com.cheatbreaker.client.util;

import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.ResourceLocation;

public class ClientResourceFilter {
   public static boolean isAllowedInPack(ResourceLocation resourceLocation, IResourcePack resourcePack) {
      return !isBundledClientResource(resourceLocation) || resourcePack instanceof DefaultResourcePack;
   }

   public static boolean isBundledClientResource(ResourceLocation resourceLocation) {
      String resourceDomain = resourceLocation.getResourceDomain();
      String resourcePath = resourceLocation.getResourcePath();
      return "emoticons".equals(resourceDomain)
         || "voxelmap".equals(resourceDomain)
         || "minecraft".equals(resourceDomain) && (resourcePath.startsWith("client/") || resourcePath.startsWith("emoticons/") || resourcePath.startsWith("voxelmap/"));
   }
}

package com.cheatbreaker.client.event;

import net.minecraft.client.resources.IResourcePack;

public class IResourcePackEvent extends Event {
   public IResourcePack resourcePack;

   public IResourcePack getResourcePack() {
      return this.resourcePack;
   }

   public IResourcePackEvent(IResourcePack resourcePack) {
      this.resourcePack = resourcePack;
   }
}

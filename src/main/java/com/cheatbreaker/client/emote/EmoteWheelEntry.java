package com.cheatbreaker.client.emote;

import net.minecraft.util.ResourceLocation;

public class EmoteWheelEntry {
   public AbstractEmote abstractEmote;
   public String name;
   public ResourceLocation resourceLocation;

   public ResourceLocation getResourceLocation() {
      return this.resourceLocation;
   }

   public String getName() {
      return this.name;
   }

   public AbstractEmote getAbstractEmote() {
      return this.abstractEmote;
   }

   public EmoteWheelEntry(AbstractEmote abstractEmote, String str, ResourceLocation resourceLocation) {
      this.abstractEmote = abstractEmote;
      this.name = str;
      this.resourceLocation = resourceLocation;
   }
}

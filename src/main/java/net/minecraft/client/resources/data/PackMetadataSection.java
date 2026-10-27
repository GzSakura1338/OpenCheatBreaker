package net.minecraft.client.resources.data;

import net.minecraft.util.IChatComponent;

public class PackMetadataSection implements IMetadataSection {
   public IChatComponent packDescription;
   public int packFormat;

   public PackMetadataSection(IChatComponent chatComponent, int n) {
      this.packDescription = chatComponent;
      this.packFormat = n;
   }

   public IChatComponent getPackDescription() {
      return this.packDescription;
   }

   public int getPackFormat() {
      return this.packFormat;
   }
}

package net.minecraft.client.resources;

import net.minecraft.client.gui.GuiScreenResourcePacks;

public class ResourcePackListEntryFound extends ResourcePackListEntry {
   public ResourcePackRepository.Entry entry;

   public ResourcePackListEntryFound(GuiScreenResourcePacks resourcePacksGUIIn, ResourcePackRepository.Entry entry) {
      super(resourcePacksGUIIn);
      this.entry = entry;
   }

   @Override
   public void run() {
      this.entry.bindTexturePackIcon(this.mc.getTextureManager());
   }

   @Override
   public int getFormat() {
      return this.entry.getFormat();
   }

   @Override
   public String createStringBuilder() {
      return this.entry.getTexturePackDescription();
   }

   @Override
   public String getResourcePackName() {
      return this.entry.getResourcePackName();
   }

   public ResourcePackRepository.Entry getEntry() {
      return this.entry;
   }
}

package net.minecraft.client.gui;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourcePackListEntry;

public class GuiResourcePackAvailable extends GuiResourcePackList {
   public GuiResourcePackAvailable(Minecraft mcIn, int n, int n2, List<ResourcePackListEntry> list) {
      super(mcIn, n, n2, list);
   }

   @Override
   public String getListHeader() {
      return I18n.format("resourcePack.available.title");
   }
}

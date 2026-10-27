package net.minecraft.client.gui;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.util.EnumChatFormatting;

public abstract class GuiResourcePackList extends GuiListExtended {
   public Minecraft mc;
   public List<ResourcePackListEntry> widgets;

   public GuiResourcePackList(Minecraft mcIn, int n, int n2, List<ResourcePackListEntry> list) {
      super(mcIn, n, n2, 32, n2 - 55 + 4, 36);
      this.mc = mcIn;
      this.widgets = list;
      this.centerListVertically = false;
      this.setHasListHeader(true, (int)(mcIn.fontRendererObj.FONT_HEIGHT * 1.5F));
   }

   @Override
   public void drawListHeader(int n, int n2, Tessellator tessellator) {
      String str = EnumChatFormatting.UNDERLINE + "" + EnumChatFormatting.BOLD + this.getListHeader();
      this.mc
         .fontRendererObj
         .drawString(str, n + this.width / 2 - this.mc.fontRendererObj.getStringWidth(str) / 2, Math.min(this.top + 3, n2), 16777215);
   }

   public abstract String getListHeader();

   public List<ResourcePackListEntry> getList() {
      return this.widgets;
   }

   @Override
   public int getSize() {
      return this.getList().size();
   }

   public ResourcePackListEntry getListEntry(int index) {
      return this.getList().get(index);
   }

   @Override
   public int getListWidth() {
      return this.width;
   }

   @Override
   public int getScrollBarX() {
      return this.right - 6;
   }
}

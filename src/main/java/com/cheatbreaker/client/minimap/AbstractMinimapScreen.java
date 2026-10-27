package com.cheatbreaker.client.minimap;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

public class AbstractMinimapScreen extends GuiScreen {
   public FontRenderer getFontRendererObj() {
      return this.fontRendererObj;
   }

   public Minecraft getMc() {
      return this.mc;
   }

   public int getWidth() {
      return this.width;
   }

   public List getButtonList() {
      return this.buttonList;
   }

   @Override
   public void onGuiClosed() {
      MinimapSettings.instance.save();
   }

   public void run8() {
      if (!AbstractVoxelMap.instance.getMapOptions().showInScreens) {
         AbstractVoxelMap.instance.getMap().drawMinimap(this.mc);
         GlStateManager.clear(256);
      }
   }

   public int getHeight() {
      return this.height;
   }
}

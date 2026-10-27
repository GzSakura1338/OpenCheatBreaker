package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.FloatEvent7;
import com.cheatbreaker.client.mod.AbstractInternalMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.util.ClientVersion;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class VersionWatermarkMod extends AbstractInternalMod {
   public String text;

   @Override
   public void onEnable() {
      super.onEnable();
      this.run();
   }

   public void run() {
      Display.setTitle("CheatBreaker " + (this.isEnabled() ? this.text : ClientVersion.text));
   }

   public void onFloatEvent7(FloatEvent7 floatEvent7) {
      GlStateManager.pushMatrix();
      if (this.mc.isFullScreen() && !(this.mc.currentScreen instanceof GuiVideoSettings)) {
         GL11.glScalef(1.0F / this.cheatBreaker.getUiManager().getFloatValue(), 1.0F / this.cheatBreaker.getUiManager().getFloatValue(), 1.0F);
         this.cheatBreaker
            .getFontManager()
            .playRegular16
            .drawStringWithOutline(this.text, 5.0, this.mc.currentScreen.height * this.cheatBreaker.getUiManager().getFloatValue() - 12.0F, -1879048193);
      }

      GlStateManager.popMatrix();
   }

   public VersionWatermarkMod() {
      super("Version Watermark");
      this.text = ClientVersion.text + " (" + this.cheatBreaker.getCommitId() + "/" + this.cheatBreaker.getBranch() + ")";
      this.setCategories(ModCategory.MISC);
      this.setDescription("Draws a watermark in a GUI and sets the commit information on the windows title.");
      this.addEventHandler(FloatEvent7.class, this::onFloatEvent7);
      this.setEnabledByDefault(true);
      this.setCanToggle(false);
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.run();
   }
}

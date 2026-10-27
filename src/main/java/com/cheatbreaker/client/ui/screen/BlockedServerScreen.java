package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.util.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class BlockedServerScreen extends GuiScreen {
   public GuiScreen guiScreen;
   public ServerData serverData;
   public long longValue;
   public String text;
   public boolean flag;

   @Override
   public void mouseClicked(int n, int n2, int n3) throws java.io.IOException {
      super.mouseClicked(n, n2, n3);
      float f = 0.5F;
      String str = this.flag ? "Leave" : "Get me out of here!";
      float stringWidth = this.mc.fontRendererObj.getStringWidth(str);
      boolean flag = n > this.width / 2 + 20 && n < this.width / 2 + 140 && n2 > this.height / 2 + 40 && n2 < this.height / 2 + 50;
      boolean flag2 = (this.flag ? n > this.width / 2 - 20 : n > this.width / 2 - 130)
         && (this.flag ? n < this.width / 2 - 20 + stringWidth / f : n < this.width / 2 - 130 + stringWidth / f)
         && n2 > this.height / 2 + 30
         && n2 < this.height / 2 + 42;
      if (flag2) {
         Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         this.mc.displayGuiScreen(this.guiScreen);
      }

      if (flag && !this.flag) {
         Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         int n4 = this.longValue == 0L ? 10 : (int)((10999L - (System.currentTimeMillis() - this.longValue)) / 1000L);
         if (n4 <= 0) {
            this.mc.displayGuiScreen(new GuiConnecting(this.guiScreen, this.mc, this.serverData));
         }
      }
   }

   @Override
   public void drawScreen(int n, int n2, float f) {
      super.drawScreen(n, n2, f);
      this.drawDefaultBackground();
      float f2 = 1.0F;
      int n3 = this.longValue == 0L ? 10 : (int)((10999L - (System.currentTimeMillis() - this.longValue)) / 1000L);
      String str = this.flag ? "Leave" : "Get me out of here!";
      String str2 = n3 > 0 ? "Please wait (" + n3 + ")" : "Understood";
      String str3 = this.flag ? "THIS SERVER IS " + EnumChatFormatting.RED + "BLOCKED" : "WARNING!";
      float stringWidth = this.mc.fontRendererObj.getStringWidth(str);
      boolean flag = n > this.width / 2 + 20 && n < this.width / 2 + 140 && n2 > this.height / 2 + 40 && n2 < this.height / 2 + 50;
      boolean flag2 = (this.flag ? n > this.width / 2 - 20 : n > this.width / 2 - 130)
         && (this.flag ? n < this.width / 2 - 20 + stringWidth / f2 : n < this.width / 2 - 130 + stringWidth / f2)
         && n2 > this.height / 2 + 30
         && n2 < this.height / 2 + 42;
      RenderUtils.drawRoundedRectFilledPrecise(this.width / 2 - 140, this.height / 2 - 100, this.width / 2 + 140, this.height / 2 + 50, 8.0, 1342111744);
      GlStateManager.pushMatrix();
      if (!this.flag) {
         this.mc
            .fontRendererObj
            .drawStringWithShadow(
               str2,
               (int)((this.width / 2 + 130 - this.mc.fontRendererObj.getStringWidth(str2)) / f2),
               (int)((this.height / 2 + 35) / f2),
               flag ? -1 : (n3 <= 0 ? -1907998 : -5066062)
            );
      }

      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      String wrapToString = TextUtils.wrapSequence(this.text).setMaxWidth(50).setHyphenate(false).wrapToString();
      String[] split = wrapToString.split("\n");
      int n4 = 0;

      for (String str4 : split) {
         this.mc.fontRendererObj.computeInt2(str4, (int)(this.width / 2 / f2), (int)((this.height / 2 - 70 + n4 * 20) / f2), -1);
         n4++;
      }

      if (!this.flag) {
         this.mc
            .fontRendererObj
            .computeInt2(
               "Join at " + EnumChatFormatting.BOLD + "your own" + EnumChatFormatting.RESET + " risk!",
               (int)(this.width / 2 / f2),
               (int)((this.height / 2 - 70 + n4 * 20) / f2),
               -1
            );
         n4++;
      }

      int max = Math.max(n4, 5);
      GlStateManager.popMatrix();
      this.mc.fontRendererObj.computeInt2(str3, this.width / 2, this.height / 2 - 90, -1);
      if (this.flag) {
         this.mc.fontRendererObj.computeInt2(str, this.width / 2, this.height / 2 + max * 20 - 65, flag2 ? -1 : -1907998);
      } else {
         this.mc.fontRendererObj.drawStringWithShadow(str, this.width / 2 - 130, this.height / 2 + max * 20 - 65, flag2 ? -1 : -1907998);
      }
   }

   public BlockedServerScreen(GuiScreen guiScreen, ServerData serverData, String str, boolean flag) {
      this.guiScreen = guiScreen;
      this.serverData = serverData;
      this.longValue = System.currentTimeMillis();
      this.text = str;
      this.flag = flag;
   }
}

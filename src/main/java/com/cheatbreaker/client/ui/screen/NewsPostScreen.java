package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.component.ScrollBar;
import com.cheatbreaker.client.ui.component.TextButton;
import com.cheatbreaker.client.util.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class NewsPostScreen extends MenuScreen {
   public String text6;
   public String text22;
   public String text32;
   public TextButton back;
   public ScrollBar scrollBar;

   @Override
   public void drawScreenScaled(float f, float f2) {
      super.drawScreenScaled(f, f2);
      float min = Math.min(240.0F, this.getScaledWidth() - 10.0F);
      float f3 = this.getScaledWidth() / 2.0F - min / 2.0F;
      float f4 = this.getScaledWidth() / 2.0F + min / 2.0F;
      float f5 = 40.0F;
      float f6 = this.getScaledHeight() - 40.0F;
      Gui.drawRect(f3, f5, f4, f6, 788529152);
      Gui.drawRect(f3 + 8.0F, f5 + 22.0F, f4 - 8.0F, f5 + 22.5F, 452984831);
      CheatBreaker.getInstance().getFontManager().playRegular16.drawString(this.text6, f3 + 8.0F, f5 + 5.0F, -1);
      CheatBreaker.getInstance()
         .getFontManager()
         .playRegular14
         .drawString(EnumChatFormatting.ITALIC + "Posted by " + EnumChatFormatting.BOLD + this.text22, f3 + 8.0F, f5 + 13.0F, -1);
      String wrapToString = TextUtils.wrapSequence(this.text32).setMaxWidth(60).setHyphenate(false).wrapToString();
      String[] split = wrapToString.split("\n");
      int n = 0;
      this.back.setBounds(this.getScaledWidth() / 2.0F - 30.0F, this.getScaledHeight() - 35.0F, 60.0F, 12.0F);
      this.back.renderAndTestHover(f, f2, true);
      this.scrollBar.process3(f, f2, true);
      GlStateManager.pushMatrix();
      GL11.glEnable(3089);
      RenderUtils.scissorScaled(
         (int)f3,
         (int)f5 + 18,
         (int)f4,
         (int)f6 - 8,
         (int)(this.getScaledResolution().getScaleFactor() * this.getUiScale()),
         (int)this.getScaledHeight()
      );

      for (String str : split) {
         CheatBreaker.getInstance()
            .getFontManager()
            .playRegular14
            .drawString(str, f3 + 8.0F, f5 + 24.0F + n * (CheatBreaker.getInstance().getFontManager().playRegular14.getFont().getSize() / 2.0F), -1);
         n++;
      }

      this.scrollBar.setFloatValue4(n * (CheatBreaker.getInstance().getFontManager().playRegular14.getFont().getSize() / 2.0F) + 2.0F);
      GL11.glDisable(3089);
      GlStateManager.popMatrix();
      this.scrollBar.renderAndTestHover(f, f2, true);
   }

   @Override
   public void handleMouseInput() throws java.io.IOException {
      super.handleMouseInput();
      this.scrollBar.handleMouseInput();
   }

   @Override
   public void initGui() {
      super.initGui();
      float min = Math.min(240.0F, this.getScaledWidth() - 10.0F);
      float f = this.getScaledWidth() / 2.0F + min / 2.0F;
      float f2 = 40.0F;
      float f3 = this.getScaledHeight() - 40.0F;
      this.scrollBar.setBounds(f - 8.0F, f2 + 18.0F, 4.0F, f3 - f2 - 28.0F);
   }

   public NewsPostScreen(String str, String str2, String str3) {
      this.text6 = str;
      this.text22 = str2;
      this.text32 = str3;
      this.back = new TextButton("BACK");
      this.scrollBar = new ScrollBar(null);
   }

   @Override
   public void mouseClickedScaled(float f, float f2, int n) {
      super.mouseClickedScaled(f, f2, n);
      if (this.back.containsPoint(f, f2)) {
         Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         this.mc2.displayGuiScreen(CheatBreaker.getInstance().getGuiScreen());
      } else if (this.scrollBar.containsPoint(f, f2)) {
         this.scrollBar.mouseClicked(f, f2, n, true);
      }
   }
}

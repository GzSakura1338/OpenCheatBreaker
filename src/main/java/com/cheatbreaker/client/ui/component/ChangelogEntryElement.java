package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.animation.ColorAnimation;
import com.cheatbreaker.client.ui.screen.NewsPostScreen;
import com.cheatbreaker.client.util.TextUtils;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class ChangelogEntryElement extends UIElement {
   public ColorAnimation colorAnimation = new ColorAnimation(520093696, 1056964608);
   public String text;
   public String text2;
   public String text3;

   @Override
   public boolean mouseClicked(float f, float f2, int n, boolean flag) {
      if (this.containsPoint(f, f2)) {
         this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         this.mc.displayGuiScreen(new NewsPostScreen(this.text, this.text2, this.text3));
      }

      return false;
   }

   public ChangelogEntryElement(String str, String str2, String str3) {
      this.text = str;
      this.text2 = str2;
      this.text3 = str3;
   }

   @Override
   public void render(float f, float f2, boolean flag) {
      Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, this.colorAnimation.getColor(this.containsPoint(f, f2) && flag).getRGB());
      String wrapToString = TextUtils.wrapSequence(this.text3).setMaxWidth(60).setHyphenate(false).wrapToString();
      String[] split = wrapToString.split("\n");
      int n = 2;
      float f3 = CheatBreaker.getInstance().getFontManager().playRegular14.getFont().getSize() / 2.0F;
      CheatBreaker.getInstance().getFontManager().playRegular14.drawString(EnumChatFormatting.BOLD + this.text, 1.0F + this.x, this.y, -1);
      CheatBreaker.getInstance()
         .getFontManager()
         .playRegular12
         .drawString(EnumChatFormatting.ITALIC + "Posted by " + EnumChatFormatting.BOLD + this.text2, 1.0F + this.x, this.y + f3, -1);

      for (String str : split) {
         if (n < 3) {
            if (n == 2) {
               str = str + "...";
            }

            float f4 = 1.0F + this.x;
            CheatBreaker.getInstance().getFontManager().playRegular14.drawString(str, f4, this.y + n * f3 - 1.0F, -1);
         }

         n++;
      }
   }
}

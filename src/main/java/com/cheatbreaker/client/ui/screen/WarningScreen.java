package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.animation.ColorAnimation;
import com.cheatbreaker.client.ui.component.TextButton;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class WarningScreen extends UIScreen {
   public GuiScreen guiScreen;
   public boolean flag;
   public ColorAnimation colorAnimation3;
   public TextButton guiCancel;
   public TextButton iUnderstandContinue;

   @Override
   public void mouseClickedScaled(float f, float f2, int n) {
      if (this.guiCancel.containsPoint(f, f2)) {
         this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         this.mc.displayGuiScreen(this.guiScreen);
      } else if (this.iUnderstandContinue.containsPoint(f, f2)) {
         this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         this.mc.theWorld.sendQuittingDisconnectingPacket();
         this.mc.loadWorld(null);
         this.mc.displayGuiScreen(new ClassicMainMenuScreen());
      }
   }

   @Override
   public void onGuiClosed() {
      this.mc.entityRenderer.run();
   }

   @Override
   public void drawScreenScaled(float f, float f2) {
      if (this.flag && this.colorAnimation3.isFinished()) {
         this.flag = false;
      } else if (!this.flag && this.colorAnimation3.isFinished()) {
         this.flag = true;
      }

      this.process(this.getScaledWidth(), this.getScaledHeight());
      float f3 = this.getScaledWidth() / 2.0F;
      float f4 = this.getScaledHeight() / 2.0F - 50.0F;
      CheatBreaker.getInstance().getFontManager().playBold18.drawCenteredString("WARNING!", f3, f4, this.colorAnimation3.getColor(this.flag).getRGB());
      CheatBreaker.getInstance()
         .getFontManager()
         .playRegular16
         .drawCenteredString("Leaving competitive games may result in penalties.", f3, f4 + 15.0F, -1);
      CheatBreaker.getInstance()
         .getFontManager()
         .playRegular16
         .drawCenteredString("You may be suspended from competitive games if you continue leaving games!", f3, f4 + 25.0F, -1);
      this.guiCancel.renderAndTestHover(f, f2, true);
      this.iUnderstandContinue.renderAndTestHover(f, f2, true);
   }

   public WarningScreen(GuiScreen guiScreen) {
      this.guiScreen = guiScreen;
      this.colorAnimation3 = new ColorAnimation(2000L, -1, -52429);
      this.guiCancel = new TextButton(I18n.format("gui.cancel"));
      this.iUnderstandContinue = new TextButton("I understand, continue");
   }

   @Override
   public void initGui() {
      this.run2();
      this.flag = true;
      float f = this.getScaledWidth() / 2.0F;
      float f2 = this.getScaledHeight() / 2.0F - 50.0F;
      this.iUnderstandContinue.setBounds(f - 75.0F, f2 + 50.0F, 150.0F, 12.0F);
      this.guiCancel.setBounds(f - 75.0F, f2 + 64.0F, 150.0F, 12.0F);
      this.guiCancel.run();
   }

   @Override
   public void mouseReleasedScaled(float f, float f2, int n) {
   }
}

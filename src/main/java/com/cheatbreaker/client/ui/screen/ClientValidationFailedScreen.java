package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.ui.component.IconButton;
import com.cheatbreaker.client.ui.component.StatusLabel;
import com.cheatbreaker.client.ui.component.TextButton;
import com.cheatbreaker.client.util.ClientValidator;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

public class ClientValidationFailedScreen extends LogoMenuScreen {
   public TextButton tryAgain = new TextButton("TRY AGAIN");

   public ClientValidationFailedScreen() {
      this.textButtonList.add(this.tryAgain);
      this.text6 = "Unable to validate client.";
      this.text22 = "Please ensure everything is genuine.";
   }

   @Override
   public void initGui() {
      super.initGui();

      for (StatusLabel statusLabel : this.tabs) {
         statusLabel.setEnabled(false);
      }

      this.discordLinkButton.setEnabled(false);
      this.versionLabel.setEnabled(false);
      this.copyrightLabel.setEnabled(false);
      this.logoButton.setEnabled(false);

      for (IconButton iconButton : this.iconButtons) {
         iconButton.setEnabled(false);
      }
   }

   @Override
   public void mouseClickedScaled(float f, float f2, int n) {
      super.mouseClickedScaled(f, f2, n);
      if (this.tryAgain.containsPoint(f, f2)) {
         this.mc2.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         if (ClientValidator.check()) {
            this.mc2.displayGuiScreen(new ClassicMainMenuScreen());
         }
      }
   }
}

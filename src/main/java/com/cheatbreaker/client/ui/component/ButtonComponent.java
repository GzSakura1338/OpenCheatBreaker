package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

public class ButtonComponent extends SettingComponent {
   public Button button;

   public ButtonComponent(Setting setting, float f) {
      super(f);
      this.setting = setting;
      this.height = 18;
      this.button = new Button(
         CheatBreaker.getInstance().getFontManager().playBold16,
         null,
         setting.getName(),
         this.x + this.width - 100,
         this.y,
         96,
         18,
         this.theme.getNormalHighlightColorValue(),
         f,
         false,
         true
      );
   }

   @Override
   public void render(int n, int n2, float f) {
      this.button.scrollOffsetY = this.scrollOffsetY;
      this.button.setBounds(this.x + this.width - 109, this.y + 2, 93, 15);
      this.button.render(n, n2, f);
      this.renderSettingLabel(this.setting, n, n2);
   }

   @Override
   public void mouseClicked(int n, int n2, int n3) {
      if (!this.isHidden(this.setting)) {
         if (this.button.isMouseOver(n, n2)) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            this.setting.getOnUpdate().accept("");
         }
      }
   }

   public Button getButton() {
      return this.button;
   }
}

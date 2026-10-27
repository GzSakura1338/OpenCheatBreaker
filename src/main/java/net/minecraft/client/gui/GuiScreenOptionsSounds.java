package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class GuiScreenOptionsSounds extends GuiScreen {
   public String name = "Options";
   public GuiScreen parent;
   public GameSettings game_settings_4;
   public String off;

   public GuiScreenOptionsSounds(GuiScreen guiScreen, GameSettings gameSettings) {
      this.parent = guiScreen;
      this.game_settings_4 = gameSettings;
   }

   @Override
   public void initGui() {
      int n = 0;
      this.name = I18n.format("options.sounds.title");
      this.off = I18n.format("options.off");
      this.buttonList
         .add(
            new GuiScreenOptionsSounds.Button(
               SoundCategory.MASTER.getCategoryId(), this.width / 2 - 155 + n % 2 * 160, this.height / 6 - 12 + 24 * (n >> 1), SoundCategory.MASTER, true
            )
         );
      n += 2;

      for (SoundCategory soundCategory : SoundCategory.values()) {
         if (soundCategory != SoundCategory.MASTER) {
            this.buttonList
               .add(
                  new GuiScreenOptionsSounds.Button(
                     soundCategory.getCategoryId(), this.width / 2 - 155 + n % 2 * 160, this.height / 6 - 12 + 24 * (n >> 1), soundCategory, false
                  )
               );
            n++;
         }
      }

      this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done")));
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled && button.id == 200) {
         this.mc.gameSettings.saveOptions();
         this.mc.displayGuiScreen(this.parent);
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, this.name, this.width / 2, 15, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public String getSoundVolume(SoundCategory soundCategory) {
      float soundLevel = this.game_settings_4.getSoundLevel(soundCategory);
      return soundLevel == 0.0F ? this.off : (int)(soundLevel * 100.0F) + "%";
   }

   class Button extends GuiButton {
      public float volume = 1.0F;
      public SoundCategory category;
      public String categoryName;
      public boolean mouseButtonPressed;

      public Button(int n, int n2, int n3, SoundCategory soundCategory, boolean flag) {
         super(n, n2, n3, flag ? 310 : 150, 20, "");
         this.category = soundCategory;
         this.categoryName = I18n.format("soundCategory." + soundCategory.getCategoryName());
         this.displayString = this.categoryName + ": " + GuiScreenOptionsSounds.this.getSoundVolume(soundCategory);
         this.volume = GuiScreenOptionsSounds.this.game_settings_4.getSoundLevel(soundCategory);
      }

      @Override
      public int getHoverState(boolean mouseOver) {
         return 0;
      }

      @Override
      public void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
         if (this.visible) {
            if (this.mouseButtonPressed) {
               this.volume = (float)(mouseX - (this.xPosition + 4)) / (this.width - 8);
               this.volume = MathHelper.clamp_float(this.volume, 0.0F, 1.0F);
               mc.gameSettings.setSoundLevel(this.category, this.volume);
               mc.gameSettings.saveOptions();
               this.displayString = this.categoryName + ": " + GuiScreenOptionsSounds.this.getSoundVolume(this.category);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.xPosition + (int)(this.volume * (this.width - 8)), this.yPosition, 0, 66, 4, 20);
            this.drawTexturedModalRect(this.xPosition + (int)(this.volume * (this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
         }
      }

      @Override
      public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
         if (super.mousePressed(mc, mouseX, mouseY)) {
            this.volume = (float)(mouseX - (this.xPosition + 4)) / (this.width - 8);
            this.volume = MathHelper.clamp_float(this.volume, 0.0F, 1.0F);
            mc.gameSettings.setSoundLevel(this.category, this.volume);
            mc.gameSettings.saveOptions();
            this.displayString = this.categoryName + ": " + GuiScreenOptionsSounds.this.getSoundVolume(this.category);
            this.mouseButtonPressed = true;
            return true;
         } else {
            return false;
         }
      }

      @Override
      public void playPressSound(SoundHandler soundHandlerIn) {
      }

      @Override
      public void mouseReleased(int mouseX, int mouseY) {
         if (this.mouseButtonPressed) {
            if (this.category == SoundCategory.MASTER) {
               float f = 1.0F;
            } else {
               GuiScreenOptionsSounds.this.game_settings_4.getSoundLevel(this.category);
            }

            GuiScreenOptionsSounds.this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         }

         this.mouseButtonPressed = false;
      }
   }
}

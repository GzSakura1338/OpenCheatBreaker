package net.minecraft.client.gui;

import com.cheatbreaker.client.ui.screen.ResourcePacksScreen;
import java.io.IOException;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.EnumDifficulty;

public class GuiOptions extends GuiScreen implements GuiYesNoCallback {
   public static Options[] OPTIONS = new Options[]{Options.FOV};
   public String title = "Options";
   public GuiScreen parent;
   public GuiButton difficultyButton;
   public GuiLockIconButton lockDifficultyButton;
   public GameSettings game_settings_1;

   public GuiOptions(GuiScreen guiScreen, GameSettings gameSettings) {
      this.parent = guiScreen;
      this.game_settings_1 = gameSettings;
   }

   @Override
   public void initGui() {
      int n = 0;
      this.title = I18n.format("options.title");

      for (Options options : OPTIONS) {
         if (options.getEnumFloat()) {
            this.buttonList
               .add(new GuiOptionSlider(options.returnEnumOrdinal(), this.width / 2 - 155 + n % 2 * 160, this.height / 6 - 12 + 24 * (n >> 1), options));
         } else {
            GuiOptionButton guiOptionButton = new GuiOptionButton(
               options.returnEnumOrdinal(),
               this.width / 2 - 155 + n % 2 * 160,
               this.height / 6 - 12 + 24 * (n >> 1),
               options,
               this.game_settings_1.getKeyBinding(options)
            );
            this.buttonList.add(guiOptionButton);
         }

         n++;
      }

      if (this.mc.theWorld != null) {
         EnumDifficulty difficulty = this.mc.theWorld.getDifficulty();
         this.difficultyButton = new GuiButton(
            108, this.width / 2 - 155 + n % 2 * 160, this.height / 6 - 12 + 24 * (n >> 1), 150, 20, this.getDifficultyButtonText(difficulty)
         );
         this.buttonList.add(this.difficultyButton);
         if (this.mc.isSingleplayer() && !this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
            this.difficultyButton.setWidth(this.difficultyButton.getButtonWidth() - 20);
            this.lockDifficultyButton = new GuiLockIconButton(
               109, this.difficultyButton.xPosition + this.difficultyButton.getButtonWidth(), this.difficultyButton.yPosition
            );
            this.buttonList.add(this.lockDifficultyButton);
            this.lockDifficultyButton.setLocked(this.mc.theWorld.getWorldInfo().isDifficultyLocked());
            this.lockDifficultyButton.enabled = !this.lockDifficultyButton.isLocked();
            this.difficultyButton.enabled = !this.lockDifficultyButton.isLocked();
         } else {
            this.difficultyButton.enabled = false;
         }
      } else {
         GuiOptionButton guiOptionButton = new GuiOptionButton(
            Options.REALMS_NOTIFICATIONS.returnEnumOrdinal(),
            this.width / 2 - 155 + n % 2 * 160,
            this.height / 6 - 12 + 24 * (n >> 1),
            Options.REALMS_NOTIFICATIONS,
            this.game_settings_1.getKeyBinding(Options.REALMS_NOTIFICATIONS)
         );
         this.buttonList.add(guiOptionButton);
      }

      this.buttonList.add(new GuiButton(110, this.width / 2 - 155, this.height / 6 + 48 - 6, 150, 20, I18n.format("options.skinCustomisation")));
      this.buttonList
         .add(
            new GuiButton(8675309, this.width / 2 + 5, this.height / 6 + 48 - 6, 150, 20, "Super Secret Settings...") {
               @Override
               public void playPressSound(SoundHandler soundHandlerIn) {
                  SoundEventAccessorComposite randomSoundFromCategories = soundHandlerIn.getRandomSoundFromCategories(
                     SoundCategory.ANIMALS, SoundCategory.BLOCKS, SoundCategory.MOBS, SoundCategory.PLAYERS, SoundCategory.WEATHER
                  );
                  if (randomSoundFromCategories != null) {
                     soundHandlerIn.playSound(PositionedSoundRecord.create(randomSoundFromCategories.getSoundEventLocation(), 0.5F));
                  }
               }
            }
         );
      this.buttonList.add(new GuiButton(106, this.width / 2 - 155, this.height / 6 + 72 - 6, 150, 20, I18n.format("options.sounds")));
      this.buttonList.add(new GuiButton(101, this.width / 2 - 155, this.height / 6 + 96 - 6, 150, 20, I18n.format("options.video")));
      this.buttonList.add(new GuiButton(100, this.width / 2 + 5, this.height / 6 + 96 - 6, 150, 20, I18n.format("options.controls")));
      this.buttonList.add(new GuiButton(102, this.width / 2 + 5, this.height / 6 + 72 - 6, 150, 20, I18n.format("options.language")));
      this.buttonList.add(new GuiButton(103, this.width / 2 + 5, this.height / 6 + 120 - 6, 150, 20, I18n.format("options.chat.title")));
      this.buttonList.add(new GuiButton(105, this.width / 2 - 155, this.height / 6 + 120 - 6, 150, 20, I18n.format("options.resourcepack")));
      this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done")));
   }

   public String getDifficultyButtonText(EnumDifficulty enumDifficulty) {
      ChatComponentText chatComponentText = new ChatComponentText("");
      chatComponentText.appendSibling(new ChatComponentTranslation("options.difficulty"));
      chatComponentText.appendText(": ");
      chatComponentText.appendSibling(new ChatComponentTranslation(enumDifficulty.getDifficultyResourceKey()));
      return chatComponentText.getFormattedText();
   }

   @Override
   public void confirmClicked(boolean result, int id) {
      this.mc.displayGuiScreen(this);
      if (id == 109 && result && this.mc.theWorld != null) {
         this.mc.theWorld.getWorldInfo().setDifficultyLocked(true);
         this.lockDifficultyButton.setLocked(true);
         this.lockDifficultyButton.enabled = false;
         this.difficultyButton.enabled = false;
      }
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled) {
         if (button.id < 100 && button instanceof GuiOptionButton) {
            Options returnEnumOptions = ((GuiOptionButton)button).returnEnumOptions();
            this.game_settings_1.setOptionValue(returnEnumOptions, 1);
            button.displayString = this.game_settings_1.getKeyBinding(Options.getEnumOptions(button.id));
         }

         if (button.id == 108) {
            this.mc.theWorld.getWorldInfo().setDifficulty(EnumDifficulty.getDifficultyEnum(this.mc.theWorld.getDifficulty().getDifficultyId() + 1));
            this.difficultyButton.displayString = this.getDifficultyButtonText(this.mc.theWorld.getDifficulty());
         }

         if (button.id == 109) {
            this.mc
               .displayGuiScreen(
                  new GuiYesNo(
                     this,
                     new ChatComponentTranslation("difficulty.lock.title").getFormattedText(),
                     new ChatComponentTranslation(
                           "difficulty.lock.question", new ChatComponentTranslation(this.mc.theWorld.getWorldInfo().getDifficulty().getDifficultyResourceKey())
                        )
                        .getFormattedText(),
                     109
                  )
               );
         }

         if (button.id == 110) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(new GuiCustomizeSkin(this));
         }

         if (button.id == 8675309) {
            this.mc.entityRenderer.activateNextShader();
         }

         if (button.id == 101) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(new GuiVideoSettings(this, this.game_settings_1));
         }

         if (button.id == 100) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(new GuiControls(this, this.game_settings_1));
         }

         if (button.id == 102) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(new GuiLanguage(this, this.game_settings_1, this.mc.getLanguageManager()));
         }

         if (button.id == 103) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(new ScreenChatOptions(this, this.game_settings_1));
         }

         if (button.id == 104) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(new GuiSnooper(this, this.game_settings_1));
         }

         if (button.id == 200) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(this.parent);
         }

         if (button.id == 105) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(new ResourcePacksScreen(this));
         }

         if (button.id == 106) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(new GuiScreenOptionsSounds(this, this.game_settings_1));
         }
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 15, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }
}

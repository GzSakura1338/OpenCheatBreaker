package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.GameSettings.Options;

public class ScreenChatOptions extends GuiScreen {
   public static Options[] OPTIONS = new Options[]{
      Options.CHAT_VISIBILITY,
      Options.CHAT_COLOR,
      Options.CHAT_LINKS,
      Options.CHAT_OPACITY,
      Options.CHAT_LINKS_PROMPT,
      Options.CHAT_SCALE,
      Options.CHAT_HEIGHT_FOCUSED,
      Options.CHAT_HEIGHT_UNFOCUSED,
      Options.CHAT_WIDTH,
      Options.REDUCED_DEBUG_INFO
   };
   public GuiScreen parentScreen;
   public GameSettings game_settings;
   public String title;

   public ScreenChatOptions(GuiScreen parentScreenIn, GameSettings gameSettingsIn) {
      this.parentScreen = parentScreenIn;
      this.game_settings = gameSettingsIn;
   }

   @Override
   public void initGui() {
      int n = 0;
      this.title = I18n.format("options.chat.title");

      for (Options options : OPTIONS) {
         if (options.getEnumFloat()) {
            this.buttonList.add(new GuiOptionSlider(options.returnEnumOrdinal(), this.width / 2 - 155 + n % 2 * 160, this.height / 6 + 24 * (n >> 1), options));
         } else {
            this.buttonList
               .add(
                  new GuiOptionButton(
                     options.returnEnumOrdinal(),
                     this.width / 2 - 155 + n % 2 * 160,
                     this.height / 6 + 24 * (n >> 1),
                     options,
                     this.game_settings.getKeyBinding(options)
                  )
               );
         }

         n++;
      }

      this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 120, I18n.format("gui.done")));
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled) {
         if (button.id < 100 && button instanceof GuiOptionButton) {
            this.game_settings.setOptionValue(((GuiOptionButton)button).returnEnumOptions(), 1);
            button.displayString = this.game_settings.getKeyBinding(Options.getEnumOptions(button.id));
         }

         if (button.id == 200) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(this.parentScreen);
         }
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 20, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }
}

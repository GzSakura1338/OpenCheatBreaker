package com.cheatbreaker.client.minimap;

import com.cheatbreaker.client.util.Translations;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class WaypointOptionsScreen extends AbstractMinimapScreen {
   public static MinimapOption[] minimapOptionArray = new MinimapOption[]{
      MinimapOption.WAYPOINTDISTANCE, MinimapOption.DEATHPOINTS, MinimapOption.WAYPOINTBORDER, MinimapOption.WAYPOINTTEXTSHADOW
   };
   public String text = "Radar Options";
   public GuiScreen guiScreen;
   public MinimapSettings minimapSettings;

   @Override
   public void drawScreen(int n, int n2, float f) {
      super.run8();
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, this.text, this.getWidth() / 2, 20, 16777215);
      super.drawScreen(n, n2, f);
   }

   @Override
   public void initGui() {
      int n = 0;
      this.text = Translations.translate("options.minimap.waypoints.title");

      for (int i = 0; i < minimapOptionArray.length; i++) {
         MinimapOption minimapOption = minimapOptionArray[i];
         if (minimapOption.isFloat()) {
            float floatOption = this.minimapSettings.getFloatOption(minimapOption);
            if (floatOption < 0.0F) {
               floatOption = 10001.0F;
            }

            floatOption = (floatOption - 50.0F) / 9951.0F;
            this.getButtonList()
               .add(
                  new SliderButton(
                     minimapOption.getId(), this.getWidth() / 2 - 155 + n % 2 * 160, this.getHeight() / 6 + 24 * (n >> 1), minimapOption, floatOption, this.minimapSettings
                  )
               );
         } else {
            MinimapOptionButton minimapOptionButton = new MinimapOptionButton(
               minimapOption.getId(),
               this.getWidth() / 2 - 155 + n % 2 * 160,
               this.getHeight() / 6 + 24 * (n >> 1),
               minimapOption,
               this.minimapSettings.getOptionLabel(minimapOption)
            );
            this.getButtonList().add(minimapOptionButton);
         }

         n++;
      }

      this.getButtonList().add(new GuiButton(200, this.getWidth() / 2 - 100, this.getHeight() / 6 + 168, Translations.translate("gui.done")));
   }

   @Override
   public void actionPerformed(GuiButton guiButton) {
      if (guiButton.enabled) {
         if (guiButton.id < 100 && guiButton instanceof MinimapOptionButton) {
            this.minimapSettings.toggleOption(((MinimapOptionButton)guiButton).getMinimapOption(), 1);
            guiButton.displayString = this.minimapSettings.getOptionLabel(MinimapOption.fromId(guiButton.id));
         }

         if (guiButton.id == 200) {
            this.getMc().displayGuiScreen(this.guiScreen);
         }
      }
   }

   public WaypointOptionsScreen(GuiScreen guiScreen, MinimapSettings minimapSettings) {
      this.guiScreen = guiScreen;
      this.minimapSettings = minimapSettings;
   }
}

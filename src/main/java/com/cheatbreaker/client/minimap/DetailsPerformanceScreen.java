package com.cheatbreaker.client.minimap;

import com.cheatbreaker.client.util.Translations;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class DetailsPerformanceScreen extends AbstractMinimapScreen {
   public String text = "Details / Performance";
   public final int intValue = -1;
   public static MinimapOption[] minimapOptionArray = new MinimapOption[]{
      MinimapOption.LIGHTING,
      MinimapOption.TERRAIN,
      MinimapOption.WATERTRANSPARENCY,
      MinimapOption.BLOCKTRANSPARENCY,
      MinimapOption.BIOMES,
      MinimapOption.FILTERING,
      MinimapOption.CHUNKGRID,
      MinimapOption.BIOMEOVERLAY
   };
   public GuiScreen guiScreen;
   public MinimapSettings minimapSettings;

   @Override
   public void initGui() {
      this.text = Translations.translate("options.minimap.detailsperformance");
      int intValue = this.getIntValue();
      int n = 0;

      for (int i = 0; i < minimapOptionArray.length; i++) {
         MinimapOption minimapOption = minimapOptionArray[i];
         String optionLabel = this.minimapSettings.getOptionLabel(minimapOption);
         if ((minimapOption == MinimapOption.WATERTRANSPARENCY || minimapOption == MinimapOption.BLOCKTRANSPARENCY || minimapOption == MinimapOption.BIOMES)
            && !this.minimapSettings.multicore
            && this.minimapSettings.getBooleanOption(minimapOption)) {
            optionLabel = "§c" + optionLabel;
         }

         MinimapOptionButton minimapOptionButton = new MinimapOptionButton(minimapOption.getId(), intValue + n % 2 * 160, this.getHeight() / 6 + 24 * (n >> 1), minimapOption, optionLabel);
         this.buttonList.add(minimapOptionButton);
         n++;
      }

      this.buttonList.add(new GuiButton(200, this.getWidth() / 2 - 100, this.getHeight() / 6 + 168, Translations.translate("gui.done")));
   }

   public DetailsPerformanceScreen(GuiScreen guiScreen, IVoxelMap voxelMap) {
      this.guiScreen = guiScreen;
      this.minimapSettings = voxelMap.getMapOptions();
   }

   @Override
   public void actionPerformed(GuiButton guiButton) {
      if (guiButton.id < 100 && guiButton instanceof MinimapOptionButton) {
         this.minimapSettings.toggleOption(((MinimapOptionButton)guiButton).getMinimapOption(), 1);
         String str = "";
         if ((
               guiButton.id == MinimapOption.WATERTRANSPARENCY.ordinal()
                  || guiButton.id == MinimapOption.BLOCKTRANSPARENCY.ordinal()
                  || guiButton.id == MinimapOption.BIOMES.ordinal()
            )
            && !this.minimapSettings.multicore
            && this.minimapSettings.getBooleanOption(MinimapOption.fromId(guiButton.id))) {
            str = "§c";
         }

         guiButton.displayString = str + this.minimapSettings.getOptionLabel(MinimapOption.fromId(guiButton.id));
      }

      if (guiButton.id == 200) {
         this.getMc().displayGuiScreen(this.guiScreen);
      }
   }

   @Override
   public void drawScreen(int n, int n2, float f) {
      super.run8();
      this.drawDefaultBackground();
      this.drawCenteredString(this.getFontRendererObj(), this.text, this.getWidth() / 2, 20, 16777215);
      super.drawScreen(n, n2, f);
   }

   public int getIntValue() {
      return this.getWidth() / 2 - 155;
   }
}

package com.cheatbreaker.client.minimap;

import com.cheatbreaker.client.util.Translations;
import net.minecraft.client.gui.GuiButton;

public class MinimapOptionsScreen extends AbstractMinimapScreen {
   public String text = "Minimap Options";
   public IVoxelMap voxelMap;
   public MinimapSettings minimapSettings;
   public static MinimapOption[] minimapOptionArray;

   public MinimapOptionsScreen(IVoxelMap voxelMap) {
      this.voxelMap = voxelMap;
      this.minimapSettings = voxelMap.getMapOptions();
   }

   @Override
   public void drawScreen(int n, int n2, float f) {
      super.run8();
      this.drawDefaultBackground();
      this.drawCenteredString(this.getFontRendererObj(), this.text, this.getWidth() / 2, 20, 16777215);
      super.drawScreen(n, n2, f);
   }

   @Override
   public void initGui() {
      minimapOptionArray = new MinimapOption[]{
         MinimapOption.COORDS,
         MinimapOption.HIDE,
         MinimapOption.LOCATION,
         MinimapOption.SIZE,
         MinimapOption.SQUARE,
         MinimapOption.OLDNORTH,
         MinimapOption.BEACONS,
         MinimapOption.CAVEMODE
      };
      int n = 0;
      this.text = Translations.translate("options.minimap.title");

      for (int i = 0; i < minimapOptionArray.length; i++) {
         MinimapOption minimapOption = minimapOptionArray[i];
         MinimapOptionButton minimapOptionButton = new MinimapOptionButton(
            minimapOption.getId(), this.getWidth() / 2 - 155 + n % 2 * 160, this.getHeight() / 6 + 24 * (n >> 1), minimapOption, this.minimapSettings.getOptionLabel(minimapOption)
         );
         this.getButtonList().add(minimapOptionButton);
         if (minimapOption.equals(MinimapOption.CAVEMODE)) {
            minimapOptionButton.enabled = this.minimapSettings.caveModeAllowed && this.voxelMap.getRadar() != null;
         }

         n++;
      }

      MinimapOptionButton minimapOptionButton = new MinimapOptionButton(
         101, this.getWidth() / 2 - 155, this.getHeight() / 6 + 120 - 6, 150, 20, Translations.translate("options.minimap.radar")
      );
      minimapOptionButton.enabled = this.minimapSettings.radarAllowed && this.voxelMap.getRadar() != null;
      this.getButtonList().add(minimapOptionButton);
      this.getButtonList()
         .add(
            new GuiButton(103, this.getWidth() / 2 + 5, this.getHeight() / 6 + 120 - 6, 150, 20, Translations.translate("options.minimap.detailsperformance"))
         );
      this.getButtonList()
         .add(new GuiButton(102, this.getWidth() / 2 - 155, this.getHeight() / 6 + 144 - 6, 150, 20, Translations.translate("options.controls")));
      this.getButtonList()
         .add(new GuiButton(100, this.getWidth() / 2 + 5, this.getHeight() / 6 + 144 - 6, 150, 20, Translations.translate("options.minimap.waypoints")));
      this.getButtonList().add(new GuiButton(200, this.getWidth() / 2 - 100, this.getHeight() / 6 + 168, Translations.translate("menu.returnToGame")));
   }

   @Override
   public void actionPerformed(GuiButton guiButton) {
      if (guiButton.enabled) {
         if (guiButton.id < 100 && guiButton instanceof MinimapOptionButton) {
            this.minimapSettings.toggleOption(((MinimapOptionButton)guiButton).getMinimapOption(), 1);
            guiButton.displayString = this.minimapSettings.getOptionLabel(MinimapOption.fromId(guiButton.id));
         }

         if (guiButton.id == 103) {
            this.getMc().displayGuiScreen(new DetailsPerformanceScreen(this, this.voxelMap));
         }

         if (guiButton.id == 102) {
            this.getMc().displayGuiScreen(new MinimapControlsScreen(this, this.voxelMap));
         }

         if (guiButton.id == 101) {
            this.getMc().displayGuiScreen(new RadarOptionsScreen(this, this.voxelMap));
         }

         if (guiButton.id == 100) {
            this.getMc().displayGuiScreen(new WaypointsScreen(this, this.voxelMap));
         }

         if (guiButton.id == 200) {
            this.getMc().displayGuiScreen(null);
         }
      }
   }
}

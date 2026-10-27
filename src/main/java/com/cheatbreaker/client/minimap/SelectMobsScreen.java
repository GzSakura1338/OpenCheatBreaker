package com.cheatbreaker.client.minimap;

import com.cheatbreaker.client.util.Translations;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class SelectMobsScreen extends AbstractMinimapScreen {
   public String text = "Select Mobs";
   public MobListSlot mobListSlot;
   public String text2 = null;
   public String text3 = null;
   public GuiScreen guiScreen;
   public RadarSettings radarSettings;
   public GuiButton optionsMinimapMobsEnable;
   public GuiButton optionsMinimapMobsDisable;

   @Override
   public void actionPerformed(GuiButton guiButton) {
      if (guiButton.enabled) {
         if (guiButton.id == -1) {
            this.handleString(this.text2, true);
         }

         if (guiButton.id == -2) {
            this.handleString(this.text2, false);
         }

         if (guiButton.id == 65336) {
            this.getMc().displayGuiScreen(this.guiScreen);
         }
      }
   }

   @Override
   public void initGui() {
      boolean flag = false;
      this.text = Translations.translate("options.minimap.mobs.title");
      this.mobListSlot = new MobListSlot(this);
      this.mobListSlot.registerScrollButtons(7, 8);
      this.getButtonList()
         .add(
            this.optionsMinimapMobsEnable = new GuiButton(
               -1, this.getWidth() / 2 - 154, this.getHeight() - 28, 100, 20, Translations.translate("options.minimap.mobs.enable")
            )
         );
      this.getButtonList()
         .add(
            this.optionsMinimapMobsDisable = new GuiButton(
               -2, this.getWidth() / 2 - 50, this.getHeight() - 28, 100, 20, Translations.translate("options.minimap.mobs.disable")
            )
         );
      this.getButtonList().add(new GuiButton(65336, this.getWidth() / 2 + 4 + 50, this.getHeight() - 28, 100, 20, Translations.translate("gui.done")));
      boolean flag2 = this.text2 != null;
      this.optionsMinimapMobsEnable.enabled = flag2;
      this.optionsMinimapMobsDisable.enabled = flag2;
   }

   public static String formatText(SelectMobsScreen selectMobsScreen, String str) {
      return selectMobsScreen.text3 = str;
   }

   @Override
   public void drawScreen(int n, int n2, float f) {
      super.run8();
      this.text3 = null;
      this.mobListSlot.drawScreen(n, n2, f);
      this.drawCenteredString(this.getFontRendererObj(), this.text, this.getWidth() / 2, 20, 16777215);
      boolean flag = this.text2 != null;
      this.optionsMinimapMobsEnable.enabled = flag && !this.checkString(this.text2);
      this.optionsMinimapMobsDisable.enabled = flag && this.checkString(this.text2);
      super.drawScreen(n, n2, f);
      if (this.text3 != null) {
         this.handleString(this.text3, n, n2);
      }
   }

   public void handleString(String str, int n, int n2) {
      if (str != null) {
         int n3 = n + 12;
         int n4 = n2 - 12;
         int stringWidth = this.getFontRendererObj().getStringWidth(str);
         this.drawGradientRect(n3 - 3, n4 - 3, n3 + stringWidth + 3, n4 + 8 + 3, -1073741824, -1073741824);
         this.getFontRendererObj().drawStringWithShadow(str, n3, n4, -1);
      }
   }

   public SelectMobsScreen(GuiScreen guiScreen, RadarSettings radarSettings) {
      this.guiScreen = guiScreen;
      this.radarSettings = radarSettings;
   }

   public void run9() {
      MobType findMobType = MobType.findMobType(this.text2);
      if (findMobType != null) {
         this.handleString(this.text2, !findMobType.flag4);
      } else {
         CustomMob findCustomMob = CustomMobs.findCustomMob(this.text2);
         if (findCustomMob != null) {
            this.handleString(this.text2, !findCustomMob.enabled);
         }
      }
   }

   public void handleString(String str, boolean flag) {
      for (MobType mobType : MobType.values()) {
         if (mobType.text.equals(str)) {
            mobType.flag4 = flag;
         }
      }

      for (CustomMob customMob : CustomMobs.customMobList) {
         if (customMob.name.equals(str)) {
            customMob.enabled = flag;
         }
      }
   }

   public void setText2(String str) {
      this.text2 = str;
   }

   public boolean checkString(String str) {
      MobType findMobType = MobType.findMobType(this.text2);
      if (findMobType != null) {
         return findMobType.flag4;
      }

      CustomMob findCustomMob = CustomMobs.findCustomMob(this.text2);
      return findCustomMob != null ? findCustomMob.enabled : false;
   }
}

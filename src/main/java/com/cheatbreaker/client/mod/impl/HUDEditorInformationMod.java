package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.ListScaledResolutionEvent;
import com.cheatbreaker.client.mod.AbstractInternalMod;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.ui.screen.ModsScreen;

public class HUDEditorInformationMod extends AbstractInternalMod {
   public Setting showAnchor = new Setting(this, "Show Anchor").changeValue(true);
   public Setting showTranslation = new Setting(this, "Show Translation").changeValue(true);

   public void onListScaledResolutionEvent(ListScaledResolutionEvent listScaledResolutionEvent) {
      for (AbstractMod abstractMod : listScaledResolutionEvent.getAbstractModList()) {
         if (ModsScreen.modsScreen.getPlacement(abstractMod) != null) {
            if (this.showAnchor.getBooleanValue()) {
               this.cheatBreaker
                  .getFontManager()
                  .ubuntuM16
                  .drawStringWithOutline(
                     abstractMod.getAnchorPoint() + "",
                     abstractMod.getHudPosition(listScaledResolutionEvent.getScaledResolution(), true)[0] * abstractMod.getHudScale(),
                     abstractMod.getHudPosition(listScaledResolutionEvent.getScaledResolution(), true)[1] * abstractMod.getHudScale() + abstractMod.hudHeight * abstractMod.getHudScale() + 1.0F,
                     -1
                  );
            }

            if (this.showTranslation.getBooleanValue()) {
               this.cheatBreaker
                  .getFontManager()
                  .ubuntuM16
                  .drawStringWithOutline(
                     abstractMod.getTranslationX() + ", " + abstractMod.getTranslationY(),
                     abstractMod.getHudPosition(listScaledResolutionEvent.getScaledResolution(), true)[0] * abstractMod.getHudScale(),
                     abstractMod.getHudPosition(listScaledResolutionEvent.getScaledResolution(), true)[1] * abstractMod.getHudScale()
                        + abstractMod.hudHeight * abstractMod.getHudScale()
                        + (this.showAnchor.getBooleanValue() ? 2.0F + this.mc.fontRendererObj.FONT_HEIGHT : 1.0F),
                     -1
                  );
            }
         }
      }
   }

   public HUDEditorInformationMod() {
      super("HUD Editor Information");
      this.setDescription("Show debug information in the HUD editor");
      this.setCategories(ModCategory.HUD);
      this.addEventHandler(ListScaledResolutionEvent.class, this::onListScaledResolutionEvent);
      this.setEnabledByDefault(true);
   }
}

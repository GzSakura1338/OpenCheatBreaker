package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.ListScaledResolutionEvent;
import com.cheatbreaker.client.mod.AbstractInternalMod;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import net.optifine.GlStateManager;

public class HUDEditorInformationMod extends AbstractInternalMod {
    public Setting showAnchor = new Setting(this, "Show Anchor").changeValue(true);
    public Setting showTranslation = new Setting(this, "Show Translation").changeValue(true);

    public void onListScaledResolutionEvent(ListScaledResolutionEvent listScaledResolutionEvent) {
        for (AbstractMod abstractMod : listScaledResolutionEvent.getAbstractModList()) {
            if (ModsScreen.modsScreen.getPlacement(abstractMod) != null) {
                GlStateManager.pushMatrix();
                float[] hudPosition = abstractMod.getHudPosition(listScaledResolutionEvent.getScaledResolution(), true);
                float f = hudPosition[1] - CheatBreaker.getInstance().getFontManager().ubuntuM16.getLineHeight() - 6.0F < 0.0F ? 1.0F : 0.0F;
                float f2 = f != 0.0F ? abstractMod.hudHeight * abstractMod.getHudScale() : -CheatBreaker.getInstance().getFontManager().ubuntuM16.getLineHeight() - 4;
                float f3 = 0.0F;
                switch (abstractMod.getHudAlignment()) {
                    case LEFT:
                        f3 = 0.0F;
                        break;
                    case CENTER:
                        f3 = abstractMod.hudWidth * abstractMod.getHudScale() / 2.0F;
                }

                if (this.showAnchor.getBooleanValue()) {
                    this.cheatBreaker
                        .getFontManager()
                        .ubuntuM16
                        .drawStringWithOutline(abstractMod.getAnchorPoint().toString().replaceAll("_", " "), f3 + hudPosition[0], f2 * 2.0F + hudPosition[1], -1);
                }

                if (this.showTranslation.getBooleanValue()) {
                    this.cheatBreaker
                        .getFontManager()
                        .ubuntuM16
                        .drawStringWithOutline(abstractMod.getTranslationX() + ", " + abstractMod.getTranslationY(), f3 + hudPosition[0], f2 * 3.0F + hudPosition[1], -1);
                }

                GlStateManager.popMatrix();
            }
        }
    }

    public HUDEditorInformationMod() throws java.io.IOException {
        super("HUD Editor Information");
        this.setDescription("Show debug information in the HUD editor");
        this.setCategories(ModCategory.HUD);
        this.addEventHandler(ListScaledResolutionEvent.class, this::onListScaledResolutionEvent);
        this.setEnabledByDefault(true);
    }
}

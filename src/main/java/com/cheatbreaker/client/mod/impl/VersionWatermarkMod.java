package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.FloatEvent7;
import com.cheatbreaker.client.mod.AbstractInternalMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.util.ClientVersion;
import net.minecraft.client.gui.GuiVideoSettings;
import net.optifine.GlStateManager;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class VersionWatermarkMod extends AbstractInternalMod {
    public String watermarkText = ClientVersion.version + " (" + this.cheatBreaker.getCommitId() + "/" + this.cheatBreaker.getBranch() + ")";

    public void onFloatEvent7(FloatEvent7 floatEvent7) {
        GlStateManager.pushMatrix();
        if (this.mc.isFullScreen() && !(this.mc.currentScreen instanceof GuiVideoSettings)) {
            GL11.glScalef(1.0F / this.cheatBreaker.getUiManager().getFloatValue(), 1.0F / this.cheatBreaker.getUiManager().getFloatValue(), 1.0F);
            this.cheatBreaker
                .getFontManager()
                .playRegular16
                .drawStringWithOutline(
                    this.watermarkText, 5.0, this.mc.currentScreen.height * this.cheatBreaker.getUiManager().getFloatValue() - 12.0F, -1879048193
                );
        }

        GlStateManager.popMatrix();
    }

    @Override
    public void onEnable() throws java.io.IOException {
        super.onEnable();
        this.updateWindowTitle();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.updateWindowTitle();
    }

    public void updateWindowTitle() {
        Display.setTitle("CheatBreaker " + (this.isEnabled() ? this.watermarkText : ClientVersion.version));
    }

    public VersionWatermarkMod() throws java.io.IOException {
        super("Version Watermark");
        this.setDescription("Draws a watermark in a GUI and sets the commit information on the windows title.");
        this.setCategories(ModCategory.MISC);
        this.addEventHandler(FloatEvent7.class, this::onFloatEvent7);
        this.setEnabledByDefault(true);
        this.setCanToggle(false);
    }
}

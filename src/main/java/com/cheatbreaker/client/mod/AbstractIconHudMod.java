package com.cheatbreaker.client.mod;

import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.render.RenderUtils;
import net.minecraft.util.ResourceLocation;
import net.optifine.GlStateManager;

public abstract class AbstractIconHudMod extends AbstractBackgroundHudMod {
    public Setting showIcon;
    public ResourceLocation icon = null;

    @Override
    public void createGeneralOptions() {
        this.showIcon = new Setting(this, "Show icon")
            .setDescription("Show an icon corresponding to the mod.")
            .changeValue(true)
            .withParents(this.generalOptions);
    }

    @Override
    public BackgroundSizeLimits getSizeLimits() {
        return new BackgroundSizeLimits(10.0F, 16.0F, 64.0F, 40.0F, 56.0F, 80.0F);
    }

    public void setIcon(ResourceLocation resourceLocation) {
        this.icon = resourceLocation;
    }

    @Override
    public Setting getActiveFormat() {
        return !this.showIcon.getBooleanValue() && !this.showBackground.getBooleanValue() ? this.formatNoBackground : this.formatBackground;
    }

    public AbstractIconHudMod(String str) throws java.io.IOException {
        super(str);
    }

    public ResourceLocation getIcon() {
        return this.icon;
    }

    @Override
    public boolean wrapsTextWithoutBackground() {
        return false;
    }

    @Override
    public boolean isBoxDrawn() {
        return this.showBackground.getBooleanValue() || this.showIcon.getBooleanValue() || this.alwaysCenter.getBooleanValue();
    }

    @Override
    public void renderText(String str) throws java.io.IOException {
        this.extraWidth = this.showIcon.getBooleanValue() ? this.boxHeight : 0.0F;
        super.renderText(str);
        if (this.showIcon.getBooleanValue() && this.icon != null) {
            GlStateManager.color(1.0F, 1.0F, 1.0F);
            RenderUtils.drawSquareTexture(this.icon, this.boxHeight / 2.0F, 0.0F, 0.0F);
        }
    }
}

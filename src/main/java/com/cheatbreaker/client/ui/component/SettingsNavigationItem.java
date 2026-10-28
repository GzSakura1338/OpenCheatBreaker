package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.optifine.GlStateManager;

public class SettingsNavigationItem extends SettingComponent {
    public ResourceLocation rightPng = new ResourceLocation("client/icons/right.png");
    public int hoverProgress = 0;
    public String label;
    public int highlightColor;

    @Override
    public void mouseClicked(int n, int n2, int n3) {
    }

    public SettingsNavigationItem(String str, int n, float f) {
        super(f);
        this.label = str;
        this.highlightColor = n;
    }

    @Override
    public void render(int n, int n2, float f) {
        boolean isMouseOver = this.isMouseOver(n, n2);
        byte b = 75;
        Gui.drawRect(this.x, this.y + this.height - 1, this.x + this.width, this.y + this.height, this.theme.getPrimaryTextColorValue2());
        float animationStep = ModsScreen.animationStep(790.0F);
        if (isMouseOver) {
            if (this.hoverProgress < b) {
                this.hoverProgress = (int)(this.hoverProgress + animationStep);
                if (this.hoverProgress > b) {
                    this.hoverProgress = b;
                }
            }
        } else if (this.hoverProgress > 0) {
            this.hoverProgress = this.hoverProgress - animationStep < 0.0F ? 0 : (int)(this.hoverProgress - animationStep);
        }

        if (this.hoverProgress > 0) {
            float f2 = (float)this.hoverProgress / b * 100.0F;
            Gui.drawRect(this.x, (int)(this.y + (this.height - this.height * f2 / 100.0F)), this.x + this.width, this.y + this.height, this.highlightColor);
        }

        float red = RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getRed();
        float green = RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getGreen();
        float blue = RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getBlue();
        GlStateManager.color(red, green, blue, 0.35F);
        RenderUtils.drawSquareTextureViaRenderEngine(this.rightPng, 2.5F, this.x + 6, this.y + 6.0F);
        CheatBreaker.getInstance()
            .getFontManager()
            .playBold18
            .drawString(this.label.toUpperCase(), this.x + 14.0F, this.y + 3.0F, this.theme.getTextColor2Value2());
    }
}

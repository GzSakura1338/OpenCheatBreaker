package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.render.RenderUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.optifine.GlStateManager;

public class MenuIconButton extends UIElement {
    public ResourceLocation resourceLocation;
    public String label;
    public boolean flag;

    public MenuIconButton(ResourceLocation resourceLocation, String str, boolean flag) {
        this.resourceLocation = resourceLocation;
        this.label = str;
        this.flag = flag;
    }

    @Override
    public void render(float f, float f2, boolean flag) {
        boolean containsPoint = this.containsPoint(f, f2);
        if (this.flag) {
            Gui.drawRect(this.x, this.y, this.x + this.getWidth(), this.y + this.getHeight(), containsPoint ? -15395563 : -14540254);
        }

        int n = -3092272;
        if (containsPoint) {
            n = -1;
        }

        CheatBreaker.getInstance()
            .getFontManager()
            .playRegular16
            .drawCenteredString(this.label, this.x + this.getWidth() / 2.0F, this.y + this.getHeight() / 2.0F - (this.flag ? 6 : 5), n);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.3F);
        RenderUtils.drawSquareTextureViaRenderEngine(this.resourceLocation, 7.0F, this.x + this.getWidth() - 20.0F, this.y + 5.0F);
    }
}

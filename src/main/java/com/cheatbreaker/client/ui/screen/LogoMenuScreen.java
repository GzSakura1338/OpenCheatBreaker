package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.animation.CosineAnimation;
import com.cheatbreaker.client.animation.EaseInOutAnimation;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.component.TextButton;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.optifine.GlStateManager;
import org.lwjgl.opengl.GL11;

public class LogoMenuScreen extends MenuScreen {
    public static int intValue;
    public ResourceLocation logo255OuterPng = new ResourceLocation("client/logo_255_outer.png");
    public ResourceLocation logo108InnerPng = new ResourceLocation("client/logo_108_inner.png");
    public List<TextButton> textButtonList = new ArrayList<>();
    public EaseInOutAnimation easeInOutAnimation = new EaseInOutAnimation(750L);
    public CosineAnimation cosineAnimation = new CosineAnimation(4000L);
    public EaseInOutAnimation easeInOutAnimation2 = new EaseInOutAnimation(400L);
    public String text3 = "";
    public String text2 = "";

    @Override
    public void mouseClickedScaled(float f, float f2, int n) throws java.io.IOException {
        super.mouseClickedScaled(f, f2, n);

        for (TextButton textButton : this.textButtonList) {
            textButton.mouseClicked(f, f2, n, true);
        }
    }

    @Override
    public void updateScreen() throws java.io.IOException {
        super.updateScreen();
        if (this.shouldFadeIn() && !this.easeInOutAnimation.hasStarted()) {
            this.easeInOutAnimation.start();
        }

        if ((!this.shouldFadeIn() || this.easeInOutAnimation.isFinished()) && !this.cosineAnimation.hasStarted()) {
            this.easeInOutAnimation2.start();
            this.cosineAnimation.start();
            this.cosineAnimation.enableRepeat();
        }
    }

    public boolean shouldFadeIn() {
        return intValue == 1;
    }

    @Override
    public void drawScreenScaled(float f, float f2) {
        super.drawScreenScaled(f, f2);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        for (TextButton textButton : this.textButtonList) {
            textButton.renderAndTestHover(f, f2, true);
        }

        float f3 = this.textButtonList.get(this.textButtonList.size() - 1).getY() + this.textButtonList.get(this.textButtonList.size() - 1).getHeight();
        Gui.drawRect(
            this.textButtonList.get(0).getX() - 20.0F,
            this.getScaledHeight() / 2.0F - 80.0F,
            this.textButtonList.get(0).getX() + this.textButtonList.get(0).getWidth() + 20.0F,
            f3 + 14.0F + (this.text3.isEmpty() ? 0 : 24),
            788529152
        );
        float f4 = f3 + 9.5F;
        if (!this.text3.isEmpty()) {
            CheatBreaker.getInstance().getFontManager().playRegular16.drawCenteredStringWithShadow(this.text3, this.getScaledWidth() / 2.0F, f4, -806424850);
            CheatBreaker.getInstance()
                .getFontManager()
                .playRegular14
                .drawCenteredStringWithShadow(this.text2, this.getScaledWidth() / 2.0F, f4 + 10.0F, -806424850);
        }

        float f5 = this.shouldFadeIn() ? this.easeInOutAnimation.getProgress() : 1.0F;
        if (this.shouldFadeIn()) {
            Gui.drawRect(
                0.0F,
                0.0F,
                this.getScaledWidth(),
                this.getScaledHeight(),
                new Color(0.047F, 0.047F, 0.047F, 1.0F - this.easeInOutAnimation2.getProgress()).getRGB()
            );
        }

        this.render(this.getScaledWidth(), this.getScaledHeight(), f5);
        float f6 = this.getScaledWidth() / 2.0F - 80.0F;
        float f7 = this.getScaledHeight() - 40.0F;
        CheatBreaker.getInstance()
            .getFontManager()
            .ubuntuM14
            .drawCenteredString(
                "finished", this.getScaledWidth() / 2.0F, f7 - 11.0F, new Color(208, 208, 208, (int)(255.0F * (1.0F - Math.min(f5, 0.984F)))).getRGB()
            );
        RenderUtils.drawRoundedRectFilledPrecise(f6, f7, f6 + 160.0F, f7 + 10.0F, 8.0, new Color(218, 66, 83, (int)(255.0F * (1.0F - f5))).getRGB());
    }

    public void render(double d, double d2, float f) {
        float f2 = 27.0F;
        double d3 = d / 2.0 - f2;
        double d4 = d2 / 2.0 - f2 - 35.0F * f;
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glTranslatef((float)d3, (float)d4, 1.0F);
        GL11.glTranslatef(f2, f2, f2);
        GlStateManager.rotate(180.0F * this.cosineAnimation.getProgress(), 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(-f2, -f2, -f2);
        RenderUtils.drawSquareTexture(this.logo255OuterPng, f2, 0.0F, 0.0F);
        GlStateManager.popMatrix();
        RenderUtils.drawSquareTexture(this.logo108InnerPng, f2, (float)d3, (float)d4);
    }

    public EaseInOutAnimation getEaseInOutAnimation2() {
        return this.easeInOutAnimation2;
    }

    @Override
    public void initGui() throws java.io.IOException {
        super.initGui();
        float f = 5.0F;

        for (TextButton textButton : this.textButtonList) {
            textButton.setBounds(this.getScaledWidth() / 2.0F - 50.0F, this.getScaledHeight() / 2.0F + f, 100.0F, 12.0F);
            f += 19.0F;
        }

        intValue++;
    }
}

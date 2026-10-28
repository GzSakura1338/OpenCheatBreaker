package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.AnchorPoint;
import com.cheatbreaker.client.ui.AnchorPointUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.optifine.GlStateManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class ModSettingsScreen extends GuiScreen {
    public AbstractMod abstractMod;
    public ModsScreen modsScreen;

    @Override
    public void mouseReleased(int n, int n2, int n3) {
    }

    public float clampHudX(AbstractMod abstractMod, float f, float[] floats, float f2, boolean flag) {
        float f3 = f;
        int n = flag ? 0 : 3;
        if (f3 + floats[0] < n) {
            f3 = -floats[0] + n;
        } else if (f3 + floats[0] * abstractMod.getHudScale() + f2 > this.width - n) {
            f3 = (int)(this.width - floats[0] * abstractMod.getHudScale() - f2 - n);
        }

        return f3;
    }

    @Override
    public void drawScreen(int n, int n2, float f) {
        this.updateBlurAnimation();
        RenderUtils.drawRoundedRectFilledPrecise(0.0, this.height / 3, this.width, this.height / 3 + 0.5F, 0.0, 1862270976);
        RenderUtils.drawRoundedRectFilledPrecise(0.0, this.height / 3 * 2, this.width, this.height / 3 * 2 + 0.5F, 0.0, 1862270976);
        RenderUtils.drawRoundedRectFilledPrecise(this.width / 3, 0.0, this.width / 3 + 0.5F, this.height, 0.0, 1862270976);
        RenderUtils.drawRoundedRectFilledPrecise(this.width / 3 * 2, 0.0, this.width / 3 * 2 + 0.5F, this.height, 0.0, 1862270976);
        RenderUtils.drawRoundedRectFilledPrecise(
            this.width / 3 + this.width / 6, this.height / 3 * 2, this.width / 3 + this.width / 6 + 0.5F, this.height, 0.0, 1862270976
        );
        float f2 = 1.0F / CheatBreaker.getInstance().getUiManager().getFloatValue() / this.abstractMod.getHudScale();
        ScaledResolution scaledResolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        float[] cellOrigin = AnchorPointUtils.getCellOrigin(n, n2, scaledResolution);
        AnchorPoint anchorPointAt = AnchorPointUtils.getAnchorPointAt(n, n2, scaledResolution);
        if (anchorPointAt != AnchorPoint.MIDDLE_MIDDLE) {
            if (anchorPointAt != AnchorPoint.MIDDLE_BOTTOM_LEFT && anchorPointAt != AnchorPoint.MIDDLE_BOTTOM_RIGHT) {
                Gui.drawRect(cellOrigin[0], cellOrigin[1], cellOrigin[0] + scaledResolution.getScaledWidth() / 3, cellOrigin[1] + scaledResolution.getScaledHeight() / 3, 788529152);
            } else {
                Gui.drawRect(cellOrigin[0], cellOrigin[1], cellOrigin[0] + scaledResolution.getScaledWidth() / 6, cellOrigin[1] + scaledResolution.getScaledHeight() / 3, 788529152);
            }
        }

        int scaledWidth = scaledResolution.getScaledWidth();
        int scaledHeight = scaledResolution.getScaledHeight();
        float[] modCenterInCell = AnchorPointUtils.getModCenterInCell(this.abstractMod, n, n2, scaledResolution);
        if (anchorPointAt != this.abstractMod.getAnchorPoint()) {
            this.abstractMod.setAnchorPoint(anchorPointAt);
            this.abstractMod.setTranslation(0.0F, 0.0F);
        }

        if (!Mouse.isButtonDown(1)) {
            RenderUtils.drawRoundedRectFilledPrecise(2.0, 0.0, 2.5, scaledHeight, 0.0, -15599126);
            RenderUtils.drawRoundedRectFilledPrecise(scaledWidth - 2.5F, 0.0, scaledWidth - 2, scaledHeight, 0.0, -15599126);
            RenderUtils.drawRoundedRectFilledPrecise(0.0, 2.0, scaledWidth, 2.5, 0.0, -15599126);
            RenderUtils.drawRoundedRectFilledPrecise(0.0, scaledHeight - 3.5F, scaledWidth, scaledHeight - 3, 0.0, -15599126);
        }

        float f3 = n - cellOrigin[0] - modCenterInCell[0];
        float f4 = n2 - cellOrigin[1] - modCenterInCell[1];
        if (!Mouse.isButtonDown(1)) {
            float[] hudPosition = this.abstractMod.getHudPosition(scaledResolution, false);
            f3 = this.clampHudX(this.abstractMod, f3, hudPosition, (int)(this.abstractMod.hudWidth * this.abstractMod.getHudScale()), false);
            f4 = this.clampHudY(this.abstractMod, f4, hudPosition, (int)(this.abstractMod.hudHeight * this.abstractMod.getHudScale()), false);
        }

        this.abstractMod.setTranslation(f3, f4);
        GlStateManager.pushMatrix();
        this.abstractMod.applyHudTransformForSize(scaledResolution);
        RenderUtils.drawRoundedRectFilledPrecise(-2.0, -2.0, this.abstractMod.hudWidth + 2.0F, this.abstractMod.hudHeight + 2.0F, 4.0, 551805923);
        GlStateManager.pushMatrix();
        GL11.glScalef(f2, f2, f2);
        if ((Boolean)CheatBreaker.getInstance().getKeybindSettings().showModNameGui.getValue()) {
            float f5 = n2 < 9 ? this.abstractMod.hudHeight / f2 : -CheatBreaker.getInstance().getFontManager().ubuntuM16.getLineHeight() - 4;
            switch (this.abstractMod.getHudAlignment()) {
                case LEFT:
                    float f6 = 0.0F;
                    CheatBreaker.getInstance().getFontManager().ubuntuM16.drawStringWithOutline(this.abstractMod.getName(), f6, f5, -1);
                    break;
                case CENTER:
                    float f7 = this.abstractMod.hudWidth / f2 / 2.0F;
                    CheatBreaker.getInstance().getFontManager().ubuntuM16.drawCenteredStringWithShadow(this.abstractMod.getName(), f7, f5, -1);
                    break;
                case RIGHT:
                    float f8 = this.abstractMod.hudWidth / f2
                        - CheatBreaker.getInstance().getFontManager().ubuntuM16.getStringWidth(this.abstractMod.getName());
                    CheatBreaker.getInstance().getFontManager().ubuntuM16.drawStringWithOutline(this.abstractMod.getName(), f8, f5, -1);
            }
        }

        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }

    @Override
    public void initGui() {
    }

    public float clampHudY(AbstractMod abstractMod, float f, float[] floats, float f2, boolean flag) {
        float f3 = f;
        int n = flag ? 0 : 2;
        if (f3 + floats[1] < n) {
            f3 = -floats[1] + n;
        } else if (f3 + floats[1] * abstractMod.getHudScale() + f2 > this.height - n) {
            f3 = (int)(this.height - floats[1] * abstractMod.getHudScale() - f2 - n);
        }

        return f3;
    }

    @Override
    public void keyTyped(char c, int n) throws java.io.IOException {
        if (n == 1) {
            this.abstractMod.setEnabled(false);
            ModsScreen modsScreen = new ModsScreen();
            this.mc.displayGuiScreen(modsScreen);
            modsScreen.currentPanel = modsScreen.modListPanel;
            modsScreen.currentPanel.flag = false;
            modsScreen.currentPanel.scrollOffset = this.modsScreen.modListPanel.scrollOffset;
            modsScreen.currentPanel.scrollOffsetY = 0;
        }
    }

    public ModSettingsScreen(ModsScreen modsScreen, AbstractMod abstractMod) throws java.io.IOException {
        abstractMod.setEnabled(true);
        this.abstractMod = abstractMod;
        this.modsScreen = modsScreen;
    }

    @Override
    public void updateScreen() {
    }

    @Override
    public void mouseClicked(int n, int n2, int n3) throws java.io.IOException {
        if (n3 == 0) {
            ScaledResolution scaledResolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
            AnchorPoint anchorPointAt = AnchorPointUtils.getAnchorPointAt(n, n2, scaledResolution);
            this.abstractMod.setAnchorPoint(anchorPointAt);
            Minecraft.getMinecraft()
                .getSoundHandler()
                .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            this.abstractMod.setEnabled(true);
            this.abstractMod.setWasRenderHud(true);
            ModsScreen modsScreen = new ModsScreen();
            this.mc.displayGuiScreen(modsScreen);
            modsScreen.currentPanel = modsScreen.modListPanel;
            modsScreen.currentPanel.flag = false;
            modsScreen.currentPanel.scrollOffset = this.modsScreen.modListPanel.scrollOffset;
            modsScreen.currentPanel.scrollOffsetY = 0;
        }
    }
}

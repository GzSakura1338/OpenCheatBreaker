package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.ListScaledResolutionEvent;
import com.cheatbreaker.client.font.CustomFontRenderer;
import com.cheatbreaker.client.mod.AbstractHudMod;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.theme.Theme;
import com.cheatbreaker.client.ui.AnchorPoint;
import com.cheatbreaker.client.ui.AnchorPointUtils;
import com.cheatbreaker.client.ui.ScreenCorner;
import com.cheatbreaker.client.ui.component.Button;
import com.cheatbreaker.client.ui.component.ModListPanel;
import com.cheatbreaker.client.ui.component.Panel;
import com.cheatbreaker.client.ui.component.ProfilesPanel;
import com.cheatbreaker.client.ui.component.SettingComponent;
import com.cheatbreaker.client.ui.component.SettingsPanel;
import com.cheatbreaker.client.ui.component.ThemeManagerPanel;
import com.cheatbreaker.client.util.DeveloperFlags;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.optifine.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class ModsScreen extends GuiScreen {
    public SettingsPanel settingsPanel;
    public Button button;
    public Button eye64Png;
    public DragHandle dragHandle;
    public ResourceLocation cog64Png = new ResourceLocation("client/icons/cog-64.png");
    public List<HudEditorState> undoStack;
    public float dragStartY;
    public ResourceLocation delete64Png = new ResourceLocation("client/icons/delete-64.png");
    public Panel panel;
    public List<HudEditorState> redoStack;
    public List<HudPlacement> selectedMods = new ArrayList<>();
    public List<Panel> panels = new ArrayList<>();
    public int intValue;
    public List<Button> tabButtons = new ArrayList<>();
    public Panel themeManagerPanel;
    public Panel panel2;
    public Panel currentPanel = null;
    public static ModsScreen modsScreen;
    public ProfilesPanel profilesPanel;
    public Panel draggedPanel = null;
    public boolean panelDragging = false;
    public ModListPanel modListPanel;
    public List<AbstractMod> mods;
    public boolean showBackground = true;
    public boolean flag = false;
    public boolean flag2 = false;
    public int intValue2;
    public float fadeProgress = 0.0F;
    public float dragStartX;
    public int intValue3 = 0;
    public Button shirt64Png;
    public int intValue4;
    public static AbstractMod abstractMod;

    public boolean drawModOutline(float f, AbstractMod abstractMod, ScaledResolution scaledResolution, int n, int n2, boolean flag) {
        int n3 = 0;
        if (abstractMod.getAnchorPoint() != null
            && abstractMod.isEnabled()
            && abstractMod != CheatBreaker.getInstance().getModManager().zansMinimapMod
            && (abstractMod.hudOnly || abstractMod.isRenderHud())) {
            boolean flag2 = false;
            GlStateManager.pushMatrix();
            float[] hudPosition = abstractMod.getHudPosition(scaledResolution, true);
            abstractMod.applyHudTransformForSize(scaledResolution);
            boolean flag3 = this.intValue != -1;
            if (flag3) {
                Rectangle rectangle = new Rectangle(
                    (int)(hudPosition[0] * abstractMod.getHudScale() - 2.0F),
                    (int)(hudPosition[1] * abstractMod.getHudScale() - 2.0F),
                    (int)(abstractMod.hudWidth * abstractMod.getHudScale() + 4.0F),
                    (int)(abstractMod.hudHeight * abstractMod.getHudScale() + 4.0F)
                );
                n3 = Math.min(this.intValue, n);
                int min = Math.min(this.intValue4, n2);
                int n4 = Math.max(this.intValue, n) - n3;
                int n5 = Math.max(this.intValue4, n2) - min;
                Rectangle rectangle2 = new Rectangle(n3, min, n4, n5);
                flag3 = rectangle.intersects(rectangle2);
            }

            float[] hudPosition2 = abstractMod.getHudPosition(scaledResolution, true);
            boolean flag4 = abstractMod.hudWidth < 22.0F || abstractMod.hudHeight < 8.0F;
            boolean flag5 = n > hudPosition2[0] * abstractMod.getHudScale()
                && n < (hudPosition2[0] + abstractMod.hudWidth) * abstractMod.getHudScale()
                && n2 > hudPosition2[1] * abstractMod.getHudScale()
                && n2 < (hudPosition2[1] + abstractMod.hudHeight) * abstractMod.getHudScale();
            boolean flag6 = n > hudPosition2[0] * abstractMod.getHudScale()
                && n < (hudPosition2[0] + abstractMod.hudWidth) * abstractMod.getHudScale()
                && n2 > hudPosition2[1] * abstractMod.getHudScale()
                && n2 < (hudPosition2[1] + abstractMod.hudHeight + 10.0F) * abstractMod.getHudScale();
            Theme theme = CheatBreaker.getInstance().getConfigManager().theme;
            if (!this.flag2 && !(this.mc.currentScreen instanceof ProfileNameScreen)) {
                if (this.getPlacement(abstractMod) == null && !flag3) {
                    Gui.drawRectWithInsetBorder(
                        0.0F,
                        0.0F,
                        abstractMod.hudWidth,
                        abstractMod.hudHeight,
                        0.4F,
                        flag5 ? theme.getUnselecHoveringOlColorValue() : theme.getUnselectedOutlineColorValue(),
                        flag5 ? theme.getUnselecHoveringOvColorValue() : theme.getUnselectedOverlayColorValue()
                    );
                } else {
                    Gui.drawRectWithInsetBorder(
                        0.0F,
                        0.0F,
                        abstractMod.hudWidth,
                        abstractMod.hudHeight,
                        0.4F,
                        flag5 ? theme.getSelecHoveringOlColorValue() : theme.getSelectedOutlineColorValue(),
                        flag5 ? theme.getSelecHoveringOvColorValue() : theme.getSelectedOverlayColorValue()
                    );
                }
            }

            if (!this.flag2 && (flag5 || flag4 && flag6)) {
                boolean flag7 = !abstractMod.getSettings().isEmpty()
                    && n >= (hudPosition2[0] + 2.0F) * abstractMod.getHudScale()
                    && n <= (hudPosition2[0] + 10.0F) * abstractMod.getHudScale()
                    && n2 >= (hudPosition2[1] + abstractMod.hudHeight - 8.0F) * abstractMod.getHudScale()
                    && n2 <= (hudPosition2[1] + abstractMod.hudHeight - 2.0F) * abstractMod.getHudScale();
                boolean flag8 = n > (hudPosition2[0] + abstractMod.hudWidth - 10.0F) * abstractMod.getHudScale()
                    && n < (hudPosition2[0] + abstractMod.hudWidth - 2.0F) * abstractMod.getHudScale()
                    && n2 > (hudPosition2[1] + abstractMod.hudHeight - 8.0F) * abstractMod.getHudScale()
                    && n2 < (hudPosition2[1] + abstractMod.hudHeight - 2.0F) * abstractMod.getHudScale();
                boolean flag9 = !abstractMod.getSettings().isEmpty()
                    && n >= (hudPosition2[0] + abstractMod.hudWidth / 2.0F - 10.0F) * abstractMod.getHudScale()
                    && n <= (hudPosition2[0] + abstractMod.hudWidth / 2.0F - 2.0F) * abstractMod.getHudScale()
                    && n2 >= (hudPosition2[1] + abstractMod.hudHeight + 2.0F) * abstractMod.getHudScale()
                    && n2 <= (hudPosition2[1] + abstractMod.hudHeight + 10.0F) * abstractMod.getHudScale();
                boolean flag10 = n > (hudPosition2[0] + abstractMod.hudWidth / 2.0F + 0.0F) * abstractMod.getHudScale()
                    && n < (hudPosition2[0] + abstractMod.hudWidth / 2.0F + 8.0F) * abstractMod.getHudScale()
                    && n2 > (hudPosition2[1] + abstractMod.hudHeight + 2.0F) * abstractMod.getHudScale()
                    && n2 < (hudPosition2[1] + abstractMod.hudHeight + 10.0F) * abstractMod.getHudScale();
                float f2 = flag4 ? abstractMod.hudWidth / 2.0F - 7.0F : 2.0F;
                float f3 = flag4 ? abstractMod.hudWidth / 2.0F + 1.0F : abstractMod.hudWidth - 8.0F;
                float f4 = flag4 ? abstractMod.hudHeight + 2.0F : abstractMod.hudHeight - 7.5F;
                if (!abstractMod.getSettings().isEmpty()) {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, (flag4 ? !flag9 : !flag7) ? 0.6F : 1.0F);
                    RenderUtils.drawSquareTextureViaRenderEngine(this.cog64Png, 3.0F, f2, f4);
                }

                GlStateManager.color(0.8F, 0.2F, 0.2F, (flag4 ? !flag10 : !flag8) ? 0.6F : 1.0F);
                RenderUtils.drawSquareTextureViaRenderEngine(this.delete64Png, 3.0F, f3, f4);
            }

            GlStateManager.pushMatrix();
            float f2 = f / abstractMod.getHudScale();
            GL11.glScalef(f2, f2, f2);
            if (flag) {
                boolean flag7 = this.dragHandle != null && this.dragHandle.abstractHudMod == abstractMod && this.dragHandle.screenCorner == ScreenCorner.LEFT_BOTTOM
                    || n3 == 0
                        && n >= (hudPosition2[0] + abstractMod.hudWidth - 5.0F) * abstractMod.getHudScale()
                        && n <= (hudPosition2[0] + abstractMod.hudWidth + 5.0F) * abstractMod.getHudScale()
                        && n2 >= (hudPosition2[1] - 5.0F) * abstractMod.getHudScale()
                        && n2 <= (hudPosition2[1] + 5.0F) * abstractMod.getHudScale();
                boolean flag8 = this.dragHandle != null && this.dragHandle.abstractHudMod == abstractMod && this.dragHandle.screenCorner == ScreenCorner.RIGHT_TOP
                    || n3 == 0
                        && n >= (hudPosition2[0] - 5.0F) * abstractMod.getHudScale()
                        && n <= (hudPosition2[0] + 5.0F) * abstractMod.getHudScale()
                        && n2 >= (hudPosition2[1] + abstractMod.hudHeight - 5.0F) * abstractMod.getHudScale()
                        && n2 <= (hudPosition2[1] + abstractMod.hudHeight + 5.0F) * abstractMod.getHudScale();
                boolean flag9 = this.dragHandle != null && this.dragHandle.abstractHudMod == abstractMod && this.dragHandle.screenCorner == ScreenCorner.RIGHT_BOTTOM
                    || n3 == 0
                        && n >= (hudPosition2[0] - 5.0F) * abstractMod.getHudScale()
                        && n <= (hudPosition2[0] + 5.0F) * abstractMod.getHudScale()
                        && n2 >= (hudPosition2[1] - 5.0F) * abstractMod.getHudScale()
                        && n2 <= (hudPosition2[1] + 5.0F) * abstractMod.getHudScale();
                boolean flag10 = this.dragHandle != null && this.dragHandle.abstractHudMod == abstractMod && this.dragHandle.screenCorner == ScreenCorner.LEFT_TOP
                    || n3 == 0
                        && n >= (hudPosition2[0] + abstractMod.hudWidth - 5.0F) * abstractMod.getHudScale()
                        && n <= (hudPosition2[0] + abstractMod.hudWidth + 5.0F) * abstractMod.getHudScale()
                        && n2 >= (hudPosition2[1] + abstractMod.hudHeight - 5.0F) * abstractMod.getHudScale()
                        && n2 <= (hudPosition2[1] + abstractMod.hudHeight + 5.0F) * abstractMod.getHudScale();
                GlStateManager.pushMatrix();
                float f3 = 4.0F;
                if (this.intValue == -1 && flag9) {
                    GL11.glTranslatef(0.0F, 0.0F, 0.0F);
                    Gui.drawRect(-f3 / 2.0F, -f3 / 2.0F, f3 / 2.0F, f3 / 2.0F, -16711936);
                } else if (this.intValue == -1 && flag7) {
                    GL11.glTranslatef(abstractMod.hudWidth / f2, 0.0F, 0.0F);
                    Gui.drawRect(-f3 / 2.0F, -f3 / 2.0F, f3 / 2.0F, f3 / 2.0F, -16711936);
                } else if (this.intValue == -1 && flag10) {
                    GL11.glTranslatef(abstractMod.hudWidth / f2, abstractMod.hudHeight / f2, 0.0F);
                    Gui.drawRect(-f3 / 2.0F, -f3 / 2.0F, f3 / 2.0F, f3 / 2.0F, -16711936);
                } else if (this.intValue == -1 && flag8) {
                    GL11.glTranslatef(0.0F, abstractMod.hudHeight / f2, 0.0F);
                    Gui.drawRect(-f3 / 2.0F, -f3 / 2.0F, f3 / 2.0F, f3 / 2.0F, -16711936);
                }

                GlStateManager.popMatrix();
                flag2 = this.intValue == -1 && (flag9 || flag7 || flag8 || flag10);
            }

            boolean flag7 = hudPosition[1] - CheatBreaker.getInstance().getFontManager().ubuntuM16.getLineHeight() - 6.0F < 0.0F;
            float f3 = flag7 ? abstractMod.hudHeight * abstractMod.getHudScale() / f : -CheatBreaker.getInstance().getFontManager().ubuntuM16.getLineHeight() - 4;
            if ((Boolean)CheatBreaker.getInstance().getKeybindSettings().showModNameGui.getValue()) {
                String str = abstractMod.getName() + (abstractMod.hidden ? EnumChatFormatting.GRAY + " (Hidden)" : "");
                switch (abstractMod.getHudAlignment()) {
                    case LEFT:
                        float f4 = 0.0F;
                        CheatBreaker.getInstance().getFontManager().ubuntuM16.drawStringWithOutline(str, f4, f3, -1);
                        break;
                    case CENTER:
                        float f5 = abstractMod.hudWidth * abstractMod.getHudScale() / f / 2.0F;
                        CheatBreaker.getInstance().getFontManager().ubuntuM16.drawCenteredStringWithShadow(str, f5, f3, -1);
                        break;
                    case RIGHT:
                        float f6 = abstractMod.hudWidth * abstractMod.getHudScale() / f - CheatBreaker.getInstance().getFontManager().ubuntuM16.getStringWidth(str);
                        CheatBreaker.getInstance().getFontManager().ubuntuM16.drawStringWithOutline(str, f6, f3, -1);
                }
            }

            GlStateManager.popMatrix();
            GlStateManager.popMatrix();
            return !flag2;
        } else {
            return true;
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        this.mc.entityRenderer.stopUseShader();
    }

    public void drawBackdrop(boolean flag, float f) {
        int n = (int)(this.width / f);
        int n2 = (int)(this.height / f);
        float f2 = this.fadeProgress * 4.0F / 50.0F;
        if (flag) {
            f2 = this.fadeProgress * 8.0F / 255.0F;
        }

        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, f2);
        int n3 = 16777215;
        if (this.showBackground && f2 / 4.0F > 0.0F && f2 / 4.0F < 1.0F) {
            n3 = new Color(1.0F, 1.0F, 1.0F, f2 / 4.0F).getRGB();
        }

        float f3 = flag ? -(this.fadeProgress * 2.0F - 32.0F) / 12.0F - 1.0F : -(40.0F - 4.0F / f2) - 1.0F;
        float f4 = -40.80392F;
        boolean flag2 = flag ? f2 > 1.0F : f2 / 4.0F > 0.0F;
        if (flag2 && this.showBackground) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, f2);
            GL11.glTranslatef(f3, 0.0F, 0.0F);
        } else if (!this.showBackground) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glTranslatef(f4, 0.0F, 0.0F);
        }

        RenderUtils.drawTexture(new ResourceLocation("client/logo_white.png"), n / 2 - 14.0F, n2 / 2 - 47.0F - 22.0F * this.intValue3, 28.0F, 15.0F);
        float f5 = flag ? 1.0F : 0.0F;
        String clientName = CheatBreaker.clientName;
        String[] split = clientName.split("(?<=.)(?=\\p{Lu})");
        if (this.showBackground) {
            if (f2 > f5) {
                CheatBreaker.getInstance()
                    .getFontManager()
                    .playBold18
                    .drawString("| " + split[0].toUpperCase(), n / 2.0F + 18.0F, n2 / 2 - 45.0F - 22.0F * this.intValue3, n3);
                CheatBreaker.getInstance()
                    .getFontManager()
                    .playRegular18
                    .drawString(split[1].toUpperCase(), n / 2.0F + 53.0F, n2 / 2 - 45.0F - 22.0F * this.intValue3, n3);
            }
        } else {
            CheatBreaker.getInstance()
                .getFontManager()
                .playBold18
                .drawString("| " + split[0].toUpperCase(), n / 2.0F + 18.0F, n2 / 2 - 45.0F - 22.0F * this.intValue3, n3);
            CheatBreaker.getInstance()
                .getFontManager()
                .playRegular18
                .drawString(split[1].toUpperCase(), n / 2.0F + 53.0F, n2 / 2 - 45.0F - 22.0F * this.intValue3, n3);
        }

        GlStateManager.popMatrix();
    }

    public AbstractMod getModAt(ScaledResolution scaledResolution, int n, int n2) {
        for (AbstractMod abstractMod : this.mods) {
            if (abstractMod.getAnchorPoint() != null) {
                float[] hudPosition = abstractMod.getHudPosition(scaledResolution, true);
                boolean flag = !abstractMod.getSettings().isEmpty()
                    && n >= hudPosition[0] * abstractMod.getHudScale()
                    && n <= (hudPosition[0] + 10.0F) * abstractMod.getHudScale()
                    && n2 >= (hudPosition[1] + abstractMod.hudHeight - 10.0F) * abstractMod.getHudScale()
                    && n2 <= (hudPosition[1] + abstractMod.hudHeight + 2.0F) * abstractMod.getHudScale();
                boolean flag2 = n > (hudPosition[0] + abstractMod.hudWidth - 10.0F) * abstractMod.getHudScale()
                    && n < (hudPosition[0] + abstractMod.hudWidth + 2.0F) * abstractMod.getHudScale()
                    && n2 > (hudPosition[1] + abstractMod.hudHeight - 10.0F) * abstractMod.getHudScale()
                    && n2 < (hudPosition[1] + abstractMod.hudHeight + 2.0F) * abstractMod.getHudScale();
                boolean flag3 = !abstractMod.getSettings().isEmpty()
                    && n >= (hudPosition[0] + abstractMod.hudWidth / 2.0F - 10.0F) * abstractMod.getHudScale()
                    && n <= (hudPosition[0] + abstractMod.hudWidth / 2.0F - 2.0F) * abstractMod.getHudScale()
                    && n2 >= (hudPosition[1] + abstractMod.hudHeight + 2.0F) * abstractMod.getHudScale()
                    && n2 <= (hudPosition[1] + abstractMod.hudHeight + 12.0F) * abstractMod.getHudScale();
                boolean flag4 = n > (hudPosition[0] + abstractMod.hudWidth / 2.0F + 0.0F) * abstractMod.getHudScale()
                    && n < (hudPosition[0] + abstractMod.hudWidth / 2.0F + 8.0F) * abstractMod.getHudScale()
                    && n2 > (hudPosition[1] + abstractMod.hudHeight + 2.0F) * abstractMod.getHudScale()
                    && n2 < (hudPosition[1] + abstractMod.hudHeight + 12.0F) * abstractMod.getHudScale();
                if (flag2 || flag || flag3 || flag4) {
                    return abstractMod;
                }
            }
        }

        return null;
    }

    public void drawKeyHint(String str, int n, int n2) {
        CustomFontRenderer playRegular14 = CheatBreaker.getInstance().getFontManager().playRegular14;
        float stringWidth = playRegular14.getStringWidth(str);
        RenderUtils.drawRoundedRectFilledPrecise(n, n2, n + stringWidth + 4.0F, n2 + 10, 2.0, -1073741825);
        playRegular14.drawString(str, n + 2, n2, -16777216);
    }

    public float clampY(AbstractMod abstractMod, float f, float[] floats, int n) {
        float f2 = f;
        float f3 = 2.0F;
        if (f2 + floats[1] * abstractMod.getHudScale() < f3) {
            f2 = -floats[1] * abstractMod.getHudScale() + f3;
        } else if (f2 + floats[1] * abstractMod.getHudScale() + n > this.height - f3) {
            f2 = this.height - floats[1] * abstractMod.getHudScale() - n - f3;
        }

        return f2;
    }

    public void releasePanel(int n) {
        if (this.panelDragging) {
            if (this.draggedPanel != null) {
                this.closePanel(this.draggedPanel, true, n);
            }
        } else if (this.currentPanel != null) {
            if (this.draggedPanel != null) {
                this.closePanel(this.draggedPanel, true, n);
            }

            this.closePanel(this.currentPanel, false, n);
        }
    }

    @Override
    public void keyTyped(char c, int n) throws java.io.IOException {
        if (CheatBreaker.getInstance().getModManager().autoTextMod.getActiveHotkey().equals("") && modsScreen.settingsPanel.keybindComponent == null) {
            super.keyTyped(c, n);
        }

        if (n == 1) {
            CheatBreaker.getInstance().configFileManager.saveAll();
        }

        if (n == 30 && isCtrlKeyDown()) {
            for (AbstractMod abstractMod : this.mods) {
                if (abstractMod.isEnabled() && abstractMod.getAnchorPoint() != null && this.getPlacement(abstractMod) == null) {
                    float f = this.intValue - abstractMod.getTranslationX() * abstractMod.getHudScale();
                    float f2 = this.intValue4 - abstractMod.getTranslationY() * abstractMod.getHudScale();
                    this.selectedMods.add(new HudPlacement(abstractMod, f, f2));
                }
            }
        }

        if (n == 45 && isCtrlKeyDown()) {
            for (AbstractMod abstractMod : this.mods) {
                if (this.getPlacement(abstractMod) != null) {
                    abstractMod.setEnabled(false);
                }
            }
        }

        if (n == 19 && isCtrlKeyDown()) {
            for (AbstractMod abstractMod : this.mods) {
                if (this.getPlacement(abstractMod) != null) {
                    ((AbstractHudMod)abstractMod).scale.changeValue(1.0F);
                }
            }
        }

        if (n == 44 && isCtrlKeyDown()) {
            if (!this.undoStack.isEmpty()) {
                int n2 = this.undoStack.size() - 1;
                HudEditorState hudEditorState = this.undoStack.get(this.undoStack.size() - 1);

                for (int i = 0; i < hudEditorState.mods.size(); i++) {
                    AbstractMod abstractMod = hudEditorState.mods.get(i);
                    float f = hudEditorState.translationsX.get(i);
                    float f2 = hudEditorState.translationsY.get(i);
                    AnchorPoint anchorPoint = hudEditorState.anchorPoints.get(i);
                    Float floatValue = (Float)hudEditorState.hudScales.get(i);
                    abstractMod.setAnchorPoint(anchorPoint);
                    abstractMod.setTranslation(f, f2);
                    ((AbstractHudMod)abstractMod).scale.changeValue(floatValue);
                }

                if (this.redoStack.size() > 50) {
                    this.redoStack.remove(0);
                }

                this.redoStack.add(hudEditorState);
                this.undoStack.remove(n2);
            }
        } else if (n == 21 && isCtrlKeyDown()) {
            if (!this.redoStack.isEmpty()) {
                int n2 = this.redoStack.size() - 1;
                HudEditorState hudEditorState = this.redoStack.get(this.redoStack.size() - 1);

                for (int i = 0; i < hudEditorState.mods.size(); i++) {
                    AbstractMod abstractMod = hudEditorState.mods.get(i);
                    float f = hudEditorState.translationsX.get(i);
                    float f2 = hudEditorState.translationsY.get(i);
                    AnchorPoint anchorPoint = hudEditorState.anchorPoints.get(i);
                    Float floatValue = (Float)hudEditorState.hudScales.get(i);
                    abstractMod.setAnchorPoint(anchorPoint);
                    abstractMod.setTranslation(f, f2);
                    ((AbstractHudMod)abstractMod).scale.changeValue(floatValue);
                }

                if (this.redoStack.size() > 50) {
                    this.redoStack.remove(0);
                }

                this.undoStack.add(hudEditorState);
                this.redoStack.remove(n2);
            }
        } else {
            this.intValue2 = 0;

            for (HudPlacement hudPlacement : this.selectedMods) {
                AbstractMod abstractMod = hudPlacement.abstractMod;
                if (abstractMod != null) {
                    float f = 1.0F;
                    if (isCtrlKeyDown() && isShiftKeyDown()) {
                        f = 1.0F / CheatBreaker.getInstance().getUiManager().getFloatValue() / 4.0F;
                    } else if (isCtrlKeyDown()) {
                        f = 4.0F;
                    } else if (isShiftKeyDown()) {
                        f = 1.0F / CheatBreaker.getInstance().getUiManager().getFloatValue() / 2.0F;
                    }

                    switch (n) {
                        case 200:
                            abstractMod.setTranslation(abstractMod.getTranslationX(), abstractMod.getTranslationY() - f);
                        case 201:
                        case 202:
                        case 204:
                        case 206:
                        case 207:
                        default:
                            break;
                        case 203:
                            abstractMod.setTranslation(abstractMod.getTranslationX() - f, abstractMod.getTranslationY());
                            break;
                        case 205:
                            abstractMod.setTranslation(abstractMod.getTranslationX() + f, abstractMod.getTranslationY());
                            break;
                        case 208:
                            abstractMod.setTranslation(abstractMod.getTranslationX(), abstractMod.getTranslationY() + f);
                    }
                }
            }
        }

        for (Panel panel : this.panels) {
            if (panel == this.draggedPanel || panel == this.currentPanel) {
                panel.keyTyped(c, n);
            }
        }
    }

    public void moveModToAnchor(AbstractMod abstractMod, AnchorPoint anchorPoint, int n, int n2, ScaledResolution scaledResolution) {
        if (anchorPoint != abstractMod.getAnchorPoint()) {
            float[] hudPosition = abstractMod.getHudPosition(scaledResolution, true);
            abstractMod.setAnchorPoint(anchorPoint);
            float[] hudPosition2 = abstractMod.getHudPosition(scaledResolution, false);
            abstractMod.setTranslation(hudPosition[0] * abstractMod.getHudScale() - hudPosition2[0] * abstractMod.getHudScale(), hudPosition[1] * abstractMod.getHudScale() - hudPosition2[1] * abstractMod.getHudScale());
        }
    }

    public void closePanel(Panel panel, boolean flag, int n) {
        if (flag) {
            panel.x = panel.intValue;
            this.panelDragging = false;
            this.draggedPanel = null;
        } else {
            panel.x = n / 2 - 185;
            this.currentPanel = null;
            this.draggedPanel = panel;
        }
    }

    @Override
    public void mouseClicked(int n, int n2, int n3) throws java.io.IOException {
        ScaledResolution scaledResolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        if (!this.settingsPanel.search.isMouseOverScaled(n, n2) && this.settingsPanel.search.isFocused()) {
            this.settingsPanel.search.setFocused(false);
        }

        if (!this.modListPanel.searchMods.isMouseOverScaled(n, n2) && this.modListPanel.searchMods.isFocused()) {
            this.modListPanel.searchMods.setFocused(false);
        }

        if (this.shirt64Png.isMouseOver(n, n2)) {
            Minecraft.getMinecraft().displayGuiScreen(new CosmeticsScreen());
            Minecraft.getMinecraft()
                .getSoundHandler()
                .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
        }

        if (this.draggedPanel != null && this.draggedPanel.isMouseOver(n, n2)) {
            this.draggedPanel.mouseClicked(n, n2, n3);
        } else {
            if (CheatBreaker.getInstance().getKeybindSettings().hideWhenClickedOff.getBooleanValue()) {
                this.draggedPanel = null;
                this.currentPanel = null;
            }

            AbstractMod modAt = this.getModAt(scaledResolution, n, n2);
            if ((abstractMod == null || !this.flag) && modAt != null) {
                float[] hudPosition = modAt.getHudPosition(scaledResolution, true);
                boolean flag = modAt.hudWidth < 22.0F || modAt.hudHeight < 8.0F;
                boolean flag2 = !flag
                    && modAt.isEnabled()
                    && !modAt.getSettings().isEmpty()
                    && n >= hudPosition[0] * modAt.getHudScale()
                    && n <= (hudPosition[0] + 10.0F) * modAt.getHudScale()
                    && n2 >= (hudPosition[1] + modAt.hudHeight - 10.0F) * modAt.getHudScale()
                    && n2 <= (hudPosition[1] + modAt.hudHeight + 2.0F) * modAt.getHudScale();
                boolean flag3 = !flag
                    && modAt.isEnabled()
                    && n > (hudPosition[0] + modAt.hudWidth - 10.0F) * modAt.getHudScale()
                    && n < (hudPosition[0] + modAt.hudWidth + 2.0F) * modAt.getHudScale()
                    && n2 > (hudPosition[1] + modAt.hudHeight - 10.0F) * modAt.getHudScale()
                    && n2 < (hudPosition[1] + modAt.hudHeight + 2.0F) * modAt.getHudScale();
                boolean flag4 = flag
                    && modAt.isEnabled()
                    && !modAt.getSettings().isEmpty()
                    && n >= (hudPosition[0] + modAt.hudWidth / 2.0F - 10.0F) * modAt.getHudScale()
                    && n <= (hudPosition[0] + modAt.hudWidth / 2.0F - 2.0F) * modAt.getHudScale()
                    && n2 >= (hudPosition[1] + modAt.hudHeight + 2.0F) * modAt.getHudScale()
                    && n2 <= (hudPosition[1] + modAt.hudHeight + 12.0F) * modAt.getHudScale();
                boolean flag5 = flag
                    && modAt.isEnabled()
                    && n > (hudPosition[0] + modAt.hudWidth / 2.0F + 0.0F) * modAt.getHudScale()
                    && n < (hudPosition[0] + modAt.hudWidth / 2.0F + 8.0F) * modAt.getHudScale()
                    && n2 > (hudPosition[1] + modAt.hudHeight + 2.0F) * modAt.getHudScale()
                    && n2 < (hudPosition[1] + modAt.hudHeight + 12.0F) * modAt.getHudScale();
                if (flag2 || flag4) {
                    Minecraft.getMinecraft()
                        .getSoundHandler()
                        .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
                    this.settingsPanel.showingGlobalSettings = false;
                    this.settingsPanel.abstractMod = modAt;
                    this.currentPanel = this.settingsPanel;
                } else if (flag3 || flag5) {
                    Minecraft.getMinecraft()
                        .getSoundHandler()
                        .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
                    modAt.setEnabled(false);
                }

                if (!flag) {
                    return;
                }
            }

            for (AbstractMod abstractMod : this.mods) {
                if (abstractMod.isEnabled() && abstractMod.getAnchorPoint() != null && abstractMod.isEnabled()) {
                    float[] hudPosition = abstractMod.getHudPosition(scaledResolution, true);
                    boolean flag = n > hudPosition[0] * abstractMod.getHudScale()
                        && n < (hudPosition[0] + abstractMod.hudWidth) * abstractMod.getHudScale()
                        && n2 > hudPosition[1] * abstractMod.getHudScale()
                        && n2 < (hudPosition[1] + abstractMod.hudHeight) * abstractMod.getHudScale();
                    boolean flag2 = this.dragHandle != null && this.dragHandle.abstractHudMod == abstractMod && this.dragHandle.screenCorner == ScreenCorner.LEFT_BOTTOM
                        || !flag
                            && n >= (hudPosition[0] + abstractMod.hudWidth - 5.0F) * abstractMod.getHudScale()
                            && n <= (hudPosition[0] + abstractMod.hudWidth + 5.0F) * abstractMod.getHudScale()
                            && n2 >= (hudPosition[1] - 5.0F) * abstractMod.getHudScale()
                            && n2 <= (hudPosition[1] + 5.0F) * abstractMod.getHudScale();
                    boolean flag3 = this.dragHandle != null && this.dragHandle.abstractHudMod == abstractMod && this.dragHandle.screenCorner == ScreenCorner.RIGHT_TOP
                        || !flag
                            && n >= (hudPosition[0] - 5.0F) * abstractMod.getHudScale()
                            && n <= (hudPosition[0] + 5.0F) * abstractMod.getHudScale()
                            && n2 >= (hudPosition[1] + abstractMod.hudHeight - 5.0F) * abstractMod.getHudScale()
                            && n2 <= (hudPosition[1] + abstractMod.hudHeight + 5.0F) * abstractMod.getHudScale();
                    boolean flag4 = this.dragHandle != null && this.dragHandle.abstractHudMod == abstractMod && this.dragHandle.screenCorner == ScreenCorner.RIGHT_BOTTOM
                        || !flag
                            && n >= (hudPosition[0] - 5.0F) * abstractMod.getHudScale()
                            && n <= (hudPosition[0] + 5.0F) * abstractMod.getHudScale()
                            && n2 >= (hudPosition[1] - 5.0F) * abstractMod.getHudScale()
                            && n2 <= (hudPosition[1] + 5.0F) * abstractMod.getHudScale();
                    boolean flag5 = this.dragHandle != null && this.dragHandle.abstractHudMod == abstractMod && this.dragHandle.screenCorner == ScreenCorner.LEFT_TOP
                        || !flag
                            && n >= (hudPosition[0] + abstractMod.hudWidth - 5.0F) * abstractMod.getHudScale()
                            && n <= (hudPosition[0] + abstractMod.hudWidth + 5.0F) * abstractMod.getHudScale()
                            && n2 >= (hudPosition[1] + abstractMod.hudHeight - 5.0F) * abstractMod.getHudScale()
                            && n2 <= (hudPosition[1] + abstractMod.hudHeight + 5.0F) * abstractMod.getHudScale();
                    if (this.intValue == -1 && (flag2 || flag3 || flag4 || flag5)) {
                        AnchorPoint anchorPoint;
                        ScreenCorner screenCorner;
                        if (flag2) {
                            screenCorner = ScreenCorner.LEFT_BOTTOM;
                            anchorPoint = AnchorPoint.LEFT_BOTTOM;
                        } else if (flag3) {
                            screenCorner = ScreenCorner.RIGHT_TOP;
                            anchorPoint = AnchorPoint.RIGHT_TOP;
                        } else if (flag4) {
                            screenCorner = ScreenCorner.RIGHT_BOTTOM;
                            anchorPoint = AnchorPoint.RIGHT_BOTTOM;
                        } else {
                            screenCorner = ScreenCorner.LEFT_TOP;
                            anchorPoint = AnchorPoint.LEFT_TOP;
                        }

                        if (!this.isClickOnMod(scaledResolution, n, n2)) {
                            if (n3 == 0) {
                                this.undoStack.add(new HudEditorState(this, this.selectedMods));
                                this.dragHandle = new DragHandle(this, (AbstractHudMod)abstractMod, screenCorner, n, n2);
                                this.moveModToAnchor(abstractMod, anchorPoint, n, n2, scaledResolution);
                            } else if (n3 == 1) {
                                AnchorPoint anchorPoint2 = abstractMod.getAnchorPoint();
                                this.moveModToAnchor(abstractMod, anchorPoint, n, n2, scaledResolution);
                                ((AbstractHudMod)abstractMod).scale.changeValue(1.0F);
                                this.moveModToAnchor(abstractMod, anchorPoint2, n, n2, scaledResolution);
                            }

                            return;
                        }
                    }
                }
            }

            if (abstractMod == null) {
                if (this.eye64Png.isMouseOver(n, n2)) {
                    Minecraft.getMinecraft()
                        .getSoundHandler()
                        .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
                    this.flag2 = !this.flag2;
                }

                this.clickTabButtons(n, n2, n3);
                this.drawAnchorGuides(scaledResolution, n, n2, n3);
            }

            for (SettingComponent settingComponent : this.tabButtons) {
                if (settingComponent.isMouseOver(n, n2)) {
                    return;
                }
            }

            boolean isClickOnMod = this.isClickOnMod(scaledResolution, n, n2);
            if (isClickOnMod) {
                return;
            }

            if (!this.selectedMods.isEmpty()) {
                this.selectedMods.clear();
                Minecraft.getMinecraft()
                    .getSoundHandler()
                    .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            }

            this.intValue = n;
            this.intValue4 = n2;
        }

        if (!this.selectedMods.isEmpty()) {
            this.intValue2 = 0;
        }
    }

    public HudPlacement getPlacement(AbstractMod abstractMod) {
        for (HudPlacement hudPlacement : this.selectedMods) {
            if (abstractMod == hudPlacement.abstractMod) {
                return hudPlacement;
            }
        }

        return null;
    }

    public void moveSelectedX(float f) {
        for (HudPlacement hudPlacement : this.selectedMods) {
            hudPlacement.abstractMod.setTranslation(hudPlacement.abstractMod.getTranslationX() + f, hudPlacement.abstractMod.getTranslationY());
        }
    }

    public void setShowBackground(boolean flag) {
        this.showBackground = flag;
    }

    @Override
    public void drawScreen(int n, int n2, float f) throws java.io.IOException {
        super.drawScreen(n, n2, f);
        this.updateBlurAnimation();
        ScaledResolution scaledResolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        Theme theme = CheatBreaker.getInstance().getConfigManager().theme;
        float f2 = 1.0F / CheatBreaker.getInstance().getUiManager().getFloatValue();
        if (abstractMod != null) {
            CheatBreaker.getInstance().getEventBus().handleEvent(new ListScaledResolutionEvent(scaledResolution, this.mods));
            if (!Mouse.isButtonDown(1)) {
                RenderUtils.drawRoundedRectFilledPrecise(2.0, 2.0, 2.5, this.height - 2, 0.0, theme.getHudBorderColorValue());
                RenderUtils.drawRoundedRectFilledPrecise(this.width - 2.5F, 2.0, this.width - 2, this.height - 2, 0.0, theme.getHudBorderColorValue());
                RenderUtils.drawRoundedRectFilledPrecise(2.0, 2.0, this.width - 2, 2.5, 0.0, theme.getHudBorderColorValue());
                RenderUtils.drawRoundedRectFilledPrecise(2.0, this.height - 2.5F, this.width - 2, this.height - 2, 0.0, theme.getHudBorderColorValue());
            }

            try {
                this.mods
                    .sort(
                        (abstractMod2, abstractMod3) -> {
                            if (abstractMod2 == null || abstractMod3 == null) {
                                return 0;
                            }

                            if (abstractMod2 != abstractMod && abstractMod3 != abstractMod && abstractMod2.getAnchorPoint() != null && abstractMod3.getAnchorPoint() != null) {
                                float[] hudPosition = abstractMod2.getHudPosition(scaledResolution, true);
                                float[] hudPosition2 = abstractMod3.getHudPosition(scaledResolution, true);
                                float[] hudPosition3 = abstractMod.getHudPosition(scaledResolution, true);
                                Rectangle rectangle = new Rectangle(
                                    (int)(hudPosition[0] * abstractMod2.getHudScale()),
                                    (int)(hudPosition[1] * abstractMod2.getHudScale()),
                                    (int)(abstractMod2.hudHeight * abstractMod2.getHudScale()),
                                    (int)(abstractMod2.hudWidth * abstractMod2.getHudScale())
                                );
                                Rectangle rectangle2 = new Rectangle(
                                    (int)(hudPosition2[0] * abstractMod3.getHudScale()),
                                    (int)(hudPosition2[1] * abstractMod3.getHudScale()),
                                    (int)(abstractMod3.hudHeight * abstractMod3.getHudScale()),
                                    (int)(abstractMod3.hudWidth * abstractMod3.getHudScale())
                                );
                                Rectangle rectangle3 = new Rectangle(
                                    (int)(hudPosition3[0] * abstractMod.getHudScale()),
                                    (int)(hudPosition3[1] * abstractMod.getHudScale()),
                                    (int)(abstractMod.hudWidth * abstractMod.getHudScale()),
                                    (int)(abstractMod.hudHeight * abstractMod.getHudScale())
                                );

                                try {
                                    return this.distanceSquared(rectangle, rectangle3) > this.distanceSquared(rectangle2, rectangle3) ? -1 : 1;
                                } catch (Exception e) {
                                    return 0;
                                }
                            } else {
                                return 0;
                            }
                        }
                    );
            } catch (Exception e) {
                e.printStackTrace();
            }

            HudPlacement placement = this.getPlacement(abstractMod);
            if (placement != null) {
                this.selectedMods.remove(placement);
                this.selectedMods.add(placement);
            }

            for (HudPlacement hudPlacement : this.selectedMods) {
                this.dragPlacement(hudPlacement, n, n2, scaledResolution);
                if ((Boolean)CheatBreaker.getInstance().getKeybindSettings().snapModsToOtherModsGui.getValue()
                    && this.flag
                    && !Mouse.isButtonDown(1)
                    && hudPlacement.abstractMod == abstractMod) {
                    for (AbstractMod abstractMod : this.mods) {
                        float[] hudPosition = hudPlacement.abstractMod.getHudPosition(scaledResolution, true);
                        float f3 = this.height / 2 - (hudPosition[1] + hudPlacement.abstractMod.hudHeight / 2.0F) * hudPlacement.abstractMod.getHudScale();
                        float f4 = this.width / 2 - (hudPosition[0] + hudPlacement.abstractMod.hudWidth / 2.0F) * hudPlacement.abstractMod.getHudScale();
                        float value = (Float)CheatBreaker.getInstance().getKeybindSettings().snappingStrength.getValue();
                        if (f4 >= -value && f4 <= value) {
                            this.moveSelectedX(f4);
                        }

                        if (f3 >= -value && f3 <= value) {
                            this.moveSelectedY(f3);
                        }

                        if (this.getPlacement(abstractMod) == null
                            && abstractMod.getAnchorPoint() != null
                            && abstractMod.isRenderHud()
                            && abstractMod.isEnabled()
                            && (!abstractMod.getName().contains("Zans") || !CheatBreaker.getInstance().getModManager().zansMinimapMod.getVoxelMap().getMapOptions().hide)) {
                            float[] hudPosition2 = abstractMod.getHudPosition(scaledResolution, true);
                            boolean flag = true;
                            boolean flag2 = true;
                            float f5 = hudPosition2[0] * abstractMod.getHudScale() - hudPosition[0] * hudPlacement.abstractMod.getHudScale();
                            float f6 = (hudPosition2[0] + abstractMod.hudWidth) * abstractMod.getHudScale()
                                - (hudPosition[0] + hudPlacement.abstractMod.hudWidth) * hudPlacement.abstractMod.getHudScale();
                            float f7 = (hudPosition2[0] + abstractMod.hudWidth) * abstractMod.getHudScale() - hudPosition[0] * hudPlacement.abstractMod.getHudScale();
                            float f8 = hudPosition2[0] * abstractMod.getHudScale() - (hudPosition[0] + hudPlacement.abstractMod.hudWidth) * hudPlacement.abstractMod.getHudScale();
                            float f9 = hudPosition2[1] * abstractMod.getHudScale() - hudPosition[1] * hudPlacement.abstractMod.getHudScale();
                            float f10 = (hudPosition2[1] + abstractMod.hudHeight) * abstractMod.getHudScale()
                                - (hudPosition[1] + hudPlacement.abstractMod.hudHeight) * hudPlacement.abstractMod.getHudScale();
                            float f11 = (hudPosition2[1] + abstractMod.hudHeight) * abstractMod.getHudScale() - hudPosition[1] * hudPlacement.abstractMod.getHudScale();
                            float f12 = hudPosition2[1] * abstractMod.getHudScale() - (hudPosition[1] + hudPlacement.abstractMod.hudHeight) * hudPlacement.abstractMod.getHudScale();
                            if (f5 >= -value && f5 <= value) {
                                flag = false;
                                this.moveSelectedX(f5);
                            }

                            if (f6 >= -value && f6 <= value && flag) {
                                flag = false;
                                this.moveSelectedX(f6);
                            }

                            if (f8 >= -value && f8 <= value && flag) {
                                flag = false;
                                this.moveSelectedX(f8);
                            }

                            if (f7 >= -value && f7 <= value && flag) {
                                this.moveSelectedX(f7);
                            }

                            if (f9 >= -value && f9 <= value) {
                                flag2 = false;
                                this.moveSelectedY(f9);
                            }

                            if (f10 >= -value && f10 <= value && flag2) {
                                flag2 = false;
                                this.moveSelectedY(f10);
                            }

                            if (f12 >= -value && f12 <= value && flag2) {
                                flag2 = false;
                                this.moveSelectedY(f12);
                            }

                            if (f11 >= -value && f11 <= value && flag2) {
                                this.moveSelectedY(f11);
                            }
                        }
                    }
                }
            }
        } else if (this.dragHandle != null) {
            float f3 = 1.0F;
            switch (this.dragHandle.screenCorner) {
                case RIGHT_BOTTOM:
                    int n3 = n2 - this.dragHandle.mouseY + (n - this.dragHandle.mouseX);
                    f3 = this.dragHandle.originScale - n3 / 115.0F;
                    break;
                case LEFT_TOP:
                    int n4 = n2 - this.dragHandle.mouseY + (n - this.dragHandle.mouseX);
                    f3 = this.dragHandle.originScale + n4 / 115.0F;
                    break;
                case RIGHT_TOP:
                    int n5 = n - this.dragHandle.mouseX - (n2 - this.dragHandle.mouseY);
                    f3 = this.dragHandle.originScale - n5 / 115.0F;
                    break;
                case LEFT_BOTTOM:
                    int n6 = n - this.dragHandle.mouseX - (n2 - this.dragHandle.mouseY);
                    f3 = this.dragHandle.originScale + n6 / 115.0F;
            }

            if (f3 >= 0.5F && f3 <= 1.5F) {
                this.dragHandle.abstractHudMod.scale.changeValue((float)(Math.round(f3 * 100.0) / 100.0));
            }
        }

        this.drawSelection(scaledResolution);
        boolean flag = true;

        for (AbstractMod abstractMod : this.mods) {
            boolean drawModOutline = this.drawModOutline(f2, abstractMod, scaledResolution, n, n2, flag);
            if (!drawModOutline) {
                flag = false;
            }
        }

        GlStateManager.pushMatrix();
        GL11.glScalef(f2, f2, f2);
        int n3 = (int)(this.width / f2);
        int n4 = (int)(this.height / f2);
        this.eye64Png.render(n, n2, f);
        this.button.render(n, n2, f);
        this.shirt64Png.render(n, n2, f);
        boolean equalsIgnoreCase = CheatBreaker.getInstance().getKeybindSettings().logoAnimation.getStringValue().equalsIgnoreCase("Original");
        this.drawBackdrop(equalsIgnoreCase, f2);

        for (Button button : this.tabButtons) {
            button.render(n, n2, f);
        }

        if (abstractMod == null) {
            GlStateManager.pushMatrix();
            GL11.glEnable(3089);
            RenderUtils.scissorScaled(n3 / 2 - 185, n4 / 2 + 15, n3 / 2 + 185, n4 - 20, scaledResolution.getScaleFactor() * f2, n4);

            for (Panel panel : this.panels) {
                if (panel == this.draggedPanel || panel == this.currentPanel) {
                    panel.render(n, n2, f);
                }
            }

            GL11.glDisable(3089);
            GlStateManager.popMatrix();
        }

        GlStateManager.popMatrix();
        if (this.intValue != -1) {
            if (Mouse.isButtonDown(0)) {
                if (this.intValue != n && this.intValue4 != n2) {
                    drawRectWithInsetBorder(this.intValue, this.intValue4, n, n2, 0.49F, theme.getSelectionOutlineColorValue(), theme.getSelectionColorValue());
                }
            } else {
                this.selectedMods.clear();

                for (AbstractMod abstractMod : this.mods) {
                    if (abstractMod.getAnchorPoint() != null
                        && abstractMod.isEnabled()
                        && (!abstractMod.getName().contains("Zans") || !CheatBreaker.getInstance().getModManager().zansMinimapMod.getVoxelMap().getMapOptions().hide)) {
                        float[] hudPosition = abstractMod.getHudPosition(scaledResolution, true);
                        float f3 = f2 / abstractMod.getHudScale();
                        Rectangle rectangle = new Rectangle(
                            (int)(hudPosition[0] * abstractMod.getHudScale() - 2.0F),
                            (int)(hudPosition[1] * abstractMod.getHudScale() - 2.0F),
                            (int)(abstractMod.hudWidth * abstractMod.getHudScale() + 4.0F),
                            (int)(abstractMod.hudHeight * abstractMod.getHudScale() + 4.0F)
                        );
                        int n5;
                        int n6;
                        if (rectangle.intersects(
                            new Rectangle(
                                n6 = Math.min(this.intValue, n),
                                n5 = Math.min(this.intValue4, n2),
                                Math.max(this.intValue, n) - n6,
                                Math.max(this.intValue4, n2) - n5
                            )
                        )) {
                            float f4 = n - abstractMod.getTranslationX();
                            float f5 = n2 - abstractMod.getTranslationY();
                            this.selectedMods.add(new HudPlacement(abstractMod, f4, f5));
                        }
                    }
                }

                this.intValue = -1;
                this.intValue4 = -1;
            }
        }

        if (this.button.isMouseOver(n, n2) && (this.draggedPanel == null || !this.draggedPanel.isMouseOver(n, n2))) {
            this.drawHelpOverlay(f2);
        }
    }

    public float distanceSquared(Rectangle rectangle, Rectangle rectangle2) {
        float max = Math.max(Math.abs(rectangle.x - rectangle2.x) - rectangle2.width / 2, 0);
        float max2 = Math.max(Math.abs(rectangle.y - rectangle2.y) - rectangle2.height / 2, 0);
        return max * max + max2 * max2;
    }

    @Override
    public void initGui() {
        modsScreen = this;
        this.startBlur();
        Keyboard.enableRepeatEvents(true);
        this.mods = new ArrayList<>();
        this.mods.addAll(CheatBreaker.getInstance().getModManager().abstractModList);
        this.undoStack = new ArrayList<>();
        this.redoStack = new ArrayList<>();
        this.intValue = -1;
        this.intValue4 = -1;
        this.intValue2 = 0;
        abstractMod = null;
        this.draggedPanel = null;
        this.currentPanel = null;
        this.dragHandle = null;
        this.panelDragging = false;
        if (CheatBreaker.getInstance().getKeybindSettings().logoAnimation.getStringValue().equalsIgnoreCase("None")) {
            this.showBackground = false;
        }

        float f = 1.0F / CheatBreaker.getInstance().getUiManager().getFloatValue();
        int n = (int)(this.width / f);
        int n2 = (int)(this.height / f);
        this.panels.clear();
        this.tabButtons.clear();
        List abstractModList = CheatBreaker.getInstance().getModManager().abstractModList;
        List abstractNamedModList = CheatBreaker.getInstance().getModManager().abstractNamedModList;
        List abstractInternalModList = CheatBreaker.getInstance().getModManager().abstractInternalModList;
        ArrayList list = new ArrayList();
        list.addAll(abstractModList);
        list.addAll(abstractNamedModList);
        list.addAll(abstractInternalModList);
        Theme theme = CheatBreaker.getInstance().getConfigManager().theme;
        this.modListPanel = new ModListPanel(abstractModList, f, n / 2 - 565, n2 / 2 + 14, 370, n2 / 2 - 35);
        this.panels.add(this.modListPanel);
        this.panel2 = new ModListPanel(abstractNamedModList, f, n / 2 - 565, n2 / 2 + 14, 370, n2 / 2 - 35);
        this.panels.add(this.panel2);
        this.panel = new ModListPanel(abstractInternalModList, f, n / 2 - 565, n2 / 2 + 14, 370, n2 / 2 - 35);
        this.panels.add(this.panel);
        this.settingsPanel = new SettingsPanel(list, f, n / 2 + 195, n2 / 2 + 14, 370, n2 / 2 - 35);
        this.panels.add(this.settingsPanel);
        this.profilesPanel = new ProfilesPanel(f, n / 2 - 565, n2 / 2 + 14, 370, n2 / 2 - 35);
        this.panels.add(this.profilesPanel);
        this.themeManagerPanel = new ThemeManagerPanel(f, n / 2 - 565, n2 / 2 + 14, 370, n2 / 2 - 35);
        this.panels.add(this.themeManagerPanel);
        this.eye64Png = new Button(null, "eye-64.png", 4, n2 - 32, 28, 28, theme.getNormalHighlightColorValue(), f);
        this.button = new Button(null, "?", 36, n2 - 32, 28, 28, theme.getNormalHighlightColorValue(), f);
        this.shirt64Png = new Button(null, "shirt-64.png", 68, n2 - 32, 28, 28, theme.getNormalHighlightColorValue(), f);
        this.intValue3 = 0;
        if (CheatBreaker.getInstance().getModManager().isStaffModActive()) {
            this.intValue3++;
            this.tabButtons
                .add(new Button(this.panel2, "Staff Mods", n / 2 - 50, n2 / 2 - 19 - 25 * this.intValue3, 100, 20, theme.getLabelHighlightColorValue(), f));
        }

        if (DeveloperFlags.showInternalMods) {
            this.intValue3++;
            this.tabButtons
                .add(new Button(this.panel, "Debug Mods", n / 2 - 50, n2 / 2 - 19 - 25 * this.intValue3, 100, 20, theme.getLabelHighlightColorValue(), f));
        }

        this.tabButtons.add(new Button(this.modListPanel, "Mods", n / 2 - 50, n2 / 2 - 19, 100, 28, theme.getModsHighlightColorValue(), f));
        this.tabButtons
            .add(new Button(this.settingsPanel, "cog-64.png", n / 2 + 54, n2 / 2 - 19, 28, 28, theme.getNormalHighlightColorValue(), f, "Settings"));
        this.tabButtons
            .add(new Button(this.profilesPanel, "profiles-64.png", n / 2 - 82, n2 / 2 - 19, 28, 28, theme.getNormalHighlightColorValue(), f, "Profiles"));
        this.panelDragging = false;
        this.draggedPanel = null;
        this.fadeProgress = 5.0F;
    }

    public void drawHelpOverlay(float f) {
        GlStateManager.pushMatrix();
        GL11.glTranslatef(4.0F, this.height - 245.0F * f, 0.0F);
        GL11.glScalef(f, f, f);
        Gui.drawRect(0.0F, 0.0F, 240.0F, 200.0F, -1895825408);
        CheatBreaker.getInstance().getFontManager().ubuntuM16.drawString("Shortcuts & Movement", 4.0F, 2.0F, -1);
        Gui.drawRect(4.0F, 12.0F, 234.0F, 12.5F, 1342177279);
        int n = 16;
        String str = Minecraft.isRunningOnMac ? "CMD" : "CTRL";
        int n2 = Minecraft.isRunningOnMac ? 2 : 0;
        this.drawKeyHint("Mouse1", 6, n);
        CheatBreaker.getInstance()
            .getFontManager()
            .playRegular14
            .drawString("| " + EnumChatFormatting.AQUA + "HOLD" + EnumChatFormatting.RESET + " Add mods to selected region", 80.0F, n, -1);
        n += 12;
        this.drawKeyHint("Mouse1", 6, n);
        CheatBreaker.getInstance()
            .getFontManager()
            .playRegular14
            .drawString("| " + EnumChatFormatting.AQUA + "HOLD" + EnumChatFormatting.RESET + " Select & drag mods", 80.0F, n, -1);
        n += 12;
        this.drawKeyHint("Mouse2", 6, n);
        CheatBreaker.getInstance()
            .getFontManager()
            .playRegular14
            .drawString("| " + EnumChatFormatting.AQUA + "CLICK" + EnumChatFormatting.RESET + " Reset mod to closest position", 80.0F, n, -1);
        n += 12;
        this.drawKeyHint("Mouse2", 6, n);
        CheatBreaker.getInstance()
            .getFontManager()
            .playRegular14
            .drawString("| " + EnumChatFormatting.AQUA + "HOLD" + EnumChatFormatting.RESET + " Don't lock mods while dragging", 80.0F, n, -1);
        n += 12;
        this.drawKeyHint(str, 6, n);
        CheatBreaker.getInstance().getFontManager().playRegular14.drawString("+", 30 - n2, n, -1);
        this.drawKeyHint("Mouse1", 36 - n2, n);
        CheatBreaker.getInstance().getFontManager().playRegular14.drawString("| Toggle (multiple) mod selection", 80.0F, n, -1);
        n += 12;
        this.drawKeyHint(str, 6, n);
        CheatBreaker.getInstance().getFontManager().playRegular14.drawString("+", 30 - n2, n, -1);
        this.drawKeyHint("A", 36 - n2, n);
        CheatBreaker.getInstance().getFontManager().playRegular14.drawString("| Select all mods", 80.0F, n, -1);
        n += 12;
        this.drawKeyHint(str, 6, n);
        CheatBreaker.getInstance().getFontManager().playRegular14.drawString("+", 30 - n2, n, -1);
        this.drawKeyHint("X", 36 - n2, n);
        CheatBreaker.getInstance().getFontManager().playRegular14.drawString("| Remove selected mod(s) from HUD", 80.0F, n, -1);
        n += 12;
        this.drawKeyHint(str, 6, n);
        CheatBreaker.getInstance().getFontManager().playRegular14.drawString("+", 30 - n2, n, -1);
        this.drawKeyHint("R", 36 - n2, n);
        CheatBreaker.getInstance().getFontManager().playRegular14.drawString("| Reset selected mod(s) scale", 80.0F, n, -1);
        n += 12;
        this.drawKeyHint(str, 6, n);
        CheatBreaker.getInstance().getFontManager().playRegular14.drawString("+", 30 - n2, n, -1);
        this.drawKeyHint("Z", 36 - n2, n);
        CheatBreaker.getInstance().getFontManager().playRegular14.drawString("| Undo mod movements", 80.0F, n, -1);
        n += 12;
        this.drawKeyHint(str, 6, n);
        CheatBreaker.getInstance().getFontManager().playRegular14.drawString("+", 30 - n2, n, -1);
        this.drawKeyHint("Y", 36 - n2, n);
        CheatBreaker.getInstance().getFontManager().playRegular14.drawString("| Redo mod movements", 80.0F, n, -1);
        n += 12;
        this.drawKeyHint("SHIFT", 6, n);
        CheatBreaker.getInstance().getFontManager().playRegular14.drawString("+", 32.0F, n, -1);
        this.drawKeyHint("Arrows", 38, n);
        CheatBreaker.getInstance().getFontManager().playRegular14.drawString("| Smaller mod movements", 80.0F, n, -1);
        n += 12;
        this.drawKeyHint(str, 6, n);
        CheatBreaker.getInstance().getFontManager().playRegular14.drawString("+", 30 - n2, n, -1);
        this.drawKeyHint("Arrows", 36 - n2, n);
        CheatBreaker.getInstance().getFontManager().playRegular14.drawString("| Larger mod movements", 80.0F, n, -1);
        int n3 = 172;
        this.drawKeyHint("Up", 31, n3);
        n3 += 12;
        this.drawKeyHint("Left", 6, n3);
        this.drawKeyHint("Down", 26, n3);
        this.drawKeyHint("Right", 51, n3);
        CheatBreaker.getInstance().getFontManager().playRegular14.drawString("| Move selected mod(s) with precision", 80.0F, n3, -1);
        GlStateManager.popMatrix();
    }

    public void clickTabButtons(int n, int n2, int n3) {
        for (Button button : this.tabButtons) {
            if (n3 == 0 && button.isMouseOver(n, n2) && !this.panelDragging) {
                if (button.panel != null && this.draggedPanel != button.panel && this.currentPanel == null) {
                    Minecraft.getMinecraft()
                        .getSoundHandler()
                        .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
                    this.currentPanel = button.panel;
                } else if (button.panel != null && this.currentPanel == null) {
                    Minecraft.getMinecraft()
                        .getSoundHandler()
                        .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
                    this.panelDragging = true;
                }
            }
        }
    }

    @Override
    public void updateScreen() {
        float f = 1.0F / CheatBreaker.getInstance().getUiManager().getFloatValue();
        int n = (int)(this.width / f);
        this.releasePanel(n);

        for (Panel panel : this.panels) {
            panel.tick();
        }

        if (!this.selectedMods.isEmpty()) {
            boolean isKeyDown = Keyboard.isKeyDown(203);
            boolean isKeyDown2 = Keyboard.isKeyDown(205);
            boolean isKeyDown3 = Keyboard.isKeyDown(200);
            boolean isKeyDown4 = Keyboard.isKeyDown(208);
            if (isKeyDown || isKeyDown2 || isKeyDown3 || isKeyDown4) {
                this.intValue2++;
                if (this.intValue2 > 10) {
                    for (HudPlacement hudPlacement : this.selectedMods) {
                        AbstractMod abstractMod = hudPlacement.abstractMod;
                        if (abstractMod != null) {
                            if (isKeyDown) {
                                abstractMod.setTranslation((int)abstractMod.getTranslationX() - 1, (int)abstractMod.getTranslationY());
                            } else if (isKeyDown2) {
                                abstractMod.setTranslation((int)abstractMod.getTranslationX() + 1, (int)abstractMod.getTranslationY());
                            } else if (isKeyDown3) {
                                abstractMod.setTranslation((int)abstractMod.getTranslationX(), (int)abstractMod.getTranslationY() - 1);
                            } else {
                                abstractMod.setTranslation((int)abstractMod.getTranslationX(), (int)abstractMod.getTranslationY() + 1);
                            }
                        }
                    }
                }
            }
        }

        float f2 = this.fadeProgress > 30.0F ? 2.0F + this.fadeProgress / 2.0F : 4.0F;
        this.fadeProgress = this.fadeProgress + f2 >= 255.0F ? 255.0F : (int)(this.fadeProgress + f2);
    }

    public float clampX(AbstractMod abstractMod, float f, float[] floats, int n) {
        float f2 = f;
        float f3 = 2.0F;
        if (f2 + floats[0] * abstractMod.getHudScale() < f3) {
            f2 = -floats[0] * abstractMod.getHudScale() + f3;
        } else if (f2 + floats[0] * abstractMod.getHudScale() + n > this.width - f3) {
            f2 = this.width - floats[0] * abstractMod.getHudScale() - n - f3;
        }

        return f2;
    }

    public void dragPlacement(HudPlacement hudPlacement, int n, int n2, ScaledResolution scaledResolution) {
        if (hudPlacement.abstractMod.getAnchorPoint() != null
            && hudPlacement.abstractMod.isEnabled()
            && hudPlacement.abstractMod != CheatBreaker.getInstance().getModManager().zansMinimapMod
            && (hudPlacement.abstractMod.hudOnly || hudPlacement.abstractMod.isRenderHud())) {
            float f = n - hudPlacement.x;
            float f2 = n2 - hudPlacement.y;
            if (!this.flag && hudPlacement.abstractMod == abstractMod && (n != this.dragStartX || n2 != this.dragStartY)) {
                if (this.undoStack.size() > 50) {
                    this.undoStack.remove(0);
                }

                this.undoStack.add(new HudEditorState(this, this.selectedMods));
                CheatBreaker.getInstance().getConfigManager().ensureProfileExists();
                this.flag = true;
            }

            float[] hudPosition = hudPlacement.abstractMod.getHudPosition(scaledResolution, false);
            if (!Mouse.isButtonDown(1) && this.flag && hudPlacement.abstractMod == abstractMod) {
                float f3 = f;
                float f4 = f2;
                f = this.clampX(hudPlacement.abstractMod, f, hudPosition, (int)(hudPlacement.abstractMod.hudWidth * hudPlacement.abstractMod.getHudScale()));
                f2 = this.clampY(hudPlacement.abstractMod, f2, hudPosition, (int)(hudPlacement.abstractMod.hudHeight * hudPlacement.abstractMod.getHudScale()));
                float f5 = f3 - f;
                float f6 = f4 - f2;

                for (HudPlacement hudPlacement2 : this.selectedMods) {
                    if (hudPlacement2 != hudPlacement) {
                        hudPosition = hudPlacement2.abstractMod.getHudPosition(scaledResolution, false);
                        float clampX = this.clampX(
                            hudPlacement2.abstractMod, hudPlacement2.abstractMod.getTranslationX() - f5, hudPosition, (int)(hudPlacement2.abstractMod.hudWidth * hudPlacement2.abstractMod.getHudScale())
                        );
                        float clampY = this.clampY(
                            hudPlacement2.abstractMod, hudPlacement2.abstractMod.getTranslationY() - f6, hudPosition, (int)(hudPlacement2.abstractMod.hudHeight * hudPlacement2.abstractMod.getHudScale())
                        );
                        hudPlacement2.abstractMod.setTranslation(clampX, clampY);
                    }
                }
            }

            if (this.flag) {
                hudPlacement.abstractMod.setTranslation(f, f2);
            }
        }
    }

    public void beginDrag(ScaledResolution scaledResolution, int n, int n2) {
        for (HudPlacement hudPlacement : this.selectedMods) {
            if (hudPlacement.abstractMod != null && hudPlacement.abstractMod.getAnchorPoint() != null) {
                hudPlacement.x = n - hudPlacement.abstractMod.getTranslationX();
                hudPlacement.y = n2 - hudPlacement.abstractMod.getTranslationY();
            }
        }
    }

    public boolean isClickOnMod(ScaledResolution scaledResolution, int n, int n2) {
        boolean flag = false;

        for (AbstractMod abstractMod : this.mods) {
            if (abstractMod.getAnchorPoint() != null) {
                float[] hudPosition = abstractMod.getHudPosition(scaledResolution, true);
                boolean flag2 = n > hudPosition[0] * abstractMod.getHudScale()
                    && n < (hudPosition[0] + abstractMod.hudWidth) * abstractMod.getHudScale()
                    && n2 > hudPosition[1] * abstractMod.getHudScale()
                    && n2 < (hudPosition[1] + abstractMod.hudHeight) * abstractMod.getHudScale();
                flag = flag || flag2;
            }
        }

        return flag;
    }

    public void drawSelection(ScaledResolution scaledResolution) {
        Theme theme = CheatBreaker.getInstance().getConfigManager().theme;
        if (!Mouse.isButtonDown(1) && abstractMod != null) {
            for (HudPlacement hudPlacement : this.selectedMods) {
                if (hudPlacement.abstractMod == abstractMod && (Boolean)CheatBreaker.getInstance().getKeybindSettings().snapModsToOtherModsGui.getValue()) {
                    for (AbstractMod abstractMod : this.mods) {
                        float[] hudPosition = hudPlacement.abstractMod.getHudPosition(scaledResolution, true);
                        float f = this.width / 2 - (hudPosition[0] + hudPlacement.abstractMod.hudWidth / 2.0F) * hudPlacement.abstractMod.getHudScale();
                        float f2 = this.height / 2 - (hudPosition[1] + hudPlacement.abstractMod.hudHeight / 2.0F) * hudPlacement.abstractMod.getHudScale();
                        float value = (Float)CheatBreaker.getInstance().getKeybindSettings().snappingStrength.getValue();
                        if (f2 >= -value && f2 <= value) {
                            RenderUtils.drawRoundedRectFilledPrecise(
                                0.0, this.height / 2 - 0.25F, this.width, this.height / 2 + 0.25F, 0.0, theme.getSnapLineColorValue()
                            );
                        }

                        if (f >= -value && f <= value) {
                            RenderUtils.drawRoundedRectFilledPrecise(
                                this.width / 2 - 0.25F, 0.0, this.width / 2 + 0.25F, this.height, 0.0, theme.getSnapLineColorValue()
                            );
                        }

                        if (this.getPlacement(abstractMod) == null
                            && abstractMod.getAnchorPoint() != null
                            && abstractMod.isEnabled()
                            && abstractMod != CheatBreaker.getInstance().getModManager().zansMinimapMod
                            && (abstractMod.hudOnly || abstractMod.isRenderHud())) {
                            float[] hudPosition2 = abstractMod.getHudPosition(scaledResolution, true);
                            boolean flag = false;
                            float f3 = hudPosition2[0] * abstractMod.getHudScale() - hudPosition[0] * hudPlacement.abstractMod.getHudScale();
                            float f4 = (hudPosition2[0] + abstractMod.hudWidth) * abstractMod.getHudScale() - (hudPosition[0] + hudPlacement.abstractMod.hudWidth) * hudPlacement.abstractMod.getHudScale();
                            float f5 = (hudPosition2[0] + abstractMod.hudWidth) * abstractMod.getHudScale() - hudPosition[0] * hudPlacement.abstractMod.getHudScale();
                            float f6 = hudPosition2[0] * abstractMod.getHudScale() - (hudPosition[0] + hudPlacement.abstractMod.hudWidth) * hudPlacement.abstractMod.getHudScale();
                            float f7 = hudPosition2[1] * abstractMod.getHudScale() - hudPosition[1] * hudPlacement.abstractMod.getHudScale();
                            float f8 = (hudPosition2[1] + abstractMod.hudHeight) * abstractMod.getHudScale() - (hudPosition[1] + hudPlacement.abstractMod.hudHeight) * hudPlacement.abstractMod.getHudScale();
                            float f9 = (hudPosition2[1] + abstractMod.hudHeight) * abstractMod.getHudScale() - hudPosition[1] * hudPlacement.abstractMod.getHudScale();
                            float f10 = hudPosition2[1] * abstractMod.getHudScale() - (hudPosition[1] + hudPlacement.abstractMod.hudHeight) * hudPlacement.abstractMod.getHudScale();
                            if (f3 >= -value && f3 <= value) {
                                flag = true;
                                RenderUtils.drawRoundedRectFilledPrecise(
                                    hudPosition2[0] * abstractMod.getHudScale() - 0.5F, 0.0, hudPosition2[0] * abstractMod.getHudScale(), this.height, 0.0, theme.getSnapLineColorValue()
                                );
                            }

                            if (f4 >= -value && f4 <= value) {
                                flag = true;
                                RenderUtils.drawRoundedRectFilledPrecise(
                                    (hudPosition2[0] + abstractMod.hudWidth) * abstractMod.getHudScale(),
                                    0.0,
                                    (hudPosition2[0] + abstractMod.hudWidth) * abstractMod.getHudScale() + 0.5F,
                                    this.height,
                                    0.0,
                                    theme.getSnapLineColorValue()
                                );
                            }

                            if (f6 >= -value && f6 <= value) {
                                flag = true;
                                RenderUtils.drawRoundedRectFilledPrecise(
                                    hudPosition2[0] * abstractMod.getHudScale(), 0.0, hudPosition2[0] * abstractMod.getHudScale() + 0.5F, this.height, 0.0, theme.getSnapLineColorValue()
                                );
                            }

                            if (f5 >= -value && f5 <= value) {
                                flag = true;
                                RenderUtils.drawRoundedRectFilledPrecise(
                                    (hudPosition2[0] + abstractMod.hudWidth) * abstractMod.getHudScale(),
                                    0.0,
                                    (hudPosition2[0] + abstractMod.hudWidth) * abstractMod.getHudScale() + 0.5F,
                                    this.height,
                                    0.0,
                                    theme.getSnapLineColorValue()
                                );
                            }

                            if (f7 >= -value && f7 <= value) {
                                flag = true;
                                RenderUtils.drawRoundedRectFilledPrecise(
                                    0.0, hudPosition2[1] * abstractMod.getHudScale(), this.width, hudPosition2[1] * abstractMod.getHudScale() + 0.5F, 0.0, theme.getSnapLineColorValue()
                                );
                            }

                            if (f8 >= -value && f8 <= value) {
                                flag = true;
                                RenderUtils.drawRoundedRectFilledPrecise(
                                    0.0,
                                    (hudPosition2[1] + abstractMod.hudHeight) * abstractMod.getHudScale(),
                                    this.width,
                                    (hudPosition2[1] + abstractMod.hudHeight) * abstractMod.getHudScale() + 0.5F,
                                    0.0,
                                    theme.getSnapLineColorValue()
                                );
                            }

                            if (f10 >= -value && f10 <= value) {
                                flag = true;
                                RenderUtils.drawRoundedRectFilledPrecise(
                                    0.0, hudPosition2[1] * abstractMod.getHudScale(), this.width, hudPosition2[1] * abstractMod.getHudScale() + 0.5F, 0.0, theme.getSnapLineColorValue()
                                );
                            }

                            if (f9 >= -value && f9 <= value) {
                                flag = true;
                                RenderUtils.drawRoundedRectFilledPrecise(
                                    0.0,
                                    (hudPosition2[1] + abstractMod.hudHeight) * abstractMod.getHudScale() - 0.5F,
                                    this.width,
                                    (hudPosition2[1] + abstractMod.hudHeight) * abstractMod.getHudScale(),
                                    0.0,
                                    theme.getSnapLineColorValue()
                                );
                            }

                            if (flag) {
                                GlStateManager.pushMatrix();
                                abstractMod.applyHudTransformForSize(scaledResolution);
                                Gui.drawRectWithInsetBorder(0.0F, 0.0F, abstractMod.hudWidth, abstractMod.hudHeight, 0.5F, 0, 449387978);
                                GlStateManager.popMatrix();
                            }
                        }
                    }
                }
            }
        }
    }

    public void moveSelectedY(float f) {
        for (HudPlacement hudPlacement : this.selectedMods) {
            hudPlacement.abstractMod.setTranslation(hudPlacement.abstractMod.getTranslationX(), hudPlacement.abstractMod.getTranslationY() + f);
        }
    }

    @Override
    public void handleMouseInput() throws java.io.IOException {
        super.handleMouseInput();
        int eventDWheel = Mouse.getEventDWheel();
        if (this.draggedPanel != null) {
            this.draggedPanel.mouseScrolled(eventDWheel);
        }
    }

    public static float animationStep(float f) {
        float f2 = f / (Minecraft.debugFPS + 1);
        return Math.max(f2, 1.0F);
    }

    public void drawAnchorGuides(ScaledResolution scaledResolution, int n, int n2, int n3) throws java.io.IOException {
        for (AbstractMod abstractMod2 : this.mods) {
            float[] floats;
            if (abstractMod2.getAnchorPoint() != null
                && abstractMod2.isEnabled()
                && (!abstractMod2.getName().contains("Zans") || !CheatBreaker.getInstance().getModManager().zansMinimapMod.getVoxelMap().getMapOptions().hide)
                && n > (floats = abstractMod2.getHudPosition(scaledResolution, true))[0] * abstractMod2.getHudScale()
                && n < (floats[0] + abstractMod2.hudWidth) * abstractMod2.getHudScale()
                && n2 > floats[1] * abstractMod2.getHudScale()
                && n2 < (floats[1] + abstractMod2.hudHeight) * abstractMod2.getHudScale()) {
                boolean flag = !abstractMod2.getSettings().isEmpty()
                    && n >= floats[0] * abstractMod2.getHudScale()
                    && n <= (floats[0] + 10.0F) * abstractMod2.getHudScale()
                    && n2 >= (floats[1] + abstractMod2.hudHeight - 10.0F) * abstractMod2.getHudScale()
                    && n2 <= (floats[1] + abstractMod2.hudHeight + 2.0F) * abstractMod2.getHudScale();
                boolean flag2 = n > (floats[0] + abstractMod2.hudWidth - 10.0F) * abstractMod2.getHudScale()
                    && n < (floats[0] + abstractMod2.hudWidth + 2.0F) * abstractMod2.getHudScale()
                    && n2 > (floats[1] + abstractMod2.hudHeight - 10.0F) * abstractMod2.getHudScale()
                    && n2 < (floats[1] + abstractMod2.hudHeight + 2.0F) * abstractMod2.getHudScale();
                boolean flag3 = !abstractMod2.getSettings().isEmpty()
                    && n >= (floats[0] + abstractMod2.hudWidth / 2.0F - 10.0F) * abstractMod2.getHudScale()
                    && n <= (floats[0] + abstractMod2.hudWidth / 2.0F - 2.0F) * abstractMod2.getHudScale()
                    && n2 >= (floats[1] + abstractMod2.hudHeight + 2.0F) * abstractMod2.getHudScale()
                    && n2 <= (floats[1] + abstractMod2.hudHeight + 12.0F) * abstractMod2.getHudScale();
                boolean flag4 = n > (floats[0] + abstractMod2.hudWidth / 2.0F + 0.0F) * abstractMod2.getHudScale()
                    && n < (floats[0] + abstractMod2.hudWidth / 2.0F + 8.0F) * abstractMod2.getHudScale()
                    && n2 > (floats[1] + abstractMod2.hudHeight + 2.0F) * abstractMod2.getHudScale()
                    && n2 < (floats[1] + abstractMod2.hudHeight + 12.0F) * abstractMod2.getHudScale();
                boolean flag5 = abstractMod2.hudWidth < 22.0F || abstractMod2.hudHeight < 8.0F;
                boolean flag6 = flag5 ? !flag3 && !flag4 : !flag && !flag2;
                if (n3 == 0 && flag6) {
                    boolean flag7 = true;
                    if (this.getPlacement(abstractMod2) != null) {
                        this.removePlacement(abstractMod2);
                        flag7 = false;
                    }

                    float f = n - abstractMod2.getTranslationX() * abstractMod2.getHudScale();
                    float f2 = n2 - abstractMod2.getTranslationY() * abstractMod2.getHudScale();
                    this.dragStartX = n;
                    this.dragStartY = n2;
                    this.flag = false;
                    abstractMod = abstractMod2;
                    if (this.getPlacement(abstractMod2) == null) {
                        if (!isCtrlKeyDown() && flag7) {
                            this.selectedMods.clear();
                        }

                        if (flag7 || !isCtrlKeyDown()) {
                            this.selectedMods.add(new HudPlacement(abstractMod2, f, f2));
                        }
                    }

                    this.beginDrag(scaledResolution, n, n2);
                }

                if (n3 == 0 && (this.draggedPanel == null || !this.draggedPanel.isMouseOver(n, n2))) {
                    if (flag5 ? !flag3 : !flag) {
                        if (flag5 ? flag4 : flag2) {
                            Minecraft.getMinecraft()
                                .getSoundHandler()
                                .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
                            abstractMod2.setEnabled(false);
                        }
                    } else {
                        Minecraft.getMinecraft()
                            .getSoundHandler()
                            .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
                        this.settingsPanel.showingGlobalSettings = false;
                        this.settingsPanel.abstractMod = abstractMod2;
                        this.currentPanel = this.settingsPanel;
                    }
                } else if (n3 == 1) {
                    Minecraft.getMinecraft()
                        .getSoundHandler()
                        .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
                    this.undoStack.add(new HudEditorState(this, this.selectedMods));
                    float[] anchorOffset = AnchorPointUtils.getAnchorOffset(abstractMod2.getAnchorPoint());
                    if (abstractMod2.usesDefaultTranslation() && abstractMod2.getAnchorPoint() == abstractMod2.getDefaultAnchorPoint()) {
                        abstractMod2.setTranslation(abstractMod2.defaultTranslationX, abstractMod2.defaultTranslationY);
                    } else {
                        abstractMod2.setTranslation(anchorOffset[0], anchorOffset[1]);
                    }
                }

                if (abstractMod2 != CheatBreaker.getInstance().getModManager().zansMinimapMod) {
                    break;
                }
            }
        }
    }

    @Override
    public void mouseReleased(int n, int n2, int n3) {
        ScaledResolution scaledResolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        if (this.dragHandle != null && n3 == 0) {
            this.moveModToAnchor(this.dragHandle.abstractHudMod, this.dragHandle.anchorPoint, n, n2, scaledResolution);
            this.dragHandle = null;
        }

        if (abstractMod != null && n3 == 0) {
            if (this.flag) {
                for (HudPlacement hudPlacement : this.selectedMods) {
                    AnchorPoint anchorPointAt = AnchorPointUtils.getAnchorPointAt(n, n2, scaledResolution);
                    if (anchorPointAt != AnchorPoint.MIDDLE_MIDDLE && anchorPointAt != hudPlacement.abstractMod.getAnchorPoint() && this.flag) {
                        this.moveModToAnchor(hudPlacement.abstractMod, anchorPointAt, n, n2, scaledResolution);
                        hudPlacement.x = n - hudPlacement.abstractMod.getTranslationX();
                        hudPlacement.y = n2 - hudPlacement.abstractMod.getTranslationY();
                    }
                }

                if (this.getPlacement(abstractMod) == null) {
                    float[] hudPosition = abstractMod.getHudPosition(scaledResolution, true);
                    float f = n - abstractMod.getTranslationX();
                    float f2 = n2 - abstractMod.getTranslationY();
                    this.selectedMods.add(new HudPlacement(abstractMod, f, f2));
                }

                Minecraft.getMinecraft()
                    .getSoundHandler()
                    .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            }

            abstractMod = null;
        }
    }

    public void removePlacement(AbstractMod abstractMod) {
        this.selectedMods.removeIf(hudPlacement -> hudPlacement.abstractMod == abstractMod);
    }
}

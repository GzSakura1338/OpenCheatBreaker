package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.font.CustomFontRenderer;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.HudDisplayType;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingType;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.screen.ModSettingsScreen;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.optifine.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class ModListItem extends SettingComponent {
    public boolean compact = CheatBreaker.getInstance().getKeybindSettings().modList.getValue().equals("Compact");
    public AbstractMod abstractMod;
    public Panel panel;
    public Button toggleButton;
    public static ModListItem instance;
    public Button optionsButton;
    public Button hudButton;

    public void applyDefaultColors() {
        if (this.abstractMod.isApplyDefaultColorOnEnable()
            && (Boolean)CheatBreaker.getInstance().getKeybindSettings().applyDefaultColorWhenEnablingMods.getValue()) {
            for (Setting setting : this.abstractMod.getSettings()) {
                if (setting.getType() == SettingType.INTEGER
                    && setting.getName().toLowerCase().contains("color")
                    && !setting.getName().toLowerCase().contains("background")
                    && !setting.getName().toLowerCase().contains("border")
                    && !setting.getName().toLowerCase().contains("pressed")
                    && !setting.getName().toLowerCase().contains("amount")
                    && !setting.getName().toLowerCase().contains("line")) {
                    Minecraft.getMinecraft()
                        .getSoundHandler()
                        .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
                    setting.changeValue(CheatBreaker.getInstance().getKeybindSettings().textColor.getColorValue());
                }
            }
        }
    }

    @Override
    public void render(int n, int n2, float f) {
        int n3 = this.abstractMod.isEnabled()
            ? (this.abstractMod.isRestricted() ? -5859328 : this.theme.getEnabledModColorValue())
            : this.theme.getDisabledModColorValue();
        RenderUtils.drawRoundedRectFilledPrecise(
            this.x, this.y, this.x + this.width, this.compact ? this.y + 16 : this.y + this.height, this.theme.getButtonCornerRadiusValue(), n3
        );
        CustomFontRenderer playBold18 = CheatBreaker.getInstance().getFontManager().playBold18;
        GlStateManager.pushMatrix();
        int iconOffsetX = this.abstractMod.getIconOffsetX();
        int iconOffsetY = this.abstractMod.getIconOffsetY();
        if (!this.compact) {
            this.abstractMod.renderPreview(this.x, this.y, this.width, this.height);
            if ((this.abstractMod.getHudDisplayType() == null || this.abstractMod.getHudDisplayType() == HudDisplayType.LABEL)
                && this.abstractMod.isShowLabelInList()) {
                String str = "";
                float f2;
                if (this.abstractMod.getHudDisplayType() == null) {
                    f2 = 2.0F;

                    for (String str2 : this.abstractMod.getName().split(" ")) {
                        String substring = str2.substring(0, 1);
                        str = str + (Objects.equals(str, "") ? substring : substring.toLowerCase());
                    }
                } else {
                    f2 = this.abstractMod.getLabelScale();
                    str = this.abstractMod.getLabel();
                }

                GL11.glScalef(f2, f2, f2);
                float f3 = Minecraft.getMinecraft().fontRendererObj.getStringWidth(str) * f2;
                if (this.abstractMod.getHudDisplayType() == null && !this.compact) {
                    int n4 = (int)((this.x + 1 + this.width / 2 - f3 / 2.0F) / f2);
                    Minecraft.getMinecraft().fontRendererObj.drawString(str, n4, (int)((this.y + this.height / 2 - 32) / f2), -13750738);
                } else {
                    float f4 = (int)((this.x + 1 + this.width / 2 - f3 / 2.0F) / f2);
                    Minecraft.getMinecraft().fontRendererObj.drawShadowedString(str, f4, (int)((this.y + this.height / 2 - 32) / f2), -1);
                }
            } else if (this.abstractMod.getHudDisplayType() == HudDisplayType.ICON) {
                float iconWidth = this.abstractMod.getIconWidth();
                float iconHeight = this.abstractMod.getIconHeight();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                RenderUtils.drawTexture(
                    this.abstractMod.getResourceLocation(),
                    this.x + this.width / 2 - iconWidth / 2.0F + iconOffsetX,
                    this.y + iconOffsetY + this.height / 2 - 26 - iconHeight / 2.0F,
                    iconWidth,
                    iconHeight
                );
            }
        }

        GlStateManager.popMatrix();
        String name = this.abstractMod.getName();
        byte b = 14;
        if (this.compact && name.replaceAll(" ", "").length() >= b) {
            name = name.substring(0, b).trim() + "..";
        }

        if (this.compact) {
            playBold18.drawStringWithOutline(name, this.x + 3.0F, this.y + 3, this.theme.getModNameTextColorValue());
        } else {
            playBold18.drawCenteredStringWithShadow(name, this.x + this.width / 2 - 1.5F, this.y + this.height / 2 - 4, this.theme.getModNameTextColorValue());
        }

        if (!this.compact) {
            this.toggleButton.label = this.abstractMod.isEnabled() ? "Disable" : "Enable";
            this.toggleButton.scrollOffsetY = this.scrollOffsetY;
            this.toggleButton.setCompact(false);
            if (!this.abstractMod.hudOnly) {
                this.toggleButton
                    .setBounds(this.x + this.width / 2 + 8, this.y + this.height - 38, this.width / 2 - 12, this.y + this.height - 24 - (this.y + this.height - 38));
                this.toggleButton.render(n, n2, f);
            }
        }

        this.hudButton.label = this.compact
            ? (this.abstractMod.isEnabled() ? "delete-64.png" : "plus-64.png")
            : (
                this.abstractMod.getAnchorPoint() == null
                    ? (this.abstractMod.isRenderHud() && this.abstractMod.isEnabled() ? "Disable" : "Enable")
                    : (
                        this.abstractMod.isRenderHud() && this.abstractMod.isEnabled()
                            ? "Hide from HUD"
                            : (this.abstractMod.wasRenderHud() && !Keyboard.isKeyDown(42) ? "Return to HUD" : "Add to HUD")
                    )
            );
        this.hudButton.hoverColor = this.abstractMod.isRenderHud() && this.abstractMod.isEnabled()
            ? this.theme.getDisableModHighlightColorValue()
            : this.theme.getEnableModHighlightColorValue();
        this.hudButton
            .setBounds(
                this.x + (this.compact ? 87 : 4),
                this.y + (this.compact ? 3 : this.height - 38),
                this.compact ? 10 : (this.abstractMod.hudOnly ? this.width - 8 : this.width / 2 + 2),
                this.compact ? 10 : this.y + this.height - 24 - (this.y + this.height - 38)
            );
        this.hudButton.scrollOffsetY = this.scrollOffsetY;
        this.hudButton.setCompact(this.compact);
        this.hudButton.render(n, n2, f);
        this.optionsButton
            .setBounds(
                this.x + (this.compact ? 102 : 4), this.y + (this.compact ? 3 : this.height - 20), this.compact ? 10 : this.width - 8, this.compact ? 10 : 16
            );
        this.optionsButton.setCompact(this.compact);
        this.optionsButton.scrollOffsetY = this.scrollOffsetY;
        this.optionsButton.render(n, n2, f);
    }

    @Override
    public void mouseClicked(int n, int n2, int n3) throws java.io.IOException {
        if (this.optionsButton.isMouseOver(n, n2)) {
            Minecraft.getMinecraft()
                .getSoundHandler()
                .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            ModsScreen.modsScreen.settingsPanel.showingGlobalSettings = false;
            ModsScreen.modsScreen.settingsPanel.panel = this.panel;
            ModsScreen.modsScreen.settingsPanel.abstractMod = this.abstractMod;
            ModsScreen.modsScreen.settingsPanel.search.setText("");
            ModsScreen.modsScreen.currentPanel = ModsScreen.modsScreen.settingsPanel;
        } else if (!this.abstractMod.hudOnly && this.toggleButton.isMouseOver(n, n2)) {
            Minecraft.getMinecraft()
                .getSoundHandler()
                .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            this.abstractMod.setEnabled(!this.abstractMod.isEnabled());
            this.toggleButton.label = this.abstractMod.isEnabled() ? "Disable" : "Enable";
            this.toggleButton.hoverColor = this.abstractMod.isEnabled()
                ? this.theme.getDisableModHighlightColorValue()
                : this.theme.getEnableModHighlightColorValue();
            if (this.abstractMod.isEnabled()) {
                this.applyDefaultColors();
                this.abstractMod.setEnabled(true);
            }
        } else if (this.hudButton.clickable && this.hudButton.isMouseOver(n, n2)) {
            Minecraft.getMinecraft()
                .getSoundHandler()
                .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            if (!this.abstractMod.isEnabled()) {
                this.abstractMod.setRenderHud(true);
                this.applyDefaultColors();
                if (this.abstractMod.getAnchorPoint() == null || !Keyboard.isKeyDown(42) && this.abstractMod.wasRenderHud()) {
                    this.abstractMod.setEnabled(true);
                } else {
                    Minecraft.getMinecraft().displayGuiScreen(new ModSettingsScreen(ModsScreen.modsScreen, this.abstractMod));
                }
            } else {
                this.abstractMod.setRenderHud(!this.abstractMod.isRenderHud());
                if (!this.abstractMod.isRenderHud()) {
                    if (this.abstractMod.hudOnly && this.abstractMod.isEnabled()) {
                        this.abstractMod.setEnabled(false);
                    }
                } else {
                    this.applyDefaultColors();
                    if (this.abstractMod.getAnchorPoint() == null || !Keyboard.isKeyDown(42) && this.abstractMod.wasRenderHud()) {
                        this.abstractMod.setEnabled(true);
                    } else {
                        Minecraft.getMinecraft().displayGuiScreen(new ModSettingsScreen(ModsScreen.modsScreen, this.abstractMod));
                    }
                }
            }

            this.hudButton.label = this.abstractMod.getAnchorPoint() == null
                ? (this.abstractMod.isRenderHud() && this.abstractMod.isEnabled() ? "Disable" : "Enable")
                : (
                    this.abstractMod.isRenderHud() && this.abstractMod.isEnabled()
                        ? "Hide from HUD"
                        : (this.abstractMod.wasRenderHud() && !Keyboard.isKeyDown(42) ? "Return to HUD" : "Add to HUD")
                );
            this.hudButton.hoverColor = this.abstractMod.isRenderHud() && this.abstractMod.isEnabled()
                ? this.theme.getDisableModHighlightColorValue()
                : this.theme.getEnableModHighlightColorValue();
        }

        if (this.compact
            && this.isMouseOver(n, n2)
            && !this.hudButton.isMouseOver(n, n2)
            && !this.toggleButton.isMouseOver(n, n2)
            && !this.optionsButton.isMouseOver(n, n2)) {
            Minecraft.getMinecraft()
                .getSoundHandler()
                .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            this.abstractMod.setEnabled(!this.abstractMod.isEnabled());
            if (this.abstractMod.isEnabled()) {
                this.applyDefaultColors();
                this.abstractMod.setEnabled(true);
            }
        }
    }

    public ModListItem(Panel panel, AbstractMod abstractMod, float f) {
        super(f);
        instance = this;
        this.abstractMod = abstractMod;
        this.panel = panel;
        boolean canToggle = abstractMod.canToggle();
        String str = abstractMod.getAnchorPoint() == null ? (abstractMod.isRenderHud() ? "Disable" : "Enable") : (abstractMod.isRenderHud() ? "Hide from HUD" : "Add to HUD");
        String str2 = abstractMod.isEnabled() ? "Disable" : "Enable";
        String str3 = this.compact ? "cog-64.png" : "Options";
        CustomFontRenderer playBold18 = CheatBreaker.getInstance().getFontManager().playBold18;
        CustomFontRenderer playRegular14 = CheatBreaker.getInstance().getFontManager().playRegular14;
        this.toggleButton = new Button(
            playRegular14,
            null,
            str2,
            this.x + this.width / 2 + 2,
            this.y + this.height - 38,
            this.x + this.width - 4,
            this.y + this.height - 24,
            abstractMod.isEnabled() ? this.theme.getDisableModHighlightColorValue() : this.theme.getEnableModHighlightColorValue(),
            f
        );
        this.optionsButton = new Button(
            playBold18,
            null,
            str3,
            this.x + 4,
            this.y + this.height - 20,
            this.x + this.width - 4,
            this.y + this.height - 6,
            this.theme.getNormalHighlightColorValue(),
            f
        );
        this.hudButton = new Button(
            playRegular14,
            null,
            str,
            this.x + 4,
            this.y + this.height - 38,
            this.x + this.width / 2 - 2,
            this.y + this.height - 24,
            abstractMod.isRenderHud() ? this.theme.getDisableModHighlightColorValue() : this.theme.getEnableModHighlightColorValue(),
            f
        );
        this.hudButton.setClickable(canToggle);
    }
}

package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.ScaledResolutionEvent3;
import com.cheatbreaker.client.event.ScaledResolutionEvent7;
import com.cheatbreaker.client.mod.AbstractHudMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import com.cheatbreaker.client.mod.setting.SettingsLevel;
import com.cheatbreaker.client.network.PlayerDataManager;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.AnchorPoint;
import java.awt.Color;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.optifine.GlStateManager;
import org.lwjgl.opengl.GL11;

public class PlayerListMod extends AbstractHudMod {
    public Pattern pattern = Pattern.compile("(?i)§[0-689A-E]");
    public Setting generalOptions;
    public Setting showBackground;
    public Setting showRowBackground;
    public Setting highlightOwnRow;
    public Setting highlightCheatbreakerPlayersRow;
    public Setting usePingColorForRowBackground;
    public Setting rowBackgroundColor;
    public Setting selfRowColor;
    public Setting cheatbreakerRowColor;
    public Setting backgroundColor;
    public Setting nameOptions;
    public Setting nameTextShadow;
    public Setting showCheatbreakerLogo;
    public Setting cheatbreakerLogoType;
    public Setting highlightOwnName;
    public Setting highlightCheatbreakerPlayersName;
    public Setting selfTextColor;
    public Setting cheatbreakerTextColor;
    public Setting pingOptions;
    public Setting showPing;
    public Setting showPingAsNumber;
    public Setting pingTextShadow;
    public Setting dynamicPingNumberColor;
    public Setting staticPingColor;
    public Setting lowPingColor;
    public static ResourceLocation iconsPng = new ResourceLocation("textures/gui/icons.png");
    public Setting mediumPingColor;
    public Setting highPingColor;
    public Setting extremelyHighPingColor;

    public void render(int n, int n2) {
        PlayerDataManager playerDataManager = CheatBreaker.getInstance().getPlayerDataManager();
        GlStateManager.enableBlend();
        FontRenderer fontRendererObj = this.mc.fontRendererObj;
        ScoreObjective objectiveInDisplaySlot = this.mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(0);
        NetHandlerPlayClient sendQueue = this.mc.thePlayer.sendQueue;
        List playerInfoList = sendQueue.playerInfoList;
        int currentServerMaxPlayers = sendQueue.currentServerMaxPlayers;
        int n3 = currentServerMaxPlayers;

        int n4;
        for (n4 = 1; n3 > 20; n3 = (currentServerMaxPlayers + n4 - 1) / n4) {
            n4++;
        }

        int n5 = 300 / n4;
        if (n5 > 150) {
            n5 = 150;
        }

        byte b = (byte)n2;
        if ((Boolean)this.showBackground.getValue()) {
            Gui.drawRect(n, b, (float)n + (n5 * n4 + 1), b + 9 * n3 + 1.0F, this.backgroundColor.getColorValue());
        }

        this.setHudSize(n + n5 * n4 + 1.01F, b + 9 * n3 + 1);

        for (int i = 0; i < currentServerMaxPlayers; i++) {
            int n6 = n + i % n4 * n5 + 1;
            int n7 = b + i / n4 * 9 + 1;
            int colorValue = this.rowBackgroundColor.getColorValue();
            if (i < playerInfoList.size()) {
                GuiPlayerInfo guiPlayerInfo = (GuiPlayerInfo)playerInfoList.get(i);
                if (guiPlayerInfo.responseTime < -1) {
                    colorValue = this.lowPingColor.getColorValue();
                } else if (guiPlayerInfo.responseTime < 150) {
                    colorValue = this.lowPingColor.getColorValue();
                } else if (guiPlayerInfo.responseTime < 300) {
                    colorValue = this.mediumPingColor.getColorValue();
                } else if (guiPlayerInfo.responseTime < 600) {
                    colorValue = this.highPingColor.getColorValue();
                } else if (guiPlayerInfo.responseTime < 1000) {
                    colorValue = this.extremelyHighPingColor.getColorValue();
                } else {
                    colorValue = this.rowBackgroundColor.getColorValue();
                }
            }

            int min = Math.min(this.rowBackgroundColor.getColorValue() >> 24 & 0xFF, colorValue >> 24 & 0xFF);
            int n8 = colorValue >> 16 & 0xFF;
            int n9 = colorValue >> 8 & 0xFF;
            int n10 = colorValue & 0xFF;
            if ((Boolean)this.showRowBackground.getValue() && i >= playerInfoList.size()) {
                Gui.drawRect(
                    n6,
                    n7,
                    n6 + n5 - 1,
                    n7 + 8,
                    (boolean)this.usePingColorForRowBackground.getValue() ? new Color(n8, n9, n10, min).getRGB() : this.rowBackgroundColor.getColorValue()
                );
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(3008);
            if (i < playerInfoList.size()) {
                GuiPlayerInfo guiPlayerInfo = (GuiPlayerInfo)playerInfoList.get(i);
                ScorePlayerTeam playersTeam = this.mc.theWorld.getScoreboard().getPlayersTeam(guiPlayerInfo.name);
                String formatPlayerName = ScorePlayerTeam.formatPlayerName(playersTeam, guiPlayerInfo.name);
                if (this.showRowBackground.getBooleanValue()) {
                    if ((
                            formatPlayerName.contains(this.mc.getSession().getUsername())
                                || formatPlayerName.contains(CheatBreaker.getInstance().getModManager().nickHiderMod.customNameString.getStringValue())
                        )
                        && (Boolean)this.highlightOwnRow.getValue()) {
                        Gui.drawRect(n6, n7, n6 + n5 - 1, n7 + 8, this.selfRowColor.getColorValue());
                    } else if (playerDataManager.containsPlayerName(formatPlayerName, true) && (Boolean)this.highlightCheatbreakerPlayersRow.getValue()) {
                        Gui.drawRect(n6, n7, n6 + n5 - 1, n7 + 8, this.cheatbreakerRowColor.getColorValue());
                    } else {
                        Gui.drawRect(
                            n6,
                            n7,
                            n6 + n5 - 1,
                            n7 + 8,
                            (boolean)this.usePingColorForRowBackground.getValue() ? new Color(n8, n9, n10, min).getRGB() : this.rowBackgroundColor.getColorValue()
                        );
                    }
                }

                int n11 = playerDataManager.findPlayerInText(formatPlayerName) == null ? 0 : playerDataManager.findPlayerInText(formatPlayerName).getIntegerList().get(0);
                int n12 = playerDataManager.findPlayerInText(formatPlayerName) == null ? 0 : playerDataManager.findPlayerInText(formatPlayerName).getIntegerList().get(1);
                float f = (n11 >> 16 & 0xFF) / 255.0F;
                float f2 = (n11 >> 8 & 0xFF) / 255.0F;
                float f3 = (n11 & 0xFF) / 255.0F;
                float f4 = (n12 >> 16 & 0xFF) / 255.0F;
                float f5 = (n12 >> 8 & 0xFF) / 255.0F;
                float f6 = (n12 & 0xFF) / 255.0F;
                boolean flag = (Boolean)this.showCheatbreakerLogo.getValue() && playerDataManager.containsPlayerName(formatPlayerName, true);
                float f7 = this.cheatbreakerLogoType.getBooleanValue() ? 9.7F : 15.7F;
                float f8 = flag ? (formatPlayerName.contains(" ") ? f7 + 1.0F : f7) : 0.0F;
                if (flag) {
                    if (this.nameTextShadow.getBooleanValue()) {
                        if (this.cheatbreakerLogoType.getBooleanValue()) {
                            GL11.glColor4f(f / 4.0F, f2 / 4.0F, f3 / 4.0F, 1.0F);
                            RenderUtils.drawSquareTextureViaRenderEngine(RenderUtils.logoCircle1Png, 3.6F, n6 + 1.5F, n7 + 0.8F);
                        } else {
                            GlStateManager.color(f / 4.0F, f2 / 4.0F, f3 / 4.0F, 1.0F);
                            RenderUtils.drawTexture(RenderUtils.logoWhite1Png, n6 + 2.0F, n7 + 1.2F, 13.0F, 7.0F);
                            GlStateManager.color(f4 / 4.0F, f5 / 4.0F, f6 / 4.0F, 1.0F);
                            RenderUtils.drawTexture(RenderUtils.logoWhite2Png, n6 + 2.0F, n7 + 1.2F, 13.0F, 7.0F);
                        }
                    }

                    if (this.cheatbreakerLogoType.getBooleanValue()) {
                        GlStateManager.color(f, f2, f3, 1.0F);
                        RenderUtils.drawSquareTextureViaRenderEngine(RenderUtils.logoCircle1Png, 3.6F, n6 + 1.0F, n7 + 0.2F);
                    } else {
                        GlStateManager.color(f, f2, f3, 1.0F);
                        RenderUtils.drawTexture(RenderUtils.logoWhite1Png, n6 + 1.0F, n7 + 0.2F, 13.0F, 7.0F);
                        GlStateManager.color(f4, f5, f6, 1.0F);
                        RenderUtils.drawTexture(RenderUtils.logoWhite2Png, n6 + 1.0F, n7 + 0.2F, 13.0F, 7.0F);
                    }
                }

                if ((
                        formatPlayerName.contains(this.mc.getSession().getUsername())
                            || formatPlayerName.contains(CheatBreaker.getInstance().getModManager().nickHiderMod.customNameString.getStringValue())
                    )
                    && (Boolean)this.highlightOwnName.getValue()) {
                    fontRendererObj.drawString(
                        this.normalizeFormatting(formatPlayerName), f8 + n6, n7, this.selfTextColor.getColorValue(), (Boolean)this.nameTextShadow.getValue()
                    );
                } else if (playerDataManager.containsPlayerName(formatPlayerName, true) && (Boolean)this.highlightCheatbreakerPlayersName.getValue()) {
                    fontRendererObj.drawString(
                        EnumChatFormatting.getTextWithoutFormattingCodes(formatPlayerName),
                        f8 + n6,
                        n7,
                        this.cheatbreakerTextColor.getColorValue(),
                        (Boolean)this.nameTextShadow.getValue()
                    );
                } else {
                    fontRendererObj.drawString(formatPlayerName, f8 + n6, n7, 16777215, (Boolean)this.nameTextShadow.getValue());
                }

                if (objectiveInDisplaySlot != null) {
                    int n13 = n6 + fontRendererObj.getStringWidth(formatPlayerName) + 5;
                    int n14 = n6 + n5 - 12 - 5;
                    if (n14 - n13 > 5) {
                        Score valueFromObjective = objectiveInDisplaySlot.getScoreboard().getValueFromObjective(guiPlayerInfo.name, objectiveInDisplaySlot);
                        String str = EnumChatFormatting.YELLOW + "" + valueFromObjective.getScorePoints();
                        fontRendererObj.drawShadowedString(str, n14 - fontRendererObj.getStringWidth(str), n7, 16777215);
                    }
                }

                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                byte b2;
                int n13;
                if (guiPlayerInfo.responseTime < -1) {
                    b2 = 5;
                    n13 = this.lowPingColor.getColorValue();
                } else if (guiPlayerInfo.responseTime < 150) {
                    b2 = 0;
                    n13 = this.lowPingColor.getColorValue();
                } else if (guiPlayerInfo.responseTime < 300) {
                    b2 = 1;
                    n13 = this.mediumPingColor.getColorValue();
                } else if (guiPlayerInfo.responseTime < 600) {
                    b2 = 2;
                    n13 = this.highPingColor.getColorValue();
                } else if (guiPlayerInfo.responseTime < 1000) {
                    b2 = 3;
                    n13 = this.extremelyHighPingColor.getColorValue();
                } else {
                    b2 = 4;
                    n13 = -1;
                }

                if (!(Boolean)this.dynamicPingNumberColor.getValue()) {
                    n13 = this.staticPingColor.getColorValue();
                }

                if (guiPlayerInfo.responseTime != -1 && guiPlayerInfo.responseTime != 0 && (Boolean)this.showPing.getValue()) {
                    this.mc.getTextureManager().bindTexture(iconsPng);
                    if (!(Boolean)this.showPingAsNumber.getValue()) {
                        RenderUtils.drawTexturedRect(n6 + n5 - 12, n7, 0.0F, 176 + b2 * 8, 10, 8);
                    } else {
                        fontRendererObj.drawString(
                            guiPlayerInfo.responseTime + "",
                            (float)n6 + n5 - fontRendererObj.getStringWidth(guiPlayerInfo.responseTime + ""),
                            n7,
                            n13,
                            (Boolean)this.pingTextShadow.getValue()
                        );
                    }
                }
            }
        }

        GlStateManager.disableBlend();
    }

    @Override
    public void renderPreview(int n, int n2, int n3, int n4) {
        Gui.drawRect(n + 20, n2 + n4 / 2 - 44, n + n3 - 20, n2 + n4 / 2 - 7, 1862270976);
        Gui.drawRect(n + 21, n2 + n4 / 2 - 43, n + n3 - 21, n2 + n4 / 2 - 35, 553648127);
        Gui.drawRect(n + 21, n2 + n4 / 2 - 34, n + n3 - 21, n2 + n4 / 2 - 26, 553648127);
        Gui.drawRect(n + 21, n2 + n4 / 2 - 25, n + n3 - 21, n2 + n4 / 2 - 17, 553648127);
        Gui.drawRect(n + 21, n2 + n4 / 2 - 16, n + n3 - 21, n2 + n4 / 2 - 8, 553648127);
        Minecraft.getMinecraft().fontRendererObj.drawShadowedString(Minecraft.getMinecraft().getSession().getUsername(), n + 21, n2 + n4 / 2 - 43, -1);
        Minecraft.getMinecraft().fontRendererObj.drawShadowedString("Steve", n + 21, n2 + n4 / 2 - 34, -1);
        Minecraft.getMinecraft().fontRendererObj.drawShadowedString("Alex", n + 21, n2 + n4 / 2 - 25, -1);
        Minecraft.getMinecraft().fontRendererObj.drawShadowedString("Notch", n + 21, n2 + n4 / 2 - 16, -1);
        Minecraft.getMinecraft()
            .fontRendererObj
            .drawShadowedString(
                EnumChatFormatting.GREEN + "15", n + n3 - 21 - Minecraft.getMinecraft().fontRendererObj.getStringWidth("15"), n2 + n4 / 2 - 43, -1
            );
        Minecraft.getMinecraft()
            .fontRendererObj
            .drawShadowedString(
                EnumChatFormatting.GREEN + "75", n + n3 - 21 - Minecraft.getMinecraft().fontRendererObj.getStringWidth("75"), n2 + n4 / 2 - 34, -1
            );
        Minecraft.getMinecraft()
            .fontRendererObj
            .drawShadowedString(
                EnumChatFormatting.YELLOW + "155", n + n3 - 21 - Minecraft.getMinecraft().fontRendererObj.getStringWidth("155"), n2 + n4 / 2 - 25, -1
            );
        Minecraft.getMinecraft()
            .fontRendererObj
            .drawShadowedString(
                EnumChatFormatting.YELLOW + "250", n + n3 - 21 - Minecraft.getMinecraft().fontRendererObj.getStringWidth("250"), n2 + n4 / 2 - 16, -1
            );
    }

    public void onScaledResolutionEvent7(ScaledResolutionEvent7 scaledResolutionEvent7) {
        if (this.isRenderHud()) {
            GlStateManager.pushMatrix();
            this.applyHudTransformForSize(scaledResolutionEvent7.getScaledResolution());
            this.render(0, 0);
            GlStateManager.popMatrix();
        }
    }

    public void onScaledResolutionEvent3(ScaledResolutionEvent3 scaledResolutionEvent3) {
        if (this.shouldRender()) {
            ScoreObjective objectiveInDisplaySlot = this.mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(0);
            GlStateManager.pushMatrix();
            this.applyHudTransformForSize(scaledResolutionEvent3.getScaledResolution());
            if (this.mc.gameSettings.keyBindPlayerList.getIsKeyPressed()
                && (!this.mc.isIntegratedServerRunning() || this.mc.thePlayer.sendQueue.playerInfoList.size() > 1 || objectiveInDisplaySlot != null)) {
                this.render(0, 0);
            }

            GlStateManager.popMatrix();
        }
    }

    public String normalizeFormatting(String str) {
        return str == null ? null : this.pattern.matcher(str).replaceAll("§r");
    }

    public PlayerListMod() throws java.io.IOException {
        super("Player List");
        this.setAnchorPointAndDefault(AnchorPoint.MIDDLE_TOP);
        this.setTranslationAndDefault(0.0F, 12.0F);
        this.setApplyDefaultColorOnEnable(false);
        this.setUseDefaultTranslation(true);
        this.hudOnly = false;
        this.setRenderHudAndDefault(false);
        this.generalOptions = new SettingGroup(this, "General Options").changeValue(true);
        this.showBackground = new Setting(this, "Show Background").withHierarchyParents(this.generalOptions).changeValue(true);
        this.showRowBackground = new Setting(this, "Show Row Background").withHierarchyParents(this.generalOptions).changeValue(true);
        this.highlightOwnRow = new Setting(this, "Highlight Own Row")
            .withHierarchyParents(this.generalOptions)
            .changeValue(false)
            .withHierarchyParents(this.showRowBackground);
        this.highlightCheatbreakerPlayersRow = new Setting(this, "Highlight CheatBreaker Players' Row")
            .withHierarchyParents(this.generalOptions)
            .changeValue(false)
            .withHierarchyParents(this.showRowBackground);
        this.usePingColorForRowBackground = new Setting(this, "Use Ping Color for Row Background")
            .withHierarchyParents(this.generalOptions)
            .changeValue(false)
            .withHierarchyParents(this.showRowBackground);
        this.rowBackgroundColor = new Setting(this, "Row Background Color")
            .withHierarchyParents(this.generalOptions)
            .changeValue(553648127)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .withHierarchyParents(this.showRowBackground);
        this.selfRowColor = new Setting(this, "Self Row Color")
            .withHierarchyParents(this.generalOptions)
            .changeValue(1084882944)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withHierarchyParents(this.highlightOwnRow);
        this.cheatbreakerRowColor = new Setting(this, "CheatBreaker Row Color")
            .withHierarchyParents(this.generalOptions)
            .changeValue(1088045651)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withHierarchyParents(this.highlightCheatbreakerPlayersRow);
        this.backgroundColor = new Setting(this, "Background Color")
            .withHierarchyParents(this.generalOptions)
            .changeValue(Integer.MIN_VALUE)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .withHierarchyParents(this.showBackground);
        this.nameOptions = new SettingGroup(this, "Name Options").changeValue(false);
        this.nameTextShadow = new Setting(this, "Name Text Shadow").withHierarchyParents(this.nameOptions).changeValue(true);
        this.showCheatbreakerLogo = new Setting(this, "Show CheatBreaker Logo").withHierarchyParents(this.nameOptions).changeValue(true);
        this.cheatbreakerLogoType = new Setting(this, "CheatBreaker Logo Type")
            .withHierarchyParents(this.showCheatbreakerLogo)
            .changeValue(false)
            .setToggleTexts("Text", "Circle");
        this.highlightOwnName = new Setting(this, "Highlight Own Name").withHierarchyParents(this.nameOptions).changeValue(false);
        this.highlightCheatbreakerPlayersName = new Setting(this, "Highlight CheatBreaker Players' Name")
            .withHierarchyParents(this.nameOptions)
            .changeValue(false);
        this.selfTextColor = new Setting(this, "Self Text Color")
            .withHierarchyParents(this.nameOptions)
            .changeValue(-3399134)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withHierarchyParents(this.highlightOwnName);
        this.cheatbreakerTextColor = new Setting(this, "CheatBreaker Text Color")
            .withHierarchyParents(this.nameOptions)
            .changeValue(-301706)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withHierarchyParents(this.highlightCheatbreakerPlayersName);
        this.pingOptions = new SettingGroup(this, "Ping Options").changeValue(false);
        this.showPing = new Setting(this, "Show Ping").withHierarchyParents(this.pingOptions).changeValue(true);
        this.showPingAsNumber = new Setting(this, "Show Ping as Number")
            .withHierarchyParents(this.pingOptions)
            .changeValue(false)
            .withHierarchyParents(this.showPing);
        this.pingTextShadow = new Setting(this, "Ping Text Shadow")
            .withHierarchyParents(this.pingOptions)
            .changeValue(true)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withHierarchyParents(this.showPingAsNumber);
        this.dynamicPingNumberColor = new Setting(this, "Dynamic Ping Number Color")
            .withHierarchyParents(this.pingOptions)
            .changeValue(true)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withHierarchyParents(this.showPingAsNumber);
        this.staticPingColor = new Setting(this, "Static Ping Color")
            .withHierarchyParents(this.pingOptions)
            .changeValue(-171)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .setCustomizationLevel(SettingsLevel.ADVANCED)
            .withInvertedHierarchyParents(this.dynamicPingNumberColor);
        this.lowPingColor = new Setting(this, "Low Ping Color")
            .withHierarchyParents(this.pingOptions)
            .changeValue(-11141291)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .setCustomizationLevel(SettingsLevel.ADVANCED)
            .withHierarchyParents(this.dynamicPingNumberColor);
        this.mediumPingColor = new Setting(this, "Medium Ping Color")
            .withHierarchyParents(this.pingOptions)
            .changeValue(-171)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .setCustomizationLevel(SettingsLevel.ADVANCED)
            .withHierarchyParents(this.dynamicPingNumberColor);
        this.highPingColor = new Setting(this, "High Ping Color")
            .withHierarchyParents(this.pingOptions)
            .changeValue(-43691)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .setCustomizationLevel(SettingsLevel.ADVANCED)
            .withHierarchyParents(this.dynamicPingNumberColor);
        this.extremelyHighPingColor = new Setting(this, "Extremely High Ping Color")
            .withHierarchyParents(this.pingOptions)
            .changeValue(-5636096)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .setCustomizationLevel(SettingsLevel.ADVANCED)
            .withHierarchyParents(this.dynamicPingNumberColor);
        this.setDescription("Customize the player list tab.");
        this.setKeywords("Tab");
        this.setCategories(ModCategory.HUD, ModCategory.SERVER);
        this.setShowLabelInList(false);
        this.addEventHandler(ScaledResolutionEvent7.class, this::onScaledResolutionEvent7);
        this.addEventHandler(ScaledResolutionEvent3.class, this::onScaledResolutionEvent3);
        this.setEnabledByDefault(true);
    }
}

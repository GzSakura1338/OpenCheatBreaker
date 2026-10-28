package com.cheatbreaker.client.mod;

import com.cheatbreaker.client.event.IntEvent1;
import com.cheatbreaker.client.event.IntEvent4;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import com.cheatbreaker.client.mod.setting.SettingsLevel;
import com.cheatbreaker.client.ui.AnchorPoint;
import com.cheatbreaker.client.ui.screen.FriendsScreen;
import com.cheatbreaker.client.ui.screen.ModSettingsScreen;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import com.cheatbreaker.client.ui.screen.ProfileNameScreen;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import org.lwjgl.input.Mouse;

public abstract class AbstractHudMod extends AbstractMod {
    public List<HudVisibilityCondition> visibilityConditions = new ArrayList<HudVisibilityCondition>();
    public Setting toggleHudKeybind;
    public Setting tempToggleHudKeybind;
    public Setting scaleSettings;
    public Setting scale;
    public Setting guiScale;
    public Setting hudConditionSettings;
    public HudVisibilityCondition debugView;
    public HudVisibilityCondition friendMenu;
    public HudVisibilityCondition chat;
    public HudVisibilityCondition containers;
    public HudVisibilityCondition singleplayer;
    public HudVisibilityCondition multiplayer;
    public HudVisibilityCondition creativeMode;
    public HudVisibilityCondition survivalMode;
    public HudVisibilityCondition adventureMode;
    public HudVisibilityCondition hardcoreMode;
    public HudVisibilityCondition fullscreen;
    public HudVisibilityCondition windowed;

    @Override
    public void onKeyPress(IntEvent4 intEvent4) {
        super.onKeyPress(intEvent4);
        if (!this.isEnabled()) {
            return;
        }
        if (intEvent4.getIntValue() == this.toggleHudKeybind.getIntValue() && !this.toggleHudKeybind.hasMouseBind() && !this.hudOnly) {
            this.setRenderHud(!this.isRenderHud());
        }
        if (intEvent4.getIntValue() == this.tempToggleHudKeybind.getIntValue() && !this.tempToggleHudKeybind.hasMouseBind()) {
            this.setHidden(!this.hidden);
        }
    }

    public boolean shouldRender() {
        if (!this.isRenderHud() && this.hudOnly) {
            return false;
        }
        ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        int n = scaledResolution.getScaledWidth();
        int n2 = scaledResolution.getScaledHeight();
        if (this.getAnchorPoint() == AnchorPoint.LEFT_BOTTOM && this.mc.currentScreen instanceof ModsScreen && ((ModsScreen)this.mc.currentScreen).button.isMouseOver(Mouse.getX() * n / this.mc.displayWidth, n2 - Mouse.getY() * n2 / this.mc.displayHeight - 1)) {
            return false;
        }
        if (this.mc.currentScreen instanceof ModsScreen || this.mc.currentScreen instanceof ModSettingsScreen || this.mc.currentScreen instanceof ProfileNameScreen) {
            return true;
        }
        if (this.hidden) {
            return false;
        }
        for (HudVisibilityCondition hudVisibilityCondition : this.visibilityConditions) {
            if (hudVisibilityCondition.showHudIn.getBooleanValue() || !hudVisibilityCondition.condition.getAsBoolean()) continue;
            return false;
        }
        return true;
    }

    public AbstractHudMod(String string) {
        super(string);
        this.setAnchorPointAndDefault(AnchorPoint.RIGHT_TOP);
        this.setTranslationAndDefault(0.0f, 0.0f);
        this.setCategories(ModCategory.HUD);
        this.toggleHudKeybind = new Setting(this, "Toggle HUD Keybind").changeValue(0).setMouseBind(false).setCondition(() -> !this.hudOnly);
        this.tempToggleHudKeybind = new Setting(this, "Temp Toggle HUD Keybind").changeValue(0).setMouseBind(false);
        this.scaleSettings = new SettingGroup(this, "Scale Settings").changeValue(true);
        this.scale = new Setting(this, "Scale").setDescription("Change the scale of the mod.").changeValue(Float.valueOf(1.0f)).setRange(Float.valueOf(0.5f), Float.valueOf(1.5f)).setUnit("x").setCenterLabel("1.0x").setIconNames("letter-t-64", "letter-t-64").setIconScales(6.0f, 8.0f).setIconXOffsets(0.0f, -2.0f).setIconYOffsets(2.0f, -1.5f).withParents(this.scaleSettings);
        this.guiScale = new Setting(this, "GUI Scale").setDescription("Change the scale of the mod.").changeValue("Global").setStringValues("Global", "Default", "Small", "Normal", "Large", "Auto").setCustomizationLevel((Object)SettingsLevel.MEDIUM).withParents(this.scaleSettings);
        this.hudConditionSettings = new SettingGroup(this, "Hud Condition Settings").changeValue(false).setCustomizationLevel((Object)SettingsLevel.ADVANCED);
        this.debugView = new HudVisibilityCondition(this, "Debug View").setCondition(() -> this.mc.gameSettings.showDebugInfo);
        this.friendMenu = new HudVisibilityCondition(this, "Friend Menu").setCondition(() -> this.mc.currentScreen instanceof FriendsScreen);
        this.chat = new HudVisibilityCondition(this, "Chat").setCondition(() -> this.mc.ingameGUI.getChatGUI().getChatOpen());
        this.containers = new HudVisibilityCondition(this, "Containers").setCondition(() -> this.mc.currentScreen instanceof GuiContainer);
        this.singleplayer = new HudVisibilityCondition(this, "Singleplayer").setCondition(() -> this.mc.isSingleplayer());
        this.multiplayer = new HudVisibilityCondition(this, "Multiplayer").setCondition(() -> !this.mc.isSingleplayer());
        this.creativeMode = new HudVisibilityCondition(this, "Creative Mode").setCondition(() -> this.mc.playerController.isInCreativeMode());
        this.survivalMode = new HudVisibilityCondition(this, "Survival Mode").setCondition(() -> this.mc.playerController.isNotCreative() && !this.mc.playerController.getCurrentGameType().isAdventure());
        this.adventureMode = new HudVisibilityCondition(this, "Adventure Mode").setCondition(() -> this.mc.playerController.getCurrentGameType().isAdventure());
        this.hardcoreMode = new HudVisibilityCondition(this, "Hardcore Mode").setCondition(() -> this.mc.thePlayer.worldObj.getWorldInfo().isHardcoreModeEnabled());
        this.fullscreen = new HudVisibilityCondition(this, "Fullscreen").setCondition(() -> this.mc.isFullScreen());
        this.windowed = new HudVisibilityCondition(this, "Windowed").setCondition(() -> !this.mc.isFullScreen());
    }

    @Override
    public void onMousePress(IntEvent1 intEvent1) {
        super.onMousePress(intEvent1);
        if (intEvent1.getEventButton() == 0) {
            return;
        }
        if (intEvent1.getEventButton() == this.toggleHudKeybind.getIntValue() && this.toggleHudKeybind.hasMouseBind() && !this.hudOnly) {
            this.setRenderHud(!this.isRenderHud());
        }
        if (intEvent1.getEventButton() == this.tempToggleHudKeybind.getIntValue() && this.tempToggleHudKeybind.hasMouseBind()) {
            this.setHidden(!this.hidden);
        }
    }

    @Override
    public float getHudScale() {
        float f = 1.0f;
        if (this.guiScale.getValue().equals("Global")) {
            switch (this.cheatBreaker.getKeybindSettings().modScale.getStringValue()) {
                case "Small": {
                    f *= 0.5f / this.cheatBreaker.getUiManager().getFloatValue();
                    break;
                }
                case "Normal": {
                    f /= this.cheatBreaker.getUiManager().getFloatValue();
                    break;
                }
                case "Large": {
                    f *= 1.5f / this.cheatBreaker.getUiManager().getFloatValue();
                    break;
                }
                case "Auto": {
                    f *= 2.0f / this.cheatBreaker.getUiManager().getFloatValue();
                }
            }
        } else {
            switch (this.guiScale.getStringValue()) {
                case "Small": {
                    f *= 0.5f / this.cheatBreaker.getUiManager().getFloatValue();
                    break;
                }
                case "Normal": {
                    f /= this.cheatBreaker.getUiManager().getFloatValue();
                    break;
                }
                case "Large": {
                    f *= 1.5f / this.cheatBreaker.getUiManager().getFloatValue();
                    break;
                }
                case "Auto": {
                    f *= 2.0f / this.cheatBreaker.getUiManager().getFloatValue();
                }
            }
        }
        return this.scale.getFloatValue() * this.cheatBreaker.getKeybindSettings().modScaleMultiplier.getFloatValue() * f;
    }

    public class HudVisibilityCondition {
        public Setting showHudIn;
        public BooleanSupplier condition;
        public /* synthetic */ AbstractHudMod this$0;

        public HudVisibilityCondition(AbstractHudMod abstractHudMod, String string) {
            this.this$0 = abstractHudMod;
            this.showHudIn = new Setting(abstractHudMod, "Show HUD in " + string).changeValue(true).withParents(abstractHudMod.hudConditionSettings).setCustomizationLevel((Object)SettingsLevel.ADVANCED);
            abstractHudMod.visibilityConditions.add(this);
        }

        public HudVisibilityCondition setCondition(BooleanSupplier booleanSupplier) {
            this.condition = booleanSupplier;
            return this;
        }

        public HudVisibilityCondition setShowHudIn(Setting setting) {
            this.showHudIn = setting;
            return this;
        }
    }
}

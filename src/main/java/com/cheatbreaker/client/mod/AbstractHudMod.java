package com.cheatbreaker.client.mod;

import com.cheatbreaker.client.event.IntEvent1;
import com.cheatbreaker.client.event.IntEvent4;
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
   public List<AbstractHudMod.HudVisibilityCondition> visibilityConditions = new ArrayList<>();
   public Setting toggleHudKeybind;
   public Setting tempToggleHudKeybind;
   public Setting scaleSettings;
   public Setting scale;
   public Setting guiScale;
   public Setting hudConditionSettings;
   public AbstractHudMod.HudVisibilityCondition debugView;
   public AbstractHudMod.HudVisibilityCondition friendMenu;
   public AbstractHudMod.HudVisibilityCondition chat;
   public AbstractHudMod.HudVisibilityCondition containers;
   public AbstractHudMod.HudVisibilityCondition singleplayer;
   public AbstractHudMod.HudVisibilityCondition multiplayer;
   public AbstractHudMod.HudVisibilityCondition creativeMode;
   public AbstractHudMod.HudVisibilityCondition survivalMode;
   public AbstractHudMod.HudVisibilityCondition adventureMode;
   public AbstractHudMod.HudVisibilityCondition hardcoreMode;
   public AbstractHudMod.HudVisibilityCondition fullscreen;
   public AbstractHudMod.HudVisibilityCondition windowed;

   public boolean shouldRender() {
      if (!this.isRenderHud() && this.hudOnly) {
         return false;
      }

      ScaledResolution scaledResolution = new ScaledResolution(this.mc);
      int scaledWidth = scaledResolution.getScaledWidth();
      int scaledHeight = scaledResolution.getScaledHeight();
      if (this.getAnchorPoint() == AnchorPoint.LEFT_BOTTOM
         && this.mc.currentScreen instanceof ModsScreen
         && ((ModsScreen)this.mc.currentScreen)
            .button
            .isMouseOver(Mouse.getX() * scaledWidth / this.mc.displayWidth, scaledHeight - Mouse.getY() * scaledHeight / this.mc.displayHeight - 1)) {
         return false;
      }

      if (!(this.mc.currentScreen instanceof ModsScreen)
         && !(this.mc.currentScreen instanceof ModSettingsScreen)
         && !(this.mc.currentScreen instanceof ProfileNameScreen)) {
         if (this.hidden) {
            return false;
         }

         for (AbstractHudMod.HudVisibilityCondition hudVisibilityCondition : this.visibilityConditions) {
            if (!hudVisibilityCondition.showHudIn.getBooleanValue() && hudVisibilityCondition.condition.getAsBoolean()) {
               return false;
            }
         }

         return true;
      } else {
         return true;
      }
   }

   @Override
   public void onKeyPress(IntEvent4 intEvent4) {
      super.onKeyPress(intEvent4);
      if (this.isEnabled()) {
         if (intEvent4.getIntValue() == this.toggleHudKeybind.getIntValue() && !this.toggleHudKeybind.hasMouseBind() && !this.hudOnly) {
            this.setRenderHud(!this.isRenderHud());
         }

         if (intEvent4.getIntValue() == this.tempToggleHudKeybind.getIntValue() && !this.tempToggleHudKeybind.hasMouseBind()) {
            this.setHidden(!this.hidden);
         }
      }
   }

   public AbstractHudMod(String str) {
      super(str);
      this.setAnchorPointAndDefault(AnchorPoint.RIGHT_TOP);
      this.setTranslationAndDefault(0.0F, 0.0F);
      this.setCategories(ModCategory.HUD);
      this.toggleHudKeybind = new Setting(this, "Toggle HUD Keybind").changeValue(0).setMouseBind(false).setCondition(() -> !this.hudOnly);
      this.tempToggleHudKeybind = new Setting(this, "Temp Toggle HUD Keybind").changeValue(0).setMouseBind(false);
      this.scaleSettings = new SettingGroup(this, "Scale Settings").changeValue(true);
      this.scale = new Setting(this, "Scale")
         .setDescription("Change the scale of the mod.")
         .changeValue(1.0F)
         .setRange(0.5F, 1.5F)
         .setUnit("x")
         .setCenterLabel("1.0x")
         .setIconNames("letter-t-64", "letter-t-64")
         .setIconScales(6.0F, 8.0F)
         .setIconXOffsets(0.0F, -2.0F)
         .setIconYOffsets(2.0F, -1.5F)
         .withParents(this.scaleSettings);
      this.guiScale = new Setting(this, "GUI Scale")
         .setDescription("Change the scale of the mod.")
         .changeValue("Global")
         .setStringValues("Global", "Default", "Small", "Normal", "Large", "Auto")
         .setCustomizationLevel(SettingsLevel.MEDIUM)
         .withParents(this.scaleSettings);
      this.hudConditionSettings = new SettingGroup(this, "Hud Condition Settings").changeValue(false).setCustomizationLevel(SettingsLevel.ADVANCED);
      this.debugView = new AbstractHudMod.HudVisibilityCondition("Debug View").setCondition(() -> this.mc.gameSettings.showDebugInfo);
      this.friendMenu = new AbstractHudMod.HudVisibilityCondition("Friend Menu").setCondition(() -> this.mc.currentScreen instanceof FriendsScreen);
      this.chat = new AbstractHudMod.HudVisibilityCondition("Chat").setCondition(() -> this.mc.ingameGUI.getChatGUI().getChatOpen());
      this.containers = new AbstractHudMod.HudVisibilityCondition("Containers").setCondition(() -> this.mc.currentScreen instanceof GuiContainer);
      this.singleplayer = new AbstractHudMod.HudVisibilityCondition("Singleplayer").setCondition(() -> this.mc.isSingleplayer());
      this.multiplayer = new AbstractHudMod.HudVisibilityCondition("Multiplayer").setCondition(() -> !this.mc.isSingleplayer());
      this.creativeMode = new AbstractHudMod.HudVisibilityCondition("Creative Mode").setCondition(() -> this.mc.playerController.isInCreativeMode());
      this.survivalMode = new AbstractHudMod.HudVisibilityCondition("Survival Mode")
         .setCondition(() -> this.mc.playerController.isNotCreative() && !this.mc.playerController.getCurrentGameType().isAdventure());
      this.adventureMode = new AbstractHudMod.HudVisibilityCondition("Adventure Mode")
         .setCondition(() -> this.mc.playerController.getCurrentGameType().isAdventure());
      this.hardcoreMode = new AbstractHudMod.HudVisibilityCondition("Hardcore Mode")
         .setCondition(() -> this.mc.thePlayer.worldObj.getWorldInfo().isHardcoreModeEnabled());
      this.fullscreen = new AbstractHudMod.HudVisibilityCondition("Fullscreen").setCondition(() -> this.mc.isFullScreen());
      this.windowed = new AbstractHudMod.HudVisibilityCondition("Windowed").setCondition(() -> !this.mc.isFullScreen());
   }

   @Override
   public float getHudScale() {
      float f = 1.0F;
      if (this.guiScale.getValue().equals("Global")) {
         switch (this.cheatBreaker.getKeybindSettings().modScale.getStringValue()) {
            case "Small":
               f *= 0.5F / this.cheatBreaker.getUiManager().getFloatValue();
               break;
            case "Normal":
               f /= this.cheatBreaker.getUiManager().getFloatValue();
               break;
            case "Large":
               f *= 1.5F / this.cheatBreaker.getUiManager().getFloatValue();
               break;
            case "Auto":
               f *= 2.0F / this.cheatBreaker.getUiManager().getFloatValue();
         }
      } else {
         switch (this.guiScale.getStringValue()) {
            case "Small":
               f *= 0.5F / this.cheatBreaker.getUiManager().getFloatValue();
               break;
            case "Normal":
               f /= this.cheatBreaker.getUiManager().getFloatValue();
               break;
            case "Large":
               f *= 1.5F / this.cheatBreaker.getUiManager().getFloatValue();
               break;
            case "Auto":
               f *= 2.0F / this.cheatBreaker.getUiManager().getFloatValue();
         }
      }

      return this.scale.getFloatValue() * this.cheatBreaker.getKeybindSettings().modScaleMultiplier.getFloatValue() * f;
   }

   @Override
   public void onMousePress(IntEvent1 intEvent1) {
      super.onMousePress(intEvent1);
      if (this.isEnabled()) {
         if (intEvent1.getEventButton() != 0) {
            if (intEvent1.getEventButton() == this.toggleHudKeybind.getIntValue() && this.toggleHudKeybind.hasMouseBind() && !this.hudOnly) {
               this.setRenderHud(!this.isRenderHud());
            }

            if (intEvent1.getEventButton() == this.tempToggleHudKeybind.getIntValue() && this.tempToggleHudKeybind.hasMouseBind()) {
               this.setHidden(!this.hidden);
            }
         }
      }
   }

   public class HudVisibilityCondition {
      public BooleanSupplier condition;
      public Setting showHudIn;

      public HudVisibilityCondition(String str) {
         this.showHudIn = new Setting(AbstractHudMod.this, "Show HUD in " + str)
            .changeValue(true)
            .withParents(AbstractHudMod.this.hudConditionSettings)
            .setCustomizationLevel(SettingsLevel.ADVANCED);
         AbstractHudMod.this.visibilityConditions.add(this);
      }

      public AbstractHudMod.HudVisibilityCondition setCondition(BooleanSupplier booleanSupplier) {
         this.condition = booleanSupplier;
         return this;
      }

      public AbstractHudMod.HudVisibilityCondition setShowHudIn(Setting setting) {
         this.showHudIn = setting;
         return this;
      }
   }
}

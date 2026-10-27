package com.cheatbreaker.client.mod;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.config.ServerIndex;
import com.cheatbreaker.client.discord.DiscordRpcManager;
import com.cheatbreaker.client.emote.EmoteManager;
import com.cheatbreaker.client.input.ControllerMouseHelper;
import com.cheatbreaker.client.mod.setting.ButtonSetting;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import com.cheatbreaker.client.mod.setting.SettingType;
import com.cheatbreaker.client.mod.setting.SettingsLevel;
import com.cheatbreaker.client.voice.VoiceEngine;
import com.cheatbreaker.client.radio.RadioApi;
import com.cheatbreaker.client.ui.component.ColorSwatch;
import com.cheatbreaker.client.ui.screen.MainMenuStyle;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import com.cheatbreaker.client.util.Logging;
import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.entities.pipe.PipeStatus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Util;
import net.minecraft.util.Util.EnumOS;
import org.apache.commons.lang3.ArrayUtils;

public class KeybindSettings {
   public ModCategory modCategory = ModCategory.ALL;
   public List<Setting> allSettings = new ArrayList<>();
   public List<String[]> featuredServers = new ArrayList<>();
   public boolean firstLaunch = true;
   public List<ColorSwatch> savedColors = new ArrayList<>();
   public List<ColorSwatch> recentColors = new ArrayList<>();
   public Map<Entity, Float> entityDistanceCache = new ConcurrentHashMap<>();
   public Map<TileEntity, Float> tileEntityDistanceCache = new ConcurrentHashMap<>();
   public Setting keybindSettings;
   public Setting dropStackKeybind;
   public Setting emoteWheelKeybind;
   public Setting friendMenuKeybind;
   public Setting copyHoveredTextKeybind;
   public Setting emoteWheelQuickActions;
   public Setting emoteWheelQuickActionKeybinds;
   public Setting favoriteCosmetics;
   public Setting recentCosmetics;
   public Setting audioSettings;
   public Setting microphone;
   public Setting muteCheatbreakerSounds;
   public Setting pinRadioPlayer;
   public Setting radioVolume;
   public Setting microphoneVolume;
   public Setting speakerVolume;
   public Setting inGameMenuSettings;
   public Setting holdServerListButton;
   public Setting holdServerListIn;
   public Setting holdServerListDuration;
   public Setting disableServerListButton;
   public Setting disableServerListIn;
   public Setting disableServerListDuration;
   public Setting holdDisconnectButton;
   public Setting holdDisconnectIn;
   public Setting holdDisconnectDuration;
   public Setting disableDisconnectButton;
   public Setting disableDisconnectIn;
   public Setting disableDisconnectDuration;
   public Setting disconnectConfirmationPrompt;
   public Setting showReconnectOption;
   public Setting showPromptIn;
   public Setting smartDisconnect;
   public Setting waitDuration;
   public Setting fpsBoost;
   public Setting enableFpsBoost;
   public Setting multithreadedChunkLoading;
   public Setting slowChunkLoading;
   public Setting entityDistance;
   public Setting tileEntityDistance;
   public Setting fullbright;
   public Setting lowerAnimationTicks;
   public Setting disableParticlePhysics;
   public Setting entityShadows;
   public Setting hideEndPortals;
   public Setting hideGroundedArrows;
   public Setting hideFoliage;
   public Setting hideStuckArrows;
   public Setting hideMovingArrows;
   public Setting hidePlacedSkulls;
   public Setting fpsLimitingSettings;
   public Setting useCustomFpsLimiter;
   public Setting limitWhenGameIsUnfocused;
   public Setting limitInPauseMenu;
   public Setting limitInContainers;
   public Setting limitInHudEditor;
   public Setting maximumFps;
   public Setting unfocusedFps;
   public Setting pauseMenuFps;
   public Setting containerFps;
   public Setting hudEditorFps;
   public Setting mainMenuFps;
   public Setting teamViewSettings;
   public Setting enableTeamView;
   public Setting showOffScreenMarker;
   public Setting showDistance;
   public Setting generalSettings;
   public Setting aprilFools;
   public Setting theScale;
   public Setting showFriendsMenu;
   public Setting scrollHotbarSlots;
   public Setting centerShortLists;
   public Setting rawMouseInput;
   public Setting inputMode;
   public Setting usedMouse;
   public Setting rawPollingRate;
   public Setting displaySettings;
   public Setting borderlessFullscreen;
   public Setting exitFullscreenWhenUnfocused;
   public Setting hudEditorSettings;
   public Setting hideWhenClickedOff;
   public Setting modList;
   public Setting logoAnimation;
   public Setting customizationLevel;
   public Setting showModNameGui;
   public Setting snapModsToOtherModsGui;
   public Setting snappingStrength;
   public Setting searchSettings;
   public Setting searchForSettingsInMods;
   public Setting bypassCustomizationLevel;
   public Setting renderSettings;
   public Setting showPotionInfoInInventory;
   public Setting showPotionInfoBackground;
   public Setting potionInfoShiftsInventory;
   public Setting ghostLiquidFix;
   public Setting showAchievements;
   public Setting guiBlur;
   public Setting containerBackground;
   public Setting currentMenu;
   public Setting streamerModeSettings;
   public Setting streamerMode;
   public Setting disableModMenuKeybind;
   public Setting notifyWhenPressed;
   public Setting holdModsButton;
   public Setting holdModsDuration;
   public Setting disableModsButton;
   public Setting disableModsDuration;
   public Setting zoomSettings;
   public Setting scrollZoom;
   public Setting useCinematicCamera;
   public Setting resourcePackSettings;
   public Setting widePackMenu;
   public Setting transparentBackground;
   public Setting showPackFolderInformation;
   public Setting showPackFolderIcons;
   public Setting showPackIcons;
   public Setting showPackDescriptions;
   public Setting showSearchBar;
   public Setting sortMethod;
   public Setting smoothScrolling;
   public Setting listBackgroundColor;
   public Setting keybindHandlingSettings;
   public Setting modernKeybindHandling;
   public Setting excludeSneakKeybind;
   public Setting throwKeybind;
   public Setting scalingSettings;
   public Setting excludeSelectedMods;
   public Setting scale;
   public Setting modScale;
   public Setting applyToAll;
   public Setting modScaleMultiplier;
   public Setting hotbarScale;
   public Setting hotbarScaleMultiplier;
   public Setting useLegacyScaling;
   public Setting screenshotSettings;
   public Setting makeShutterSound;
   public Setting copyScreenshotAutomatically;
   public Setting sendMessage;
   public Setting compactOptions;
   public Setting showOpenOption;
   public Setting showCopyOption;
   public Setting showUploadOption;
   public Setting cosmeticSettings;
   public Setting showOptifineCapes;
   public Setting showOptifineHats;
   public Setting showCheatbreakerCapes;
   public Setting showCheatbreakerWings;
   public Setting showCheatbreakerHats;
   public Setting discordRichPresenceSettings;
   public Setting showDiscordRichPresence;
   public Setting showActiveServer;
   public Setting showActiveAccount;
   public Setting defaultColorSettings;
   public Setting applyDefaultColorWhenEnablingMods;
   public Setting excludeSelectedMods2;
   public Setting textColor;
   public Setting backgroundColor;
   public Setting borderColor;
   public Setting defaultSizeSettings;
   public Setting excludeSelectedMods3;
   public Setting showBackground;
   public Setting staticBackgroundWidth;
   public Setting textOverflow;
   public Setting backgroundWidthPadding;
   public Setting cornerRadius;
   public Setting backgroundWidth;
   public Setting backgroundHeight;
   public Setting showBorder;
   public Setting borderThickness;
   public Setting applyToAll2;
   public Setting emoteRadialColor;
   public KeyBinding voiceChat;
   public KeyBinding openMenu;
   public KeyBinding openVoiceMenu;
   public KeyBinding hideNametags;

   public Setting getCurrentMenu() {
      return this.currentMenu;
   }

   public boolean shouldRenderEntity(Entity entity) {
      if (entity != Minecraft.getMinecraft().thePlayer && !(entity instanceof EntityPlayer)) {
         float orDefault = this.entityDistanceCache.getOrDefault(entity, Minecraft.getMinecraft().thePlayer.getDistanceToEntity(entity));
         this.entityDistanceCache.put(entity, orDefault);
         return !(orDefault > this.entityDistance.getFloatValue() * 256.0F);
      } else {
         return true;
      }
   }

   public MainMenuStyle getMainMenuStyle() {
      return MainMenuStyle.values()[this.currentMenu.getIntValue()];
   }

   public boolean isColorSaved(int n) {
      for (ColorSwatch colorSwatch : this.savedColors) {
         if (colorSwatch.color == n) {
            return true;
         }
      }

      return false;
   }

   public boolean shouldRenderTileEntity(TileEntity tileEntity) {
      BlockPos pos = tileEntity.getPos();
      float orDefault = this.tileEntityDistanceCache
         .getOrDefault(tileEntity, (float)Minecraft.getMinecraft().thePlayer.getDistance(pos.getX(), pos.getY(), pos.getZ()));
      this.tileEntityDistanceCache.put(tileEntity, orDefault);
      return !(orDefault > this.tileEntityDistance.getFloatValue() * 256.0F);
   }

   public void copyValueIfSameName(Setting setting, Setting setting2) {
      if (Objects.equals(setting.getName(), setting2.getName())) {
         setting.changeValue(setting2.getValue());
      }
   }

   public boolean shouldRenderSpawnerMob(Entity entity, double d, double d2, double d3) {
      if (entity == null) {
         return true;
      }

      float orDefault = this.entityDistanceCache.getOrDefault(entity, (float)Minecraft.getMinecraft().thePlayer.getDistance(d, d2, d3));
      this.entityDistanceCache.put(entity, orDefault);
      return !(orDefault > this.entityDistance.getFloatValue() * 256.0F);
   }

   public KeybindSettings() {
      String[] stringArray = CheatBreaker.getInstance().getAudioManager().getStringArray();
      StringBuilder sb = new StringBuilder();

      for (String str : stringArray) {
         sb.append("\n- ").append(str);
      }

      this.keybindSettings = new SettingGroup(this.allSettings, "Keybind Settings").changeValue(true);
      this.dropStackKeybind = new Setting(this.allSettings, "Drop Stack Keybind").changeValue(29).setMouseBind(false).withParents(this.keybindSettings);
      this.emoteWheelKeybind = new Setting(this.allSettings, "Emote Wheel Keybind").changeValue(48).setMouseBind(false).withParents(this.keybindSettings);
      this.friendMenuKeybind = new Setting(this.allSettings, "Friend Menu Keybind")
         .changeValue(15)
         .setMouseBind(false)
         .setRequireShift(true)
         .withParents(this.keybindSettings);
      this.copyHoveredTextKeybind = new Setting(this.allSettings, "Copy Hovered Text Keybind")
         .changeValue(46)
         .setMouseBind(false)
         .setRequireCtrl(true)
         .withParents(this.keybindSettings);
      ArrayList list = new ArrayList();

      for (Object obj : EmoteManager.emoteClassIds.keySet()) {
         list.add((Integer)obj);
      }

      list.addAll(EmoteManager.resourceEmoteIds.keySet());
      this.emoteWheelQuickActions = new Setting(this.allSettings, "Emote Wheel Quick Actions")
         .changeValue(new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7)))
         .setAcceptedValues((Integer[])list.toArray(new Integer[0]));
      this.emoteWheelQuickActionKeybinds = new Setting(this.allSettings, "Emote Wheel Quick Action Keybinds")
         .changeValue(new ArrayList<>(Arrays.asList(-1, -1, -1, -1, -1, -1, -1, -1)));
      this.favoriteCosmetics = new Setting(this.allSettings, "Favorite Cosmetics").changeValue("");
      this.recentCosmetics = new Setting(this.allSettings, "Recent Cosmetics").changeValue("");
      this.audioSettings = new SettingGroup(this.allSettings, "Audio Settings").changeValue(true);
      this.microphone = stringArray.length > 0
         ? new Setting(this.allSettings, "Microphone").changeValue(stringArray[0]).setStringValues(stringArray).setOnUpdate(obj -> VoiceEngine.getInstance().setMicrophone((String)obj)).withHierarchyParents(this.audioSettings)
         : new Setting(this.allSettings, "Microphone").changeValue("Unknown").setStringValues("Unknown").setOnUpdate(obj -> VoiceEngine.getInstance().setMicrophone((String)obj)).withHierarchyParents(this.audioSettings);
      this.muteCheatbreakerSounds = new Setting(this.allSettings, "Mute CheatBreaker sounds")
         .setDescription("Mute notification sounds generated from CheatBreaker.")
         .changeValue(false)
         .withHierarchyParents(this.audioSettings);
      this.pinRadioPlayer = new Setting(this.allSettings, "Pin Radio Player")
         .setDescription("Pin the radio to the HUD.")
         .withHierarchyParents(this.audioSettings)
         .changeValue(false);
      this.radioVolume = new Setting(this.allSettings, "Radio Volume")
         .setDescription("Adjusts the volume of the Dash Radio.")
         .setOnUpdate(obj -> {
            if (RadioApi.isPlaying()) {
               RadioApi.getVolume().process(Float.parseFloat(obj.toString()));
            }
         })
         .withHierarchyParents(this.audioSettings)
         .changeValue(85)
         .setRange(0, 100)
         .setUnit("%")
         .setCenterLabel("50%")
         .setIconNames("volume-mute-64", "volume-up-64")
         .setIconScales(7.5F, 7.5F)
         .setIconXOffsets(0.0F, -2.0F);
      this.microphoneVolume = new Setting(this.allSettings, "Microphone Volume").setOnUpdate(obj -> VoiceEngine.getInstance().setMicrophoneVolume(Float.parseFloat(obj.toString()))).changeValue(70.0F).setRange(0.0F, 100.0F).setUnit("%").setCenterLabel("MEDIUM");
      this.speakerVolume = new Setting(this.allSettings, "Speaker Volume").setOnUpdate(obj -> VoiceEngine.getInstance().setSpeakerVolume((Float)obj)).changeValue(85.0F).setRange(0.0F, 100.0F).setUnit("%").setCenterLabel("MEDIUM");
      this.inGameMenuSettings = new SettingGroup(this.allSettings, "In-Game Menu Settings").changeValue(false);
      this.holdServerListButton = new Setting(this.allSettings, "Hold Server List Button")
         .setDescription("Requires the mouse to be held for a certain period of time when clicking on Disconnect.")
         .withHierarchyParents(this.inGameMenuSettings)
         .changeValue(false);
      this.holdServerListIn = new Setting(this.allSettings, "Hold Server List In")
         .setDescription("Requires the mouse to held for a certain period of time in Singleplayer, Multiplayer, or both")
         .withHierarchyParents(this.inGameMenuSettings)
         .changeValue("Multiplayer")
         .setStringValues("Multiplayer", "Singleplayer", "Both")
         .withHierarchyParents(this.holdServerListButton);
      this.holdServerListDuration = new Setting(this.allSettings, "Hold Server List Duration")
         .setDescription("The amount of time the server list button has to be hold the mouse button on.")
         .withHierarchyParents(this.inGameMenuSettings)
         .changeValue(1.0F)
         .setRange(0.25F, 10.0F)
         .setUnit("s")
         .withHierarchyParents(this.holdServerListButton);
      this.disableServerListButton = new Setting(this.allSettings, "Disable Server List Button")
         .setDescription("Disables the server list button in the game menu")
         .withHierarchyParents(this.inGameMenuSettings)
         .changeValue(false);
      this.disableServerListIn = new Setting(this.allSettings, "Disable Server List In")
         .setDescription("Disables the button in Singleplayer, Multiplayer, or both")
         .withHierarchyParents(this.inGameMenuSettings)
         .changeValue("Multiplayer")
         .setStringValues("Singleplayer", "Multiplayer", "Both")
         .withHierarchyParents(this.disableServerListButton);
      this.disableServerListDuration = new Setting(this.allSettings, "Disable Server List Duration")
         .setDescription("The amount of time the server list button will remain disabled.")
         .withHierarchyParents(this.inGameMenuSettings)
         .changeValue(1.0F)
         .setRange(0.25F, 10.0F)
         .setUnit("s")
         .withHierarchyParents(this.disableServerListButton);
      this.holdDisconnectButton = new Setting(this.allSettings, "Hold Disconnect Button")
         .setDescription("Requires the mouse to be held for a certain period of time when clicking on Disconnect.")
         .withHierarchyParents(this.inGameMenuSettings)
         .changeValue(false);
      this.holdDisconnectIn = new Setting(this.allSettings, "Hold Disconnect In")
         .setDescription("Show the prompt in Singleplayer, Multiplayer, or both")
         .changeValue("Multiplayer")
         .setStringValues("Singleplayer", "Multiplayer", "Both")
         .withHierarchyParents(this.holdDisconnectButton);
      this.holdDisconnectDuration = new Setting(this.allSettings, "Hold Disconnect Duration")
         .setDescription("The amount of time the Mods button must be held down before entering the Mod Menu.")
         .withHierarchyParents(this.inGameMenuSettings)
         .changeValue(5.0F)
         .setRange(1.0F, 10.0F)
         .setUnit("s")
         .withHierarchyParents(this.holdDisconnectButton);
      this.disableDisconnectButton = new Setting(this.allSettings, "Disable Disconnect Button")
         .setDescription("Disable Disconnect button temporarily while in the Game Menu.")
         .withHierarchyParents(this.inGameMenuSettings)
         .changeValue(false);
      this.disableDisconnectIn = new Setting(this.allSettings, "Disable Disconnect In")
         .setDescription("Show the prompt in Singleplayer, Multiplayer, or both")
         .withHierarchyParents(this.inGameMenuSettings)
         .changeValue("Multiplayer")
         .setStringValues("Singleplayer", "Multiplayer", "Both")
         .withHierarchyParents(this.disableDisconnectButton);
      this.disableDisconnectDuration = new Setting(this.allSettings, "Disable Disconnect Duration")
         .setDescription("The amount of time the disconnect button will remain disabled.")
         .withHierarchyParents(this.inGameMenuSettings)
         .changeValue(1.0F)
         .setRange(0.25F, 10.0F)
         .setUnit("s")
         .withHierarchyParents(this.disableDisconnectButton);
      this.disconnectConfirmationPrompt = new Setting(this.allSettings, "Disconnect Confirmation Prompt")
         .setDescription("Show a disconnect confirmation prompt before actually disconnecting.")
         .withHierarchyParents(this.inGameMenuSettings)
         .changeValue(false);
      this.showReconnectOption = new Setting(this.allSettings, "Show Reconnect Option")
         .setDescription("Show a reconnect prompt.")
         .withHierarchyParents(this.inGameMenuSettings)
         .changeValue(true)
         .withHierarchyParents(this.disconnectConfirmationPrompt);
      this.showPromptIn = new Setting(this.allSettings, "Show Prompt In")
         .setDescription("Show the prompt in Singleplayer, Multiplayer, or both.")
         .withHierarchyParents(this.inGameMenuSettings)
         .changeValue("Multiplayer")
         .setStringValues("Singleplayer", "Multiplayer", "Both")
         .withHierarchyParents(this.disconnectConfirmationPrompt);
      this.smartDisconnect = new Setting(this.allSettings, "Smart Disconnect")
         .setDescription("Only show the disconnect prompt if clicking too soon.")
         .withHierarchyParents(this.inGameMenuSettings)
         .changeValue(true)
         .withHierarchyParents(this.disconnectConfirmationPrompt);
      this.waitDuration = new Setting(this.allSettings, "Wait Duration")
         .setDescription("The amount of time the user should wait before the disconnect prompt does not show.")
         .withHierarchyParents(this.inGameMenuSettings)
         .changeValue(2.0F)
         .setRange(0.25F, 10.0F)
         .setUnit("s")
         .withHierarchyParents(this.smartDisconnect);
      this.fpsBoost = new SettingGroup(this.allSettings, "FPS Boost").changeValue(false);
      this.enableFpsBoost = new Setting(this.allSettings, "Enable FPS Boost")
         .setDescription("Enables all FPS boost settings in this category.")
         .withHierarchyParents(this.fpsBoost)
         .changeValue(true);
      this.multithreadedChunkLoading = new Setting(this.allSettings, "Multithreaded chunk loading").changeValue(true).withHierarchyParents(this.enableFpsBoost);
      this.slowChunkLoading = new Setting(this.allSettings, "Slow chunk loading")
         .setDescription(
            "Determines how fast chunks should load as a percentage.\n§e§oHigher values may increase performance!\n§6§oHigher values will make chunks load slower.\n\n§276% - 100%:§r Slowest chunk loading speed (Higher FPS).\n§a51% - 75%:§r Slow chunk loading speed (High FPS).\n§e50%:§r Medium chunk loading speed (Medium FPS).\n§626% - 49%:§r Fast chunk loading speed (Low FPS).\n§c0.1% - 25%:§r Faster chunk loading speed (Lower FPS).\n§b0%:§r Vanilla chunk loading speed (Lowest FPS)."
         )
         .withHierarchyParents(this.fpsBoost)
         .setRange(0.0F, 100.0F)
         .changeValue(30.0F)
         .setUnit("%")
         .setCenterLabel("MEDIUM")
         .setIconNames("fps-inverse-52", "fps-52")
         .setIconYOffsets(-2.0F, -2.0F)
         .withHierarchyParents(this.enableFpsBoost);
      this.entityDistance = new Setting(this.allSettings, "Entity Distance")
         .setRange(2.0F, 100.0F)
         .changeValue(18.75F)
         .setUnit("%")
         .setCenterLabel("MEDIUM")
         .setIconNames("fps-52", "fps-inverse-52")
         .setIconYOffsets(-2.0F, -2.0F)
         .withHierarchyParents(this.enableFpsBoost);
      this.tileEntityDistance = new Setting(this.allSettings, "Tile Entity Distance")
         .setRange(2.0F, 100.0F)
         .changeValue(18.75F)
         .setUnit("%")
         .setCenterLabel("MEDIUM")
         .setIconNames("fps-52", "fps-inverse-52")
         .setIconYOffsets(-2.0F, -2.0F)
         .withHierarchyParents(this.enableFpsBoost);
      this.fullbright = new Setting(this.allSettings, "Fullbright")
         .setDescription("Set the brightness/gamma to the max.")
         .changeValue(true)
         .withHierarchyParents(this.enableFpsBoost)
         .setOnUpdate(obj -> Minecraft.getMinecraft().gameSettings.gammaSetting = (Boolean)obj ? 100.0F : 0.0F)
         .setAliases("Full Bright", "Gammabright", "Gamma Bright");
      this.lowerAnimationTicks = new Setting(this.allSettings, "Lower Animation Ticks")
         .setDescription("Lower the animation ticks from 1000 to 100.")
         .withHierarchyParents(this.fpsBoost)
         .changeValue(true);
      this.disableParticlePhysics = new Setting(this.allSettings, "Disable Particle Physics")
         .setDescription("If particles should disable collision checks.")
         .withHierarchyParents(this.fpsBoost)
         .changeValue(true);
      this.entityShadows = new Setting(this.allSettings, "Entity Shadows")
         .setDescription("Draws a shadow below entities.")
         .withHierarchyParents(this.fpsBoost)
         .changeValue(true)
         .setOnUpdate(obj -> Minecraft.getMinecraft().gameSettings.entityShadows = (Boolean)obj);
      this.hideEndPortals = new Setting(this.allSettings, "Hide End Portals")
         .setDescription("Hide end portals.")
         .changeValue(false)
         .withHierarchyParents(this.enableFpsBoost);
      this.hideGroundedArrows = new Setting(this.allSettings, "Hide Grounded Arrows").withHierarchyParents(this.fpsBoost).changeValue(false);
      this.hideFoliage = new Setting(this.allSettings, "Hide Foliage")
         .setDescription("Hides things like tall grass, flowers, and shrubs")
         .changeValue(false)
         .withHierarchyParents(this.fpsBoost)
         .setOnUpdate(obj -> {
            if (Minecraft.getMinecraft().currentScreen instanceof ModsScreen) {
               CheatBreaker.getInstance().getMc().renderGlobal.loadRenderers();
            }
         });
      this.hideStuckArrows = new Setting(this.allSettings, "Hide Stuck Arrows").withHierarchyParents(this.fpsBoost).changeValue(false);
      this.hideMovingArrows = new Setting(this.allSettings, "Hide Moving Arrows").withHierarchyParents(this.fpsBoost).changeValue(false);
      this.hidePlacedSkulls = new Setting(this.allSettings, "Hide Placed Skulls").withHierarchyParents(this.fpsBoost).changeValue(false);
      this.fpsLimitingSettings = new SettingGroup(this.allSettings, "FPS Limiting Settings").changeValue(false);
      this.useCustomFpsLimiter = new Setting(this.allSettings, "Use Custom FPS Limiter")
         .setDescription("Overrides the default FPS limiter with a custom one.\n§e§oWill not override when VSync is enabled.")
         .withHierarchyParents(this.fpsLimitingSettings)
         .changeValue(false);
      this.limitWhenGameIsUnfocused = new Setting(this.allSettings, "Limit When Game is Unfocused")
         .setDescription("Limits the FPS when you are not interacting with the game.")
         .withHierarchyParents(this.fpsLimitingSettings)
         .changeValue(true);
      this.limitInPauseMenu = new Setting(this.allSettings, "Limit In Pause Menu")
         .setDescription("Limits the FPS when you are in the Pause Menu.")
         .withHierarchyParents(this.fpsLimitingSettings)
         .changeValue(true);
      this.limitInContainers = new Setting(this.allSettings, "Limit In Containers")
         .setDescription("Limits the FPS when you are in containers.")
         .withHierarchyParents(this.fpsLimitingSettings)
         .changeValue(true);
      this.limitInHudEditor = new Setting(this.allSettings, "Limit In HUD Editor")
         .setDescription("Limits the FPS when you are in the HUD Editor.")
         .withHierarchyParents(this.fpsLimitingSettings)
         .changeValue(true);
      this.maximumFps = new Setting(this.allSettings, "Maximum FPS")
         .withHierarchyParents(this.fpsLimitingSettings)
         .setRange(5, 2000)
         .changeValue(540)
         .setUnit(" FPS")
         .setIconNames("fps-inverse-52", "fps-52")
         .setIconYOffsets(-2.0F, -2.0F)
         .withHierarchyParents(this.useCustomFpsLimiter);
      this.unfocusedFps = new Setting(this.allSettings, "Unfocused FPS")
         .withHierarchyParents(this.fpsLimitingSettings)
         .setRange(1, 540)
         .changeValue(60)
         .setUnit(" FPS")
         .setIconNames("fps-inverse-52", "fps-52")
         .setIconYOffsets(-2.0F, -2.0F)
         .withHierarchyParents(this.limitWhenGameIsUnfocused);
      this.pauseMenuFps = new Setting(this.allSettings, "Pause Menu FPS")
         .withHierarchyParents(this.fpsLimitingSettings)
         .setRange(1, 540)
         .changeValue(120)
         .setUnit(" FPS")
         .setIconNames("fps-inverse-52", "fps-52")
         .setIconYOffsets(-2.0F, -2.0F)
         .withHierarchyParents(this.limitInPauseMenu);
      this.containerFps = new Setting(this.allSettings, "Container FPS")
         .withHierarchyParents(this.fpsLimitingSettings)
         .setRange(1, 540)
         .changeValue(540)
         .setUnit(" FPS")
         .setIconNames("fps-inverse-52", "fps-52")
         .setIconYOffsets(-2.0F, -2.0F)
         .withHierarchyParents(this.limitInContainers);
      this.hudEditorFps = new Setting(this.allSettings, "HUD Editor FPS")
         .withHierarchyParents(this.fpsLimitingSettings)
         .setRange(1, 2000)
         .changeValue(700)
         .setUnit(" FPS")
         .setIconNames("fps-inverse-52", "fps-52")
         .setIconYOffsets(-2.0F, -2.0F)
         .withHierarchyParents(this.limitInHudEditor);
      this.mainMenuFps = new Setting(this.allSettings, "Main Menu FPS")
         .withHierarchyParents(this.fpsLimitingSettings)
         .setRange(30, 500)
         .changeValue(90)
         .setUnit(" FPS")
         .setIconNames("fps-inverse-52", "fps-52")
         .setIconYOffsets(-2.0F, -2.0F);
      this.teamViewSettings = new SettingGroup(this.allSettings, "Team View Settings").changeValue(false);
      this.enableTeamView = new Setting(this.allSettings, "Enable Team View")
         .setDescription("Enables the Team View.")
         .withHierarchyParents(this.teamViewSettings)
         .changeValue(true)
         .setOnUpdate(obj -> {
            if (!(Boolean)obj) {
               CheatBreaker.getInstance().getModManager().teamManager.process((Boolean)obj);
            }
         });
      this.showOffScreenMarker = new Setting(this.allSettings, "Show off-screen marker")
         .setDescription("Show a marker above players.")
         .withHierarchyParents(this.teamViewSettings)
         .changeValue(true)
         .withHierarchyParents(this.enableTeamView);
      this.showDistance = new Setting(this.allSettings, "Show distance")
         .setDescription("Show the distance amount above a your teammates' heads.")
         .withHierarchyParents(this.teamViewSettings)
         .changeValue(true)
         .withHierarchyParents(this.enableTeamView);
      this.generalSettings = new SettingGroup(this.allSettings, "General Settings").changeValue(true);
      this.aprilFools = new Setting(this.allSettings, "April Fools")
         .withHierarchyParents(this.generalSettings)
         .changeValue(true)
         .setCondition(CheatBreaker::isAprilFoolsEnabled);
      this.theScale = new Setting(this.allSettings, "The Scale")
         .withHierarchyParents(this.aprilFools)
         .changeValue(3.0F)
         .setRange(0.1F, 5.0F)
         .setCondition(CheatBreaker::isAprilFoolsEnabled);
      this.showFriendsMenu = new Setting(this.allSettings, "Show Friends menu").withHierarchyParents(this.generalSettings).changeValue(true);
      this.scrollHotbarSlots = new Setting(this.allSettings, "Scroll Hotbar Slots").withHierarchyParents(this.generalSettings).changeValue(true);
      this.centerShortLists = new Setting(this.allSettings, "Center Short Lists")
         .setDescription(
            "Vertically center list screens (like Select World or Waypoints) when they don't have enough entries to fill the screen, instead of anchoring them to the top."
         )
         .withHierarchyParents(this.generalSettings)
         .changeValue(false);
      this.rawMouseInput = new Setting(this.allSettings, "Raw Mouse Input")
         .withHierarchyParents(this.generalSettings)
         .setAliases("Raw Input", "Raw Mouse Tracking", "Raw Tracking")
         .changeValue(false)
         .setOnUpdate(obj -> {
            if (Minecraft.getMinecraft().mouseHelper instanceof ControllerMouseHelper) {
               if ((Boolean)obj) {
                  ((ControllerMouseHelper)Minecraft.getMinecraft().mouseHelper).run3();
               } else {
                  ((ControllerMouseHelper)Minecraft.getMinecraft().mouseHelper).run();
               }
            }
         });
      this.inputMode = new Setting(this.allSettings, "Input Mode")
         .withHierarchyParents(this.rawMouseInput)
         .changeValue("Direct & Raw")
         .setStringValues("Direct & Raw", "Direct")
         .setCondition(() -> Util.getOSType() == EnumOS.WINDOWS)
         .setOnUpdate(obj -> {
            if (Minecraft.getMinecraft().mouseHelper instanceof ControllerMouseHelper && this.rawMouseInput.getBooleanValue()) {
               ((ControllerMouseHelper)Minecraft.getMinecraft().mouseHelper).run6();
            }
         });
      this.usedMouse = new Setting(this.allSettings, "Used Mouse").withHierarchyParents(this.rawMouseInput).changeValue("ALL").setStringValues("ALL");
      this.rawPollingRate = new Setting(this.allSettings, "Raw Polling Rate")
         .withHierarchyParents(this.rawMouseInput)
         .changeValue(1000)
         .setRange(1, 8000)
         .setOnUpdate(obj -> {
            if (Minecraft.getMinecraft().mouseHelper instanceof ControllerMouseHelper && this.rawMouseInput.getBooleanValue()) {
               ((ControllerMouseHelper)Minecraft.getMinecraft().mouseHelper).run3();
            }
         });
      this.displaySettings = new SettingGroup(this.allSettings, "Display Settings").changeValue(false);
      this.borderlessFullscreen = new Setting(this.allSettings, "Borderless Fullscreen")
         .changeValue(false)
         .withHierarchyParents(this.displaySettings)
         .setOnUpdate(obj -> {
            if (Minecraft.getMinecraft().currentScreen instanceof ModsScreen) {
               if (Minecraft.getMinecraft().isFullScreen()) {
                  Minecraft.getMinecraft().toggleFullscreen();
                  Minecraft.getMinecraft().toggleFullscreen();
                  CheatBreaker.getInstance().getUiManager().run4();
               }
            }
         });
      this.exitFullscreenWhenUnfocused = new Setting(this.allSettings, "Exit fullscreen when Unfocused")
         .withHierarchyParents(this.displaySettings)
         .changeValue(false);
      this.hudEditorSettings = new SettingGroup(this.allSettings, "Hud Editor Settings").changeValue(true);
      this.hideWhenClickedOff = new Setting(this.allSettings, "Hide When Clicked Off").withHierarchyParents(this.hudEditorSettings).changeValue(false);
      this.modList = new Setting(this.allSettings, "Mod List")
         .withHierarchyParents(this.hudEditorSettings)
         .changeValue("Normal")
         .setStringValues("Normal", "Compact")
         .setCustomizationLevel(SettingsLevel.ADVANCED)
         .setOnUpdate(obj -> CheatBreaker.getInstance().getUiManager().run4());
      this.logoAnimation = new Setting(this.allSettings, "Logo Animation")
         .withHierarchyParents(this.hudEditorSettings)
         .changeValue("Original")
         .setStringValues("New", "Original", "None")
         .setCustomizationLevel(SettingsLevel.ADVANCED);
      this.customizationLevel = new Setting(this.allSettings, "Customization Level")
         .withHierarchyParents(this.hudEditorSettings)
         .changeValue("Advanced")
         .setStringValues("Simple", "Medium", "Advanced");
      this.showModNameGui = new Setting(this.allSettings, "Show Mod Name (GUI)")
         .withHierarchyParents(this.hudEditorSettings)
         .changeValue(true)
         .setCustomizationLevel(SettingsLevel.MEDIUM);
      this.snapModsToOtherModsGui = new Setting(this.allSettings, "Snap mods to other mods (GUI)")
         .withHierarchyParents(this.hudEditorSettings)
         .changeValue(true);
      this.snappingStrength = new Setting(this.allSettings, "Snapping Strength")
         .withHierarchyParents(this.hudEditorSettings)
         .changeValue(2.0F)
         .setRange(1.0F, 10.0F)
         .setUnit("px")
         .setCustomizationLevel(SettingsLevel.MEDIUM)
         .withHierarchyParents(this.snapModsToOtherModsGui);
      this.searchSettings = new SettingGroup(this.allSettings, "Search Settings").changeValue(true);
      this.searchForSettingsInMods = new Setting(this.allSettings, "Search for Settings in Mods").withHierarchyParents(this.searchSettings).changeValue(true);
      this.bypassCustomizationLevel = new Setting(this.allSettings, "Bypass Customization Level").withHierarchyParents(this.searchSettings).changeValue(true);
      this.renderSettings = new SettingGroup(this.allSettings, "Render Settings").changeValue(false);
      this.showPotionInfoInInventory = new Setting(this.allSettings, "Show Potion info in inventory")
         .withHierarchyParents(this.renderSettings)
         .changeValue(true)
         .setCondition(() -> !CheatBreaker.getInstance().getModManager().potionEffectsMod.isEnabled());
      this.showPotionInfoBackground = new Setting(this.allSettings, "Show Potion info background").withHierarchyParents(this.renderSettings).changeValue(true);
      this.potionInfoShiftsInventory = new Setting(this.allSettings, "Potion info shifts inventory")
         .setDescription("Choose to make the potion info shift the inventory position.")
         .withHierarchyParents(this.renderSettings)
         .changeValue(false);
      this.ghostLiquidFix = new Setting(this.allSettings, "Ghost Liquid Fix")
         .setDescription("Prevents ghost liquid from appearing")
         .withHierarchyParents(this.renderSettings)
         .changeValue(true);
      this.showAchievements = new Setting(this.allSettings, "Show Achievements").withHierarchyParents(this.renderSettings).changeValue(true);
      this.guiBlur = new Setting(this.allSettings, "GUI Blur")
         .setDescription("Blurs the menus.")
         .setOnUpdate(obj -> Minecraft.getMinecraft().entityRenderer.run3())
         .withHierarchyParents(this.renderSettings)
         .changeValue(false);
      this.containerBackground = new Setting(this.allSettings, "Container Background")
         .withHierarchyParents(this.renderSettings)
         .changeValue(CheatBreaker.clientName)
         .setStringValues("Vanilla", CheatBreaker.clientName, "None");
      this.currentMenu = new Setting(this.allSettings, "Current Menu")
         .setDescription("Sets which main menu should show.\n\n0: 2017\n1: 2018\n2: 2016")
         .withHierarchyParents(this.renderSettings)
         .changeValue(1)
         .setRange(0, 2);
      this.streamerModeSettings = new SettingGroup(this.allSettings, "Streamer Mode Settings").changeValue(false);
      this.streamerMode = new Setting(this.allSettings, "Streamer Mode")
         .setDescription("Enables Streamer Mode Features.")
         .withHierarchyParents(this.streamerModeSettings)
         .changeValue(false);
      this.disableModMenuKeybind = new Setting(this.allSettings, "Disable Mod Menu Keybind")
         .setDescription("Disables the Mod Menu keybind from opening the Mod Menu when Streamer Mode is enabled.")
         .changeValue(true)
         .withHierarchyParents(this.streamerMode);
      this.notifyWhenPressed = new Setting(this.allSettings, "Notify when pressed")
         .setDescription("Notifies when the player presses the Mod Menu keybind while disabled.")
         .changeValue(true)
         .withHierarchyParents(this.disableModMenuKeybind);
      this.holdModsButton = new Setting(this.allSettings, "Hold Mods Button")
         .setDescription("Requires the mouse to be held for a certain period of time when clicking on Mods.")
         .changeValue(true)
         .withHierarchyParents(this.streamerMode);
      this.holdModsDuration = new Setting(this.allSettings, "Hold Mods Duration")
         .setDescription("The amount of time the Mods button must be held down before entering the Mod Menu.")
         .withHierarchyParents(this.streamerModeSettings)
         .changeValue(5.0F)
         .setRange(1.0F, 10.0F)
         .setUnit("s")
         .withHierarchyParents(this.holdModsButton);
      this.disableModsButton = new Setting(this.allSettings, "Disable Mods Button")
         .setDescription("Disable Mods button temporarily while in the Game Menu.")
         .withHierarchyParents(this.streamerModeSettings)
         .changeValue(true)
         .withHierarchyParents(this.streamerMode);
      this.disableModsDuration = new Setting(this.allSettings, "Disable Mods Duration")
         .setDescription("The amount of time the Mods button will remain disabled.")
         .withHierarchyParents(this.streamerModeSettings)
         .changeValue(2.0F)
         .setRange(1.0F, 10.0F)
         .setUnit("s")
         .withHierarchyParents(this.disableModsButton);
      this.zoomSettings = new SettingGroup(this.allSettings, "Zoom Settings").changeValue(false).setCustomizationLevel(SettingsLevel.MEDIUM);
      this.scrollZoom = new Setting(this.allSettings, "Scroll Zoom").withHierarchyParents(this.zoomSettings).changeValue(true);
      this.useCinematicCamera = new Setting(this.allSettings, "Use Cinematic Camera").withHierarchyParents(this.zoomSettings).changeValue(true);
      this.resourcePackSettings = new SettingGroup(this.allSettings, "Resource Pack Settings").changeValue(false);
      this.widePackMenu = new Setting(this.allSettings, "Wide Pack Menu").withHierarchyParents(this.resourcePackSettings).changeValue(false);
      this.transparentBackground = new Setting(this.allSettings, "Transparent background")
         .setDescription("Remove the dirt background in the resource pack menu.")
         .withHierarchyParents(this.resourcePackSettings)
         .changeValue(false);
      this.showPackFolderInformation = new Setting(this.allSettings, "Show Pack Folder Information")
         .setDescription("Show the pack folder information.")
         .withHierarchyParents(this.resourcePackSettings)
         .changeValue(true)
         .setCustomizationLevel(SettingsLevel.MEDIUM);
      this.showPackFolderIcons = new Setting(this.allSettings, "Show Pack Folder Icons")
         .withHierarchyParents(this.resourcePackSettings)
         .changeValue(true)
         .setCustomizationLevel(SettingsLevel.MEDIUM);
      this.showPackIcons = new Setting(this.allSettings, "Show Pack Icons")
         .withHierarchyParents(this.resourcePackSettings)
         .changeValue(true)
         .setCustomizationLevel(SettingsLevel.MEDIUM);
      this.showPackDescriptions = new Setting(this.allSettings, "Show Pack Descriptions")
         .withHierarchyParents(this.resourcePackSettings)
         .changeValue(true)
         .setCustomizationLevel(SettingsLevel.MEDIUM);
      this.showSearchBar = new Setting(this.allSettings, "Show Search Bar")
         .withHierarchyParents(this.resourcePackSettings)
         .changeValue(true)
         .setCustomizationLevel(SettingsLevel.MEDIUM);
      this.sortMethod = new Setting(this.allSettings, "Sort Method")
         .setDescription(
            "Sort packs in a specific order.\n\n§bA-Z:§r Sort packs alphabetically starting from A to Z.\n§bZ-A:§r Sort packs alphabetically in reverse starting from Z to A."
         )
         .withHierarchyParents(this.resourcePackSettings)
         .changeValue("A-Z")
         .setStringValues("A-Z", "Z-A");
      this.smoothScrolling = new Setting(this.allSettings, "Smooth Scrolling")
         .setDescription("Makes scrolling smooth")
         .withHierarchyParents(this.resourcePackSettings)
         .changeValue(false);
      this.listBackgroundColor = new Setting(this.allSettings, "List Background color")
         .setDescription("Change the background color in the resource pack lists.")
         .withHierarchyParents(this.resourcePackSettings)
         .changeValue(Integer.MIN_VALUE)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
      this.keybindHandlingSettings = new SettingGroup(this.allSettings, "Keybind Handling Settings").changeValue(false);
      this.modernKeybindHandling = new Setting(this.allSettings, "Modern Keybind Handling")
         .withHierarchyParents(this.keybindHandlingSettings)
         .changeValue(true);
      this.excludeSneakKeybind = new Setting(this.allSettings, "Exclude Sneak Keybind").changeValue(true).withHierarchyParents(this.modernKeybindHandling);
      this.throwKeybind = new Setting(this.allSettings, "Exclude Block/Throw Keybind").changeValue(true).withHierarchyParents(this.modernKeybindHandling);
      this.scalingSettings = new SettingGroup(this.allSettings, "Scaling Settings").changeValue(false);
      this.excludeSelectedMods = new Setting(this.allSettings, "Exclude Selected Mods")
         .withHierarchyParents(this.scalingSettings)
         .changeValue(true)
         .setToggleTexts("Include", "Exclude");
      this.scale = new Setting(this.allSettings, "Scale")
         .setDescription("Change the scale of the mod.")
         .changeValue(1.0F)
         .setRange(0.5F, 1.5F)
         .setUnit("x")
         .setCenterLabel("1.0x")
         .setIconNames("letter-t-64", "letter-t-64")
         .setIconScales(6.0F, 8.0F)
         .setIconXOffsets(0.0F, -2.0F)
         .setIconYOffsets(2.0F, -1.5F)
         .withHierarchyParents(this.scalingSettings);
      this.modScale = new Setting(this.allSettings, "Mod Scale")
         .withHierarchyParents(this.scalingSettings)
         .changeValue("Default")
         .setStringValues("Default", "Small", "Normal", "Large", "Auto");
      this.applyToAll = new ButtonSetting(this.allSettings, "Apply to all")
         .changeValue("Apply to All")
         .withHierarchyParents(this.scalingSettings)
         .setOnUpdate(
            obj -> {
               if (Minecraft.getMinecraft().currentScreen instanceof ModsScreen) {
                  for (AbstractMod abstractMod : CheatBreaker.getInstance().getModManager().abstractModList) {
                     for (Setting setting : abstractMod.getSettings()) {
                        if (setting.getName().toLowerCase().contains("scale")
                           && this.excludeSelectedMods.getBooleanValue() != (ModsScreen.modsScreen.getPlacement(abstractMod) != null)) {
                           this.copyValueIfSameName(setting, this.scale);
                           if (Objects.equals(setting.getName(), "GUI Scale")) {
                              setting.changeValue(this.modScale.getValue());
                           }
                        }
                     }
                  }
               }
            }
         );
      this.modScaleMultiplier = new Setting(this.allSettings, "Mod Scale Multiplier")
         .withHierarchyParents(this.scalingSettings)
         .changeValue(1.0F)
         .setRange(0.25F, 2.0F)
         .setUnit("x")
         .setIconNames("letter-t-64", "letter-t-64")
         .setIconScales(6.0F, 8.0F)
         .setIconXOffsets(0.0F, -2.0F)
         .setIconYOffsets(2.0F, -1.5F);
      this.hotbarScale = new Setting(this.allSettings, "Hotbar Scale")
         .withHierarchyParents(this.scalingSettings)
         .changeValue("Default")
         .setStringValues("Default", "Small", "Normal", "Large", "Auto");
      this.hotbarScaleMultiplier = new Setting(this.allSettings, "Hotbar Scale Multiplier")
         .withHierarchyParents(this.scalingSettings)
         .changeValue(1.0F)
         .setRange(0.25F, 2.0F)
         .setUnit("x")
         .setIconNames("letter-t-64", "letter-t-64")
         .setIconScales(6.0F, 8.0F)
         .setIconXOffsets(0.0F, -2.0F)
         .setIconYOffsets(2.0F, -1.5F);
      this.useLegacyScaling = new Setting(this.allSettings, "Use Legacy Scaling").withHierarchyParents(this.scalingSettings).changeValue(false);
      this.screenshotSettings = new SettingGroup(this.allSettings, "Screenshot Settings").changeValue(false);
      this.makeShutterSound = new Setting(this.allSettings, "Make Shutter Sound").withHierarchyParents(this.screenshotSettings).changeValue(true);
      this.copyScreenshotAutomatically = new Setting(this.allSettings, "Copy Screenshot Automatically")
         .withHierarchyParents(this.screenshotSettings)
         .changeValue(false);
      this.sendMessage = new Setting(this.allSettings, "Send Message").withHierarchyParents(this.screenshotSettings).changeValue(true);
      this.compactOptions = new Setting(this.allSettings, "Compact Options").changeValue(false).withHierarchyParents(this.sendMessage);
      this.showOpenOption = new Setting(this.allSettings, "Show Open Option").changeValue(true).withHierarchyParents(this.sendMessage);
      this.showCopyOption = new Setting(this.allSettings, "Show Copy Option").changeValue(true).withHierarchyParents(this.sendMessage);
      this.showUploadOption = new Setting(this.allSettings, "Show Upload Option").changeValue(true).withHierarchyParents(this.sendMessage);
      this.cosmeticSettings = new SettingGroup(this.allSettings, "Cosmetic Settings").changeValue(false);
      this.showOptifineCapes = new Setting(this.allSettings, "Show OptiFine Capes").withHierarchyParents(this.cosmeticSettings).changeValue(true);
      this.showOptifineHats = new Setting(this.allSettings, "Show OptiFine Hats").withHierarchyParents(this.cosmeticSettings).changeValue(true);
      this.showCheatbreakerCapes = new Setting(this.allSettings, "Show CheatBreaker Capes").withHierarchyParents(this.cosmeticSettings).changeValue(true);
      this.showCheatbreakerWings = new Setting(this.allSettings, "Show CheatBreaker Wings").withHierarchyParents(this.cosmeticSettings).changeValue(true);
      this.showCheatbreakerHats = new Setting(this.allSettings, "Show CheatBreaker Hats").withHierarchyParents(this.cosmeticSettings).changeValue(true);
      this.discordRichPresenceSettings = new SettingGroup(this.allSettings, "Discord Rich Presence Settings").changeValue(false);
      this.showDiscordRichPresence = new Setting(this.allSettings, "Show Discord Rich Presence")
         .withHierarchyParents(this.discordRichPresenceSettings)
         .changeValue(true)
         .setOnUpdate(obj -> {
            if (Minecraft.getMinecraft().currentScreen instanceof ModsScreen) {
               DiscordRpcManager discordRpcManager = CheatBreaker.getInstance().getDiscordRpcManager();
               IPCClient ipcclient = discordRpcManager != null ? discordRpcManager.getIpcclient() : null;
               if (discordRpcManager != null && ipcclient != null) {
                  try {
                     if ((Boolean)obj && ipcclient.getStatus() == PipeStatus.CLOSED) {
                        discordRpcManager.run2();
                     } else if (ipcclient.getStatus() != PipeStatus.CLOSED) {
                        ipcclient.close();
                     }
                  } catch (Exception e) {
                     Logging.errorTagged("An error occurred while handling RPC", e);
                  }
               }
            }
         });
      this.showActiveServer = new Setting(this.allSettings, "Show Active Server")
         .withHierarchyParents(this.discordRichPresenceSettings)
         .changeValue(true)
         .setOnUpdate(obj -> this.updateActiveAccount())
         .withHierarchyParents(this.showDiscordRichPresence);
      this.showActiveAccount = new Setting(this.allSettings, "Show Active Account")
         .withHierarchyParents(this.discordRichPresenceSettings)
         .changeValue(true)
         .setOnUpdate(obj -> this.updateActiveAccount())
         .withHierarchyParents(this.showDiscordRichPresence);
      this.defaultColorSettings = new SettingGroup(this.allSettings, "Default Color Settings").changeValue(true);
      this.applyDefaultColorWhenEnablingMods = new Setting(this.allSettings, "Apply Default Color When Enabling Mods")
         .withHierarchyParents(this.defaultColorSettings)
         .changeValue(true);
      this.excludeSelectedMods2 = new Setting(this.allSettings, "Exclude Selected Mods")
         .withHierarchyParents(this.defaultColorSettings)
         .changeValue(true)
         .setToggleTexts("Include", "Exclude");
      this.textColor = new Setting(this.allSettings, "Text Color")
         .withHierarchyParents(this.defaultColorSettings)
         .changeValue(-1)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
      this.backgroundColor = new Setting(this.allSettings, "Background Color")
         .withHierarchyParents(this.defaultColorSettings)
         .changeValue(1862270976)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
      this.borderColor = new Setting(this.allSettings, "Border Color")
         .withHierarchyParents(this.defaultColorSettings)
         .changeValue(-1627389952)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
      this.applyToAll = new ButtonSetting(this.allSettings, "Apply to all")
         .changeValue("Apply to All")
         .withHierarchyParents(this.defaultColorSettings)
         .setOnUpdate(
            obj -> {
               if (Minecraft.getMinecraft().currentScreen instanceof ModsScreen) {
                  for (AbstractMod abstractMod : CheatBreaker.getInstance().getModManager().abstractModList) {
                     for (Setting setting : abstractMod.getSettings()) {
                        if (setting.getType() == SettingType.INTEGER
                           && setting.getName().toLowerCase().contains("color")
                           && this.excludeSelectedMods2.getBooleanValue() != (ModsScreen.modsScreen.getPlacement(abstractMod) != null)) {
                           if (setting.getName().toLowerCase().contains("text")) {
                              setting.changeValue(this.textColor.getColorValue());
                           }

                           if (setting.getName().toLowerCase().contains("background")) {
                              setting.changeValue(this.backgroundColor.getColorValue());
                           }

                           if (setting.getName().toLowerCase().contains("border")) {
                              setting.changeValue(this.borderColor.getColorValue());
                           }
                        }
                     }
                  }
               }
            }
         );
      this.defaultSizeSettings = new SettingGroup(this.allSettings, "Default Size Settings").changeValue(true);
      this.excludeSelectedMods3 = new Setting(this.allSettings, "Exclude Selected Mods")
         .withParents(this.defaultSizeSettings)
         .changeValue(true)
         .setToggleTexts("Include", "Exclude");
      this.showBackground = new Setting(this.allSettings, "Show Background").withParents(this.defaultSizeSettings).changeValue(true);
      this.staticBackgroundWidth = new Setting(this.allSettings, "Static Background Width")
         .setDescription("§2Enabled:§r Background width is set by a slider.\n§4Disabled:§r Background width is set by the text size.")
         .changeValue(true)
         .setCustomizationLevel(SettingsLevel.MEDIUM)
         .withOptionalHierarchyParents(this.showBackground);
      this.textOverflow = new Setting(this.allSettings, "Text Overflow")
         .setDescription(
            "What the mod will do if the text overflows from the background\n§bExtend Width:§r Increases the background width.\n§bScale Text:§r Scales the text down.\n§bDo Nothing:§r Does no action."
         )
         .changeValue("Extend Width")
         .setStringValues("Extend Width", "Scale Text", "Do Nothing")
         .setCustomizationLevel(SettingsLevel.ADVANCED)
         .withHierarchyParents(this.staticBackgroundWidth);
      this.backgroundWidthPadding = new Setting(this.allSettings, "Background Width Padding")
         .setDescription("Change how spaced the background width is from the text.")
         .withHierarchyParents(this.showBackground)
         .changeValue(6.0F)
         .setRange(0.0F, 10.0F)
         .setUnit("px")
         .setCondition(() -> !this.staticBackgroundWidth.getBooleanValue() || !this.textOverflow.getValue().equals("Do Nothing"))
         .setCustomizationLevel(SettingsLevel.MEDIUM);
      this.cornerRadius = new Setting(this.allSettings, "Corner Radius")
         .setDescription("Change the corner radius of the background.")
         .changeValue(0.0F)
         .setRange(0.0F, 10.0F)
         .setUnit("px")
         .withHierarchyParents(this.showBackground)
         .setCustomizationLevel(SettingsLevel.MEDIUM);
      this.backgroundWidth = new Setting(this.allSettings, "Background Width")
         .withParents(this.defaultSizeSettings)
         .changeValue(56.0F)
         .setRange(40.0F, 80.0F)
         .setUnit("px")
         .setCustomizationLevel(SettingsLevel.MEDIUM);
      this.backgroundHeight = new Setting(this.allSettings, "Background Height")
         .withParents(this.defaultSizeSettings)
         .changeValue(13.0F)
         .setRange(10.0F, 24.0F)
         .setUnit("px")
         .setCustomizationLevel(SettingsLevel.MEDIUM);
      this.showBorder = new Setting(this.allSettings, "Show Border").changeValue(false).withHierarchyParents(this.showBackground);
      this.borderThickness = new Setting(this.allSettings, "Border Thickness")
         .setDescription("Change the thickness of the border.")
         .changeValue(1.0F)
         .setRange(0.25F, 5.0F)
         .setUnit("px")
         .setCustomizationLevel(SettingsLevel.MEDIUM)
         .withHierarchyParents(this.showBorder);
      this.applyToAll2 = new ButtonSetting(this.allSettings, "Apply to all")
         .changeValue("Apply to All")
         .withParents(this.defaultSizeSettings)
         .setOnUpdate(
            obj -> {
               if (Minecraft.getMinecraft().currentScreen instanceof ModsScreen) {
                  for (AbstractMod abstractMod : CheatBreaker.getInstance().getModManager().abstractModList) {
                     if (abstractMod instanceof AbstractBackgroundHudMod
                        && this.excludeSelectedMods3.getBooleanValue() != (ModsScreen.modsScreen.getPlacement(abstractMod) != null)) {
                        for (Setting setting : abstractMod.getSettings()) {
                           this.copyValueIfSameName(setting, this.backgroundWidth);
                           this.copyValueIfSameName(setting, this.backgroundHeight);
                           this.copyValueIfSameName(setting, this.staticBackgroundWidth);
                           this.copyValueIfSameName(setting, this.showBackground);
                           this.copyValueIfSameName(setting, this.cornerRadius);
                           this.copyValueIfSameName(setting, this.backgroundWidthPadding);
                           this.copyValueIfSameName(setting, this.textOverflow);
                           this.copyValueIfSameName(setting, this.showBorder);
                           this.copyValueIfSameName(setting, this.borderThickness);
                        }
                     }
                  }
               }
            }
         );
      this.emoteRadialColor = new Setting(this.allSettings, "Emote Radial color")
         .changeValue(-2144128179)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .setCustomizationLevel(SettingsLevel.MEDIUM);
      GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
      this.voiceChat = new KeyBinding("Voice Chat", 47, "CheatBreakerClient", true);
      this.openMenu = new KeyBinding("Open Menu", 54, "CheatBreaker Client", true);
      this.openVoiceMenu = new KeyBinding("Open Voice Menu", 25, "CheatBreaker Client", true);
      this.hideNametags = new KeyBinding("Hide Nametags", 0, "CheatBreaker Client", true);
      KeyBinding keyBinding = new KeyBinding(
         "Drop Stack",
         this.dropStackKeybind.hasMouseBind() ? this.dropStackKeybind.getIntValue() - 100 : this.dropStackKeybind.getIntValue(),
         "CheatBreaker Client",
         true
      );
      KeyBinding keyBinding2 = new KeyBinding(
         "Emote Wheel",
         this.emoteWheelKeybind.hasMouseBind() ? this.emoteWheelKeybind.getIntValue() - 100 : this.emoteWheelKeybind.getIntValue(),
         "CheatBreaker Client",
         true
      );
      KeyBinding keyBinding3 = new KeyBinding(
         "Friend Menu",
         this.friendMenuKeybind.hasMouseBind() ? this.friendMenuKeybind.getIntValue() - 100 : this.friendMenuKeybind.getIntValue(),
         "CheatBreaker Client",
         true
      );
      KeyBinding keyBinding4 = new KeyBinding(
         "Copy Hovered Text",
         this.copyHoveredTextKeybind.hasMouseBind() ? this.copyHoveredTextKeybind.getIntValue() - 100 : this.copyHoveredTextKeybind.getIntValue(),
         "CheatBreaker Client",
         true
      );
      keyBinding.setting = this.dropStackKeybind;
      keyBinding2.setting = this.emoteWheelKeybind;
      keyBinding3.setting = this.friendMenuKeybind;
      keyBinding4.setting = this.copyHoveredTextKeybind;
      keyBinding.flag2 = this.dropStackKeybind.isRequireShift();
      keyBinding.flag4 = this.dropStackKeybind.isRequireCtrl();
      keyBinding.flag6 = this.dropStackKeybind.isRequireAlt();
      keyBinding2.flag2 = this.emoteWheelKeybind.isRequireShift();
      keyBinding2.flag4 = this.emoteWheelKeybind.isRequireCtrl();
      keyBinding2.flag6 = this.emoteWheelKeybind.isRequireAlt();
      keyBinding3.flag2 = this.friendMenuKeybind.isRequireShift();
      keyBinding3.flag4 = this.friendMenuKeybind.isRequireCtrl();
      keyBinding3.flag6 = this.friendMenuKeybind.isRequireAlt();
      keyBinding4.flag2 = this.copyHoveredTextKeybind.isRequireShift();
      keyBinding4.flag4 = this.copyHoveredTextKeybind.isRequireCtrl();
      keyBinding4.flag6 = this.copyHoveredTextKeybind.isRequireAlt();
      this.dropStackKeybind.setLinkedKeyBinding(keyBinding);
      this.emoteWheelKeybind.setLinkedKeyBinding(keyBinding2);
      this.friendMenuKeybind.setLinkedKeyBinding(keyBinding3);
      this.copyHoveredTextKeybind.setLinkedKeyBinding(keyBinding4);
      gameSettings.keyBindings = (KeyBinding[])ArrayUtils.addAll(
         gameSettings.keyBindings, new KeyBinding[]{this.voiceChat, this.openVoiceMenu, this.openMenu, keyBinding, keyBinding2, keyBinding3, keyBinding4}
      );
      this.featuredServers.add(ServerIndex.getStringArray("coldnetwork"));
      this.featuredServers.add(ServerIndex.getStringArray("pvprivals"));
   }

   public void removeSavedColor(int n) {
      this.savedColors.removeIf(colorSwatch -> colorSwatch.color == n);
   }

   public void updateActiveAccount() {
      try {
         if (Minecraft.getMinecraft().currentScreen instanceof ModsScreen) {
            String str = this.showActiveAccount.getBooleanValue() ? Minecraft.getMinecraft().getSession().getUsername() : null;
            CheatBreaker.getInstance().getDiscordRpcManager().handleString(str);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void applyKeybinds() {
      this.dropStackKeybind.applyToKeyBinding();
      this.emoteWheelKeybind.applyToKeyBinding();
      this.friendMenuKeybind.applyToKeyBinding();
      this.copyHoveredTextKeybind.applyToKeyBinding();
   }

   public List<String[]> getFeaturedServers() {
      return this.featuredServers;
   }

   public void setCurrentMenu(Setting setting) {
      this.currentMenu = setting;
   }
}

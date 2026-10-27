package com.cheatbreaker.client.mod;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.Event;
import com.cheatbreaker.client.event.IntEvent1;
import com.cheatbreaker.client.event.IntEvent4;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.ui.AnchorPoint;
import com.cheatbreaker.client.ui.AnchorPointUtils;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public abstract class AbstractMod {
   public String description;
   public Minecraft mc = Minecraft.getMinecraft();
   public List<ModCategory> categories;
   public CheatBreaker cheatBreaker = CheatBreaker.getInstance();
   public Map<Class<? extends Event>, Consumer> eventHandlers = new HashMap<>();
   public boolean defaultEnabled = false;
   public boolean useDefaultTranslation = false;
   public AnchorPoint defaultAnchorPoint;
   public HudDisplayType hudDisplayType;
   public List<String> authors;
   public boolean defaultRenderHud = true;
   public float labelScale;
   public boolean defaultWasRenderHud = false;
   public boolean hidden = false;
   public boolean hudOnly = true;
   public boolean staffMod = false;
   public ResourceLocation resourceLocation;
   public boolean staffModEnabled = false;
   public boolean restricted = false;
   public boolean enabled = false;
   public boolean renderHud = true;
   public String[] restrictedServerAddresses;
   public boolean wasRenderHud = false;
   public float iconWidth;
   public List<String> aliases;
   public boolean applyDefaultColorOnEnable = true;
   public boolean showLabelInList = true;
   public boolean canToggle = true;
   public float hudWidth = 0.0F;
   public float hudHeight = 0.0F;
   public AnchorPoint anchorPoint;
   public float defaultTranslationX = 0.0F;
   public float defaultTranslationY = 0.0F;
   public float translationX = 0.0F;
   public String label;
   public float translationY = 0.0F;
   public int iconOffsetX = 0;
   public int iconOffsetY = 0;
   public String name;
   public List<Setting> settings;
   public List<Object> defaultSettingValues;
   public Setting toggleModKeybind;
   public float iconHeight;
   public List<String> keywords;
   public String restrictionMessage;

   public boolean isHudOnly() {
      return this.hudOnly;
   }

   public int getIconOffsetX() {
      return this.iconOffsetX;
   }

   public void setMc(Minecraft mc) {
      this.mc = mc;
   }

   public void setTranslationX(float f) {
      this.translationX = f;
   }

   public void setLabel(String str, float f) {
      this.handleString(str);
      this.labelScale = f;
   }

   public List<Setting> getSettings() {
      return this.settings;
   }

   public void setEnabled(boolean flag) {
      if (flag != this.defaultEnabled) {
         this.cheatBreaker.getConfigManager().run4();
      }

      if (flag) {
         if (!this.enabled) {
            this.enabled = true;
            this.onEnable();
         }
      } else if (this.enabled) {
         this.enabled = false;
         this.onDisable();
      }
   }

   public boolean wasRenderHud() {
      return this.wasRenderHud;
   }

   public boolean getDefaultWasRenderHud() {
      return this.defaultWasRenderHud;
   }

   public Map<Class<? extends Event>, Consumer> getEventHandlers() {
      return this.eventHandlers;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public boolean isRestricted() {
      return this.restricted;
   }

   public boolean canToggle() {
      return this.canToggle;
   }

   public CheatBreaker getCheatBreaker() {
      return this.cheatBreaker;
   }

   public boolean isStaffMod() {
      return this.staffMod;
   }

   public HudAlignment getHudAlignment() {
      return AnchorPointUtils.getHorizontalAlignment(this.anchorPoint);
   }

   public String getLabel() {
      return this.label;
   }

   public Minecraft getMc() {
      return this.mc;
   }

   public boolean matchesCondition(Setting setting, Number number, float f) {
      switch (setting.getStringValue()) {
         case "Above":
            return number.intValue() > f;
         case "Below":
            return number.intValue() < f;
         case "At":
            return number.intValue() == f;
         case "Not At":
            return number.intValue() != f;
         default:
            return false;
      }
   }

   public void setDescription(String str) {
      this.description = str;
   }

   public void setApplyDefaultColorOnEnable(boolean flag) {
      this.applyDefaultColorOnEnable = flag;
   }

   public void onEnable() {
      for (Entry entry : this.eventHandlers.entrySet()) {
         this.cheatBreaker.getEventBus().checkClass2((Class)entry.getKey(), (Consumer)entry.getValue());
      }
   }

   public void applyHudTransform(ScaledResolution scaledResolution, float f, float f2) {
      float f3 = 0.0F;
      float f4 = 0.0F;
      float hudScale = this.getHudScale();
      GL11.glScalef(hudScale, hudScale, hudScale);
      f *= hudScale;
      f2 *= hudScale;
      switch (this.anchorPoint) {
         case LEFT_TOP:
            f3 = 2.0F;
            f4 = 2.0F;
            break;
         case LEFT_MIDDLE:
            f3 = 2.0F;
            f4 = scaledResolution.getScaledHeight() / 2 - f2 / 2.0F;
            break;
         case LEFT_BOTTOM:
            f4 = scaledResolution.getScaledHeight() - f2 - 2.0F;
            f3 = 2.0F;
            break;
         case MIDDLE_TOP:
            f3 = scaledResolution.getScaledWidth() / 2 - f / 2.0F;
            f4 = 2.0F;
            break;
         case MIDDLE_MIDDLE:
            f3 = scaledResolution.getScaledWidth() / 2 - f / 2.0F;
            f4 = scaledResolution.getScaledHeight() / 2 - f2 / 2.0F;
            break;
         case MIDDLE_BOTTOM_LEFT:
            f3 = scaledResolution.getScaledWidth() / 2 - f;
            f4 = scaledResolution.getScaledHeight() - f2 - 2.0F;
            break;
         case MIDDLE_BOTTOM_RIGHT:
            f3 = scaledResolution.getScaledWidth() / 2;
            f4 = scaledResolution.getScaledHeight() - f2 - 2.0F;
            break;
         case RIGHT_TOP:
            f3 = scaledResolution.getScaledWidth() - f - 2.0F;
            f4 = 2.0F;
            break;
         case RIGHT_MIDDLE:
            f3 = scaledResolution.getScaledWidth() - f;
            f4 = scaledResolution.getScaledHeight() / 2 - f2 / 2.0F;
            break;
         case RIGHT_BOTTOM:
            f3 = scaledResolution.getScaledWidth() - f;
            f4 = scaledResolution.getScaledHeight() - f2;
      }

      GL11.glTranslatef(f3 / hudScale, f4 / hudScale, 0.0F);
      GL11.glTranslatef(this.translationX / hudScale, this.translationY / hudScale, 0.0F);
   }

   public void setHidden(boolean flag) {
      this.hidden = flag;
   }

   public List<ModCategory> getCategories() {
      return this.categories;
   }

   public float getTranslationY() {
      return this.translationY;
   }

   public void setIconHeight(float f) {
      this.iconHeight = f;
   }

   public String getName() {
      return this.name;
   }

   public <T extends Event> void addEventHandler(Class<T> clazz, Consumer<T> consumer) {
      this.eventHandlers.put(clazz, consumer);
   }

   public void onKeyPress(IntEvent4 intEvent4) {
      if (!this.staffMod) {
         if (intEvent4.getIntValue() == this.toggleModKeybind.getIntValue() && !this.toggleModKeybind.hasMouseBind()) {
            this.setEnabled(!this.enabled);
         }
      }
   }

   public boolean loadCustomConfig(JsonObject jsonObject) {
      return false;
   }

   public boolean getDefaultEnabled() {
      return this.defaultEnabled;
   }

   public void setDefaultWasRenderHud(boolean flag) {
      this.defaultWasRenderHud = flag;
   }

   public AnchorPoint getAnchorPoint() {
      return this.anchorPoint;
   }

   public <T extends Event> void subscribe(Class<T> clazz, Consumer<T> consumer) {
      this.cheatBreaker.getEventBus().checkClass2(clazz, consumer);
   }

   public boolean isShowLabelInList() {
      return this.showLabelInList;
   }

   public float getDefaultTranslationY() {
      return this.defaultTranslationY;
   }

   public float getIconWidth() {
      return this.iconWidth;
   }

   public void setIconOffsetX(int n) {
      this.iconOffsetX = n;
   }

   public AnchorPoint getDefaultAnchorPoint() {
      return this.defaultAnchorPoint;
   }

   public void setStaffModEnabled(boolean flag) {
      this.staffModEnabled = flag;
      if (!flag && this.isEnabled()) {
         this.setEnabled(false);
      }
   }

   public AbstractMod(String str) {
      this.name = str;
      this.settings = new ArrayList<>();
      this.defaultSettingValues = new ArrayList<>();
      this.toggleModKeybind = new Setting(this, "Toggle Mod Keybind").changeValue(0).setMouseBind(false);
      this.subscribe(IntEvent4.class, this::onKeyPress);
      this.subscribe(IntEvent1.class, this::onMousePress);
   }

   public void setKeywords(String... strs) {
      this.keywords = Arrays.asList(strs);
   }

   public void setRestrictionMessage(String str) {
      this.restrictionMessage = str;
   }

   public HudDisplayType getHudDisplayType() {
      return this.hudDisplayType;
   }

   public void setIconWidth(float f) {
      this.iconWidth = f;
   }

   public List<Object> getDefaultSettingValues() {
      return this.defaultSettingValues;
   }

   public boolean isStaffModEnabled() {
      return this.staffModEnabled;
   }

   public void setAuthors(String... strs) {
      this.authors = Arrays.asList(strs);
   }

   public List<String> getKeywords() {
      return this.keywords;
   }

   public void setToggleModKeybind(Setting setting) {
      this.toggleModKeybind = setting;
   }

   public float[] getHudPosition(ScaledResolution scaledResolution, boolean flag) {
      float f = 0.0F;
      float f2 = 0.0F;
      float f3 = this.hudWidth * this.getHudScale();
      float f4 = this.hudHeight * this.getHudScale();
      switch (this.anchorPoint) {
         case LEFT_TOP:
            f = 2.0F;
            f2 = 2.0F;
            break;
         case LEFT_MIDDLE:
            f = 2.0F;
            f2 = scaledResolution.getScaledHeight() / 2 - f4 / 2.0F;
            break;
         case LEFT_BOTTOM:
            f2 = scaledResolution.getScaledHeight() - f4 - 2.0F;
            f = 2.0F;
            break;
         case MIDDLE_TOP:
            f = scaledResolution.getScaledWidth() / 2 - f3 / 2.0F;
            f2 = 2.0F;
            break;
         case MIDDLE_MIDDLE:
            f = scaledResolution.getScaledWidth() / 2 - f3 / 2.0F;
            f2 = scaledResolution.getScaledHeight() / 2 - f4 / 2.0F;
            break;
         case MIDDLE_BOTTOM_LEFT:
            f = scaledResolution.getScaledWidth() / 2 - f3;
            f2 = scaledResolution.getScaledHeight() - f4 - 2.0F;
            break;
         case MIDDLE_BOTTOM_RIGHT:
            f = scaledResolution.getScaledWidth() / 2;
            f2 = scaledResolution.getScaledHeight() - f4 - 2.0F;
            break;
         case RIGHT_TOP:
            f = scaledResolution.getScaledWidth() - f3 - 2.0F;
            f2 = 2.0F;
            break;
         case RIGHT_MIDDLE:
            f = scaledResolution.getScaledWidth() - f3;
            f2 = scaledResolution.getScaledHeight() / 2 - f4 / 2.0F;
            break;
         case RIGHT_BOTTOM:
            f = scaledResolution.getScaledWidth() - f3;
            f2 = scaledResolution.getScaledHeight() - f4;
      }

      return new float[]{(f + (flag ? this.translationX : 0.0F)) / this.getHudScale(), (f2 + (flag ? this.translationY : 0.0F)) / this.getHudScale()};
   }

   public void setIconOffsetY(int n) {
      this.iconOffsetY = n;
   }

   public float getIconHeight() {
      return this.iconHeight;
   }

   public void setRenderHudAndDefault(boolean flag) {
      this.renderHud = flag;
      this.defaultRenderHud = flag;
   }

   public void setAnchorPoint(AnchorPoint anchorPoint) {
      if (anchorPoint != this.defaultAnchorPoint) {
         this.cheatBreaker.getConfigManager().run4();
      }

      this.anchorPoint = anchorPoint;
   }

   public void setCategories(ModCategory... modCategories) {
      this.categories = Arrays.asList(modCategories);
   }

   public void setTranslation(float f, float f2) {
      this.translationX = f;
      this.translationY = f2;
   }

   public void setDefaultAnchorPoint(AnchorPoint anchorPoint) {
      this.defaultAnchorPoint = anchorPoint;
   }

   public float getHudWidth() {
      return this.hudWidth;
   }

   public void setTranslationY(float f) {
      this.translationY = f;
   }

   public void setShowLabelInList(boolean flag) {
      this.showLabelInList = flag;
   }

   public boolean isRenderHud() {
      return this.renderHud;
   }

   public void applyHudTransformForSize(ScaledResolution scaledResolution) {
      this.applyHudTransform(scaledResolution, this.hudWidth, this.hudHeight);
   }

   public void handleString(String str) {
      this.hudDisplayType = HudDisplayType.LABEL;
      this.label = str;
   }

   public String[] getRestrictedServerAddresses() {
      return this.restrictedServerAddresses;
   }

   public void saveCustomSetting(Setting setting) {
   }

   public float getHudHeight() {
      return this.hudHeight;
   }

   public void onDisable() {
      for (Entry entry : this.eventHandlers.entrySet()) {
         this.cheatBreaker.getEventBus().checkClass((Class)entry.getKey(), (Consumer)entry.getValue());
      }
   }

   public List<String> getAliases() {
      return this.aliases;
   }

   public void setUseDefaultTranslation(boolean flag) {
      this.useDefaultTranslation = flag;
   }

   public void setAnchorPointAndDefault(AnchorPoint anchorPoint) {
      this.anchorPoint = anchorPoint;
      this.defaultAnchorPoint = anchorPoint;
   }

   public void setCheatBreaker(CheatBreaker cheatBreaker) {
      this.cheatBreaker = cheatBreaker;
   }

   public boolean usesDefaultTranslation() {
      return this.useDefaultTranslation;
   }

   public void setEnabledFlag(boolean flag) {
      this.enabled = flag;
   }

   public void setWasRenderHud(boolean flag) {
      this.wasRenderHud = flag;
   }

   public void setAliases(String... strs) {
      this.aliases = Arrays.asList(strs);
   }

   public void setHudSize(float f, float f2) {
      this.hudWidth = f;
      this.hudHeight = f2;
   }

   public void setResourceLocation(ResourceLocation resourceLocation) {
      this.resourceLocation = resourceLocation;
   }

   public int getIconOffsetY() {
      return this.iconOffsetY;
   }

   public float getHudScale() {
      return 1.0F;
   }

   public void setCanToggle(boolean flag) {
      this.canToggle = flag;
   }

   public boolean isApplyDefaultColorOnEnable() {
      return this.applyDefaultColorOnEnable;
   }

   public Setting getToggleModKeybind() {
      return this.toggleModKeybind;
   }

   public void setHudOnly(boolean flag) {
      this.hudOnly = flag;
   }

   public void writeSettingComments(BufferedWriter bufferedWriter, Setting setting) throws java.io.IOException {
   }

   public String getDescription() {
      return this.description;
   }

   public void setTranslationAndDefault(float f, float f2) {
      this.translationX = f;
      this.translationY = f2;
      this.defaultTranslationX = f;
      this.defaultTranslationY = f2;
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public void onMousePress(IntEvent1 intEvent1) {
      if (intEvent1.getEventButton() != 0) {
         if (intEvent1.getEventButton() == this.toggleModKeybind.getIntValue() && this.toggleModKeybind.hasMouseBind()) {
            this.setEnabled(!this.enabled);
         }
      }
   }

   public void setDefaultTranslationY(float f) {
      this.defaultTranslationY = f;
   }

   public float getTranslationX() {
      return this.translationX;
   }

   public void setCurrentAnchorPoint(AnchorPoint anchorPoint) {
      this.anchorPoint = anchorPoint;
   }

   public void setStaffMod(boolean flag) {
      this.staffMod = flag;
   }

   public void setEnabledByDefault(boolean flag) {
      if (flag) {
         if (!this.enabled) {
            this.enabled = true;
            this.onEnable();
         }
      } else if (this.enabled) {
         this.enabled = false;
         this.onDisable();
      }

      this.wasRenderHud = flag;
      this.defaultEnabled = this.enabled;
   }

   public void setRenderHud(boolean flag) {
      if (flag != this.defaultRenderHud) {
         this.cheatBreaker.getConfigManager().run4();
      }

      this.renderHud = flag;
   }

   public void beforeSave() {
   }

   public void setHudHeight(float f) {
      this.hudHeight = f;
   }

   public float getDefaultTranslationX() {
      return this.defaultTranslationX;
   }

   public void setRestrictedServerAddresses(String[] strs) {
      this.restrictedServerAddresses = strs;
   }

   public void setHudDisplayType(HudDisplayType hudDisplayType) {
      this.hudDisplayType = hudDisplayType;
   }

   public boolean getDefaultRenderHud() {
      return this.defaultRenderHud;
   }

   public void saveCustomConfig(JsonObject jsonObject) {
   }

   public String getRestrictionMessage() {
      return this.restrictionMessage;
   }

   public void setDefaultTranslationX(float f) {
      this.defaultTranslationX = f;
   }

   public void setLabelScale(float f) {
      this.labelScale = f;
   }

   public void renderPreview(int n, int n2, int n3, int n4) {
   }

   public boolean hasSetting(String str) {
      for (Setting setting : this.getSettings()) {
         if (setting.getName().equalsIgnoreCase(str)) {
            return true;
         }
      }

      return false;
   }

   public void setIcon(ResourceLocation resourceLocation, int n, int n2) {
      this.hudDisplayType = HudDisplayType.ICON;
      this.resourceLocation = resourceLocation;
      this.iconWidth = n;
      this.iconHeight = n2;
   }

   public ResourceLocation getResourceLocation() {
      return this.resourceLocation;
   }

   public void setRestriction(String str, String... strs) {
      this.restrictionMessage = str;
      this.restrictedServerAddresses = strs;
   }

   public void setHudWidth(float f) {
      this.hudWidth = f;
   }

   public List<String> getAuthors() {
      return this.authors;
   }

   public void setRestricted(boolean flag) {
      this.restricted = flag;
   }

   public float getLabelScale() {
      return this.labelScale;
   }
}

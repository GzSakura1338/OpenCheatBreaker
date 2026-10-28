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
    public float iconWidth;
    public Minecraft mc = Minecraft.getMinecraft();
    public CheatBreaker cheatBreaker = CheatBreaker.getInstance();
    public Map<Class<? extends Event>, Consumer> eventHandlers = new HashMap<>();
    public boolean defaultEnabled = false;
    public boolean useDefaultTranslation = false;
    public boolean defaultRenderHud = true;
    public boolean defaultWasRenderHud = false;
    public AnchorPoint anchorPoint;
    public boolean hidden = false;
    public boolean hudOnly = true;
    public boolean staffMod = false;
    public boolean staffModEnabled = false;
    public String description;
    public boolean restricted = false;
    public List<String> authors;
    public HudDisplayType hudDisplayType;
    public boolean enabled = false;
    public List<String> keywords;
    public boolean renderHud = true;
    public boolean wasRenderHud = false;
    public boolean applyDefaultColorOnEnable = true;
    public boolean showLabelInList = true;
    public boolean canToggle = true;
    public float labelScale;
    public float hudWidth = 0.0F;
    public List<ModCategory> categories;
    public float hudHeight = 0.0F;
    public List<String> aliases;
    public float defaultTranslationX = 0.0F;
    public String label;
    public ResourceLocation resourceLocation;
    public String restrictionMessage;
    public String[] restrictedServerAddresses;
    public float defaultTranslationY = 0.0F;
    public float translationX = 0.0F;
    public float translationY = 0.0F;
    public int iconOffsetX = 0;
    public float iconHeight;
    public int iconOffsetY = 0;
    public String name;
    public List<Setting> settings;
    public List<Object> defaultSettingValues;
    public Setting toggleModKeybind;
    public AnchorPoint defaultAnchorPoint;

    public boolean isStaffMod() {
        return this.staffMod;
    }

    public void setHudDisplayType(HudDisplayType hudDisplayType) {
        this.hudDisplayType = hudDisplayType;
    }

    public boolean canToggle() {
        return this.canToggle;
    }

    public void setIconWidth(float f) {
        this.iconWidth = f;
    }

    public void setEnabled2(boolean flag) {
        this.enabled = flag;
    }

    public boolean wasRenderHud() {
        return this.wasRenderHud;
    }

    public void setRenderHud(boolean flag) {
        if (flag != this.defaultRenderHud) {
            this.cheatBreaker.getConfigManager().ensureProfileExists();
        }

        this.renderHud = flag;
    }

    public List<Object> getDefaultSettingValues() {
        return this.defaultSettingValues;
    }

    public void setTranslationY(float f) {
        this.translationY = f;
    }

    public boolean isHudOnly() {
        return this.hudOnly;
    }

    public void setRestricted(boolean flag) {
        this.restricted = flag;
    }

    public Minecraft getMc() {
        return this.mc;
    }

    public float getHudScale() {
        return 1.0F;
    }

    public void setAliases(String... strs) {
        this.aliases = Arrays.asList(strs);
    }

    public void setEnabledByDefault(boolean flag) throws java.io.IOException {
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

    public String getLabel() {
        return this.label;
    }

    public void setStaffMod(boolean flag) {
        this.staffMod = flag;
    }

    public void setRestrictionMessage(String str) {
        this.restrictionMessage = str;
    }

    public void setCheatBreaker(CheatBreaker cheatBreaker) {
        this.cheatBreaker = cheatBreaker;
    }

    public void saveCustomSetting(Setting setting) {
    }

    public void setHidden(boolean flag) {
        this.hidden = flag;
    }

    public void setLabel(String str) {
        this.hudDisplayType = HudDisplayType.LABEL;
        this.label = str;
    }

    public void setHudHeight(float f) {
        this.hudHeight = f;
    }

    public boolean isDefaultEnabled() {
        return this.defaultEnabled;
    }

    public List<String> getAliases() {
        return this.aliases;
    }

    public <T extends Event> void addEventHandler(Class<T> clazz, Consumer<T> consumer) {
        this.eventHandlers.put(clazz, consumer);
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public String[] getRestrictedServerAddresses() {
        return this.restrictedServerAddresses;
    }

    public void setDefaultWasRenderHud(boolean flag) {
        this.defaultWasRenderHud = flag;
    }

    public void setDefaultTranslationX(float f) {
        this.defaultTranslationX = f;
    }

    public void setRestrictedServerAddresses(String[] strs) {
        this.restrictedServerAddresses = strs;
    }

    public void setAuthors(String... strs) {
        this.authors = Arrays.asList(strs);
    }

    public String getRestrictionMessage() {
        return this.restrictionMessage;
    }

    public void setTranslationX(float f) {
        this.translationX = f;
    }

    public void setRestriction(String str, String... strs) {
        this.restrictionMessage = str;
        this.restrictedServerAddresses = strs;
    }

    public ResourceLocation getResourceLocation() {
        return this.resourceLocation;
    }

    public void setHudOnly(boolean flag) {
        this.hudOnly = flag;
    }

    public void setIconHeight(float f) {
        this.iconHeight = f;
    }

    public void onDisable() {
        for (Entry entry : this.eventHandlers.entrySet()) {
            this.cheatBreaker.getEventBus().checkClass((Class)entry.getKey(), (Consumer)entry.getValue());
        }
    }

    public void setMc(Minecraft mc) {
        this.mc = mc;
    }

    public void setHudSize(float f, float f2) {
        this.hudWidth = f;
        this.hudHeight = f2;
    }

    public void setAnchorPoint(AnchorPoint anchorPoint) {
        if (anchorPoint != this.defaultAnchorPoint) {
            this.cheatBreaker.getConfigManager().ensureProfileExists();
        }

        this.anchorPoint = anchorPoint;
    }

    public boolean loadCustomConfig(JsonObject jsonObject) {
        return false;
    }

    public List<String> getKeywords() {
        return this.keywords;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setDefaultTranslationY(float f) {
        this.defaultTranslationY = f;
    }

    public CheatBreaker getCheatBreaker() {
        return this.cheatBreaker;
    }

    public float getLabelScale() {
        return this.labelScale;
    }

    public void setIconOffsetY(int n) {
        this.iconOffsetY = n;
    }

    public void setIconOffsetX(int n) {
        this.iconOffsetX = n;
    }

    public void setKeywords(String... strs) {
        this.keywords = Arrays.asList(strs);
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

    public void setAnchorPoint2(AnchorPoint anchorPoint) {
        this.anchorPoint = anchorPoint;
    }

    public float getIconHeight() {
        return this.iconHeight;
    }

    public float getIconWidth() {
        return this.iconWidth;
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

    public boolean isDefaultRenderHud() {
        return this.defaultRenderHud;
    }

    public Map<Class<? extends Event>, Consumer> getEventHandlers() {
        return this.eventHandlers;
    }

    public boolean isApplyDefaultColorOnEnable() {
        return this.applyDefaultColorOnEnable;
    }

    public void applyHudTransformForSize(ScaledResolution scaledResolution) {
        this.applyHudTransform(scaledResolution, this.hudWidth, this.hudHeight);
    }

    public boolean isRenderHud() {
        return this.renderHud;
    }

    public boolean isRestricted() {
        return this.restricted;
    }

    public int getIconOffsetY() {
        return this.iconOffsetY;
    }

    public void renderPreview(int n, int n2, int n3, int n4) {
    }

    public AnchorPoint getDefaultAnchorPoint() {
        return this.defaultAnchorPoint;
    }

    public boolean isDefaultWasRenderHud() {
        return this.defaultWasRenderHud;
    }

    public int getIconOffsetX() {
        return this.iconOffsetX;
    }

    public <T extends Event> void subscribe(Class<T> clazz, Consumer<T> consumer) {
        this.cheatBreaker.getEventBus().checkClass2(clazz, consumer);
    }

    public float getHudHeight() {
        return this.hudHeight;
    }

    public void setUseDefaultTranslation(boolean flag) {
        this.useDefaultTranslation = flag;
    }

    public float getHudWidth() {
        return this.hudWidth;
    }

    public void setHudWidth(float f) {
        this.hudWidth = f;
    }

    public void setAnchorPointAndDefault(AnchorPoint anchorPoint) {
        this.anchorPoint = anchorPoint;
        this.defaultAnchorPoint = anchorPoint;
    }

    public void setLabelScale(float f) {
        this.labelScale = f;
    }

    public void onMousePress(IntEvent1 intEvent1) { try { try { try { try { try { try { try { try { try { try { try { try { try {
        if (intEvent1.getEventButton() != 0) {
            if (intEvent1.getEventButton() == this.toggleModKeybind.getIntValue() && this.toggleModKeybind.hasMouseBind()) {
                this.setEnabled(!this.enabled);
            }
        }
    } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } }

    public void setWasRenderHud(boolean flag) {
        this.wasRenderHud = flag;
    }

    public void onKeyPress(IntEvent4 intEvent4) { try { try { try { try { try { try { try { try { try { try { try { try { try {
        if (!this.staffMod) {
            if (intEvent4.getIntValue() == this.toggleModKeybind.getIntValue() && !this.toggleModKeybind.hasMouseBind()) {
                this.setEnabled(!this.enabled);
            }
        }
    } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } }

    public void setApplyDefaultColorOnEnable(boolean flag) {
        this.applyDefaultColorOnEnable = flag;
    }

    public float getDefaultTranslationY() {
        return this.defaultTranslationY;
    }

    public float getTranslationX() {
        return this.translationX;
    }

    public AnchorPoint getAnchorPoint() {
        return this.anchorPoint;
    }

    public void setTranslation(float f, float f2) {
        this.translationX = f;
        this.translationY = f2;
    }

    public List<ModCategory> getCategories() {
        return this.categories;
    }

    public boolean isStaffModEnabled() {
        return this.staffModEnabled;
    }

    public void setCategories(ModCategory... modCategories) {
        this.categories = Arrays.asList(modCategories);
    }

    public String getName() {
        return this.name;
    }

    public void setLabel(String str, float f) {
        this.setLabel(str);
        this.labelScale = f;
    }

    public void setIcon(ResourceLocation resourceLocation, int n, int n2) {
        this.hudDisplayType = HudDisplayType.ICON;
        this.resourceLocation = resourceLocation;
        this.iconWidth = n;
        this.iconHeight = n2;
    }

    public void setShowLabelInList(boolean flag) {
        this.showLabelInList = flag;
    }

    public void setDescription(String str) {
        this.description = str;
    }

    public String getDescription() {
        return this.description;
    }

    public void writeSettingComments(BufferedWriter bufferedWriter, Setting setting) throws java.io.IOException {
    }

    public Setting getToggleModKeybind() {
        return this.toggleModKeybind;
    }

    public void beforeSave() {
    }

    public float getDefaultTranslationX() {
        return this.defaultTranslationX;
    }

    public void setDefaultAnchorPoint(AnchorPoint anchorPoint) {
        this.defaultAnchorPoint = anchorPoint;
    }

    public void setStaffModEnabled(boolean flag) throws java.io.IOException {
        this.staffModEnabled = flag;
        if (!flag && this.isEnabled()) {
            this.setEnabled(false);
        }
    }

    public boolean hasSetting(String str) {
        for (Setting setting : this.getSettings()) {
            if (setting.getName().equalsIgnoreCase(str)) {
                return true;
            }
        }

        return false;
    }

    public void setRenderHudAndDefault(boolean flag) {
        this.renderHud = flag;
        this.defaultRenderHud = flag;
    }

    public List<Setting> getSettings() {
        return this.settings;
    }

    public float getTranslationY() {
        return this.translationY;
    }

    public HudAlignment getHudAlignment() {
        return AnchorPointUtils.getHorizontalAlignment(this.anchorPoint);
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

    public List<String> getAuthors() {
        return this.authors;
    }

    public boolean isShowLabelInList() {
        return this.showLabelInList;
    }

    public void setCanToggle(boolean flag) {
        this.canToggle = flag;
    }

    public void setResourceLocation(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    public AbstractMod(String str) {
        this.name = str;
        this.settings = new ArrayList<>();
        this.defaultSettingValues = new ArrayList<>();
        this.toggleModKeybind = new Setting(this, "Toggle Mod Keybind").changeValue(0).setMouseBind(false);
        this.subscribe(IntEvent4.class, this::onKeyPress);
        this.subscribe(IntEvent1.class, this::onMousePress);
    }

    public HudDisplayType getHudDisplayType() {
        return this.hudDisplayType;
    }

    public void setToggleModKeybind(Setting setting) {
        this.toggleModKeybind = setting;
    }

    public void setEnabled(boolean flag) throws java.io.IOException {
        if (flag != this.defaultEnabled) {
            this.cheatBreaker.getConfigManager().ensureProfileExists();
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

    public void onEnable() throws java.io.IOException {
        for (Entry entry : this.eventHandlers.entrySet()) {
            this.cheatBreaker.getEventBus().checkClass2((Class)entry.getKey(), (Consumer)entry.getValue());
        }
    }

    public boolean usesDefaultTranslation() {
        return this.useDefaultTranslation;
    }

    public void setTranslationAndDefault(float f, float f2) {
        this.translationX = f;
        this.translationY = f2;
        this.defaultTranslationX = f;
        this.defaultTranslationY = f2;
    }

    public void saveCustomConfig(JsonObject jsonObject) {
    }
}

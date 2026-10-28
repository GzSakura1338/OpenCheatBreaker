package com.cheatbreaker.client.mod.setting;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class Setting {
    public String description = "";
    public int keyCode;
    public Object value;
    public Object maximumValue;
    public boolean requireShift;
    public String[] stringValues;
    public String unit = "";
    public String centerLabel = "";
    public boolean speed;
    public String leftIconName = "";
    public boolean hasKeycode;
    public String rightIconName = "";
    public Object[] acceptedValues;
    public String offText = "OFF";
    public Setting[] invertedHierarchyParents;
    public Object customizationLevel;
    public Setting[] optionalParents;
    public Setting[] hierarchyParents;
    public boolean hasMouseBind;
    public String onText = "ON";
    public int[] color;
    public boolean useInvertedParent = false;
    public boolean chroma;
    public boolean requireCtrl;
    public boolean defaultKeybindCaptured = false;
    public BooleanSupplier condition;
    public boolean showValue = true;
    public Setting[] optionalHierarchyParents;
    public float leftIconScale = 10.0F;
    public boolean requireAlt;
    public Object minimumValue;
    public String defaultStringValue;
    public float rightIconScale = 10.0F;
    public float leftIconXOffset = 0.0F;
    public String[] aliases;
    public float rightIconXOffset = 0.0F;
    public Consumer<Object> onUpdate;
    public float leftIconYOffset = 0.0F;
    public float rightIconYOOffset = 0.0F;
    public KeyBinding linkedKeyBinding;
    public Setting[] invertedParents;
    public Setting[] parents;
    public boolean defaultKeybindHasMouseBind;
    public Object defaultKeybindValue;
    public AbstractMod container;
    public String name;

    public Setting setRequireShift(boolean flag) {
        this.requireShift = flag;
        return this;
    }

    public Setting setRightIconScale(float f) {
        this.rightIconScale = f;
        return this;
    }

    public Setting setAliases(String... strs) {
        this.aliases = strs;
        return this;
    }

    public Setting setInvertedHierarchyParents(Setting[] settings) {
        this.invertedHierarchyParents = settings;
        return this;
    }

    public Setting withInvertedHierarchyParents(Setting... settings) {
        for (Setting setting : settings) {
            if (setting.getType() != SettingType.BOOLEAN) {
                throw new IllegalStateException("Parent can only be boolean.");
            }
        }

        this.invertedHierarchyParents = settings;
        return this;
    }

    public Setting withHierarchyParents(Setting... settings) {
        for (Setting setting : settings) {
            if (setting.getType() != SettingType.BOOLEAN) {
                throw new IllegalStateException("Parent can only be boolean.");
            }
        }

        this.hierarchyParents = settings;
        return this;
    }

    public Setting setLeftIconYOffset(float f) {
        this.leftIconYOffset = f;
        return this;
    }

    public boolean isRequireAlt() {
        return this.requireAlt;
    }

    public Setting enableKeyCode(int n) {
        this.keyCode = n;
        this.hasKeycode = true;
        return this;
    }

    public Setting setChroma(boolean flag) {
        this.chroma = flag;
        return this;
    }

    public AbstractMod getContainer() {
        return this.container;
    }

    public Object getDefaultKeybindValue() {
        return this.defaultKeybindValue;
    }

    public boolean isRequireShift() {
        return this.requireShift;
    }

    public Setting setStringValues(String... strs) {
        this.stringValues = strs;
        return this;
    }

    public Setting setOnText(String str) {
        this.onText = str;
        return this;
    }

    public boolean useInvertedParent() {
        return this.useInvertedParent;
    }

    public Setting setHasKeycode(boolean flag) {
        this.hasKeycode = flag;
        return this;
    }

    public Setting[] getInvertedHierarchyParents() {
        return this.invertedHierarchyParents;
    }

    public String getRightIconName() {
        return this.rightIconName;
    }

    public ArrayList getListValue() {
        return (ArrayList)this.value;
    }

    public BooleanSupplier getCondition() {
        return this.condition;
    }

    public Setting setLeftIconScale(float f) {
        this.leftIconScale = f;
        return this;
    }

    public Setting setAliases2(String[] strs) {
        this.aliases = strs;
        return this;
    }

    public Setting setCenterLabel(String str) {
        this.centerLabel = str;
        return this;
    }

    public Color toColor(int n) {
        float f = (n >> 24 & 0xFF) / 255.0F;
        float f2 = (n >> 16 & 0xFF) / 255.0F;
        float f3 = (n >> 8 & 0xFF) / 255.0F;
        float f4 = (n & 0xFF) / 255.0F;
        return new Color(f2, f3, f4, f);
    }

    public String getOnText() {
        return this.onText;
    }

    public Object getValue() {
        return this.value;
    }

    public Object getMaximumValue() {
        return this.maximumValue;
    }

    public Setting withOptionalHierarchyParents(Setting... settings) {
        for (Setting setting : settings) {
            if (setting.getType() != SettingType.BOOLEAN) {
                throw new IllegalStateException("Parent can only be boolean.");
            }
        }

        this.optionalHierarchyParents = settings;
        return this;
    }

    public Setting setIconNames(String str, String str2) {
        this.leftIconName = str;
        this.rightIconName = str2;
        return this;
    }

    public int getColorValue() {
        if (this.chroma) {
            Integer value = (Integer)this.value;
            int n = value >> 24 & 0xFF;
            float f = (float)System.nanoTime() / 1.0E10F % 1.0F;
            return this.speed
                ? n << 24 | Color.HSBtoRGB((float)(System.currentTimeMillis() % 1000L) / 1000.0F, 1.0F, 1.0F) & 16777215
                : n << 24 | Color.HSBtoRGB(f, 1.0F, 1.0F) & 16777215;
        } else {
            return (Integer)this.value;
        }
    }

    public Setting setHierarchyParents(Setting[] settings) {
        this.hierarchyParents = settings;
        return this;
    }

    public boolean isChroma() {
        return this.chroma;
    }

    public Consumer<Object> getOnUpdate() {
        return this.onUpdate;
    }

    @Override
    public String toString() {
        return "Setting(parents="
            + Arrays.deepToString(this.getParents())
            + ", invertedParents="
            + Arrays.deepToString(this.getInvertedParents())
            + ", optionalParents="
            + Arrays.deepToString(this.getOptionalParents())
            + ", hierarchyParents="
            + Arrays.deepToString(this.getHierarchyParents())
            + ", invertedHierarchyParents="
            + Arrays.deepToString(this.getInvertedHierarchyParents())
            + ", optionalHierarchyParents="
            + Arrays.deepToString(this.getOptionalHierarchyParents())
            + ", condition="
            + this.getCondition()
            + ", onUpdate="
            + this.getOnUpdate()
            + ", container="
            + this.getContainer()
            + ", name="
            + this.getName()
            + ", description="
            + this.getDescription()
            + ", aliases="
            + Arrays.deepToString(this.getAliases())
            + ", defaultStringValue="
            + this.getDefaultStringValue()
            + ", unit="
            + this.getUnit()
            + ", centerLabel="
            + this.getCenterLabel()
            + ", leftIconName="
            + this.getLeftIconName()
            + ", rightIconName="
            + this.getRightIconName()
            + ", offText="
            + this.getOffText()
            + ", onText="
            + this.getOnText()
            + ", stringValues="
            + Arrays.deepToString(this.getStringValues())
            + ", color="
            + Arrays.toString(this.getColor())
            + ", keyCode="
            + this.getKeyCode()
            + ", useInvertedParent="
            + this.useInvertedParent()
            + ", hasKeycode="
            + this.hasKeycode()
            + ", hasMouseBind="
            + this.hasMouseBind()
            + ", requireShift="
            + this.isRequireShift()
            + ", requireCtrl="
            + this.isRequireCtrl()
            + ", requireAlt="
            + this.isRequireAlt()
            + ", defaultKeybindCaptured="
            + this.isDefaultKeybindCaptured()
            + ", defaultKeybindValue="
            + this.getDefaultKeybindValue()
            + ", defaultKeybindHasMouseBind="
            + this.isDefaultKeybindHasMouseBind()
            + ", linkedKeyBinding="
            + this.getLinkedKeyBinding()
            + ", showValue="
            + this.showValue()
            + ", chroma="
            + this.isChroma()
            + ", speed="
            + this.isSpeed()
            + ", value="
            + this.getValue()
            + ", minimumValue="
            + this.getMinimumValue()
            + ", maximumValue="
            + this.getMaximumValue()
            + ", customizationLevel="
            + this.getCustomizationLevel()
            + ", acceptedValues="
            + Arrays.deepToString(this.getAcceptedValues())
            + ", leftIconScale="
            + this.getLeftIconScale()
            + ", rightIconScale="
            + this.getRightIconScale()
            + ", leftIconXOffset="
            + this.getLeftIconXOffset()
            + ", rightIconXOffset="
            + this.getRightIconXOffset()
            + ", leftIconYOffset="
            + this.getLeftIconYOffset()
            + ", rightIconYOOffset="
            + this.getRightIconYOOffset()
            + ")";
    }

    public Setting setDefaultKeybindCaptured(boolean flag) {
        this.defaultKeybindCaptured = flag;
        return this;
    }

    public Setting withParents(Setting... settings) {
        for (Setting setting : settings) {
            if (setting.getType() != SettingType.BOOLEAN) {
                throw new IllegalStateException("Parent can only be boolean.");
            }
        }

        this.parents = settings;
        return this;
    }

    public int[] getColor() {
        return this.color;
    }

    public Setting[] getInvertedParents() {
        return this.invertedParents;
    }

    public boolean isDefaultKeybindHasMouseBind() {
        return this.defaultKeybindHasMouseBind;
    }

    public boolean showValue() {
        return this.showValue;
    }

    public Setting setCondition(BooleanSupplier booleanSupplier) {
        this.condition = booleanSupplier;
        return this;
    }

    public float getRightIconXOffset() {
        return this.rightIconXOffset;
    }

    public float getLeftIconScale() {
        return this.leftIconScale;
    }

    public String getCenterLabel() {
        return this.centerLabel;
    }

    public Setting setDescription(String str) {
        this.description = str;
        return this;
    }

    public Setting setRightIconYOOffset(float f) {
        this.rightIconYOOffset = f;
        return this;
    }

    public Object[] getAcceptedValues() {
        return this.acceptedValues;
    }

    public Setting setParents(Setting[] settings) {
        this.parents = settings;
        return this;
    }

    public void applyToKeyBinding() {
        if (this.linkedKeyBinding != null) {
            int n = this.hasMouseBind() ? this.getIntValue() - 100 : this.getIntValue();
            this.linkedKeyBinding.setKeyCode(n);
            this.linkedKeyBinding.flag6 = this.requireShift;
            this.linkedKeyBinding.flag5 = this.requireCtrl;
            this.linkedKeyBinding.flag7 = this.requireAlt;
            KeyBinding.resetKeyBindingArrayAndHash();
        }
    }

    public boolean areParentsSatisfied(Setting[] settings, boolean flag, boolean flag2) {
        if (settings == null) {
            return true;
        }

        boolean flag3 = flag2 == flag;

        for (Setting setting : settings) {
            if (flag2 != (flag == setting.getBooleanValue())) {
                flag3 = flag2 != flag;
            }
        }

        return flag != flag3;
    }

    public Object getCustomizationLevel() {
        return this.customizationLevel;
    }

    public SettingType getType() {
        if (this.value.getClass().isAssignableFrom(Boolean.class)) {
            return SettingType.BOOLEAN;
        }

        if (this.value.getClass().isAssignableFrom(ArrayList.class)) {
            return SettingType.ARRAYLIST;
        }

        if (!this.value.getClass().isAssignableFrom(String.class)) {
            if (this.value.getClass().isAssignableFrom(Float.class)) {
                return SettingType.FLOAT;
            } else if (this.value.getClass().isAssignableFrom(Double.class)) {
                return SettingType.DOUBLE;
            } else if (this.value.getClass().isAssignableFrom(String[].class)) {
                return SettingType.STRING_ARRAY;
            } else {
                return this.value.getClass().isAssignableFrom(Integer.class) ? SettingType.INTEGER : null;
            }
        } else {
            return this.stringValues != null && this.stringValues.length != 0 ? SettingType.STRING_ARRAY : SettingType.STRING;
        }
    }

    public Setting setColor(int[] ints) {
        this.color = ints;
        return this;
    }

    public String getDefaultStringValue() {
        return this.defaultStringValue;
    }

    public Setting withInvertedParents(Setting... settings) {
        for (Setting setting : settings) {
            if (setting.getType() != SettingType.BOOLEAN) {
                throw new IllegalStateException("Parent can only be boolean.");
            }
        }

        this.invertedParents = settings;
        return this;
    }

    public boolean areModifiersDown() {
        if (this.requireShift && !Keyboard.isKeyDown(42) && !Keyboard.isKeyDown(54)) {
            return false;
        } else {
            return this.requireCtrl && !Keyboard.isKeyDown(29) && !Keyboard.isKeyDown(157)
                ? false
                : !this.requireAlt || Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
        }
    }

    public Setting setRequireCtrl(boolean flag) {
        this.requireCtrl = flag;
        return this;
    }

    public Setting setIconXOffsets(float f, float f2) {
        this.leftIconXOffset = f;
        this.rightIconXOffset = f2;
        return this;
    }

    public Setting setDefaultStringValue(String str) {
        this.defaultStringValue = str;
        return this;
    }

    public Setting setUseInvertedParent(boolean flag) {
        this.useInvertedParent = flag;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Setting setKeyCode(int n) {
        this.keyCode = n;
        return this;
    }

    public float getRightIconScale() {
        return this.rightIconScale;
    }

    public String getOffText() {
        return this.offText;
    }

    public Setting setMaximumValue(Object obj) {
        this.maximumValue = obj;
        return this;
    }

    public Setting setOnUpdate(Consumer<Object> consumer) {
        this.onUpdate = consumer;
        return this;
    }

    public int getKeyCode() {
        return this.keyCode;
    }

    public float getRightIconYOOffset() {
        return this.rightIconYOOffset;
    }

    public Setting changeValueNotify(Object obj, boolean flag) {
        this.value = obj;
        if (CheatBreaker.getInstance().getConfigManager().profile != null && CheatBreaker.getInstance().getConfigManager().profile.getName().equals("default")) {
            if (flag) {
                CheatBreaker.getInstance().getConfigManager().ensureProfileExists();
            }
        } else if (this.container != null) {
            this.container.getDefaultSettingValues().add(obj);
        }

        if (this.onUpdate != null
            && !(this instanceof ButtonSetting)
            && Minecraft.getMinecraft().currentScreen instanceof ModsScreen
            && Minecraft.getMinecraft().theWorld != null) {
            this.onUpdate.accept(obj);
        }

        return this;
    }

    public Setting resetKeybind() {
        if (this.defaultKeybindCaptured) {
            this.changeValueNotify(this.defaultKeybindValue, false);
            this.hasMouseBind = this.defaultKeybindHasMouseBind;
        }

        this.requireShift = false;
        this.requireCtrl = false;
        this.requireAlt = false;
        this.applyToKeyBinding();
        return this;
    }

    public Setting setToggleTexts(String str, String str2) {
        this.offText = str;
        this.onText = str2;
        return this;
    }

    public Setting setOptionalHierarchyParents(Setting[] settings) {
        this.optionalHierarchyParents = settings;
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public Setting setAcceptedValues(Integer... integers) {
        this.acceptedValues = integers;
        return this;
    }

    public String[] getStringValues() {
        return this.stringValues;
    }

    public Setting setUnit(String str) {
        this.unit = str;
        return this;
    }

    public Setting setContainer(AbstractMod abstractMod) {
        this.container = abstractMod;
        return this;
    }

    public Setting setValue(Object obj) {
        this.value = obj;
        return this;
    }

    public Setting setIconScales(float f, float f2) {
        this.leftIconScale = f;
        this.rightIconScale = f2;
        return this;
    }

    public float getFloatValue() {
        return (Float)this.value;
    }

    public Setting setRequireAlt(boolean flag) {
        this.requireAlt = flag;
        return this;
    }

    public Setting[] getOptionalParents() {
        return this.optionalParents;
    }

    public Setting setRightIconXOffset(float f) {
        this.rightIconXOffset = f;
        return this;
    }

    public boolean getBooleanValue() {
        return (Boolean)this.value;
    }

    public float getLeftIconXOffset() {
        return this.leftIconXOffset;
    }

    public Setting setIconYOffsets(float f, float f2) {
        this.leftIconYOffset = f;
        this.rightIconYOOffset = f2;
        return this;
    }

    public Setting setInvertedParents(Setting[] settings) {
        this.invertedParents = settings;
        return this;
    }

    public boolean hasKeycode() {
        return this.hasKeycode;
    }

    public String getKeybindText() {
        StringBuilder sb = new StringBuilder();
        if (this.requireCtrl) {
            sb.append("Ctrl+");
        }

        if (this.requireShift) {
            sb.append("Shift+");
        }

        if (this.requireAlt) {
            sb.append("Alt+");
        }

        if (this.hasMouseBind) {
            sb.append("Button ").append(this.getIntValue() + 1);
        } else {
            String keyName = Keyboard.getKeyName(this.getIntValue());
            sb.append(keyName != null ? keyName : "UNIDENTIFIED (" + this.getIntValue() + ")");
        }

        return sb.toString().toUpperCase();
    }

    public Setting setLeftIconName(String str) {
        this.leftIconName = str;
        return this;
    }

    public String getStringValue() {
        return (String)this.value;
    }

    public Setting[] getHierarchyParents() {
        return this.hierarchyParents;
    }

    public Setting changeValue(Object obj) {
        return this.changeValueNotify(obj, true);
    }

    public Setting setRange(Object obj, Object obj2) {
        this.minimumValue = obj;
        this.maximumValue = obj2;
        return this;
    }

    public KeyBinding getLinkedKeyBinding() {
        return this.linkedKeyBinding;
    }

    public Setting withOptionalParents(Setting... settings) {
        for (Setting setting : settings) {
            if (setting.getType() != SettingType.BOOLEAN) {
                throw new IllegalStateException("Parent can only be boolean.");
            }
        }

        this.optionalParents = settings;
        return this;
    }

    public float getLeftIconYOffset() {
        return this.leftIconYOffset;
    }

    public Setting[] getParents() {
        return this.parents;
    }

    public Setting setStringValues2(String[] strs) {
        this.stringValues = strs;
        return this;
    }

    public Setting setOffText(String str) {
        this.offText = str;
        return this;
    }

    public Setting setShowValue(boolean flag) {
        this.showValue = flag;
        return this;
    }

    public boolean isKeybindDown() {
        boolean flag = Keyboard.isKeyDown(this.getIntValue()) && !this.hasMouseBind() || Mouse.isButtonDown(this.getIntValue()) && this.hasMouseBind();
        return flag && this.areModifiersDown();
    }

    public Setting setMinimumValue(Object obj) {
        this.minimumValue = obj;
        return this;
    }

    public Setting setSpeed(boolean flag) {
        this.speed = flag;
        return this;
    }

    public boolean hasMouseBind() {
        return this.hasMouseBind;
    }

    public Setting setDefaultKeybindHasMouseBind(boolean flag) {
        this.defaultKeybindHasMouseBind = flag;
        return this;
    }

    public Setting setLinkedKeyBinding(KeyBinding keyBinding) {
        this.linkedKeyBinding = keyBinding;
        return this;
    }

    public Object getMinimumValue() {
        return this.minimumValue;
    }

    public Setting(AbstractMod abstractMod, String str) {
        this.container = abstractMod;
        abstractMod.getSettings().add(this);
        this.name = str;
    }

    public boolean isRequireCtrl() {
        return this.requireCtrl;
    }

    public Setting setDefaultKeybindValue(Object obj) {
        this.defaultKeybindValue = obj;
        return this;
    }

    public double getDoubleValue() {
        return (Double)this.value;
    }

    public String getUnit() {
        return this.unit;
    }

    public String getLeftIconName() {
        return this.leftIconName;
    }

    public Setting setOptionalParents(Setting[] settings) {
        this.optionalParents = settings;
        return this;
    }

    public Setting[] getOptionalHierarchyParents() {
        return this.optionalHierarchyParents;
    }

    public Setting(List<Setting> list, String str) {
        list.add(this);
        this.name = str;
    }

    public Setting setMouseBind(boolean flag) {
        this.hasMouseBind = flag;
        if (!this.defaultKeybindCaptured) {
            this.defaultKeybindCaptured = true;
            this.defaultKeybindValue = this.value;
            this.defaultKeybindHasMouseBind = flag;
        }

        return this;
    }

    public Setting setCustomizationLevel(Object obj) {
        this.customizationLevel = obj;
        return this;
    }

    public boolean isSpeed() {
        return this.speed;
    }

    public boolean isDefaultKeybindCaptured() {
        return this.defaultKeybindCaptured;
    }

    public Setting setAcceptedValues(Object[] objs) {
        this.acceptedValues = objs;
        return this;
    }

    public Setting setLeftIconXOffset(float f) {
        this.leftIconXOffset = f;
        return this;
    }

    public int getIntValue() {
        return (Integer)this.value;
    }

    public Setting setRightIconName(String str) {
        this.rightIconName = str;
        return this;
    }

    public String[] getAliases() {
        return this.aliases;
    }
}

package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.AbstractInternalMod;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModManager;
import com.cheatbreaker.client.mod.impl.AutoTextMod;
import com.cheatbreaker.client.mod.setting.ButtonSetting;
import com.cheatbreaker.client.mod.setting.CrosshairSetting;
import com.cheatbreaker.client.mod.setting.FormatSetting;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import com.cheatbreaker.client.mod.setting.SettingType;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.theme.Theme;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import com.cheatbreaker.client.util.DeveloperFlags;
import com.cheatbreaker.client.util.TextUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

public class SettingsPanel extends Panel {
    public Minecraft mc = Minecraft.getMinecraft();
    public CheatBreaker cheatBreaker = CheatBreaker.getInstance();
    public final boolean unusedFlag = false;
    public Panel panel;
    public boolean showingGlobalSettings = false;
    public boolean showingThemeSettings = false;
    public KeybindComponent keybindComponent;
    public Button resetPosition64Png = null;
    public int infoLineCount = 0;
    public List<SettingComponent> themeComponents;
    public float floatValue2;
    public SettingsNavigationItem cheatbreakerSettings;
    public SettingsNavigationItem themeManager;
    public Theme theme3;
    public List<ModEntry> modTiles;
    public List<SettingComponent> searchPool;
    public Map<AbstractMod, ArrayList<SettingComponent>> modSettingComponents;
    public TextField search;
    public Button arrow64Png;
    public Button resetCog64Png;
    public AbstractMod abstractMod;
    public Map<AbstractMod, ArrayList<SettingComponent>> visibleModSettings;
    public List<SettingComponent> visibleGlobalSettings;
    public Button addAnother;

    public List<SettingComponent> buildAllModSettingComponents() {
        List abstractModList = CheatBreaker.getInstance().getModManager().abstractModList;
        List abstractNamedModList = CheatBreaker.getInstance().getModManager().abstractNamedModList;
        List abstractInternalModList = CheatBreaker.getInstance().getModManager().abstractInternalModList;
        ArrayList list = new ArrayList();
        list.addAll(abstractModList);
        list.addAll(abstractNamedModList);
        list.addAll(abstractInternalModList);

        for (AbstractMod abstractMod : (Iterable<AbstractMod>)list) {
            if ((!(abstractMod instanceof AbstractInternalMod) || DeveloperFlags.showInternalMods) && (!abstractMod.isStaffMod() || abstractMod.isStaffModEnabled())) {
                ArrayList list2 = new ArrayList();

                for (Setting setting : abstractMod.getSettings()) {
                    switch (setting.getType()) {
                        case BOOLEAN:
                            if (setting instanceof SettingGroup) {
                                list2.add(new SettingGroupComponent(setting, this.floatValue2));
                            } else {
                                list2.add(new BooleanComponent(setting, this.floatValue2));
                            }
                            break;
                        case DOUBLE:
                        case INTEGER:
                        case FLOAT:
                            if (abstractMod.isStaffMod() && setting == abstractMod.toggleModKeybind) {
                                break;
                            }

                            if (setting.getType().equals(SettingType.INTEGER) && setting.getName().toLowerCase().contains("color")) {
                                list2.add(new ColorComponent(setting, this.floatValue2));
                            } else {
                                if (setting.getType().equals(SettingType.INTEGER) && setting.getName().endsWith("Keybind")) {
                                    KeybindComponent keybindComponent = new KeybindComponent(setting, this.floatValue2);
                                    list2.add(keybindComponent);
                                    this.cheatBreaker.getModManager().settingToKeybindComponentMap.put(setting, keybindComponent);
                                    break;
                                }

                                list2.add(new SliderComponent(setting, this.floatValue2));
                            }
                            break;
                        case ARRAYLIST:
                            list2.add(new ListComponent(setting, this.floatValue2));
                            break;
                        case STRING_ARRAY:
                            list2.add(new SelectorComponent(setting, this.floatValue2));
                            break;
                        case STRING:
                            if (setting instanceof ButtonSetting) {
                                list2.add(new ButtonComponent(setting, this.floatValue2));
                            } else if (setting instanceof FormatSetting) {
                                list2.add(new TextSettingComponent(setting, this.floatValue2));
                            } else if (!setting.getName().endsWith("String") && !setting.getName().contains("Background)")) {
                                if (!setting.getName().startsWith("Hot key") || !setting.hasKeycode() && !setting.hasMouseBind()) {
                                    if (setting.getName().equalsIgnoreCase("label")) {
                                        list2.add(new SettingGroupComponent(setting, this.floatValue2));
                                        if (setting.getValue().equals("Crosshair Preview")) {
                                            list2.add(new CrosshairComponent(this.floatValue2));
                                        }
                                    }
                                } else {
                                    TextSettingComponent textSettingComponent = new TextSettingComponent(setting, this.floatValue2);
                                    list2.add(textSettingComponent);
                                }
                            } else {
                                TextSettingComponent textSettingComponent = new TextSettingComponent(setting, this.floatValue2);
                                list2.add(textSettingComponent);
                            }
                    }
                }

                if (abstractMod.isStaffMod()) {
                    list2.add(new KeybindComponent(abstractMod.toggleModKeybind, this.floatValue2));
                    if (abstractMod == this.cheatBreaker.getModManager().xray) {
                        list2.add(new BlockListComponent(this.cheatBreaker.getModManager().xray.getVisibleBlockIds(), "Blocks", this.floatValue2));
                    }
                }

                this.visibleModSettings.put(abstractMod, list2);
            }
        }

        return this.visibleModSettings.getOrDefault(this.abstractMod, new ArrayList<>());
    }

    public void goBack() {
        if (this.showingThemeSettings) {
            this.showingThemeSettings = false;
            this.showingGlobalSettings = false;
            ModsScreen.modsScreen.currentPanel = ModsScreen.modsScreen.themeManagerPanel;
            this.theme3 = null;
        } else if (this.showingGlobalSettings) {
            this.showingGlobalSettings = false;
            if (this.panel != null) {
                ModsScreen.modsScreen.currentPanel = ModsScreen.modsScreen.settingsPanel;
            }
        } else {
            if (this.abstractMod != null) {
                ModsScreen.modsScreen.currentPanel = ModsScreen.modsScreen.modListPanel;
            }

            this.abstractMod = null;
        }
    }

    @Override
    public boolean hasSettings(AbstractMod abstractMod) {
        return !abstractMod.getSettings().isEmpty();
    }

    public void buildThemeComponents() {
        this.themeComponents = new ArrayList<>();
        if (this.theme3 != null) {
            for (Setting setting : this.theme3.getSettingList()) {
                switch (setting.getType()) {
                    case BOOLEAN:
                        if (setting instanceof SettingGroup) {
                            this.themeComponents.add(new SettingGroupComponent(setting, this.floatValue2));
                        } else {
                            this.themeComponents.add(new BooleanComponent(setting, this.floatValue2));
                        }
                        break;
                    case DOUBLE:
                    case INTEGER:
                    case FLOAT:
                        if (setting.getType().equals(SettingType.INTEGER) && setting.getName().toLowerCase().contains("color")) {
                            this.themeComponents.add(new ColorComponent(setting, this.floatValue2));
                        } else if (setting != this.cheatBreaker.getKeybindSettings().getCurrentMenu()) {
                            this.themeComponents.add(new SliderComponent(setting, this.floatValue2));
                        }
                    case ARRAYLIST:
                    default:
                        break;
                    case STRING_ARRAY:
                        this.themeComponents.add(new SelectorComponent(setting, this.floatValue2));
                        break;
                    case STRING:
                        if (setting instanceof ButtonSetting) {
                            this.themeComponents.add(new ButtonComponent(setting, this.floatValue2));
                        } else if (setting instanceof FormatSetting) {
                            this.themeComponents.add(new ThemeNameComponent(setting, this.theme3, this.floatValue2));
                        } else if (setting.getName().equalsIgnoreCase("label")) {
                            this.themeComponents.add(new SettingGroupComponent(setting, this.floatValue2));
                        }
                }
            }
        }
    }

    @Override
    public void tick() {
        this.search.tick();
    }

    public void drawInfoText(String str) {
        this.drawWrappedText(str, this.theme2.getTextColor2Value2());
    }

    @Override
    public void keyTyped(char c, int n) {
        super.keyTyped(c, n);
        if (this.keybindComponent != null) {
            this.keybindComponent.keyTyped(c, n);
        }

        if (this.abstractMod != null) {
            for (SettingComponent settingComponent : this.visibleModSettings.get(this.abstractMod)) {
                if (settingComponent.setting == null || !settingComponent.isHidden(settingComponent.setting) || settingComponent instanceof SliderComponent) {
                    settingComponent.keyTyped(c, n);
                }
            }
        }

        if (this.theme3 != null) {
            for (SettingComponent settingComponent : this.themeComponents) {
                if (settingComponent.setting == null || !settingComponent.isHidden(settingComponent.setting)) {
                    settingComponent.keyTyped(c, n);
                }
            }
        } else {
            if (this.search.isFocused()) {
                this.search.keyTyped(c, n);
            } else if ((Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157)) && Keyboard.isKeyDown(33) && !this.search.isFocused()) {
                this.search.setFocused(true);
            }

            if (this.showingGlobalSettings && this.search.isFocused()) {
                this.visibleGlobalSettings.clear();
                this.visibleGlobalSettings.addAll(this.filterBySearch(this.search.getText()));
            }

            if (this.abstractMod != null && this.search.isFocused()) {
                this.visibleModSettings.get(this.abstractMod).clear();
                this.visibleModSettings.get(this.abstractMod).addAll(this.filterBySearch(this.search.getText()));
            }

            if (n == 14 && !this.search.isFocused()) {
                this.goBack();
            }
        }
    }

    @Override
    public void mouseClicked(int n, int n2, int n3) throws java.io.IOException {
        if (!this.search.isMouseOverScaled(n, n2) && this.search.isFocused()) {
            this.search.setFocused(false);
        } else if (this.search.isMouseOverScaled(n, n2) && !this.search.isFocused()) {
            this.search.setFocused(true);
        }

        if (this.abstractMod == null && !this.showingThemeSettings && !this.showingGlobalSettings) {
            if (this.cheatbreakerSettings.isMouseOver(n, n2)) {
                this.mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
                this.showingGlobalSettings = true;
                this.scrollOffset = 5;
                this.pendingScroll = 0.0;
                this.scrollOffsetY = 0;
            } else if (this.themeManager.isMouseOver(n, n2)) {
                this.mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
                this.panel = ModsScreen.modsScreen.settingsPanel;
                ModsScreen.modsScreen.currentPanel = ModsScreen.modsScreen.themeManagerPanel;
                this.scrollOffset = 5;
                this.pendingScroll = 0.0;
                this.scrollOffsetY = 0;
            } else {
                for (ModEntry modEntry : this.modTiles) {
                    if (modEntry.isMouseOver(n, n2) && this.hasSettings(modEntry.abstractMod)) {
                        modEntry.mouseClicked(n, n2, n3);
                    }
                }
            }
        } else if (!this.arrow64Png.isMouseOver(n, n2)) {
            if (this.resetCog64Png.isMouseOver(n, n2)) {
                ModManager modManager = this.cheatBreaker.getModManager();
                this.mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
                if (this.abstractMod == this.cheatBreaker.getModManager().autoTextMod) {
                    this.abstractMod.getSettings().removeIf(setting -> setting.getName().toLowerCase().startsWith("hot key"));
                    ((AutoTextMod)this.abstractMod).hotkeySettings.clear();
                    ((AutoTextMod)this.abstractMod).nextHotkeyIndex = 1;
                    this.cheatBreaker.getUiManager().openMod(modManager.text);
                    return;
                }

                for (int i = 0; i < this.abstractMod.getSettings().size(); i++) {
                    try {
                        Setting setting = this.abstractMod.getSettings().get(i);
                        if (this.abstractMod.getSettings().get(i).hasKeycode() || this.abstractMod.getSettings().get(i).hasMouseBind()) {
                            setting.setMouseBind(false);
                            setting.enableKeyCode(0);
                        }

                        setting.changeValueNotify(this.abstractMod.getDefaultSettingValues().get(i), false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                this.cheatBreaker.getUiManager().openMod(modManager.text);
            }

            try {
                if (this.resetPosition64Png.isMouseOver(n, n2)) {
                    this.mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
                    this.abstractMod.setTranslation(this.abstractMod.defaultTranslationX, this.abstractMod.defaultTranslationY);
                    this.abstractMod.setAnchorPoint(this.abstractMod.getDefaultAnchorPoint());
                }
            } catch (NullPointerException e) {
            }

            if (this.addAnother.isMouseOver(n, n2) && this.abstractMod == this.cheatBreaker.getModManager().autoTextMod) {
                this.cheatBreaker.getModManager().autoTextMod.addHotkey();
                this.mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
                this.cheatBreaker.getUiManager().openMod(this.cheatBreaker.getModManager().text);
            }

            if (this.abstractMod != null && this.visibleModSettings.containsKey(this.abstractMod)) {
                for (SettingComponent settingComponent : this.visibleModSettings.get(this.abstractMod)) {
                    if (settingComponent.isMouseOver(n, n2) && !settingComponent.isHidden(settingComponent.setting)) {
                        settingComponent.mouseClicked(n, n2, n3);
                    }
                }
            } else if (this.showingGlobalSettings) {
                for (SettingComponent settingComponent : this.visibleGlobalSettings) {
                    if (settingComponent.isMouseOver(n, n2) && !settingComponent.isHidden(settingComponent.setting)) {
                        settingComponent.mouseClicked(n, n2, n3);
                    }
                }
            } else if (this.showingThemeSettings) {
                for (SettingComponent settingComponent : this.themeComponents) {
                    if (settingComponent.isMouseOver(n, n2) && !settingComponent.isHidden(settingComponent.setting)) {
                        settingComponent.mouseClicked(n, n2, n3);
                    }
                }
            }
        } else if (this.arrow64Png.isMouseOver(n, n2)) {
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            this.goBack();
            return;
        }

        double d = this.height - 10;
        double contentHeight = this.contentHeight;
        double d2 = d / contentHeight * 100.0;
        double d3 = d / 100.0 * d2;
        double d4 = this.scrollOffset / 100.0 * d2;
        boolean flag = n > (this.x + this.width - 9) * this.scale
            && n < (this.x + this.width - 3) * this.scale
            && n2 > (this.y + 11 - d4) * this.scale
            && n2 < (this.y + 8 + d3 - d4) * this.scale;
        boolean flag2 = n > (this.x + this.width - 9) * this.scale
            && n < (this.x + this.width - 3) * this.scale
            && n2 > (this.y + 11) * this.scale
            && n2 < (this.y + 6 + d - 3.0) * this.scale;
        if (n3 == 0 && flag2 || flag) {
            this.dragging = true;
        }
    }

    public List<SettingComponent> buildGlobalSettingComponents() {
        ArrayList list = new ArrayList();

        for (Setting setting : this.cheatBreaker.getKeybindSettings().allSettings) {
            switch (setting.getType()) {
                case BOOLEAN:
                    if (setting instanceof SettingGroup) {
                        SettingGroupComponent settingGroupComponent = new SettingGroupComponent(setting, this.floatValue2);
                        list.add(settingGroupComponent);
                    } else {
                        BooleanComponent booleanComponent = new BooleanComponent(setting, this.floatValue2);
                        list.add(booleanComponent);
                    }
                    break;
                case DOUBLE:
                case INTEGER:
                case FLOAT:
                    if (setting.getType().equals(SettingType.INTEGER) && setting.getName().toLowerCase().contains("color")) {
                        ColorComponent colorComponent = new ColorComponent(setting, this.floatValue2);
                        list.add(colorComponent);
                    } else if (setting.getType().equals(SettingType.INTEGER) && setting.getName().endsWith("Keybind")) {
                        KeybindComponent keybindComponent = new KeybindComponent(setting, this.floatValue2);
                        list.add(keybindComponent);
                        this.cheatBreaker.getModManager().settingToKeybindComponentMap.put(setting, keybindComponent);
                    } else if (setting != this.cheatBreaker.getKeybindSettings().getCurrentMenu()) {
                        SliderComponent sliderComponent = new SliderComponent(setting, this.floatValue2);
                        list.add(sliderComponent);
                    }
                case ARRAYLIST:
                default:
                    break;
                case STRING_ARRAY:
                    SelectorComponent selectorComponent = new SelectorComponent(setting, this.floatValue2);
                    list.add(selectorComponent);
                    break;
                case STRING:
                    if (setting instanceof ButtonSetting) {
                        ButtonComponent buttonComponent = new ButtonComponent(setting, this.floatValue2);
                        list.add(buttonComponent);
                    } else if (setting.getName().equalsIgnoreCase("label")) {
                        SettingGroupComponent settingGroupComponent = new SettingGroupComponent(setting, this.floatValue2);
                        list.add(settingGroupComponent);
                    }
            }
        }

        return list;
    }

    @Override
    public void selectMod(AbstractMod abstractMod) {
        this.mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
        this.scrollOffset = 0;
        this.pendingScroll = 0.0;
        this.scrollOffsetY = 0;
        this.abstractMod = abstractMod;
        this.search.setText("");
        this.panel = null;
    }

    public void drawWrappedText(String str, int n) {
        for (String str2 : TextUtils.wrapSequence(str).setMaxWidth(85).setHyphenate(false).wrapToString().split("\n")) {
            float f = this.x + 38;
            float f2 = (float)(this.y + 19) + this.infoLineCount * 9;
            this.cheatBreaker.getFontManager().ubuntuM16.drawString(str2, f, f2, n);
            this.infoLineCount++;
        }
    }

    public SettingsPanel(List<AbstractMod> list, float f, int n, int n2, int n3, int n4) {
        super(f, n, n2, n3, n4);
        this.floatValue2 = f;
        int normalHighlightColorValue = this.theme2.getNormalHighlightColorValue();
        this.cheatbreakerSettings = new SettingsNavigationItem("CheatBreaker Settings", normalHighlightColorValue, f);
        this.themeManager = new SettingsNavigationItem("Theme Manager", normalHighlightColorValue, f);
        this.modTiles = new ArrayList<>();
        this.searchPool = new ArrayList<>();
        this.modSettingComponents = new HashMap<>();
        this.search = new TextField(
            CheatBreaker.getInstance().getFontManager().playRegular14,
            "Search...",
            this.theme2.getButtonBackgroundColorValue(),
            this.theme2.getPrimaryTextColorValue()
        );
        this.search.setMaxStringLength(256);

        for (AbstractMod abstractMod : list) {
            if ((!(abstractMod instanceof AbstractInternalMod) || DeveloperFlags.showInternalMods) && (!abstractMod.isStaffMod() || abstractMod.isStaffModEnabled())) {
                this.modTiles.add(new ModEntry(this, normalHighlightColorValue, abstractMod, f));
                if (abstractMod.getAnchorPoint() != null) {
                    int n5 = this.x + 2;
                    int n6 = this.y + 4;
                    this.resetPosition64Png = new Button(
                        this.cheatBreaker.getFontManager().ubuntuM16, null, "reset-position-64.png", n5, n6, 28, 28, normalHighlightColorValue, f, false, true
                    );
                }
            }
        }

        this.arrow64Png = new Button(null, "arrow-64.png", this.x + 2, this.y + 4, 28, 28, normalHighlightColorValue, f, false, true);
        int n5 = this.x + 2;
        int n6 = this.y + 4;
        this.resetCog64Png = new Button(this.cheatBreaker.getFontManager().ubuntuM16, null, "reset-cog-64.png", n5, n6, 28, 28, normalHighlightColorValue, f, false, true);
        this.abstractMod = null;
        this.visibleModSettings = new HashMap<>();

        for (AbstractMod abstractMod : list) {
            if ((!(abstractMod instanceof AbstractInternalMod) || DeveloperFlags.showInternalMods) && (!abstractMod.isStaffMod() || abstractMod.isStaffModEnabled())) {
                ArrayList list2 = new ArrayList();

                for (Setting setting : abstractMod.getSettings()) {
                    switch (setting.getType()) {
                        case BOOLEAN:
                            if (setting instanceof SettingGroup) {
                                list2.add(new SettingGroupComponent(setting, f));
                            } else {
                                list2.add(new BooleanComponent(setting, f));
                            }
                            break;
                        case DOUBLE:
                        case INTEGER:
                        case FLOAT:
                            if (abstractMod.isStaffMod() && setting == abstractMod.toggleModKeybind) {
                                break;
                            }

                            if (setting.getType().equals(SettingType.INTEGER) && setting.getName().toLowerCase().contains("color")) {
                                list2.add(new ColorComponent(setting, f));
                            } else {
                                if (setting.getType().equals(SettingType.INTEGER) && setting.getName().endsWith("Keybind")) {
                                    KeybindComponent keybindComponent = new KeybindComponent(setting, f);
                                    list2.add(keybindComponent);
                                    this.cheatBreaker.getModManager().settingToKeybindComponentMap.put(setting, keybindComponent);
                                    break;
                                }

                                list2.add(new SliderComponent(setting, f));
                            }
                            break;
                        case ARRAYLIST:
                            list2.add(new ListComponent(setting, f));
                            break;
                        case STRING_ARRAY:
                            if (setting instanceof CrosshairSetting) {
                                list2.add(new CrosshairComponent(f));
                            }

                            list2.add(new SelectorComponent(setting, f));
                            break;
                        case STRING:
                            if (setting instanceof ButtonSetting) {
                                list2.add(new ButtonComponent(setting, f));
                            } else if (setting instanceof FormatSetting) {
                                list2.add(new TextSettingComponent(setting, f));
                            } else if (!setting.getName().endsWith("String") && !setting.getName().contains("Background)")) {
                                if (!setting.getName().startsWith("Hot key") || !setting.hasKeycode() && !setting.hasMouseBind()) {
                                    if (setting.getName().equalsIgnoreCase("label")) {
                                        list2.add(new SettingGroupComponent(setting, f));
                                    }
                                } else {
                                    TextSettingComponent textSettingComponent = new TextSettingComponent(setting, f);
                                    list2.add(textSettingComponent);
                                }
                            } else {
                                TextSettingComponent textSettingComponent = new TextSettingComponent(setting, f);
                                list2.add(textSettingComponent);
                            }
                    }
                }

                if (abstractMod.isStaffMod()) {
                    list2.add(new KeybindComponent(abstractMod.toggleModKeybind, f));
                    if (abstractMod == this.cheatBreaker.getModManager().xray) {
                        list2.add(new BlockListComponent(this.cheatBreaker.getModManager().xray.getVisibleBlockIds(), "Blocks", f));
                    }
                }

                this.visibleModSettings.put(abstractMod, list2);
                this.modSettingComponents.put(abstractMod, list2);
            }
        }

        this.visibleGlobalSettings = new ArrayList<>(this.buildGlobalSettingComponents());
        this.searchPool.addAll(this.visibleGlobalSettings);
        int n7 = 25;

        for (SettingComponent settingComponent : this.visibleGlobalSettings) {
            n7 += settingComponent.getHeight();
        }

        n5 = this.x + this.width - 120;
        this.addAnother = new Button(
            this.cheatBreaker.getFontManager().playBold18,
            null,
            "Add Another",
            n5,
            this.y + n7 + 4,
            110,
            28,
            this.theme2.getNormalHighlightColorValue(),
            f,
            false,
            true
        );
    }

    public List<SettingComponent> filterBySearch(String str) {
        if (str != null && str.length() != 0) {
            List<SettingComponent> list;
            if (this.abstractMod == null) {
                list = this.searchPool;
            } else {
                list = this.modSettingComponents.get(this.abstractMod);
            }

            return (List<SettingComponent>)(list.stream()
                .filter(
                    settingComponent -> {
                        if (!CheatBreaker.getInstance().getKeybindSettings().bypassCustomizationLevel.getBooleanValue()
                            && this.isHiddenByCustomizationLevel(settingComponent.setting)) {
                            return false;
                        }

                        if (settingComponent.setting.getAliases() != null) {
                            for (String str2 : settingComponent.setting.getAliases()) {
                                if (str2.toLowerCase().contains(str.toLowerCase())) {
                                    return true;
                                }
                            }
                        }

                        return settingComponent.setting.getName().toLowerCase().contains(str.toLowerCase()) && !(settingComponent instanceof SettingGroupComponent);
                    }
                )
                .collect(Collectors.toList()));
        } else {
            return new ArrayList<>(this.showingGlobalSettings ? this.buildGlobalSettingComponents() : this.buildAllModSettingComponents());
        }
    }

    @Override
    public void render(int n, int n2, float f) {
        RenderUtils.drawRoundedRectFilledPrecise(this.x, this.y, this.x + this.width, this.y + this.height + 2, 8.0, this.theme2.getMenuBackgroundColorValue());
        this.updateScroll(n, n2);
        this.cheatBreaker.getModManager().setIntValue2(this.scrollOffset);
        if (this.abstractMod == null && !this.showingThemeSettings && !this.showingGlobalSettings) {
            int n3 = this.cheatBreaker.getModManager().abstractModList.size() - 37;
            int n4 = 65;

            for (int i = 0; i < n3; i += 5) {
                if (n3 > 10) {
                    n4 = (int)(n4 + n3 / 1.8);
                } else {
                    n4++;
                }
            }

            this.contentHeight = (int)(this.cheatBreaker.getModManager().abstractModList.size() * 0.5) + n4;
            this.getClass();
            this.cheatbreakerSettings.setBounds(this.x + 4, this.y + 4, this.width - 12, 18);
            this.cheatbreakerSettings.scrollOffsetY = this.scrollOffset;
            this.cheatbreakerSettings.render(n, n2, f);
            this.contentHeight = this.contentHeight + this.cheatbreakerSettings.getHeight();
            this.themeManager.setBounds(this.x + 4, this.y + 24, this.width - 12, 18);
            this.themeManager.scrollOffsetY = this.scrollOffset;
            this.themeManager.render(n, n2, f);
            this.contentHeight = this.contentHeight + this.themeManager.getHeight();

            for (int i = 0; i < this.modTiles.size(); i++) {
                ModEntry modEntry = this.modTiles.get(i);
                modEntry.setBounds(this.x + 4, this.y + 44 + i * 20, this.width - 12, 18);
                modEntry.scrollOffsetY = this.scrollOffset;
                modEntry.render(n, n2, f);
                this.contentHeight = (int)(this.contentHeight + (modEntry.getHeight() + 0.9));
            }
        } else if (this.showingGlobalSettings && !this.showingThemeSettings) {
            Gui.drawRect(this.x + 32, this.y + 4, this.x + 33, this.y + Math.max(this.height, this.contentHeight) - 4, this.theme2.getPrimaryTextColorValue2());
            this.contentHeight = 25;
            this.arrow64Png.setBounds(this.x + 2, this.y + 2, 28, 28);
            this.arrow64Png.scrollOffsetY = this.scrollOffset;
            this.arrow64Png.render(n, n2, f);
            this.cheatBreaker
                .getFontManager()
                .ubuntuM16
                .drawString("CheatBreaker Settings".toUpperCase(), this.x + 38, this.y + 6, this.theme2.getTextColor4Value());
            Gui.drawRect(this.x + 38, this.y + 17, this.x + this.width - 6, this.y + 18, this.theme2.getPrimaryTextColorValue2());
            this.search.setBounds(this.x + this.width - 112, this.y + 3, 100.0F, 12.0F);
            this.search.renderAndTestHover(n, n2, true);
            int n3 = 0;

            for (SettingComponent settingComponent : this.visibleGlobalSettings) {
                if (!settingComponent.isHidden(settingComponent.setting) || !this.search.getText().equalsIgnoreCase("")) {
                    settingComponent.setBounds(this.x + 38, this.y + 22 + n3, this.width - 40, settingComponent.getHeight());
                    settingComponent.scrollOffsetY = this.scrollOffset;
                    settingComponent.render(n, n2, f);
                    n3 += 2 + settingComponent.getHeight();
                    this.contentHeight = this.contentHeight + 2 + settingComponent.getHeight();
                }
            }
        } else if (!this.showingGlobalSettings && this.showingThemeSettings) {
            Gui.drawRect(this.x + 32, this.y + 4, this.x + 33, this.y + Math.max(this.height, this.contentHeight) - 4, this.theme2.getPrimaryTextColorValue2());
            this.contentHeight = 25;
            this.arrow64Png.setBounds(this.x + 2, this.y + 2, 28, 28);
            this.arrow64Png.scrollOffsetY = this.scrollOffset;
            this.arrow64Png.render(n, n2, f);
            this.cheatBreaker
                .getFontManager()
                .ubuntuM16
                .drawString(
                    this.theme3.getThemeNameValue().toUpperCase() + " Theme Settings".toUpperCase(), this.x + 38, this.y + 6, this.theme2.getTextColor2Value()
                );
            Gui.drawRect(this.x + 38, this.y + 17, this.x + this.width - 6, this.y + 18, this.theme2.getPrimaryTextColorValue2());
            int n3 = 0;

            for (SettingComponent settingComponent : this.themeComponents) {
                if (!settingComponent.isHidden(settingComponent.setting)) {
                    settingComponent.setBounds(this.x + 38, this.y + 22 + n3, this.width - 40, settingComponent.getHeight());
                    settingComponent.scrollOffsetY = this.scrollOffset;
                    settingComponent.render(n, n2, f);
                    n3 += 2 + settingComponent.getHeight();
                    this.contentHeight = this.contentHeight + 2 + settingComponent.getHeight();
                }
            }
        } else if (this.abstractMod != null) {
            Gui.drawRect(this.x + 32, this.y + 4, this.x + 33, this.y + Math.max(this.height, this.contentHeight) - 4, this.theme2.getPrimaryTextColorValue2());
            this.contentHeight = 37;
            this.arrow64Png.setBounds(this.x + 2, this.y + 2, 28, 28);
            this.arrow64Png.scrollOffsetY = this.scrollOffset;
            this.arrow64Png.render(n, n2, f);
            this.resetCog64Png.setBounds(this.x + 2, this.y + 32, 28, 28);
            this.resetCog64Png.scrollOffsetY = this.scrollOffset;
            this.resetCog64Png.render(n, n2, f);
            if (this.abstractMod.getAnchorPoint() != null) {
                this.resetPosition64Png.setBounds(this.x + 2, this.y + 62, 28, 28);
                this.resetPosition64Png.scrollOffsetY = this.scrollOffset;
                this.resetPosition64Png.render(n, n2, f);
            }

            this.cheatBreaker.getModManager().setText(this.abstractMod.getName());
            this.cheatBreaker
                .getFontManager()
                .ubuntuM16
                .drawString((this.abstractMod.getName() + " Settings").toUpperCase(), this.x + 38, this.y + 6, this.theme2.getTextColor4Value());
            Gui.drawRect(this.x + 38, this.y + 17, this.x + this.width - 12, this.y + 18, this.theme2.getPrimaryTextColorValue2());
            this.infoLineCount = 0;
            if (this.abstractMod.getDescription() != null) {
                this.drawInfoText(this.abstractMod.getDescription());
            }

            if (this.abstractMod.getAuthors() != null) {
                this.drawInfoText(
                    "Original creator" + (this.abstractMod.getAuthors().size() != 1 ? "s: " : ": ") + String.join(", ", this.abstractMod.getAuthors())
                );
            }

            if (this.abstractMod.getKeywords() != null) {
                this.drawInfoText("Also known as: " + String.join(", ", this.abstractMod.getKeywords()));
            }

            if (this.abstractMod.isRestricted() && this.abstractMod.getRestrictionMessage() != null) {
                this.drawWrappedText(this.abstractMod.getName() + " is currently restricted:\n" + this.abstractMod.getRestrictionMessage(), -5636096);
            }

            if (this.infoLineCount != 0) {
                Gui.drawRect(
                    this.x + 38,
                    this.y + 21 + this.infoLineCount * 9,
                    this.x + this.width - 12,
                    this.y + 22 + this.infoLineCount * 9,
                    this.theme2.getPrimaryTextColorValue2()
                );
            }

            if (this.abstractMod.getSettings().isEmpty()) {
                this.cheatBreaker
                    .getFontManager()
                    .ubuntuM16
                    .drawString(
                        (this.abstractMod.getName().toUpperCase() + " DOES NOT HAVE ANY OPTIONS.").toUpperCase(),
                        this.x + 38,
                        this.y + (this.abstractMod.getDescription() == null ? 22 : (this.abstractMod.getAuthors() == null ? 35 : 44)),
                        this.theme2.getTextColor2Value()
                    );
            }

            int n3 = 0;

            for (SettingComponent settingComponent : this.visibleModSettings.get(this.abstractMod)) {
                if (settingComponent.setting == null || !settingComponent.isHidden(settingComponent.setting) || !this.search.getText().equalsIgnoreCase("")) {
                    settingComponent.setBounds(this.x + 38, this.y + 26 + this.infoLineCount * 9 + n3, this.width - 40, settingComponent.getHeight());
                    settingComponent.scrollOffsetY = this.scrollOffset;
                    settingComponent.render(n, n2, f);
                    n3 += 2 + settingComponent.getHeight();
                    this.contentHeight = this.contentHeight + 2 + settingComponent.getHeight();
                }
            }

            this.contentHeight = this.contentHeight + (9 * this.infoLineCount - 9);
            this.search.setBounds(this.x + this.width - 112, this.y + 3, 100.0F, 12.0F);
            this.search.renderAndTestHover(n, n2, true);
        }

        if (this.abstractMod == this.cheatBreaker.getModManager().autoTextMod) {
            this.addAnother.scrollOffsetY = this.scrollOffset;
            this.addAnother.setBounds(this.x + this.width - 118, this.y + this.contentHeight + 5, 100, 18);
            this.addAnother.render(n, n2, f);
            this.contentHeight += 24;
        }

        this.contentHeight += 10;
        this.endScrollRegion(n, n2);
    }
}

package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.font.CustomFontRenderer;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import com.cheatbreaker.client.render.RenderUtils;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

public class ModListPanel extends Panel {
    public List<ModListItem> modListItemList = new ArrayList<>();
    public List<ModListItem> modListItemList2 = new ArrayList<>();
    public boolean flag3 = false;
    public CustomFontRenderer customFontRenderer = CheatBreaker.getInstance().getFontManager().playBold16;
    public Button all = new Button(
        this.customFontRenderer,
        null,
        "All",
        this.x + 4,
        this.y + this.height - 20,
        this.x + this.width - 4,
        this.y + this.height - 6,
        this.theme2.getNormalHighlightColorValue(),
        1.0F,
        false,
        true
    );
    public Button hud = new Button(
        this.customFontRenderer,
        null,
        "HUD",
        this.x + 4,
        this.y + this.height - 20,
        this.x + this.width - 4,
        this.y + this.height - 6,
        this.theme2.getNormalHighlightColorValue(),
        1.0F,
        false,
        true
    );
    public Button combat = new Button(
        this.customFontRenderer,
        null,
        "Combat",
        this.x + 4,
        this.y + this.height - 20,
        this.x + this.width - 4,
        this.y + this.height - 6,
        this.theme2.getNormalHighlightColorValue(),
        1.0F,
        false,
        true
    );
    public Button server = new Button(
        this.customFontRenderer,
        null,
        "Server",
        this.x + 4,
        this.y + this.height - 20,
        this.x + this.width - 4,
        this.y + this.height - 6,
        this.theme2.getNormalHighlightColorValue(),
        1.0F,
        false,
        true
    );
    public Button misc = new Button(
        this.customFontRenderer,
        null,
        "Misc",
        this.x + 4,
        this.y + this.height - 20,
        this.x + this.width - 4,
        this.y + this.height - 6,
        this.theme2.getNormalHighlightColorValue(),
        1.0F,
        false,
        true
    );
    public List<Button> buttonList = ImmutableList.of(this.all, this.hud, this.combat, this.server, this.misc);
    public SettingsNavigationItem cheatbreakerSettings;
    public TextField searchMods;
    public Button button;
    public Button cogSearch64Png;
    public Button compact64Png;

    public ModListPanel(List list, float f, int n, int n2, int n3, int n4) {
        super(f, n, n2, n3, n4);

        for (Object obj : list) {
            if (obj != CheatBreaker.getInstance().getModManager().notificationsMod) {
                ModListItem modListItem = new ModListItem(this, (AbstractMod)obj, f);
                this.modListItemList.add(modListItem);
                this.modListItemList2.add(modListItem);
            }
        }

        this.cheatbreakerSettings = new SettingsNavigationItem("CheatBreaker Settings", this.theme2.getNormalHighlightColorValue(), f);
        CustomFontRenderer playBold16 = CheatBreaker.getInstance().getFontManager().playBold16;
        this.searchMods = new TextField(
            CheatBreaker.getInstance().getFontManager().playRegular14,
            "Search Mods...",
            this.theme2.getButtonBackgroundColorValue(),
            this.theme2.getPrimaryTextColorValue()
        );
        this.searchMods.setMaxStringLength(256);

        for (Button button : this.buttonList) {
            button.scale = f;
            if (this.getCategory(button) == CheatBreaker.getInstance().getKeybindSettings().modCategory) {
                button.setSelected(true);
            }
        }

        this.button = new Button(
            playBold16,
            null,
            this.getText(),
            this.x + 4,
            this.y + this.height - 20,
            this.x + this.width - 4,
            this.y + this.height - 6,
            this.theme2.getNormalHighlightColorValue(),
            f,
            false,
            true
        );
        this.cogSearch64Png = new Button(
            playBold16,
            null,
            "cog-search-64.png",
            this.x + 4,
            this.y + this.height - 20,
            this.x + this.width - 4,
            this.y + this.height - 6,
            this.theme2.getNormalHighlightColorValue(),
            f,
            false,
            true
        );
        this.cogSearch64Png.setTinyIcon(true);
        this.compact64Png = new Button(
            playBold16,
            null,
            "compact-64.png",
            this.x + 4,
            this.y + this.height - 20,
            this.x + this.width - 4,
            this.y + this.height - 6,
            this.theme2.getNormalHighlightColorValue(),
            f,
            false,
            true
        );
        this.compact64Png.setTinyIcon(true);
    }

    @Override
    public void keyTyped(char c, int n) {
        if (this.searchMods.isFocused()) {
            this.searchMods.keyTyped(c, n);
            this.refreshList(true);
        } else {
            if ((Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157)) && Keyboard.isKeyDown(33) && !this.searchMods.isFocused()) {
                this.searchMods.setFocused(true);
            }
        }
    }

    @Override
    public void selectMod(AbstractMod abstractMod) {
    }

    public String getText() {
        String toLowerCase = CheatBreaker.getInstance().getKeybindSettings().customizationLevel.getStringValue().toLowerCase();
        switch (toLowerCase) {
            case "medium":
                return "2";
            case "advanced":
                return "3";
            default:
                return "1";
        }
    }

    @Override
    public boolean hasSettings(AbstractMod abstractMod) {
        return false;
    }

    public List<ModListItem> filterMods(String str, boolean flag) {
        return str != null && (str.length() != 0 || !flag)
            ? this.modListItemList2
                .stream()
                .filter(
                    modListItem -> {
                        AbstractMod abstractMod = modListItem.abstractMod;
                        return abstractMod.getCategories() == null
                            ? false
                            : (
                                    abstractMod.getName().toLowerCase().contains(str.toLowerCase())
                                        || this.anyContains(abstractMod.getAuthors(), str)
                                        || this.anyContains(abstractMod.getKeywords(), str)
                                        || this.anyContains(abstractMod.getAliases(), str)
                                        || this.matchesSearch(abstractMod, str)
                                )
                                && abstractMod.getCategories().contains(CheatBreaker.getInstance().getKeybindSettings().modCategory);
                    }
                )
                .collect(Collectors.toList())
            : this.modListItemList2;
    }

    @Override
    public void mouseClicked(int n, int n2, int n3) throws java.io.IOException {
        super.mouseClicked(n, n2, n3);
        if (!this.searchMods.isMouseOverScaled(n, n2) && this.searchMods.isFocused()) {
            this.searchMods.setFocused(false);
        } else if (this.searchMods.isMouseOverScaled(n, n2) && !this.searchMods.isFocused()) {
            this.searchMods.setFocused(true);
        }

        if (this.cheatbreakerSettings.isMouseOver(n, n2) && this.flag3) {
            CheatBreaker.getInstance().getUiManager().openGlobalSettings();
            Minecraft.getMinecraft()
                .getSoundHandler()
                .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
        }

        if (this.cogSearch64Png.isMouseOver(n, n2)) {
            CheatBreaker.getInstance()
                .getKeybindSettings()
                .searchForSettingsInMods
                .changeValue(!CheatBreaker.getInstance().getKeybindSettings().searchForSettingsInMods.getBooleanValue());
            this.refreshList(false);
            Minecraft.getMinecraft()
                .getSoundHandler()
                .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
        }

        if (this.button.isMouseOver(n, n2)) {
            this.refreshList(false);
            this.cycleCustomizationLevel();
            Minecraft.getMinecraft()
                .getSoundHandler()
                .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
        }

        for (Button button : this.buttonList) {
            if (button.isMouseOver(n, n2)) {
                CheatBreaker.getInstance().getKeybindSettings().modCategory = this.getCategory(button);
                this.refreshList(false);
                Minecraft.getMinecraft()
                    .getSoundHandler()
                    .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));

                for (Button button2 : this.buttonList) {
                    if (button2 != button) {
                        button2.setSelected(false);
                        button.setSelected(true);
                    }
                }
            }
        }

        if (this.compact64Png.isMouseOver(n, n2)) {
            Setting modList = CheatBreaker.getInstance().getKeybindSettings().modList;
            String stringValue = modList.getStringValue();
            boolean equalsIgnoreCase = stringValue.equalsIgnoreCase("Compact");
            modList.changeValue(equalsIgnoreCase ? "Normal" : "Compact");
            CheatBreaker.getInstance().getUiManager().openModList();
            Minecraft.getMinecraft()
                .getSoundHandler()
                .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
        }

        for (ModListItem modListItem : this.modListItemList) {
            if (modListItem.isMouseOver(n, n2)) {
                modListItem.mouseClicked(n, n2, n3);
            }
        }
    }

    public ModCategory getCategory(Button button) {
        switch (button.label.toLowerCase()) {
            case "hud":
                return ModCategory.HUD;
            case "combat":
                return ModCategory.COMBAT;
            case "server":
                return ModCategory.SERVER;
            case "misc":
                return ModCategory.MISC;
            default:
                return ModCategory.ALL;
        }
    }

    @Override
    public void tick() {
        this.searchMods.tick();
    }

    public List<ModListItem> filterMods(String str) {
        return str != null && str.length() != 0
            ? this.modListItemList2
                .stream()
                .filter(
                    modListItem -> {
                        AbstractMod abstractMod = modListItem.abstractMod;
                        return abstractMod.getName().toLowerCase().contains(str.toLowerCase())
                            || this.anyContains(abstractMod.getAuthors(), str)
                            || this.anyContains(abstractMod.getKeywords(), str)
                            || this.anyContains(abstractMod.getAliases(), str)
                            || this.matchesSearch(abstractMod, str);
                    }
                )
                .collect(Collectors.toList())
            : this.modListItemList2;
    }

    public boolean anyContains(List<String> list, String str) {
        if (list == null) {
            return false;
        }

        for (String str2 : list) {
            if (str2.toLowerCase().contains(str.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    public boolean matchesSearch(AbstractMod abstractMod, String str) {
        if (abstractMod.getSettings() != null && CheatBreaker.getInstance().getKeybindSettings().searchForSettingsInMods.getBooleanValue()) {
            for (Setting setting : abstractMod.getSettings()) {
                if (!CheatBreaker.getInstance().getKeybindSettings().bypassCustomizationLevel.getBooleanValue() && this.isHiddenByCustomizationLevel(setting)) {
                    return false;
                }

                if (setting.getName().toLowerCase().contains(str.toLowerCase())) {
                    return true;
                }

                if (setting.getAliases() != null) {
                    for (String str2 : setting.getAliases()) {
                        if (str2.toLowerCase().contains(str.toLowerCase())) {
                            return true;
                        }
                    }
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public void cycleCustomizationLevel() {
        Setting customizationLevel = CheatBreaker.getInstance().getKeybindSettings().customizationLevel;
        String toLowerCase = customizationLevel.getStringValue().toLowerCase();
        if (toLowerCase.equalsIgnoreCase("simple")) {
            customizationLevel.changeValue("Medium");
        } else if (toLowerCase.equalsIgnoreCase("medium")) {
            customizationLevel.changeValue("Advanced");
        } else {
            customizationLevel.changeValue("Simple");
        }
    }

    public boolean hasMatchingSetting(String str) {
        if (str != null && str.length() != 0) {
            List<Setting> allSettings = CheatBreaker.getInstance().getKeybindSettings().allSettings;
            return allSettings.stream()
                .anyMatch(
                    setting -> !CheatBreaker.getInstance().getKeybindSettings().bypassCustomizationLevel.getBooleanValue() && this.isHiddenByCustomizationLevel((Setting)setting)
                        ? false
                        : setting.getName().toLowerCase().contains(str.toLowerCase()) && !(setting instanceof SettingGroup)
                );
        } else {
            return false;
        }
    }

    @Override
    public void render(int n, int n2, float f) {
        super.render(n, n2, f);
        this.searchMods.enabledColor = this.theme2.getPrimaryTextColorValue();
        this.searchMods.backgroundColor = this.theme2.getButtonBackgroundColorValue();
        boolean flag = CheatBreaker.getInstance().getKeybindSettings().modList.getValue().equals("Compact");
        int n3 = flag ? 20 : 112;
        byte b = 17;
        RenderUtils.drawRoundedRectFilledPrecise(this.x, this.y, this.x + this.width, this.y + this.height + 2, 8.0, this.theme2.getMenuBackgroundColorValue());
        this.updateScroll(n, n2);
        int n4 = 0;
        int n5 = 0;
        byte b2 = 0;

        for (ModListItem modListItem : this.modListItemList) {
            modListItem.scrollOffsetY = this.scrollOffset;
            modListItem.setBounds(this.x + 4 + n4 * 120, this.y + 4 + n5 * n3 + b, 116, flag ? 16 : 108);
            modListItem.render(n, n2, f);
            if (flag) {
                b2 = 18;
            } else {
                b2 = 112;
            }

            if (++n4 == 3) {
                n5++;
                b2 = 0;
                n4 = 0;
            }
        }

        this.contentHeight = 4 + n5 * n3 + b + b2;
        if (this.flag3) {
            this.cheatbreakerSettings.setBounds(this.x + 4, this.y + this.contentHeight, this.width - 12, 18);
            this.cheatbreakerSettings.scrollOffsetY = this.scrollOffset;
            this.cheatbreakerSettings.render(n, n2, f);
            this.contentHeight = this.contentHeight + this.cheatbreakerSettings.getHeight();
        }

        CustomFontRenderer playBold18 = CheatBreaker.getInstance().getFontManager().playBold18;
        this.compact64Png.setBounds(this.x + 4, this.y + 5, 12, 12);
        this.compact64Png.setSmallIcon(true);
        this.compact64Png.setSelected(flag);
        this.compact64Png.render(n, n2, f);
        this.compact64Png.scrollOffsetY = this.scrollOffset;
        int n6 = 20;

        for (Button button : this.buttonList) {
            button.setBounds(this.x + n6, this.y + 5, playBold18.getStringWidth(button.label) + 8, 12);
            button.scrollOffsetY = this.scrollOffset;
            button.render(n, n2, f);
            n6 += playBold18.getStringWidth(button.label) + 12;
        }

        this.button.setBounds(this.x + n6, this.y + 5, 12, 12);
        this.button.label = this.getText();
        this.button.scrollOffsetY = this.scrollOffset;
        this.button.render(n, n2, f);
        n6 += 16;
        this.cogSearch64Png.setBounds(this.x + n6, this.y + 5, 12, 12);
        this.cogSearch64Png.setSmallIcon(true);
        this.cogSearch64Png.setSelected(CheatBreaker.getInstance().getKeybindSettings().searchForSettingsInMods.getBooleanValue());
        this.cogSearch64Png.scrollOffsetY = this.scrollOffset;
        this.cogSearch64Png.render(n, n2, f);
        n6 += 16;
        this.searchMods.setBounds(this.x + n6, this.y + 5, this.width - n6 - 10, 12.0F);
        this.searchMods.scrollOffsetY = this.scrollOffset;
        this.searchMods.renderAndTestHover(n, n2, true);
        this.endScrollRegion(n, n2);
    }

    public void refreshList(boolean flag) {
        this.modListItemList.clear();
        String text = this.searchMods.getText();
        if (CheatBreaker.getInstance().getKeybindSettings().modCategory == ModCategory.ALL) {
            this.modListItemList.addAll(this.filterMods(text));
        } else {
            this.modListItemList.addAll(this.filterMods(text, flag));
        }

        this.flag3 = this.hasMatchingSetting(text) && CheatBreaker.getInstance().getKeybindSettings().modCategory == ModCategory.ALL;
    }
}

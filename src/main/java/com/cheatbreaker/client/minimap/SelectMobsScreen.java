package com.cheatbreaker.client.minimap;

import com.cheatbreaker.client.util.Translations;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class SelectMobsScreen extends AbstractMinimapScreen {
    public GuiButton optionsMinimapMobsEnable;
    public String title = "Select Mobs";
    public String selectedMob = null;
    public String tooltip = null;
    public GuiButton optionsMinimapMobsDisable;
    public GuiScreen guiScreen;
    public MobListSlot mobListSlot;
    public RadarSettings radarSettings;

    @Override
    public void drawScreen(int n, int n2, float f) throws java.io.IOException {
        super.drawMinimapBackground();
        this.tooltip = null;
        this.mobListSlot.drawScreen(n, n2, f);
        this.drawCenteredString(this.getFontRendererObj(), this.title, this.getWidth() / 2, 20, 16777215);
        boolean flag = this.selectedMob != null;
        this.optionsMinimapMobsEnable.enabled = flag && !this.isMobSelected(this.selectedMob);
        this.optionsMinimapMobsDisable.enabled = flag && this.isMobSelected(this.selectedMob);
        super.drawScreen(n, n2, f);
        if (this.tooltip != null) {
            this.drawTooltip(this.tooltip, n, n2);
        }
    }

    public void setSelectedMob(String str) {
        this.selectedMob = str;
    }

    public void toggleSelected() {
        MobType findMobType = MobType.findMobType(this.selectedMob);
        if (findMobType != null) {
            this.setMobSelected(this.selectedMob, !findMobType.selected);
        } else {
            CustomMob findCustomMob = CustomMobs.findCustomMob(this.selectedMob);
            if (findCustomMob != null) {
                this.setMobSelected(this.selectedMob, !findCustomMob.enabled);
            }
        }
    }

    public static String setTooltip(SelectMobsScreen selectMobsScreen, String str) {
        return selectMobsScreen.tooltip = str;
    }

    public void drawTooltip(String str, int n, int n2) {
        if (str != null) {
            int n3 = n + 12;
            int n4 = n2 - 12;
            int stringWidth = this.getFontRendererObj().getStringWidth(str);
            drawVerticalGradient(n3 - 3, n4 - 3, n3 + stringWidth + 3, n4 + 8 + 3, -1073741824, -1073741824);
            this.getFontRendererObj().drawShadowedString(str, n3, n4, -1);
        }
    }

    public void setMobSelected(String str, boolean flag) {
        for (MobType mobType : MobType.values()) {
            if (mobType.mobName.equals(str)) {
                mobType.selected = flag;
            }
        }

        for (CustomMob customMob : CustomMobs.customMobList) {
            if (customMob.name.equals(str)) {
                customMob.enabled = flag;
            }
        }
    }

    @Override
    public void initGui() {
        boolean flag = false;
        this.title = Translations.translate("options.minimap.mobs.title");
        this.mobListSlot = new MobListSlot(this);
        this.mobListSlot.registerScrollButtons(7, 8);
        this.getButtonList()
            .add(
                this.optionsMinimapMobsEnable = new GuiButton(
                    -1, this.getWidth() / 2 - 154, this.getHeight() - 28, 100, 20, Translations.translate("options.minimap.mobs.enable")
                )
            );
        this.getButtonList()
            .add(
                this.optionsMinimapMobsDisable = new GuiButton(
                    -2, this.getWidth() / 2 - 50, this.getHeight() - 28, 100, 20, Translations.translate("options.minimap.mobs.disable")
                )
            );
        this.getButtonList().add(new GuiButton(65336, this.getWidth() / 2 + 4 + 50, this.getHeight() - 28, 100, 20, Translations.translate("gui.done")));
        boolean flag2 = this.selectedMob != null;
        this.optionsMinimapMobsEnable.enabled = flag2;
        this.optionsMinimapMobsDisable.enabled = flag2;
    }

    public SelectMobsScreen(GuiScreen guiScreen, RadarSettings radarSettings) {
        this.guiScreen = guiScreen;
        this.radarSettings = radarSettings;
    }

    @Override
    public void actionPerformed(GuiButton guiButton) throws java.io.IOException {
        if (guiButton.enabled) {
            if (guiButton.id == -1) {
                this.setMobSelected(this.selectedMob, true);
            }

            if (guiButton.id == -2) {
                this.setMobSelected(this.selectedMob, false);
            }

            if (guiButton.id == 65336) {
                this.getMc().displayGuiScreen(this.guiScreen);
            }
        }
    }

    public boolean isMobSelected(String str) {
        MobType findMobType = MobType.findMobType(this.selectedMob);
        if (findMobType != null) {
            return findMobType.selected;
        }

        CustomMob findCustomMob = CustomMobs.findCustomMob(this.selectedMob);
        return findCustomMob != null ? findCustomMob.enabled : false;
    }
}

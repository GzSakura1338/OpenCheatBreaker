package com.cheatbreaker.client.manager;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModManager;
import com.cheatbreaker.client.ui.component.ModListItem;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class UiManager extends AbstractManager {
    public void openProfiles() throws java.io.IOException {
        ModsScreen modsScreen = new ModsScreen();
        modsScreen.setShowBackground(false);
        Minecraft.getMinecraft().displayGuiScreen(modsScreen);
        modsScreen.currentPanel = modsScreen.profilesPanel;
        ModsScreen.modsScreen.profilesPanel.scrollOffset = CheatBreaker.getInstance().getModManager().intValue2;
    }

    public void openThemes() throws java.io.IOException {
        ModsScreen modsScreen = new ModsScreen();
        modsScreen.setShowBackground(false);
        Minecraft.getMinecraft().displayGuiScreen(modsScreen);
        modsScreen.currentPanel = modsScreen.themeManagerPanel;
        ModsScreen.modsScreen.themeManagerPanel.scrollOffset = CheatBreaker.getInstance().getModManager().intValue2;
    }

    @Override
    public void initialize() {
    }

    public void openMod(String str) throws java.io.IOException {
        ModManager modManager = CheatBreaker.getInstance().getModManager();
        ModsScreen modsScreen = new ModsScreen();
        modsScreen.setShowBackground(false);
        Minecraft.getMinecraft().displayGuiScreen(modsScreen);
        modsScreen.currentPanel = modsScreen.modListPanel;
        this.handleAbstractMod(modManager.findAbstractMod(str));
    }

    public void openModList() throws java.io.IOException {
        ModsScreen modsScreen = new ModsScreen();
        modsScreen.setShowBackground(false);
        Minecraft.getMinecraft().displayGuiScreen(modsScreen);
        modsScreen.currentPanel = modsScreen.modListPanel;
    }

    public void handleAbstractMod(AbstractMod abstractMod) {
        ModsScreen.modsScreen.settingsPanel.showingGlobalSettings = false;
        ModsScreen.modsScreen.settingsPanel.panel = ModListItem.instance.panel;
        ModsScreen.modsScreen.settingsPanel.abstractMod = abstractMod;
        ModsScreen.modsScreen.settingsPanel.scrollOffset = CheatBreaker.getInstance().getModManager().intValue2;
        ModsScreen.modsScreen.currentPanel = ModsScreen.modsScreen.settingsPanel;
    }

    public float getFloatValue() {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        if (this.cheatBreaker.getKeybindSettings().useLegacyScaling.getBooleanValue()) {
            switch (Minecraft.getMinecraft().gameSettings.guiScale) {
                case 0:
                case 4:
                    return 2.0F;
                case 1:
                    return 0.5F;
                case 2:
                    return 1.0F;
                case 3:
                    return 1.5F;
                case 5:
                    return 2.5F;
                default:
                    return 1.0F;
            }
        } else {
            return scaledResolution.getScaleFactor() / 2.0F;
        }
    }

    public void openGlobalSettings() throws java.io.IOException {
        ModsScreen modsScreen = new ModsScreen();
        modsScreen.setShowBackground(false);
        Minecraft.getMinecraft().displayGuiScreen(modsScreen);
        modsScreen.currentPanel = modsScreen.settingsPanel;
        ModsScreen.modsScreen.settingsPanel.showingGlobalSettings = true;
        ModsScreen.modsScreen.settingsPanel.scrollOffset = CheatBreaker.getInstance().getModManager().intValue2;
    }
}

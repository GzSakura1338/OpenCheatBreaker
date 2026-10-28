package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.ModManager;
import com.cheatbreaker.client.mod.setting.Setting;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import net.optifine.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class StringSettingEditScreen extends GuiScreen {
    public Setting setting;
    public GuiTextField guiTextField = null;
    public GuiScreen guiScreen;
    public float scale;
    public boolean flag;

    @Override
    public void mouseClicked(int n, int n2, int n3) throws java.io.IOException {
        super.mouseClicked(n, n2, n3);
        this.guiTextField.mouseClicked(n, n2, n3);
    }

    @Override
    public void initGui() throws java.io.IOException {
        Keyboard.enableRepeatEvents(true);
        if (!this.flag) {
            this.mc.displayGuiScreen(this.guiScreen);
        } else {
            this.flag = false;
            this.guiTextField = new GuiTextField(this.mc.fontRendererObj, this.width / 2 - 70, this.height / 2 - 6, 140, 10);
            if (this.setting != null) {
                this.guiTextField.setText(this.setting.getStringValue());
            }

            this.guiTextField.setFocused(true);
        }

        if (this.setting == CheatBreaker.getInstance().getModManager().nickHiderMod.customNameString) {
            this.guiTextField.setMaxStringLength(15);
        } else {
            this.guiTextField.setMaxStringLength(64);
        }
    }

    @Override
    public void updateScreen() {
        this.guiTextField.updateCursorCounter();
    }

    public StringSettingEditScreen(Setting setting, GuiScreen guiScreen, float f) {
        this(guiScreen, f);
        this.setting = setting;
    }

    @Override
    public void keyTyped(char c, int n) throws java.io.IOException {
        ModManager modManager = CheatBreaker.getInstance().getModManager();
        switch (n) {
            case 1:
                this.mc.displayGuiScreen(this.guiScreen);
                ModsScreen.modsScreen.settingsPanel.abstractMod = modManager.findAbstractMod(modManager.text);
                ModsScreen.modsScreen.settingsPanel.scrollOffset = modManager.intValue2;
                ModsScreen.modsScreen.settingsPanel.panel = ModsScreen.modsScreen.modListPanel;
                ModsScreen.modsScreen.currentPanel = ModsScreen.modsScreen.settingsPanel;
                break;
            case 28:
                this.setting.changeValue(this.guiTextField.getText());
                this.mc.displayGuiScreen(this.guiScreen);
                CheatBreaker.getInstance()
                    .getModManager()
                    .notificationsMod
                    .addNotification("info", EnumChatFormatting.GREEN + "Updated custom string value successfully.", 5000L);
                ModsScreen.modsScreen.settingsPanel.abstractMod = modManager.findAbstractMod(modManager.text);
                ModsScreen.modsScreen.settingsPanel.scrollOffset = modManager.intValue2;
                ModsScreen.modsScreen.settingsPanel.panel = ModsScreen.modsScreen.modListPanel;
                ModsScreen.modsScreen.currentPanel = ModsScreen.modsScreen.settingsPanel;
                break;
            default:
                this.guiTextField.textboxKeyTyped(c, n);
        }
    }

    @Override
    public void drawScreen(int n, int n2, float f) throws java.io.IOException {
        this.guiScreen.drawScreen(n, n2, f);
        this.drawDefaultBackground();
        drawRect(this.width / 2 - 73, this.height / 2 - 19, this.width / 2 + 73, this.height / 2 + 8, -11250604);
        drawRect(this.width / 2 - 72, this.height / 2 - 18, this.width / 2 + 72, this.height / 2 + 7, -3881788);
        GlStateManager.pushMatrix();
        GL11.glScalef(this.scale, this.scale, this.scale);
        int n3 = (int)(this.width / this.scale);
        int n4 = (int)(this.height / this.scale);
        CheatBreaker.getInstance()
            .getFontManager()
            .ubuntuM16
            .drawString(this.setting.getName(), n3 / 2 - 70.0F / this.scale, n4 / 2 - 17.0F / this.scale, 1862270976);
        GlStateManager.popMatrix();
        this.guiTextField.drawTextBox();
    }

    public StringSettingEditScreen(GuiScreen guiScreen, float f) {
        this.guiScreen = guiScreen;
        this.scale = f;
        this.flag = true;
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
}

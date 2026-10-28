package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.theme.Theme;
import com.cheatbreaker.client.ui.component.ThemeEntryComponent;
import com.cheatbreaker.client.ui.component.ThemeManagerPanel;
import com.cheatbreaker.client.util.ClientVersion;
import java.io.File;
import java.nio.file.Files;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class ThemeNameScreen extends GuiScreen {
    public GuiTextField guiTextField = null;
    public Theme theme;
    public String errorMessage = "";
    public GuiScreen guiScreen;
    public float scale;
    public ThemeManagerPanel themeManagerPanel;
    public int intValue;
    public boolean flag;

    @Override
    public void mouseClicked(int n, int n2, int n3) throws java.io.IOException {
        super.mouseClicked(n, n2, n3);
        this.guiTextField.mouseClicked(n, n2, n3);
    }

    @Override
    public void mouseReleased(int n, int n2, int n3) {
        super.mouseReleased(n, n2, n3);
    }

    public ThemeNameScreen(Theme theme, GuiScreen guiScreen, ThemeManagerPanel themeManagerPanel, int n, float f) {
        this(guiScreen, themeManagerPanel, n, f);
        this.theme = theme;
    }

    @Override
    public void initGui() throws java.io.IOException {
        Keyboard.enableRepeatEvents(true);
        if (!this.flag) {
            this.mc.displayGuiScreen(this.guiScreen);
            ((ModsScreen)this.guiScreen).currentPanel = ((ModsScreen)this.guiScreen).themeManagerPanel;
        } else {
            this.flag = false;
            this.guiTextField = new GuiTextField(this.mc.fontRendererObj, this.width / 2 - 70, this.height / 2 - 6, 140, 10);
            if (this.theme != null) {
                this.guiTextField.setText(this.theme.getThemeNameValue());
            }

            this.guiTextField.setFocused(true);
        }
    }

    public ThemeNameScreen(GuiScreen guiScreen, ThemeManagerPanel themeManagerPanel, int n, float f) {
        this.guiScreen = guiScreen;
        this.scale = f;
        this.themeManagerPanel = themeManagerPanel;
        this.intValue = n;
        this.flag = true;
    }

    @Override
    public void keyTyped(char c, int n) throws java.io.IOException {
        switch (n) {
            case 1:
                this.mc.displayGuiScreen(this.guiScreen);
                ((ModsScreen)this.guiScreen).currentPanel = ((ModsScreen)this.guiScreen).themeManagerPanel;
                break;
            case 28:
                if (this.guiTextField.getText().length() < 3) {
                    this.errorMessage = EnumChatFormatting.RED + "Name must be at least 3 characters long.";
                } else if (this.guiTextField.getText().equalsIgnoreCase("default")) {
                    this.errorMessage = EnumChatFormatting.RED + "That name is already in use.";
                } else if (!this.guiTextField.getText().matches("([a-zA-Z0-9-_ \\]\\[]+)")) {
                    this.errorMessage = EnumChatFormatting.RED + "Illegal characters in name.";
                } else if (this.theme != null && !this.theme.isBuiltIn()) {
                    File file = new File(
                        Minecraft.getMinecraft().mcDataDir
                            + File.separator
                            + "config"
                            + File.separator
                            + "cheatbreaker-client-"
                            + ClientVersion.version
                            + "-"
                            + CheatBreaker.getInstance().getBranch()
                            + File.separator
                            + "themes"
                            + File.separator
                            + this.theme.getThemeNameValue()
                            + ".json"
                    );
                    File file2 = new File(
                        Minecraft.getMinecraft().mcDataDir
                            + File.separator
                            + "config"
                            + File.separator
                            + "cheatbreaker-client-"
                            + ClientVersion.version
                            + "-"
                            + CheatBreaker.getInstance().getBranch()
                            + File.separator
                            + "themes"
                            + File.separator
                            + this.guiTextField.getText()
                            + ".json"
                    );
                    if (file.exists()) {
                        try {
                            Files.copy(file.toPath(), file2.toPath());
                            Files.delete(file.toPath());
                            this.theme.setThemeNameValue(this.guiTextField.getText());
                            this.mc.displayGuiScreen(this.guiScreen);
                            ((ModsScreen)this.guiScreen).currentPanel = ((ModsScreen)this.guiScreen).themeManagerPanel;
                        } catch (Exception e) {
                            this.errorMessage = EnumChatFormatting.RED + "Could not save theme.";
                            e.printStackTrace();
                        }
                    }
                } else {
                    Theme theme = null;

                    for (Theme theme2 : CheatBreaker.getInstance().themes.values()) {
                        if (theme2.getThemeNameValue().toLowerCase().equalsIgnoreCase(this.guiTextField.getText())) {
                            theme = theme2;
                            break;
                        }
                    }

                    if (theme == null) {
                        CheatBreaker.getInstance().configFileManager.saveTheme(CheatBreaker.getInstance().getConfigManager().theme.getThemeNameValue());
                        Theme theme2 = CheatBreaker.getInstance().getConfigManager().theme;
                        Theme theme3 = new Theme(
                            this.guiTextField.getText(),
                            false,
                            false,
                            theme2.getPrimaryBackgroundColorValue(),
                            theme2.getMenuBackgroundColorValue(),
                            theme2.getEnabledModColorValue(),
                            theme2.getDisabledModColorValue(),
                            theme2.getModNameTextColorValue(),
                            theme2.getButtonBackgroundColorValue(),
                            theme2.getHoverBorderThicknessValue(),
                            theme2.getButtonCornerRadiusValue(),
                            theme2.getSelectedButtonColorValue(),
                            theme2.getDisabledButtonColorValue(),
                            theme2.getModsHighlightColorValue(),
                            theme2.getNormalHighlightColorValue(),
                            theme2.getLabelHighlightColorValue(),
                            theme2.getEnableModHighlightColorValue(),
                            theme2.getDisableModHighlightColorValue(),
                            theme2.getSelectedOutlineColorValue(),
                            theme2.getUnselectedOutlineColorValue(),
                            theme2.getUnselectedOverlayColorValue(),
                            theme2.getSelectedOverlayColorValue(),
                            theme2.getSelecHoveringOlColorValue(),
                            theme2.getUnselecHoveringOlColorValue(),
                            theme2.getUnselecHoveringOvColorValue(),
                            theme2.getSelecHoveringOvColorValue(),
                            theme2.getSnapLineColorValue(),
                            theme2.getNeighboringModOverlayColorValue(),
                            theme2.getHudBorderColorValue(),
                            theme2.getSelectionOutlineColorValue(),
                            theme2.getSelectionColorValue(),
                            theme2.getTrackColorValue(),
                            theme2.getThumbColorValue(),
                            theme2.getThumbOutlineColorValue(),
                            theme2.getThumbColorValue2(),
                            theme2.getThumbHoverColorValue(),
                            theme2.getTrackColorValue2(),
                            theme2.getTrackHoverColorValue(),
                            theme2.getPrimaryTextColorValue(),
                            theme2.getTextColor2Value(),
                            theme2.getTextColor3Value(),
                            theme2.getTextColor4Value(),
                            theme2.getTextColor5Value(),
                            theme2.getIntValue2(),
                            theme2.getPrimaryTextColorValue2(),
                            theme2.getTextColor2Value2(),
                            theme2.getTextColor3Value2(),
                            theme2.getTextColor4Value2()
                        );
                        theme3.intValue = theme2.intValue + 1;
                        CheatBreaker.getInstance().themes.put(theme3.getThemeNameValue(), theme3);
                        theme3.createSettings();
                        CheatBreaker.getInstance().getConfigManager().theme = theme3;
                        this.themeManagerPanel.themeEntryComponentList.add(new ThemeEntryComponent(this.themeManagerPanel, this.intValue, theme3, this.scale));
                        CheatBreaker.getInstance().configFileManager.saveTheme(CheatBreaker.getInstance().getConfigManager().theme.getThemeNameValue());
                        this.mc.displayGuiScreen(this.guiScreen);
                        ((ModsScreen)this.guiScreen).currentPanel = ((ModsScreen)this.guiScreen).themeManagerPanel;
                    } else {
                        this.errorMessage = EnumChatFormatting.RED + "That name is already in use.";
                    }
                }
                break;
            default:
                this.guiTextField.textboxKeyTyped(c, n);
        }
    }

    @Override
    public void updateScreen() {
        this.guiTextField.updateCursorCounter();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void drawScreen(int n, int n2, float f) throws java.io.IOException {
        this.guiScreen.drawScreen(n, n2, f);
        this.drawDefaultBackground();
        drawRect(this.width / 2 - 73, this.height / 2 - 19, this.width / 2 + 73, this.height / 2 + 8, -11250604);
        drawRect(this.width / 2 - 72, this.height / 2 - 18, this.width / 2 + 72, this.height / 2 + 7, -3881788);
        GL11.glPushMatrix();
        GL11.glScalef(this.scale, this.scale, this.scale);
        int n3 = (int)(this.width / this.scale);
        int n4 = (int)(this.height / this.scale);
        float f2 = n3 / 2 - 70.0F / this.scale;
        float f3 = n4 / 2 - 17.0F / this.scale;
        CheatBreaker.getInstance().getFontManager().ubuntuM16.drawString("Theme Name: ", f2, f3, 1862270976);
        f2 = n3 / 2 - 72.0F / this.scale;
        f3 = n4 / 2 + 8.0F / this.scale;
        CheatBreaker.getInstance().getFontManager().ubuntuM16.drawString(this.errorMessage, f2, f3, -1358954496);
        GL11.glPopMatrix();
        this.guiTextField.drawTextBox();
    }
}

package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.config.Profile;
import com.cheatbreaker.client.ui.component.ProfileComponent;
import com.cheatbreaker.client.ui.component.ProfilesPanel;
import com.cheatbreaker.client.util.ClientVersion;
import java.io.File;
import java.nio.file.Files;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import net.optifine.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class ProfileNameScreen extends GuiScreen {
    public GuiTextField guiTextField = null;
    public String errorMessage = "";
    public boolean flag = false;
    public GuiScreen guiScreen;
    public float scale;
    public ProfilesPanel profilesPanel;
    public Profile profile;
    public int intValue;

    @Override
    public void keyTyped(char c, int n) throws java.io.IOException {
        switch (n) {
            case 1:
                this.mc.displayGuiScreen(this.guiScreen);
                ((ModsScreen)this.guiScreen).currentPanel = ((ModsScreen)this.guiScreen).profilesPanel;
                break;
            case 28:
                if (this.guiTextField.getText().length() < 3) {
                    this.errorMessage = EnumChatFormatting.RED + "Name must be at least 3 characters long.";
                } else if (this.guiTextField.getText().equalsIgnoreCase("default")) {
                    this.errorMessage = EnumChatFormatting.RED + "That name is already in use.";
                } else if (!this.guiTextField.getText().matches("([a-zA-Z0-9-_ \\]\\[]+)")) {
                    this.errorMessage = EnumChatFormatting.RED + "Illegal characters in name.";
                } else if (this.profile != null && !this.profile.isDefault()) {
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
                            + "profiles"
                            + File.separator
                            + this.profile.getName()
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
                            + "profiles"
                            + File.separator
                            + this.guiTextField.getText()
                            + ".json"
                    );
                    if (file.exists()) {
                        try {
                            Files.copy(file.toPath(), file2.toPath());
                            Files.delete(file.toPath());
                            this.profile.setName(this.guiTextField.getText());
                            this.mc.displayGuiScreen(this.guiScreen);
                            ((ModsScreen)this.guiScreen).currentPanel = ((ModsScreen)this.guiScreen).profilesPanel;
                        } catch (Exception e) {
                            this.errorMessage = EnumChatFormatting.RED + "Could not save profile.";
                            e.printStackTrace();
                        }
                    }
                } else {
                    Profile profile = null;

                    for (Profile profile2 : CheatBreaker.getInstance().profiles) {
                        if (profile2.getName().toLowerCase().equalsIgnoreCase(this.guiTextField.getText())) {
                            profile = profile2;
                            break;
                        }
                    }

                    if (profile == null) {
                        CheatBreaker.getInstance().configFileManager.saveProfile(CheatBreaker.getInstance().getConfigManager().profile.getName());
                        Profile profile2 = new Profile(this.guiTextField.getText(), false);
                        CheatBreaker.getInstance().profiles.add(profile2);
                        CheatBreaker.getInstance().getConfigManager().profile = profile2;
                        this.profilesPanel.profileComponentList.add(new ProfileComponent(this.profilesPanel, this.intValue, profile2, this.scale));
                        CheatBreaker.getInstance().configFileManager.saveProfile(CheatBreaker.getInstance().getConfigManager().profile.getName());
                        this.mc.displayGuiScreen(this.guiScreen);
                        ((ModsScreen)this.guiScreen).currentPanel = ((ModsScreen)this.guiScreen).profilesPanel;
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
    public void mouseReleased(int n, int n2, int n3) {
        super.mouseReleased(n, n2, n3);
    }

    @Override
    public void mouseClicked(int n, int n2, int n3) throws java.io.IOException {
        super.mouseClicked(n, n2, n3);
        this.guiTextField.mouseClicked(n, n2, n3);
    }

    public ProfileNameScreen(GuiScreen guiScreen, ProfilesPanel profilesPanel, int n, float f) {
        this.guiScreen = guiScreen;
        this.scale = f;
        this.profilesPanel = profilesPanel;
        this.intValue = n;
        this.flag = true;
    }

    @Override
    public void updateScreen() {
        this.guiTextField.updateCursorCounter();
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
        float f2 = n3 / 2 - 70.0F / this.scale;
        float f3 = n4 / 2 - 17.0F / this.scale;
        CheatBreaker.getInstance().getFontManager().ubuntuM16.drawString("Profile Name: ", f2, f3, 1862270976);
        f2 = n3 / 2 - 72.0F / this.scale;
        f3 = n4 / 2 + 8.0F / this.scale;
        CheatBreaker.getInstance().getFontManager().ubuntuM16.drawString(this.errorMessage, f2, f3, -1358954496);
        GlStateManager.popMatrix();
        this.guiTextField.drawTextBox();
    }

    public ProfileNameScreen(Profile profile, GuiScreen guiScreen, ProfilesPanel profilesPanel, int n, float f) {
        this(guiScreen, profilesPanel, n, f);
        this.profile = profile;
    }

    @Override
    public void initGui() throws java.io.IOException {
        Keyboard.enableRepeatEvents(true);
        if (!this.flag) {
            this.mc.displayGuiScreen(this.guiScreen);
            ((ModsScreen)this.guiScreen).currentPanel = ((ModsScreen)this.guiScreen).profilesPanel;
        } else {
            this.flag = false;
            this.guiTextField = new GuiTextField(this.mc.fontRendererObj, this.width / 2 - 70, this.height / 2 - 6, 140, 10);
            if (this.profile != null) {
                this.guiTextField.setText(this.profile.getName());
            }

            this.guiTextField.setFocused(true);
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
}

package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import com.cheatbreaker.client.ui.screen.StringSettingEditScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class TextSettingComponent extends SettingComponent {
    public boolean listening = false;
    public boolean hotkeyEntry = false;
    public TextField textField;
    public Button button;

    public void clearHotkey() {
        CheatBreaker.getInstance().getModManager().autoTextMod.setActiveHotkey("");
        this.button.label = "NONE";
        this.setting.setMouseBind(false);
        this.setting.enableKeyCode(0);
        this.listening = false;
    }

    @Override
    public void tick() {
        this.textField.tick();
    }

    @Override
    public void keyTyped(char c, int n) {
        if (n == 1 && this.listening) {
            this.textField.setFocused(false);
            this.clearHotkey();
        } else {
            super.keyTyped(c, n);
            this.textField.keyTyped(c, n);
        }
    }

    @Override
    public void mouseClicked(int n, int n2, int n3) throws java.io.IOException {
        if (!this.isHidden(this.setting)) {
            if (this.button.isMouseOver(n, n2) && this.hotkeyEntry) {
                if (!CheatBreaker.getInstance().getModManager().autoTextMod.getActiveHotkey().equals("")) {
                    return;
                }

                CheatBreaker.getInstance().getModManager().autoTextMod.setActiveHotkey(this.setting.getName());
                Minecraft.getMinecraft()
                    .getSoundHandler()
                    .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
                this.listening = true;
                this.button.label = "<PRESS ANY KEY>";
            }

            boolean flag = n > this.x * this.scale
                && n < (this.x + this.width - (this.hotkeyEntry ? 120 : 40)) * this.scale
                && n2 > (this.y + this.scrollOffsetY) * this.scale
                && n2 < (this.y + 18 + this.scrollOffsetY) * this.scale;
            if (flag) {
                Minecraft.getMinecraft().displayGuiScreen(new StringSettingEditScreen(this.setting, ModsScreen.modsScreen, this.scale));
                Minecraft.getMinecraft()
                    .getSoundHandler()
                    .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            }
        }
    }

    @Override
    public void setBounds(int n, int n2, int n3, int n4) {
        super.setBounds(n, n2, n3, n4);
        this.textField.setBounds(this.x + (this.hotkeyEntry ? 65 : 150), n2 + n4 - 14.0F, 166.0F, 13.0F);
    }

    @Override
    public boolean mouseClickedFloat(float f, float f2, int n, boolean flag) {
        this.tick();
        this.textField.mouseClicked(f, f2, n, flag);
        return false;
    }

    public TextSettingComponent(Setting setting, float f) {
        super(f);
        this.setting = setting;
        this.textField = new TextField(CheatBreaker.getInstance().getFontManager().playBold18, this.setting.getValue().toString(), 0, 0);
        this.textField.setMaxStringLength(256);
        this.button = new Button(
            CheatBreaker.getInstance().getFontManager().playBold18,
            null,
            (setting.hasMouseBind() ? "Button " + (setting.getKeyCode() + 1) : Keyboard.getKeyName(setting.getKeyCode())).toUpperCase(),
            this.x + this.width - 100,
            this.y,
            50,
            18,
            this.theme.getLabelHighlightColorValue(),
            this.scale,
            false,
            true
        );
        this.height = 18;
        if (setting.getName().startsWith("Hot key")) {
            this.hotkeyEntry = true;
        }
    }

    public Button getButton() {
        return this.button;
    }

    @Override
    public void render(int n, int n2, float f) {
        if (this.listening && this.hotkeyEntry && Keyboard.getEventKeyState()) {
            Minecraft.getMinecraft()
                .getSoundHandler()
                .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            CheatBreaker.getInstance().getModManager().autoTextMod.setActiveHotkey(this.setting.getName());

            for (Setting setting : CheatBreaker.getInstance().getModManager().autoTextMod.hotkeySettings) {
                if (Keyboard.getEventKey() == setting.getKeyCode()
                    || CheatBreaker.getInstance().getModManager().hasKeybindConflict(this.setting, Keyboard.getEventKey())) {
                    this.clearHotkey();
                    break;
                }
            }

            if (!this.button.label.equals("NONE")) {
                if (this.button.redText) {
                    this.button.setRedText(false);
                }

                this.setting.setMouseBind(false);
                this.setting.enableKeyCode(Keyboard.getEventKey());
                this.button.label = Keyboard.getKeyName(this.setting.getKeyCode()).toUpperCase();
                this.listening = false;
                CheatBreaker.getInstance().getModManager().autoTextMod.setActiveHotkey("");
            }
        }

        if (this.listening && this.hotkeyEntry && Mouse.getEventButton() != 0 && Mouse.getEventButtonState()) {
            Minecraft.getMinecraft()
                .getSoundHandler()
                .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            CheatBreaker.getInstance().getModManager().autoTextMod.setActiveHotkey(this.setting.getName());

            for (Setting setting : CheatBreaker.getInstance().getModManager().autoTextMod.hotkeySettings) {
                if (Mouse.getEventButton() == setting.getKeyCode()
                    || CheatBreaker.getInstance().getModManager().hasKeybindConflict(this.setting, Mouse.getEventButton())) {
                    this.button.label = "NONE";
                    CheatBreaker.getInstance().getModManager().autoTextMod.setActiveHotkey("");
                    this.setting.setMouseBind(false);
                    this.setting.enableKeyCode(0);
                    this.listening = false;
                    break;
                }
            }

            if (!this.button.label.equals("NONE")) {
                if (this.button.redText) {
                    this.button.setRedText(false);
                }

                this.setting.enableKeyCode(Mouse.getEventButton());
                this.setting.setMouseBind(true);
                this.button.label = ("Button " + (this.setting.getKeyCode() + 1)).toUpperCase();
                this.listening = false;
                CheatBreaker.getInstance().getModManager().autoTextMod.setActiveHotkey("");
            }
        }

        if (this.hotkeyEntry) {
            this.button.scrollOffsetY = this.scrollOffsetY;
            this.button.setBounds(this.x + this.width - 109, this.y + 2, 93, 15);
            this.button.render(n, n2, f);
        }

        CheatBreaker.getInstance()
            .getFontManager()
            .ubuntuM16
            .drawString(this.setting.getName().toUpperCase(), this.x + 10, this.y + 4, this.theme.getTextColor2Value());
        Gui.drawRect(
            this.x + (this.hotkeyEntry ? 65 : 148),
            this.y + 16,
            this.x + this.width - (this.hotkeyEntry ? 120 : 16),
            this.y + 17,
            this.theme.getTextColor2Value2()
        );
        this.textField.setEnabledColor(this.theme.getTextColor2Value());
        this.textField.setEnableBackgroundDrawing(false);
        this.textField.renderAndTestHover(n, n2, true);
        this.renderSettingLabel(this.setting, n, n2);
        this.tick();
    }

    public TextField getTextField() {
        return this.textField;
    }

    @Override
    public void onClose() {
        this.textField.onClose();
    }
}

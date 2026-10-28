package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class KeybindComponent extends SettingComponent {
    public boolean listening = false;
    public int lastKey = 0;
    public Button button;

    public void clearKeybind() {
        ModsScreen.modsScreen.settingsPanel.keybindComponent = null;
        this.button.label = "NONE";
        this.button.hoverLabel = "Right-Click to Reset";
        this.lastKey = 0;
        this.setting.setMouseBind(false);
        this.setting.setRequireShift(false);
        this.setting.setRequireCtrl(false);
        this.setting.setRequireAlt(false);
        this.setting.changeValue(0);
        this.setting.applyToKeyBinding();
        this.listening = false;
    }

    public static boolean isModifierKey(int n) {
        return n == 42 || n == 54 || n == 29 || n == 157 || n == 56 || n == 184;
    }

    public KeybindComponent(Setting setting, float f) {
        super(f);
        this.setting = setting;
        this.height = 18;
        this.button = new Button(
            CheatBreaker.getInstance().getFontManager().playBold16,
            null,
            setting.getKeybindText(),
            this.x + this.width - 100,
            this.y,
            96,
            18,
            this.theme.getLabelHighlightColorValue(),
            f,
            false,
            true
        );
        this.button.hoverLabel = "Right-Click to Reset";
        this.button.hoverFill = false;
    }

    @Override
    public void keyTyped(char c, int n) {
        if (n == 1 && this.listening) {
            ModsScreen.modsScreen.settingsPanel.keybindComponent.clearKeybind();
        } else {
            super.keyTyped(c, n);
            if (this.listening) {
                if (isModifierKey(n)) {
                    this.lastKey = n;
                    return;
                }

                Minecraft.getMinecraft()
                    .getSoundHandler()
                    .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
                boolean flag = Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
                boolean flag2 = Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
                boolean flag3 = Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
                if (Keyboard.getKeyName(n) != null && Keyboard.getKeyName(n).equalsIgnoreCase("Escape")
                    || CheatBreaker.getInstance().getModManager().hasKeybindConflict(this.setting, n, flag, flag2, flag3)
                        && this == ModsScreen.modsScreen.settingsPanel.keybindComponent) {
                    this.clearKeybind();
                    return;
                }

                this.setting.changeValue(n);
                this.setting.setMouseBind(false);
                this.setting.setRequireShift(flag);
                this.setting.setRequireCtrl(flag2);
                this.setting.setRequireAlt(flag3);
                this.setting.applyToKeyBinding();
                this.button.label = this.setting.getKeybindText();
                this.button.hoverLabel = "Right-Click to Reset";
                this.lastKey = 0;
                this.listening = false;
                ModsScreen.modsScreen.settingsPanel.keybindComponent = null;
            }
        }
    }

    @Override
    public void render(int n, int n2, float f) {
        CheatBreaker.getInstance()
            .getFontManager()
            .ubuntuM16
            .drawString(this.setting.getName().toUpperCase(), this.x + 10, this.y + 4, this.theme.getTextColor2Value());
        if (this.listening && this.lastKey != 0 && !Keyboard.isKeyDown(this.lastKey)) {
            int lastKey = this.lastKey;
            this.lastKey = 0;
            this.setting.changeValue(lastKey);
            this.setting.setMouseBind(false);
            this.setting.setRequireShift(false);
            this.setting.setRequireCtrl(false);
            this.setting.setRequireAlt(false);
            this.setting.applyToKeyBinding();
            this.button.label = this.setting.getKeybindText();
            this.button.hoverLabel = "Right-Click to Reset";
            this.listening = false;
            ModsScreen.modsScreen.settingsPanel.keybindComponent = null;
        }

        if (this.listening && Mouse.getEventButton() != 0 && Mouse.getEventButtonState()) {
            boolean flag = Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
            boolean flag2 = Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
            boolean flag3 = Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
            if (CheatBreaker.getInstance().getModManager().hasKeybindConflict(this.setting, Mouse.getEventButton(), flag, flag2, flag3)
                && ModsScreen.modsScreen.settingsPanel.keybindComponent == this) {
                this.clearKeybind();
                return;
            }

            this.setting.changeValue(Mouse.getEventButton());
            this.setting.setMouseBind(true);
            this.setting.setRequireShift(flag);
            this.setting.setRequireCtrl(flag2);
            this.setting.setRequireAlt(flag3);
            this.setting.applyToKeyBinding();
            this.button.label = this.setting.getKeybindText();
            this.button.hoverLabel = "Right-Click to Reset";
            this.listening = false;
            ModsScreen.modsScreen.settingsPanel.keybindComponent = null;
        }

        this.button.scrollOffsetY = this.scrollOffsetY;
        this.button.setBounds(this.x + this.width - 109, this.y + 2, 93, 15);
        this.button.render(n, n2, f);
        this.renderSettingLabel(this.setting, n, n2);
    }

    public void stopListening() {
        ModsScreen.modsScreen.settingsPanel.keybindComponent = null;
        this.button.label = this.setting.getKeybindText();
        this.button.hoverLabel = "Right-Click to Reset";
        this.lastKey = 0;
        this.listening = false;
    }

    public Button getButton() {
        return this.button;
    }

    @Override
    public void mouseClicked(int n, int n2, int n3) {
        if (!this.isHidden(this.setting)) {
            if (this.button.isMouseOver(n, n2)) {
                Minecraft.getMinecraft()
                    .getSoundHandler()
                    .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
                if (n3 == 1) {
                    if (ModsScreen.modsScreen.settingsPanel.keybindComponent == this) {
                        ModsScreen.modsScreen.settingsPanel.keybindComponent = null;
                        this.listening = false;
                        this.lastKey = 0;
                    }

                    this.setting.resetKeybind();
                    this.button.label = this.setting.getKeybindText();
                    this.button.hoverLabel = "Right-Click to Reset";
                    return;
                }

                if (ModsScreen.modsScreen.settingsPanel.keybindComponent != null) {
                    ModsScreen.modsScreen.settingsPanel.keybindComponent.stopListening();
                }

                ModsScreen.modsScreen.settingsPanel.keybindComponent = this;
                this.listening = true;
                this.lastKey = 0;
                this.button.label = "<PRESS ANY KEY>";
                this.button.hoverLabel = null;
            }
        }
    }
}

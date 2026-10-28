package com.cheatbreaker.client.minimap;

import com.cheatbreaker.client.util.Translations;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;

public class MinimapControlsScreen extends AbstractMinimapScreen {
    public String title = "Controls";
    public int editingKeyIndex = -1;
    public GuiScreen guiScreen;
    public MinimapSettings minimapSettings;

    @Override
    public void initGui() {
        int intValue = this.getIntValue();

        for (int i = 0; i < this.minimapSettings.keyBindings.length; i++) {
            this.getButtonList()
                .add(
                    new MinimapOptionButton(i, intValue + i % 2 * 160, this.getHeight() / 6 + 24 * (i >> 1), 70, 20, this.minimapSettings.getBoundKeyName(i))
                );
        }

        this.getButtonList().add(new GuiButton(200, this.getWidth() / 2 - 100, this.getHeight() / 6 + 168, Translations.translate("gui.done")));
        this.title = Translations.translate("controls.minimap.title");
    }

    @Override
    public void drawScreen(int n, int n2, float f) throws java.io.IOException {
        super.drawMinimapBackground();
        this.drawDefaultBackground();
        this.drawCenteredString(this.getFontRendererObj(), this.title, this.getWidth() / 2, 20, 16777215);
        int intValue = this.getIntValue();

        label62:
        for (int i = 0; i < this.minimapSettings.keyBindings.length; i++) {
            boolean flag = false;
            int n3 = 0;

            while (true) {
                while (n3 >= this.minimapSettings.keyBindings.length) {
                    if (n3 >= this.minimapSettings.mc.gameSettings.keyBindings.length
                        || this.minimapSettings.keyBindings[i].getKeyCode() != 0
                            && this.minimapSettings.keyBindings[i].getKeyCode() == this.minimapSettings.mc.gameSettings.keyBindings[n3].getKeyCode()
                            && !this.minimapSettings.keyBindings[i].equals(this.minimapSettings.mc.gameSettings.keyBindings[n3])) {
                        flag = true;
                        if (this.editingKeyIndex == i) {
                            ((GuiButton)this.getButtonList().get(i)).displayString = "§f> §e??? §f<";
                        } else if (flag) {
                            ((GuiButton)this.getButtonList().get(i)).displayString = "§c" + this.minimapSettings.getBoundKeyName(i);
                        } else {
                            ((GuiButton)this.getButtonList().get(i)).displayString = this.minimapSettings.getBoundKeyName(i);
                        }

                        this.drawString(
                            this.getFontRendererObj(),
                            this.minimapSettings.getKeyBindingLabel(i),
                            intValue + i % 2 * 160 + 70 + 6,
                            this.getHeight() / 6 + 24 * (i >> 1) + 7,
                            -1
                        );
                        continue label62;
                    }

                    n3++;
                }

                if (this.minimapSettings.keyBindings[i].getKeyCode() != 0
                    && (
                        n3 != i && this.minimapSettings.keyBindings[i].getKeyCode() == this.minimapSettings.keyBindings[n3].getKeyCode()
                            || this.minimapSettings.keyBindings[i].getKeyCode() == this.minimapSettings.mc.gameSettings.keyBindings[n3].getKeyCode()
                                && !this.minimapSettings.keyBindings[i].equals(this.minimapSettings.mc.gameSettings.keyBindings[n3])
                    )) {
                    flag = true;
                } else {
                    n3++;
                }
            }
        }

        this.drawCenteredString(
            this.getFontRendererObj(), Translations.translate("controls.minimap.unbind1"), this.getWidth() / 2, this.getHeight() / 6 + 115, 16777215
        );
        this.drawCenteredString(
            this.getFontRendererObj(), Translations.translate("controls.minimap.unbind2"), this.getWidth() / 2, this.getHeight() / 6 + 129, 16777215
        );
        super.drawScreen(n, n2, f);
    }

    public MinimapControlsScreen(GuiScreen guiScreen, IVoxelMap voxelMap) {
        this.guiScreen = guiScreen;
        this.minimapSettings = voxelMap.getMapOptions();
    }

    @Override
    public void mouseClicked(int n, int n2, int n3) throws java.io.IOException {
        if (this.editingKeyIndex >= 0) {
            this.editingKeyIndex = -1;
        } else {
            super.mouseClicked(n, n2, n3);
        }
    }

    @Override
    public void actionPerformed(GuiButton guiButton) throws java.io.IOException {
        for (int i = 0; i < this.minimapSettings.keyBindings.length; i++) {
            ((GuiButton)this.getButtonList().get(i)).displayString = this.minimapSettings.getBoundKeyName(i);
        }

        if (guiButton.id == 200) {
            this.getMc().displayGuiScreen(this.guiScreen);
        } else {
            this.editingKeyIndex = guiButton.id;
            guiButton.displayString = "> " + this.minimapSettings.getBoundKeyName(guiButton.id) + " <";
        }
    }

    public int getIntValue() {
        return this.getWidth() / 2 - 155;
    }

    @Override
    public void keyTyped(char c, int n) throws java.io.IOException {
        if (this.editingKeyIndex >= 0) {
            if (n != 1) {
                this.minimapSettings.setKeyBindingKey(this.editingKeyIndex, n);
            } else if (this.editingKeyIndex != 0) {
                this.minimapSettings.setKeyBindingKey(this.editingKeyIndex, 0);
            }

            ((GuiButton)this.getButtonList().get(this.editingKeyIndex)).displayString = this.minimapSettings.getBoundKeyName(this.editingKeyIndex);
            this.editingKeyIndex = -1;
            KeyBinding.resetKeyBindingArrayAndHash();
        } else {
            super.keyTyped(c, n);
        }
    }
}

package net.optifine;

import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiVideoSettings;

public class GuiScreenOF extends GuiScreen {
    public void actionPerformedRightClick(GuiButton guiButton) throws java.io.IOException {
    }

    @Override
    public void mouseClicked(int n, int n2, int n3) throws java.io.IOException {
        super.mouseClicked(n, n2, n3);
        if (n3 == 1) {
            GuiButton selectedButton = getSelectedButton(n, n2, this.buttonList);
            if (selectedButton != null && selectedButton.enabled) {
                selectedButton.playPressSound(this.mc.getSoundHandler());
                this.actionPerformedRightClick(selectedButton);
            }
        }
    }

    public static GuiButton getSelectedButton(int n, int n2, List<GuiButton> list) {
        for (int i = 0; i < list.size(); i++) {
            GuiButton guiButton = (GuiButton)list.get(i);
            if (guiButton.visible) {
                int buttonWidth = GuiVideoSettings.getButtonWidth(guiButton);
                int buttonHeight = GuiVideoSettings.getButtonHeight(guiButton);
                if (n >= guiButton.xPosition && n2 >= guiButton.yPosition && n < guiButton.xPosition + buttonWidth && n2 < guiButton.yPosition + buttonHeight) {
                    return guiButton;
                }
            }
        }

        return null;
    }
}

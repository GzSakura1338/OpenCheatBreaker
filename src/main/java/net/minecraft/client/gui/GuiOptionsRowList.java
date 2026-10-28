package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended.IGuiListEntry;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.GameSettings.Options;

public class GuiOptionsRowList extends GuiListExtended {
    public List entries = Lists.newArrayList();

    public GuiOptionsRowList(Minecraft mc, int n, int n2, int n3, int n4, int n5, Options... optionses) {
        super(mc, n, n2, n3, n4, n5);
        this.centerListVertically = false;

        for (int i = 0; i < optionses.length; i += 2) {
            Options options = optionses[i];
            Options options2 = i < optionses.length - 1 ? optionses[i + 1] : null;
            GuiButton findGuiButton = this.findGuiButton(mc, n / 2 - 155, 0, options);
            GuiButton findGuiButton2 = this.findGuiButton(mc, n / 2 - 155 + 160, 0, options2);
            this.entries.add(new GuiOptionsRowList.Row(findGuiButton, findGuiButton2));
        }
    }

    public GuiButton findGuiButton(Minecraft mc, int n, int n2, Options options) {
        if (options == null) {
            return null;
        }

        int returnEnumOrdinal = options.returnEnumOrdinal();
        return options.getEnumFloat()
            ? new GuiOptionSlider(returnEnumOrdinal, n, n2, options)
            : new GuiOptionButton(returnEnumOrdinal, n, n2, options, mc.gameSettings.getKeyBinding(options));
    }

    public GuiOptionsRowList.Row getListEntry(int n) {
        return (GuiOptionsRowList.Row)this.entries.get(n);
    }

    @Override
    public int getSize() {
        return this.entries.size();
    }

    @Override
    public int getListWidth() {
        return 400;
    }

    @Override
    public int getScrollBarX() {
        return super.getScrollBarX() + 32;
    }

    public static class Row implements IGuiListEntry {
        public Minecraft minecraft = Minecraft.getMinecraft();
        public GuiButton guiButton;
        public GuiButton guiButton2;

        public Row(GuiButton guiButton, GuiButton guiButton2) {
            this.guiButton = guiButton;
            this.guiButton2 = guiButton2;
        }

        @Override
        public void drawEntry(int n, int n2, int n3, int n4, int n5, Tessellator tessellator, int n6, int n7, boolean flag) throws java.io.IOException {
            if (this.guiButton != null) {
                this.guiButton.yPosition = n3;
                this.guiButton.drawButton(this.minecraft, n6, n7);
            }

            if (this.guiButton2 != null) {
                this.guiButton2.yPosition = n3;
                this.guiButton2.drawButton(this.minecraft, n6, n7);
            }
        }

        @Override
        public boolean mousePressed(int n, int n2, int n3, int n4, int n5, int n6) throws java.io.IOException {
            if (this.guiButton.mousePressed(this.minecraft, n2, n3)) {
                if (this.guiButton instanceof GuiOptionButton) {
                    this.minecraft.gameSettings.setOptionValue(((GuiOptionButton)this.guiButton).returnEnumOptions(), 1);
                    this.guiButton.displayString = this.minecraft.gameSettings.getKeyBinding(Options.getEnumOptions(this.guiButton.id));
                }

                return true;
            } else if (this.guiButton2 != null && this.guiButton2.mousePressed(this.minecraft, n2, n3)) {
                if (this.guiButton2 instanceof GuiOptionButton) {
                    this.minecraft.gameSettings.setOptionValue(((GuiOptionButton)this.guiButton2).returnEnumOptions(), 1);
                    this.guiButton2.displayString = this.minecraft.gameSettings.getKeyBinding(Options.getEnumOptions(this.guiButton2.id));
                }

                return true;
            } else {
                return false;
            }
        }

        @Override
        public void mouseReleased(int n, int n2, int n3, int n4, int n5, int n6) {
            if (this.guiButton != null) {
                this.guiButton.mouseReleased(n2, n3);
            }

            if (this.guiButton2 != null) {
                this.guiButton2.mouseReleased(n2, n3);
            }
        }
    }
}

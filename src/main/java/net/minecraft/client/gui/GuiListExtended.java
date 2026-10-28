package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;

public abstract class GuiListExtended extends GuiSlot {
    public GuiListExtended(Minecraft mc, int n, int n2, int n3, int n4, int n5) {
        super(mc, n, n2, n3, n4, n5);
    }

    @Override
    public void elementClicked(int n, boolean flag, int n2, int n3) {
    }

    @Override
    public boolean isSelected(int n) {
        return false;
    }

    @Override
    public void drawBackground() {
    }

    @Override
    public void drawSlot(int n, int n2, int n3, int n4, Tessellator tessellator, int n5, int n6) throws java.io.IOException {
        this.getListEntry(n).drawEntry(n, n2, n3, this.getListWidth(), n4, tessellator, n5, n6, this.getSlotIndexFromScreenCoords(n5, n6) == n);
    }

    public boolean mouseClicked(int n, int n2, int n3) throws java.io.IOException {
        if (this.isMouseYWithinSlotBounds(n2)) {
            int slotIndexFromScreenCoords = this.getSlotIndexFromScreenCoords(n, n2);
            if (slotIndexFromScreenCoords >= 0) {
                int n4 = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
                int n5 = this.top + 4 - this.getAmountScrolled() + slotIndexFromScreenCoords * this.slotHeight + this.headerPadding;
                int n6 = n - n4;
                int n7 = n2 - n5;
                if (this.getListEntry(slotIndexFromScreenCoords).mousePressed(slotIndexFromScreenCoords, n, n2, n3, n6, n7)) {
                    this.setEnabled(false);
                    return true;
                }
            }
        }

        return false;
    }

    public boolean mouseReleased(int n, int n2, int n3) {
        for (int i = 0; i < this.getSize(); i++) {
            int n4 = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
            int n5 = this.top + 4 - this.getAmountScrolled() + i * this.slotHeight + this.headerPadding;
            int n6 = n - n4;
            int n7 = n2 - n5;
            this.getListEntry(i).mouseReleased(i, n, n2, n3, n6, n7);
        }

        this.setEnabled(true);
        return false;
    }

    public abstract GuiListExtended.IGuiListEntry getListEntry(int n);

    public interface IGuiListEntry {
        void drawEntry(int n, int n2, int n3, int n4, int n5, Tessellator tessellator, int n6, int n7, boolean flag) throws java.io.IOException;

        boolean mousePressed(int n, int n2, int n3, int n4, int n5, int n6) throws java.io.IOException;

        void mouseReleased(int n, int n2, int n3, int n4, int n5, int n6);
    }
}

package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.realms.RealmsScrolledSelectionList;

public class GuiSlotRealmsProxy extends GuiSlot {
    public RealmsScrolledSelectionList selectionList;

    public GuiSlotRealmsProxy(RealmsScrolledSelectionList selectionListIn, int n, int n2, int n3, int n4, int n5) {
        super(Minecraft.getMinecraft(), n, n2, n3, n4, n5);
        this.selectionList = selectionListIn;
    }

    @Override
    public int getSize() {
        return this.selectionList.getItemCount();
    }

    @Override
    public void elementClicked(int n, boolean flag, int n2, int n3) {
        this.selectionList.selectItem(n, flag, n2, n3);
    }

    @Override
    public boolean isSelected(int n) {
        return this.selectionList.isSelectedItem(n);
    }

    @Override
    public void drawBackground() {
        this.selectionList.renderBackground();
    }

    @Override
    public void drawSlot(int n, int n2, int n3, int n4, Tessellator tessellator, int n5, int n6) {
        this.selectionList.renderItem(n, n2, n3, n4, n5, n6);
    }

    public int getWidth() {
        return super.width;
    }

    @Override
    public int getContentHeight() {
        return this.selectionList.getMaxPosition();
    }

    @Override
    public int getScrollBarX() {
        return this.selectionList.getScrollbarPosition();
    }

    public int getMouseY() {
        return super.mouseY;
    }

    public int getMouseX() {
        return super.mouseX;
    }
}

package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsButton;

public class GuiButtonRealmsProxy extends GuiButton {
    public RealmsButton realmsButton;

    public GuiButtonRealmsProxy(RealmsButton realmsButton, int n, int n2, int n3, String str) {
        super(n, n2, n3, str);
        this.realmsButton = realmsButton;
    }

    public GuiButtonRealmsProxy(RealmsButton realmsButton, int n, int n2, int n3, String str, int n4, int n5) {
        super(n, n2, n3, n4, n5, str);
        this.realmsButton = realmsButton;
    }

    public int getId() {
        return super.id;
    }

    public boolean getEnabled() {
        return super.enabled;
    }

    public void setEnabled(boolean flag) {
        super.enabled = flag;
    }

    public void setText(String str) {
        super.displayString = str;
    }

    @Override
    public int getButtonWidth() {
        return super.getButtonWidth();
    }

    public int getPositionY() {
        return super.yPosition;
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) throws java.io.IOException {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.realmsButton.clicked(mouseX, mouseY);
        }

        return super.mousePressed(mc, mouseX, mouseY);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        this.realmsButton.released(mouseX, mouseY);
    }

    @Override
    public void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        this.realmsButton.renderBg(mouseX, mouseY);
    }

    public RealmsButton getRealmsButton() {
        return this.realmsButton;
    }

    @Override
    public int getHoverState(boolean mouseOver) {
        return this.realmsButton.getYImage(mouseOver);
    }

    public int getDelegateYImage(boolean flag) {
        return super.getHoverState(flag);
    }
}

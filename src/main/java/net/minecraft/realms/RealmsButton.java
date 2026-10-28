package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonRealmsProxy;

public class RealmsButton {
    public GuiButtonRealmsProxy proxy;

    public RealmsButton(int n, int n2, int n3, String str) {
        this.proxy = new GuiButtonRealmsProxy(this, n, n2, n3, str);
    }

    public RealmsButton(int n, int n2, int n3, int n4, int n5, String str) {
        this.proxy = new GuiButtonRealmsProxy(this, n, n2, n3, str, n4, n5);
    }

    public GuiButton getProxy() {
        return this.proxy;
    }

    public int id() {
        return this.proxy.getId();
    }

    public boolean active() {
        return this.proxy.getEnabled();
    }

    public void active(boolean flag) {
        this.proxy.setEnabled(flag);
    }

    public void msg(String str) {
        this.proxy.setText(str);
    }

    public void render(int n, int n2) throws java.io.IOException {
        this.proxy.drawButton(Minecraft.getMinecraft(), n, n2);
    }

    public void clicked(int n, int n2) {
    }

    public void released(int n, int n2) {
    }

    public void blit(int n, int n2, int n3, int n4, int n5, int n6) {
        Gui.drawTexturedRect(n, n2, n3, n4, n5, n6);
    }

    public void renderBg(int n, int n2) {
    }

    public int getYImage(boolean flag) {
        return this.proxy.getDelegateYImage(flag);
    }

    public int getPositionY() {
        return this.proxy.getPositionY();
    }

    public int getIntValue() {
        return this.proxy.getHeight();
    }

    public int getButtonWidth() {
        return this.proxy.getButtonWidth();
    }
}

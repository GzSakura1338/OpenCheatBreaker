package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended.IGuiListEntry;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;

public class ServerListEntryLanScan implements IGuiListEntry {
    public Minecraft client = Minecraft.getMinecraft();

    @Override
    public void drawEntry(int n, int n2, int n3, int n4, int n5, Tessellator tessellator, int n6, int n7, boolean flag) {
        int n8 = n3 + n5 / 2 - this.client.fontRendererObj.FONT_HEIGHT / 2;
        this.client
            .fontRendererObj
            .drawString(
                I18n.format("lanServer.scanning"),
                this.client.currentScreen.width / 2 - this.client.fontRendererObj.getStringWidth(I18n.format("lanServer.scanning")) / 2,
                n8,
                16777215
            );
        String str;
        switch ((int)(Minecraft.getSystemTime() / 300L % 4L)) {
            case 0:
            default:
                str = "O o o";
                break;
            case 1:
            case 3:
                str = "o O o";
                break;
            case 2:
                str = "o o O";
        }

        this.client
            .fontRendererObj
            .drawString(
                str,
                this.client.currentScreen.width / 2 - this.client.fontRendererObj.getStringWidth(str) / 2,
                n8 + this.client.fontRendererObj.FONT_HEIGHT,
                8421504
            );
    }

    @Override
    public boolean mousePressed(int n, int n2, int n3, int n4, int n5, int n6) {
        return false;
    }

    @Override
    public void mouseReleased(int n, int n2, int n3, int n4, int n5, int n6) {
    }
}

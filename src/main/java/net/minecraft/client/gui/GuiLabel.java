package net.minecraft.client.gui;

import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GL11;

public class GuiLabel extends Gui {
    public int width;
    public int height;
    public int x;
    public int y;
    public ArrayList list;
    public boolean centered;
    public boolean visible;
    public boolean labelBgEnabled;
    public int color;
    public int backgroundColor;
    public int upperRightColor;
    public int lowerLeftColor;
    public FontRenderer fontRenderer;
    public int borderThickness;

    public void drawLabel(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            GL11.glEnable(3042);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(770, 771);
            this.drawLabelBackground(mc, mouseX, mouseY);
            int n = this.y + this.height / 2 + this.borderThickness / 2;
            int n2 = n - this.list.size() * 10 / 2;

            for (int i = 0; i < this.list.size(); i++) {
                if (this.centered) {
                    this.drawCenteredString(this.fontRenderer, (String)this.list.get(i), this.x + this.width / 2, n2 + i * 10, this.color);
                } else {
                    this.drawString(this.fontRenderer, (String)this.list.get(i), this.x, n2 + i * 10, this.color);
                }
            }
        }
    }

    public void drawLabelBackground(Minecraft mc, int n, int n2) {
        if (this.labelBgEnabled) {
            int n3 = this.width + this.borderThickness * 2;
            int n4 = this.height + this.borderThickness * 2;
            int n5 = this.x - this.borderThickness;
            int n6 = this.y - this.borderThickness;
            drawRect(n5, n6, n5 + n3, n6 + n4, this.backgroundColor);
            this.drawHorizontalLine(n5, n5 + n3, n6, this.upperRightColor);
            this.drawHorizontalLine(n5, n5 + n3, n6 + n4, this.lowerLeftColor);
            this.drawVerticalLine(n5, n6, n6 + n4, this.upperRightColor);
            this.drawVerticalLine(n5 + n3, n6, n6 + n4, this.lowerLeftColor);
        }
    }
}

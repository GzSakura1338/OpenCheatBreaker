package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.render.GlStateCache;
import com.cheatbreaker.client.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public abstract class SlotList {
    public Minecraft mc;
    public boolean mouseOver;
    public float floatValue;
    public int width;
    public int intValue;
    public long lastClicked;
    public int left;
    public int top;
    public int intValue2;
    public int bottom;
    public int slotHeight;
    public String title;
    public int selectedElement;
    public int intValue3;
    public float amountScrolled;
    public int intValue4;
    public float initialClickY;
    public double[] doubleArray;

    public SlotList(Minecraft mc, int n, int n2, int n3, int n4, int n5, String str) {
        this.mc = mc;
        this.width = n3;
        this.intValue = n4;
        this.left = n;
        this.top = n2;
        this.intValue2 = n + n3;
        this.bottom = n2 + n4;
        this.slotHeight = n5;
        this.title = str;
        this.selectedElement = -1;
        this.intValue3 = -1;
        this.initialClickY = -2.0F;
        this.doubleArray = new double[]{
            5.088448,
            4.809692672,
            3.4292885120000003,
            3.268147903999999,
            2.697228288,
            2.019487744,
            1.8882322560000002,
            1.6936698879999996,
            1.4352491520000008,
            1.2045501440000006,
            0.7097322879999997,
            0.5842770560000003,
            0.5043583360000001,
            0.37950342400000014,
            0.282300416,
            0.21170873600000029,
            0.09733600000000031,
            0.08991539199999998,
            0.06209913599999961,
            0.030371328000000197,
            0.01452678400000007,
            0.006229504000000219,
            0.0017279999999999518
        };
    }

    public abstract void drawSlot(int n, int n2, int n3, int n4, int n5, int n6, boolean flag);

    public abstract int getSize();

    public void handleMouseInput() {
        int n;
        if (this.mouseOver && (n = Mouse.getEventDWheel()) != 0) {
            n = n > 0 ? -1 : 1;
            if ((Boolean)CheatBreaker.getInstance().getKeybindSettings().smoothScrolling.getValue()) {
                this.intValue4 = n;
                this.intValue3 = 0;
            } else {
                this.amountScrolled = this.amountScrolled + n * this.slotHeight / 2;
            }
        }
    }

    public void bindAmountScrolled() {
        int n = this.getContentHeight() - (this.bottom - this.top - 4);
        if (n < 0) {
            n /= 2;
        }

        if (this.amountScrolled > n) {
            this.amountScrolled = n;
        }

        if (this.amountScrolled < 0.0F) {
            this.amountScrolled = 0.0F;
        }
    }

    public abstract void elementClicked(int n, boolean flag);

    public int getContentHeight() {
        return this.getSize() * this.slotHeight;
    }

    public abstract void drawBackground();

    public void drawScreen(int n, int n2) {
        this.drawBackground();
        this.mouseOver = this.left <= n && n <= this.left + this.width && this.top <= n2 && n2 <= this.bottom;
        int size = this.getSize();
        byte b = 6;
        int n3 = this.left + this.width;
        int n4 = n3 - 6;
        int n5 = n4 - 1;
        int n6 = this.bottom - this.top;
        byte b2 = 4;
        if (Mouse.isButtonDown(0)) {
            if (this.initialClickY == -1.0F) {
                if (this.mouseOver) {
                    int n7 = n2 - this.top + (int)this.amountScrolled - 4;
                    int n8 = n7 / this.slotHeight;
                    if (0 <= n8 && n8 < size && n <= n5 && 0 <= n7) {
                        this.elementClicked(n8, n8 == this.selectedElement && System.currentTimeMillis() - this.lastClicked < 500L);
                        this.selectedElement = this.selectedElement == n8 ? -1 : n8;
                        this.lastClicked = System.currentTimeMillis();
                    }

                    if (n4 <= n && n <= n3) {
                        this.floatValue = -1.0F;
                        int n9 = this.getContentHeight() - n6 - 4;
                        if (n9 < 1) {
                            n9 = 1;
                        }

                        int n10;
                        if ((n10 = (int)((float)(n6 * n6) / this.getContentHeight())) < 32) {
                            n10 = 32;
                        }

                        if (n10 > n6 - 8) {
                            n10 = n6 - 8;
                        }

                        this.floatValue /= (float)(n6 - n10) / n9;
                    } else {
                        this.floatValue = 1.0F;
                    }

                    this.initialClickY = n2;
                } else {
                    this.initialClickY = -2.0F;
                }
            } else if (this.initialClickY >= 0.0F) {
                this.amountScrolled = this.amountScrolled - (n2 - this.initialClickY) * this.floatValue;
                this.initialClickY = n2;
            }
        } else {
            if (this.intValue3 != -1) {
                this.amountScrolled = (float)(this.amountScrolled + this.doubleArray[this.intValue3] * this.intValue4 * 2.0);
                this.intValue3++;
                if (this.intValue3 >= this.doubleArray.length) {
                    this.intValue3 = -1;
                }
            }

            this.initialClickY = -1.0F;
        }

        this.bindAmountScrolled();
        int n7 = this.top + 2 - (int)this.amountScrolled;

        for (int i = 0; i < size; i++) {
            int n8 = n7 + i * this.slotHeight;
            int n9 = this.slotHeight - 4;
            int n10 = n8 + n9 + 2;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateCache.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateCache.enableBlend();
            GlStateCache.glPushMatrix();
            GL11.glEnable(3089);
            ScaledResolution scaledResolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
            RenderUtils.scissor(this.left, this.top + 1, this.intValue2, this.bottom, scaledResolution);
            this.drawSlot(i, n5, n8, n9, n, n2, this.left <= n && n < n5 && n8 - 2 <= n2 && n2 < n10);
            GL11.glDisable(3089);
            GlStateCache.glPopMatrix();
        }

        int n8 = this.getContentHeight() - n6 - 4;
        if (n8 > 0) {
            int n9 = n6 * n6 / this.getContentHeight();
            if (n9 < 32) {
                n9 = 32;
            }

            if (n9 > n6 - 8) {
                n9 = n6 - 8;
            }

            int n10;
            if ((n10 = (int)this.amountScrolled * (n6 - n9) / n8 + this.top) < this.top) {
                n10 = this.top;
            }

            Gui.drawRect(n4, n10, n3, n10 + n9, -4144960);
        }

        GlStateCache.setShadeModel(7424);
        GL11.glEnable(3553);
        GL11.glEnable(3008);
        Gui.floatValue = 1.0F;
        Gui.drawVerticalGradient(this.left, this.top, n3, this.top + 5, -16777216, 0);
        Gui.drawVerticalGradient(this.left, this.bottom - 5, n3, this.bottom, 0, -16777216);
        Gui.floatValue = -90.0F;
    }
}

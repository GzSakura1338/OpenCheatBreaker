package net.minecraft.client.gui;

import com.cheatbreaker.client.CheatBreaker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public abstract class GuiSlot {
    public boolean centerListVertically = true;
    public float initialClickY = -2.0F;
    public int selectedElement = -1;
    public boolean showSelectionBox = true;
    public boolean enabled = true;
    public Minecraft mc;
    public int width;
    public int height;
    public int scrollUpButtonID;
    public int scrollDownButtonID;
    public int mouseX;
    public int mouseY;
    public int top;
    public int bottom;
    public float scrollMultiplier;
    public float amountScrolled;
    public int slotHeight;
    public long lastClicked;
    public int left;
    public boolean hasListHeader;
    public int headerPadding;
    public int right;

    public GuiSlot(Minecraft mc, int width, int height, int n, int n2, int n3) {
        this.mc = mc;
        this.width = width;
        this.height = height;
        this.top = n;
        this.bottom = n2;
        this.slotHeight = n3;
        this.left = 0;
        this.right = width;
    }

    public void setDimensions(int n, int n2, int n3, int n4) {
        this.width = n;
        this.height = n2;
        this.top = n3;
        this.bottom = n4;
        this.left = 0;
        this.right = n;
    }

    public void setShowSelectionBox(boolean flag) {
        this.showSelectionBox = flag;
    }

    public void setHasListHeader(boolean flag, int n) {
        this.hasListHeader = flag;
        this.headerPadding = n;
        if (!flag) {
            this.headerPadding = 0;
        }
    }

    public abstract int getSize();

    public abstract void elementClicked(int n, boolean flag, int n2, int n3) throws java.io.IOException;

    public abstract boolean isSelected(int n);

    public int getContentHeight() {
        return this.getSize() * this.slotHeight + this.headerPadding;
    }

    public abstract void drawBackground();

    public abstract void drawSlot(int n, int n2, int n3, int n4, Tessellator tessellator, int n5, int n6) throws java.io.IOException;

    public void drawListHeader(int n, int n2, Tessellator tessellator) {
    }

    public void process(int n, int n2) {
    }

    public void renderDecorations(int n, int n2) {
    }

    public int getSlotIndexFromScreenCoords(int n, int n2) {
        int n3 = this.left + this.width / 2 - this.getListWidth() / 2;
        int n4 = this.left + this.width / 2 + this.getListWidth() / 2;
        int n5 = n2 - this.top - this.headerPadding + (int)this.amountScrolled - 4;
        int n6 = n5 / this.slotHeight;
        return n < this.getScrollBarX() && n >= n3 && n <= n4 && n6 >= 0 && n5 >= 0 && n6 < this.getSize() ? n6 : -1;
    }

    public void registerScrollButtons(int n, int n2) {
        this.scrollUpButtonID = n;
        this.scrollDownButtonID = n2;
    }

    public void bindAmountScrolled() {
        int maxScroll = this.getMaxScroll();
        if (maxScroll < 0) {
            maxScroll /= 2;
        }

        if (!this.centerListVertically && maxScroll < 0) {
            maxScroll = 0;
        }

        if (this.amountScrolled < 0.0F) {
            this.amountScrolled = 0.0F;
        }

        if (this.amountScrolled > maxScroll) {
            this.amountScrolled = maxScroll;
        }
    }

    public int getMaxScroll() {
        int n = this.getContentHeight() - (this.bottom - this.top - 4);
        return CheatBreaker.getInstance().getKeybindSettings().centerShortLists.getBooleanValue() ? n : Math.max(0, n);
    }

    public int getAmountScrolled() {
        return (int)this.amountScrolled;
    }

    public boolean isMouseYWithinSlotBounds(int n) {
        return n >= this.top && n <= this.bottom;
    }

    public void scrollBy(int n) {
        this.amountScrolled += n;
        this.bindAmountScrolled();
        this.initialClickY = -2.0F;
    }

    public void actionPerformed(GuiButton guiButton) {
        if (guiButton.enabled) {
            if (guiButton.id == this.scrollUpButtonID) {
                this.amountScrolled = this.amountScrolled - this.slotHeight * 2 / 3;
                this.initialClickY = -2.0F;
                this.bindAmountScrolled();
            } else if (guiButton.id == this.scrollDownButtonID) {
                this.amountScrolled = this.amountScrolled + this.slotHeight * 2 / 3;
                this.initialClickY = -2.0F;
                this.bindAmountScrolled();
            }
        }
    }

    public void drawScreen(int n, int n2, float f) throws java.io.IOException {
        this.mouseX = n;
        this.mouseY = n2;
        this.drawBackground();
        int size = this.getSize();
        int scrollBarX = this.getScrollBarX();
        int n3 = scrollBarX + 6;
        if (n > this.left && n < this.right && n2 > this.top && n2 < this.bottom) {
            if (!Mouse.isButtonDown(0) || !this.getEnabled()) {
                for (; !this.mc.gameSettings.touchscreen && Mouse.next(); this.mc.currentScreen.handleMouseInput()) {
                    int eventDWheel = Mouse.getEventDWheel();
                    if (eventDWheel != 0) {
                        if (eventDWheel > 0) {
                            eventDWheel = -1;
                        } else if (eventDWheel < 0) {
                            eventDWheel = 1;
                        }

                        this.amountScrolled = this.amountScrolled + eventDWheel * this.slotHeight / 2;
                    }
                }

                this.initialClickY = -1.0F;
            } else if (this.initialClickY == -1.0F) {
                boolean flag = true;
                if (n2 >= this.top && n2 <= this.bottom) {
                    int n4 = this.width / 2 - this.getListWidth() / 2;
                    int n5 = this.width / 2 + this.getListWidth() / 2;
                    int n6 = n2 - this.top - this.headerPadding + (int)this.amountScrolled - 4;
                    int n7 = n6 / this.slotHeight;
                    if (n >= n4 && n <= n5 && n7 >= 0 && n6 >= 0 && n7 < size) {
                        boolean flag2 = n7 == this.selectedElement && Minecraft.getSystemTime() - this.lastClicked < 250L;
                        this.elementClicked(n7, flag2, n, n2);
                        this.selectedElement = n7;
                        this.lastClicked = Minecraft.getSystemTime();
                    } else if (n >= n4 && n <= n5 && n6 < 0) {
                        this.process(n - n4, n2 - this.top + (int)this.amountScrolled - 4);
                        flag = false;
                    }

                    if (n >= scrollBarX && n <= n3) {
                        this.scrollMultiplier = -1.0F;
                        int maxScroll = this.getMaxScroll();
                        if (maxScroll < 1) {
                            maxScroll = 1;
                        }

                        int n8 = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / this.getContentHeight());
                        if (n8 < 32) {
                            n8 = 32;
                        }

                        if (n8 > this.bottom - this.top - 8) {
                            n8 = this.bottom - this.top - 8;
                        }

                        this.scrollMultiplier = this.scrollMultiplier / ((float)(this.bottom - this.top - n8) / maxScroll);
                    } else {
                        this.scrollMultiplier = 1.0F;
                    }

                    if (flag) {
                        this.initialClickY = n2;
                    } else {
                        this.initialClickY = -2.0F;
                    }
                } else {
                    this.initialClickY = -2.0F;
                }
            } else if (this.initialClickY >= 0.0F) {
                this.amountScrolled = this.amountScrolled - (n2 - this.initialClickY) * this.scrollMultiplier;
                this.initialClickY = n2;
            }
        }

        this.bindAmountScrolled();
        GL11.glDisable(2896);
        GL11.glDisable(2912);
        Tessellator instance = Tessellator.instance;
        this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f2 = 32.0F;
        instance.startDrawingQuads();
        instance.setColorOpaque_I(2105376);
        instance.addVertexWithUV(this.left, this.bottom, 0.0, this.left / f2, (this.bottom + (int)this.amountScrolled) / f2);
        instance.addVertexWithUV(this.right, this.bottom, 0.0, this.right / f2, (this.bottom + (int)this.amountScrolled) / f2);
        instance.addVertexWithUV(this.right, this.top, 0.0, this.right / f2, (this.top + (int)this.amountScrolled) / f2);
        instance.addVertexWithUV(this.left, this.top, 0.0, this.left / f2, (this.top + (int)this.amountScrolled) / f2);
        instance.draw();
        int n4 = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
        int n5 = this.top + 4 - (int)this.amountScrolled;
        if (this.hasListHeader) {
            this.drawListHeader(n4, n5, instance);
        }

        this.drawSelectionBox(n4, n5, n, n2);
        GL11.glDisable(2929);
        byte b = 4;
        this.overlayBackground(0, this.top, 255, 255);
        this.overlayBackground(this.bottom, this.height, 255, 255);
        GL11.glEnable(3042);
        OpenGlHelper.glBlendFunc(770, 771, 0, 1);
        GL11.glDisable(3008);
        GL11.glShadeModel(7425);
        GL11.glDisable(3553);
        instance.startDrawingQuads();
        instance.setColorRGBA_I(0, 0);
        instance.addVertexWithUV(this.left, this.top + b, 0.0, 0.0, 1.0);
        instance.addVertexWithUV(this.right, this.top + b, 0.0, 1.0, 1.0);
        instance.setColorRGBA_I(0, 255);
        instance.addVertexWithUV(this.right, this.top, 0.0, 1.0, 0.0);
        instance.addVertexWithUV(this.left, this.top, 0.0, 0.0, 0.0);
        instance.draw();
        instance.startDrawingQuads();
        instance.setColorRGBA_I(0, 255);
        instance.addVertexWithUV(this.left, this.bottom, 0.0, 0.0, 1.0);
        instance.addVertexWithUV(this.right, this.bottom, 0.0, 1.0, 1.0);
        instance.setColorRGBA_I(0, 0);
        instance.addVertexWithUV(this.right, this.bottom - b, 0.0, 1.0, 0.0);
        instance.addVertexWithUV(this.left, this.bottom - b, 0.0, 0.0, 0.0);
        instance.draw();
        int maxScroll = this.getMaxScroll();
        if (maxScroll > 0) {
            int n6 = (this.bottom - this.top) * (this.bottom - this.top) / this.getContentHeight();
            if (n6 < 32) {
                n6 = 32;
            }

            if (n6 > this.bottom - this.top - 8) {
                n6 = this.bottom - this.top - 8;
            }

            int n7 = (int)this.amountScrolled * (this.bottom - this.top - n6) / maxScroll + this.top;
            if (n7 < this.top) {
                n7 = this.top;
            }

            instance.startDrawingQuads();
            instance.setColorRGBA_I(0, 255);
            instance.addVertexWithUV(scrollBarX, this.bottom, 0.0, 0.0, 1.0);
            instance.addVertexWithUV(n3, this.bottom, 0.0, 1.0, 1.0);
            instance.addVertexWithUV(n3, this.top, 0.0, 1.0, 0.0);
            instance.addVertexWithUV(scrollBarX, this.top, 0.0, 0.0, 0.0);
            instance.draw();
            instance.startDrawingQuads();
            instance.setColorRGBA_I(8421504, 255);
            instance.addVertexWithUV(scrollBarX, n7 + n6, 0.0, 0.0, 1.0);
            instance.addVertexWithUV(n3, n7 + n6, 0.0, 1.0, 1.0);
            instance.addVertexWithUV(n3, n7, 0.0, 1.0, 0.0);
            instance.addVertexWithUV(scrollBarX, n7, 0.0, 0.0, 0.0);
            instance.draw();
            instance.startDrawingQuads();
            instance.setColorRGBA_I(12632256, 255);
            instance.addVertexWithUV(scrollBarX, n7 + n6 - 1, 0.0, 0.0, 1.0);
            instance.addVertexWithUV(n3 - 1, n7 + n6 - 1, 0.0, 1.0, 1.0);
            instance.addVertexWithUV(n3 - 1, n7, 0.0, 1.0, 0.0);
            instance.addVertexWithUV(scrollBarX, n7, 0.0, 0.0, 0.0);
            instance.draw();
        }

        this.renderDecorations(n, n2);
        GL11.glEnable(3553);
        GL11.glShadeModel(7424);
        GL11.glEnable(3008);
        GL11.glDisable(3042);
    }

    public void setEnabled(boolean flag) {
        this.enabled = flag;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public int getListWidth() {
        return 220;
    }

    public void drawSelectionBox(int n, int n2, int n3, int n4) throws java.io.IOException {
        int size = this.getSize();
        Tessellator instance = Tessellator.instance;

        for (int i = 0; i < size; i++) {
            int n5 = n2 + i * this.slotHeight + this.headerPadding;
            int n6 = this.slotHeight - 4;
            if (n5 <= this.bottom && n5 + n6 >= this.top) {
                if (this.showSelectionBox && this.isSelected(i)) {
                    int n7 = this.left + (this.width / 2 - this.getListWidth() / 2);
                    int n8 = this.left + this.width / 2 + this.getListWidth() / 2;
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glDisable(3553);
                    instance.startDrawingQuads();
                    instance.setColorOpaque_I(8421504);
                    instance.addVertexWithUV(n7, n5 + n6 + 2, 0.0, 0.0, 1.0);
                    instance.addVertexWithUV(n8, n5 + n6 + 2, 0.0, 1.0, 1.0);
                    instance.addVertexWithUV(n8, n5 - 2, 0.0, 1.0, 0.0);
                    instance.addVertexWithUV(n7, n5 - 2, 0.0, 0.0, 0.0);
                    instance.setColorOpaque_I(0);
                    instance.addVertexWithUV(n7 + 1, n5 + n6 + 1, 0.0, 0.0, 1.0);
                    instance.addVertexWithUV(n8 - 1, n5 + n6 + 1, 0.0, 1.0, 1.0);
                    instance.addVertexWithUV(n8 - 1, n5 - 1, 0.0, 1.0, 0.0);
                    instance.addVertexWithUV(n7 + 1, n5 - 1, 0.0, 0.0, 0.0);
                    instance.draw();
                    GL11.glEnable(3553);
                }

                this.drawSlot(i, n, n5, n6, instance, n3, n4);
            }
        }
    }

    public int getScrollBarX() {
        return this.width / 2 + 124;
    }

    public void overlayBackground(int n, int n2, int n3, int n4) {
        Tessellator instance = Tessellator.instance;
        this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32.0F;
        instance.startDrawingQuads();
        instance.setColorRGBA_I(4210752, n4);
        instance.addVertexWithUV(this.left, n2, 0.0, 0.0, n2 / f);
        instance.addVertexWithUV(this.left + this.width, n2, 0.0, this.width / f, n2 / f);
        instance.setColorRGBA_I(4210752, n3);
        instance.addVertexWithUV(this.left + this.width, n, 0.0, this.width / f, n / f);
        instance.addVertexWithUV(this.left, n, 0.0, 0.0, n / f);
        instance.draw();
    }

    public void setSlotXBoundsFromLeft(int n) {
        this.left = n;
        this.right = n + this.width;
    }

    public int getSlotHeight() {
        return this.slotHeight;
    }
}

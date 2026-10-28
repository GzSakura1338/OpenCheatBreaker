package com.cheatbreaker.client.ui.component;

import net.minecraft.client.gui.Gui;
import net.optifine.GlStateManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class ScrollBar extends UIElement {
    public boolean flag;
    public float floatValue;
    public double pendingScroll;
    public UIElement uielement;
    public boolean dragging;
    public float scrollOffset;
    public float floatValue2;
    public boolean flag2;
    public float contentHeight;

    public ScrollBar(UIElement uielement) {
        this.uielement = uielement;
    }

    public boolean isScrollable() {
        return this.contentHeight > this.height;
    }

    public boolean isDragging() {
        return this.dragging;
    }

    public void setContentHeight(float f) {
        this.contentHeight = f;
    }

    @Override
    public void render(float f, float f2, boolean flag) {
        this.flag = true;
        GlStateManager.popMatrix();
        boolean isScrollable = this.isScrollable();
        if (this.flag2 && (!Mouse.isButtonDown(0) || !this.containsPoint(f, f2) || !flag)) {
            this.flag2 = false;
        }

        if (this.dragging && !Mouse.isButtonDown(0)) {
            this.dragging = false;
        }

        float height = this.height;
        float contentHeight = this.contentHeight;
        float f3 = height / contentHeight * 100.0F;
        float f4 = height / 100.0F * f3;
        float f5 = this.scrollOffset / 100.0F * f3;
        if (Mouse.isButtonDown(0) && this.dragging) {
            float f6 = f2 - this.y;
            float f7 = f6 / this.height;
            this.scrollOffset = -(this.contentHeight * f7) + f4 / 2.0F;
        }

        if (isScrollable) {
            float height2 = this.height;
            boolean flag2 = f >= this.x && f <= this.x + this.width && f2 > this.y - f5 && f2 < this.y + f4 - f5;
            if (this.flag2) {
                if (this.scrollOffset != this.floatValue && this.floatValue != f4 / 2.0F && this.floatValue != f4 / 2.0F + -this.contentHeight + height2) {
                    if (f2 > this.y + f4 - f4 / 4.0F - f5) {
                        this.scrollOffset -= contentHeight / 7.0F;
                    } else if (f2 < this.y + f4 / 4.0F - f5) {
                        this.scrollOffset += contentHeight / 7.0F;
                    }

                    this.floatValue = this.scrollOffset;
                } else if (f2 > this.y + f4 - f4 / 4.0F - f5 || f2 < this.y + f4 / 4.0F - f5) {
                    this.floatValue = 1.0F;
                }
            }

            if (this.scrollOffset < -this.contentHeight + height2) {
                this.scrollOffset = -this.contentHeight + height2;
                this.pendingScroll = 0.0;
            }

            if (this.scrollOffset > 0.0F) {
                this.scrollOffset = 0.0F;
                this.pendingScroll = 0.0;
            }

            Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, -13158601);
            Gui.drawRect(this.x, this.y - f5, this.x + this.width, this.y + f4 - f5, !flag2 && !this.flag2 ? -4180940 : -52429);
        }

        if (!isScrollable && this.scrollOffset != 0.0F) {
            this.scrollOffset = 0.0F;
        }
    }

    public void endScrollRegion(float f, float f2, boolean flag) {
        this.flag = true;
        GlStateManager.popMatrix();
        boolean isScrollable = this.isScrollable();
        if (this.flag2 && (!Mouse.isButtonDown(0) || !this.containsPoint(f, f2) || !flag)) {
            this.flag2 = false;
        }

        if (this.dragging && !Mouse.isButtonDown(0)) {
            this.dragging = false;
        }

        float height = this.height;
        float contentHeight = this.contentHeight;
        float f3 = height / contentHeight * 100.0F;
        float f4 = height / 100.0F * f3;
        float f5 = this.scrollOffset / 100.0F * f3;
        if (Mouse.isButtonDown(0) && this.dragging) {
            float f6 = f2 - this.y;
            float f7 = f6 / this.height;
            this.scrollOffset = -(this.contentHeight - this.height / 2.0F) + this.contentHeight * f7 + f4 / 2.0F;
        }

        if (isScrollable) {
            float height2 = this.height;
            boolean flag2 = f >= this.x && f <= this.x + this.width && f2 > this.y - f5 && f2 < this.y + f4 - f5;
            if (this.flag2) {
                if (this.scrollOffset != this.floatValue && this.floatValue != f4 / 2.0F && this.floatValue != f4 / 2.0F + -this.contentHeight + height2) {
                    if (f2 > this.y + this.height - f4 - f4 / 4.0F + f5) {
                        this.scrollOffset = contentHeight / 7.0F;
                    } else if (f2 < this.y + this.height - f4 / 4.0F + f5) {
                        this.scrollOffset += contentHeight / 7.0F;
                    }

                    this.floatValue = this.scrollOffset;
                } else if (f2 > this.y + this.height - f4 - f4 / 4.0F + f5 || f2 < this.y + this.height - f4 / 4.0F - f5) {
                    this.floatValue = 1.0F;
                }
            }

            if (this.scrollOffset < -this.contentHeight + height2) {
                this.scrollOffset = -this.contentHeight + height2;
                this.pendingScroll = 0.0;
            }

            if (this.scrollOffset > 0.0F) {
                this.scrollOffset = 0.0F;
                this.pendingScroll = 0.0;
            }

            Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, -13158601);
            Gui.drawRect(this.x, this.y + this.height + f5, this.x + this.width, this.y + this.height - f4 + f5, !flag2 && !this.flag2 ? -4180940 : -52429);
        }

        if (!isScrollable && this.scrollOffset != 0.0F) {
            this.scrollOffset = 0.0F;
        }
    }

    public boolean isFlag2() {
        return this.flag2;
    }

    public void scrollUp(float f, float f2, boolean flag) {
        if (flag && (this.uielement == null || this.uielement.containsPoint(f, f2)) && this.pendingScroll != 0.0) {
            double d = this.pendingScroll / 8.0;
            this.scrollOffset = (float)(this.scrollOffset - d);
            this.pendingScroll -= d;
            if (Math.abs(this.pendingScroll) < 0.1) {
                this.pendingScroll = 0.0;
            }
        }

        if (this.flag) {
            if (this.scrollOffset < -this.contentHeight + this.height) {
                this.scrollOffset = -this.contentHeight + this.height;
                this.pendingScroll = 0.0;
            }

            if (this.scrollOffset > 0.0F) {
                this.scrollOffset = 0.0F;
                this.pendingScroll = 0.0;
            }
        }

        GlStateManager.pushMatrix();
        GL11.glTranslatef(0.0F, -this.scrollOffset, 0.0F);
    }

    public float getScrollOffset() {
        return this.scrollOffset;
    }

    public UIElement getUielement() {
        return this.uielement;
    }

    @Override
    public boolean containsPoint(float f, float f2) {
        return f >= this.x && f <= this.x + this.width && f2 > this.y && f2 < this.y + this.height;
    }

    @Override
    public boolean mouseClicked(float f, float f2, int n, boolean flag) {
        if (this.containsPoint(f, f2) && flag) {
            this.floatValue2 = f2 - this.y;
            this.dragging = true;
        }

        return false;
    }

    @Override
    public void handleMouseInput() {
        int eventDWheel = Mouse.getEventDWheel();
        if (eventDWheel != 0 && this.contentHeight >= this.height) {
            this.pendingScroll += eventDWheel / 2.75F;
        }
    }

    public void scrollDown(float f, float f2, boolean flag) {
        if (flag && (this.uielement == null || this.uielement.containsPoint(f, f2)) && this.pendingScroll != 0.0) {
            double d = this.pendingScroll / 8.0;
            this.scrollOffset = (float)(this.scrollOffset + d);
            this.pendingScroll -= d;
            if (Math.abs(this.pendingScroll) < 0.1) {
                this.pendingScroll = 0.0;
            }
        }

        if (this.flag) {
            if (this.scrollOffset < -this.contentHeight + this.height) {
                this.scrollOffset = -this.contentHeight + this.height;
                this.pendingScroll = 0.0;
            }

            if (this.scrollOffset > 0.0F) {
                this.scrollOffset = 0.0F;
                this.pendingScroll = 0.0;
            }
        }

        GlStateManager.pushMatrix();
        GL11.glTranslatef(0.0F, this.scrollOffset, 0.0F);
    }
}

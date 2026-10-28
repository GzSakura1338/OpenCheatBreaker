package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.theme.Theme;
import net.optifine.GlStateManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public abstract class Panel extends SettingComponent {
    public int scrollOffset = 0;
    public int contentHeight = 0;
    public boolean flag = false;
    public float floatValue;
    public boolean dragging = false;
    public double pendingScroll = 0.0;
    public final boolean flag2 = false;
    public Theme theme2 = CheatBreaker.getInstance().getConfigManager().theme;
    public int intValue;
    public int intValue2;

    public abstract void selectMod(AbstractMod abstractMod);

    public void updateScroll(int n, int n2) {
        if (this.isMouseOver(n, n2)) {
            double round = Math.round(this.pendingScroll / 25.0);
            this.pendingScroll -= round;
            if (this.pendingScroll != 0.0) {
                this.scrollOffset = (int)(this.scrollOffset + round);
            }
        } else {
            this.pendingScroll = 0.0;
        }

        if (this.flag) {
            if (this.scrollOffset < -this.contentHeight + this.height) {
                this.scrollOffset = -this.contentHeight + this.height;
                this.pendingScroll = 0.0;
            }

            if (this.scrollOffset > 0) {
                this.scrollOffset = 0;
                this.pendingScroll = 0.0;
            }
        }

        GlStateManager.pushMatrix();
        GL11.glTranslatef(0.0F, this.scrollOffset, 0.0F);
    }

    @Override
    public void render(int n, int n2, float f) {
    }

    public Panel(float f, int n, int n2, int n3, int n4) {
        super(f);
        this.intValue = n;
        this.intValue2 = n2;
        this.x = n;
        this.y = n2;
        this.width = n3;
        this.height = n4;
    }

    public void endScrollRegion(int n, int n2) {
        this.flag = true;
        GlStateManager.popMatrix();
        boolean flag = this.contentHeight > this.height;
        if (this.dragging && !Mouse.isButtonDown(0)) {
            this.dragging = false;
        }

        double d = this.height - 10;
        double contentHeight = this.contentHeight;
        double d2 = d / contentHeight * 100.0;
        double d3 = d / 100.0 * d2;
        double d4 = this.scrollOffset / 100.0 * d2;
        if (flag) {
            int height = this.height;
            boolean flag2 = n > (this.x + this.width - 9) * this.scale
                && n < (this.x + this.width - 3) * this.scale
                && n2 > (this.y + 11 - d4) * this.scale
                && n2 < (this.y + 8 + d3 - d4) * this.scale;
            boolean flag3 = n > (this.x + this.width - 9) * this.scale
                && n < (this.x + this.width - 3) * this.scale
                && n2 > (this.y + 11) * this.scale
                && n2 < (this.y + 6 + d - 3.0) * this.scale;
            if (this.dragging) {
                if (this.scrollOffset != this.floatValue && this.floatValue != d3 / 2.0 && this.floatValue != d3 / 2.0 + -this.contentHeight + height) {
                    if (n2 > (this.y + 11 + d3 - d3 / 4.0 - d4) * this.scale) {
                        this.scrollOffset = (int)(this.scrollOffset - contentHeight / 70.0);
                    } else if (n2 < (this.y + 11 + d3 / 4.0 - d4) * this.scale) {
                        this.scrollOffset = (int)(this.scrollOffset + contentHeight / 70.0);
                    }

                    this.floatValue = this.scrollOffset;
                } else if (n2 > (this.y + 11 + d3 - d3 / 4.0 - d4) * this.scale || n2 < (this.y + 11 + d3 / 4.0 - d4) * this.scale) {
                    this.floatValue = 1.0F;
                }
            }

            if (this.scrollOffset < -this.contentHeight + height) {
                this.scrollOffset = -this.contentHeight + height;
                this.pendingScroll = 0.0;
            }

            if (this.scrollOffset > 0) {
                this.scrollOffset = 0;
                this.pendingScroll = 0.0;
            }

            RenderUtils.drawRoundedRectFilledPrecise(
                this.x + this.width - 6,
                this.y + 11,
                this.x + this.width - 4,
                this.y + 6 + d - 3.0,
                2.0,
                flag3 && !flag2 ? this.theme2.getTrackHoverColorValue() : this.theme2.getTrackColorValue2()
            );
            RenderUtils.drawRoundedRectFilledPrecise(
                this.x + this.width - 7,
                this.y + 11 - d4,
                this.x + this.width - 3,
                this.y + 8 + d3 - d4,
                4.0,
                !flag2 && !this.dragging ? this.theme2.getThumbColorValue2() : this.theme2.getThumbHoverColorValue()
            );
        }

        if (!flag && this.scrollOffset != 0) {
            this.scrollOffset = 0;
        }
    }

    public abstract boolean hasSettings(AbstractMod abstractMod);

    @Override
    public void mouseScrolled(int n) {
        if (n != 0 && this.contentHeight >= this.height) {
            this.pendingScroll += n / 2;
        }
    }

    @Override
    public void mouseClicked(int n, int n2, int n3) throws java.io.IOException {
        double d = this.height - 10;
        double contentHeight = this.contentHeight;
        double d2 = d / contentHeight * 100.0;
        double d3 = d / 100.0 * d2;
        double d4 = this.scrollOffset / 100.0 * d2;
        boolean flag = n > (this.x + this.width - 9) * this.scale
            && n < (this.x + this.width - 3) * this.scale
            && n2 > (this.y + 11 - d4) * this.scale
            && n2 < (this.y + 8 + d3 - d4) * this.scale;
        boolean flag2 = n > (this.x + this.width - 9) * this.scale
            && n < (this.x + this.width - 3) * this.scale
            && n2 > (this.y + 11) * this.scale
            && n2 < (this.y + 6 + d - 3.0) * this.scale;
        if (n3 == 0 && flag2 || flag) {
            this.dragging = true;
        }
    }
}

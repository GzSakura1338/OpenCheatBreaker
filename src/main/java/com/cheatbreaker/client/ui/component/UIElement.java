package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import net.minecraft.client.Minecraft;

public abstract class UIElement {
    public float width;
    public Minecraft mc = Minecraft.getMinecraft();
    public int scrollOffsetY;
    public float y;
    public float x;
    public CheatBreaker cheatBreaker = CheatBreaker.getInstance();
    public boolean enabled = true;
    public float height;

    public boolean mouseClicked(float f, float f2, int n, boolean flag) throws java.io.IOException {
        return false;
    }

    public void tick() {
    }

    public boolean containsPoint(float f, float f2) {
        return this.enabled && f > this.x && f < this.x + this.width && f2 > this.y && f2 < this.y + this.height;
    }

    public abstract void render(float f, float f2, boolean flag);

    public float getY() {
        return this.y;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public float getHeight() {
        return this.height;
    }

    public void keyTyped(char c, int n) {
    }

    public float getX() {
        return this.x;
    }

    public boolean isMouseOverScaled(float f, float f2) {
        float floatValue = CheatBreaker.getInstance().getUiManager().getFloatValue();
        return this.enabled
            && f * floatValue > this.x
            && f * floatValue < this.x + this.width
            && f2 * floatValue > this.y + this.scrollOffsetY
            && f2 * floatValue < this.y + this.height + this.scrollOffsetY;
    }

    public boolean mouseClickedFallback(float f, float f2, int n) {
        return false;
    }

    public void setEnabled(boolean flag) {
        this.enabled = flag;
    }

    public void onClose() {
    }

    public void handleMouseInput() {
    }

    public void setBounds(float f, float f2, float f3, float f4) {
        this.x = f;
        this.y = f2;
        this.width = f3;
        this.height = f4;
    }

    public boolean renderAndTestHover(float f, float f2, boolean flag) {
        this.render(f, f2, flag);
        return this.enabled && this.containsPoint(f, f2);
    }

    public float getWidth() {
        return this.width;
    }

    public boolean mouseReleased(float f, float f2, int n, boolean flag) {
        return false;
    }
}

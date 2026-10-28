package com.cheatbreaker.client.ui.notification;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.animation.Animation;
import com.cheatbreaker.client.animation.LinearAnimation;
import com.cheatbreaker.client.ui.screen.FriendsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

public class Notification {
    public boolean noTitle;
    public Animation animation = new LinearAnimation(275L);
    public String title;
    public String[] lines;
    public float targetY;
    public float startY;
    public static final float WIDTH = 140.0F;
    public long createdAt;
    public static final float HEIGHT = 55.0F;

    public static void dismiss(String str) {
        FriendsScreen.getFriendsScreen().notify(str);
    }

    public void render() {
        float f = new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth() - 140;
        float f2 = this.startY - (this.startY - this.targetY) * this.animation.getProgress();
        if (this.noTitle) {
            Gui.drawVerticalGradient(f, f2, f + 140.0F, f2 + 55.0F, -819057106, -822083584);

            for (int i = 0; i < this.lines.length && i <= 3; i++) {
                String str = this.lines[i];
                CheatBreaker.getInstance().getFontManager().playRegular14.drawString(str, f + 4.0F, f2 + 4.0F + i * 10, -1);
            }
        } else {
            Gui.drawVerticalGradient(f, f2, f + 140.0F, f2 + 55.0F, -819057106, -822083584);
            CheatBreaker.getInstance().getFontManager().playRegular14.drawStringWithOutline(this.title, f + 4.0F, f2 + 4.0F, -1);
            Gui.drawRect(f + 4.0F, f2 + 14.5F, f + 140.0F - 5.0F, f2 + 15.0F, 777936478);

            for (int i = 0; i < this.lines.length && i <= 2; i++) {
                String str = this.lines[i];
                CheatBreaker.getInstance().getFontManager().playRegular14.drawString(str, f + 4.0F, f2 + 17.0F + i * 10, -1);
            }
        }

        if (!(Minecraft.getMinecraft().currentScreen instanceof FriendsScreen)) {
            String str = "";
            if (!this.title.toLowerCase().contains("link") && !this.title.toLowerCase().contains("clipboard")) {
                str = "Press Shift + Tab";
            }

            CheatBreaker.getInstance().getFontManager().playRegular14.drawString(str, f + 4.0F, f2 + getHeight() - 12.0F, 1879048191);
        }
    }

    public static int getWidth() {
        return 140;
    }

    public boolean isSettled() {
        return !this.animation.hasStarted() || this.animation.isFinished();
    }

    public static int getHeight() {
        return 55;
    }

    public void moveTo(float f) {
        this.startY = this.targetY;
        this.targetY = f;
        this.animation.start();
    }

    public float getPreviousY() {
        return this.startY;
    }

    public void setNoTitle(boolean flag) {
        this.noTitle = flag;
    }

    public Notification(String str, String[] strs, float f) {
        this.title = str;
        this.lines = strs;
        this.targetY = f;
        this.startY = f;
        this.createdAt = System.currentTimeMillis();
    }

    public float getTargetY() {
        return this.targetY;
    }

    public float getStartY() {
        return this.startY;
    }

    public static void post(String str, String str2) {
        FriendsScreen.getFriendsScreen().notify(str, str2);
    }

    public void setStartY(float f) {
        this.startY = f;
    }

    public void unusedSetSize(float f) {
    }

    public void setTargetY(float f) {
        this.targetY = f;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - this.createdAt > 3500L;
    }
}

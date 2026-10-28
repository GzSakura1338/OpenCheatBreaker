package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.mod.AbstractValueHudMod;
import com.cheatbreaker.client.mod.ValueRange;
import com.cheatbreaker.client.mod.setting.Setting;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.MathHelper;

public class FPSMod extends AbstractValueHudMod {
    public static String lastDebugText = null;
    public Setting showMinimumFps;
    public static int fps = 0;

    public int getIntValue() {
        if (Objects.equals(this.mc.debug, lastDebugText)) {
            return fps;
        }

        lastDebugText = this.mc.debug;
        FrameTimer twitchStream = this.mc.getTwitchStream();
        long[] frames = twitchStream.getFrames();
        int index = twitchStream.getIndex();
        int lastIndex = twitchStream.getLastIndex();
        if (index == lastIndex) {
            return fps;
        }

        int debugFPS = Minecraft.debugFPS;
        if (debugFPS <= 0) {
            debugFPS = 1;
        }

        long l = (long)(1.0 / debugFPS * 1.0E9);
        long l2 = 0L;

        for (int floorMod = MathHelper.floorMod(index - 1, frames.length); floorMod != lastIndex && l2 < 1.0E9; floorMod = MathHelper.floorMod(floorMod - 1, frames.length)) {
            long l3 = frames[floorMod];
            if (l3 > l) {
                l = l3;
            }

            l2 += l3;
        }

        double d = l / 1.0E9;
        fps = (int)(1.0 / d);
        return fps;
    }

    @Override
    public Number getNumericValue() {
        return Minecraft.debugFPS;
    }

    public FPSMod() throws java.io.IOException {
        super("FPS");
        this.setDescription("Displays your frames per second.");
        this.setLabel("[144 FPS]");
    }

    @Override
    public ValueRange getHiddenValueRange() {
        return new ValueRange(0, 1000, 3000);
    }

    @Override
    public String getStaticText() {
        return Minecraft.debugFPS + (this.showMinimumFps.getBooleanValue() ? "/" + this.getIntValue() : "");
    }

    @Override
    public String getDynamicText() {
        return super.getDynamicText() == null ? null : this.getStaticText();
    }

    @Override
    public void createGeneralOptions() {
        this.showMinimumFps = new Setting(this, "Show Minimum FPS").withHierarchyParents(this.generalOptions).changeValue(false);
        super.createGeneralOptions();
    }

    @Override
    public String getLabelText() {
        return "FPS";
    }
}

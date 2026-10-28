package com.cheatbreaker.client.event;

import net.minecraft.client.gui.ScaledResolution;

public class RenderPlayerListEvent extends CancellableEvent {
    public ScaledResolution scaledResolution;
    public int intValue;
    public int intValue2;

    public int getIntValue() {
        return this.intValue;
    }

    public RenderPlayerListEvent(ScaledResolution scaledResolution, int n, int n2) {
        this.scaledResolution = scaledResolution;
        this.intValue = n;
        this.intValue2 = n2;
    }

    public ScaledResolution getScaledResolution() {
        return this.scaledResolution;
    }

    public int getIntValue2() {
        return this.intValue2;
    }
}

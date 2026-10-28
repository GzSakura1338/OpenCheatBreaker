package com.cheatbreaker.client.event;

import net.minecraft.client.gui.ScaledResolution;

public class ScaledResolutionEvent7 extends Event {
    public ScaledResolution scaledResolution;

    public ScaledResolution getScaledResolution() {
        return this.scaledResolution;
    }

    public ScaledResolutionEvent7(ScaledResolution scaledResolution) {
        this.scaledResolution = scaledResolution;
    }
}

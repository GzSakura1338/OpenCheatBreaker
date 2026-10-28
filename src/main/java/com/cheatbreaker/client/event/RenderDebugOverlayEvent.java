package com.cheatbreaker.client.event;

import net.minecraft.client.gui.ScaledResolution;

public class RenderDebugOverlayEvent extends Event {
    public ScaledResolution scaledResolution;

    public ScaledResolution getScaledResolution() {
        return this.scaledResolution;
    }

    public RenderDebugOverlayEvent(ScaledResolution scaledResolution) {
        this.scaledResolution = scaledResolution;
    }
}

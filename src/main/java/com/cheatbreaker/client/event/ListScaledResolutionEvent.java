package com.cheatbreaker.client.event;

import com.cheatbreaker.client.mod.AbstractMod;
import java.util.List;
import net.minecraft.client.gui.ScaledResolution;

public class ListScaledResolutionEvent extends Event {
    public ScaledResolution scaledResolution;
    public List<AbstractMod> abstractModList;

    public List<AbstractMod> getAbstractModList() {
        return this.abstractModList;
    }

    public ScaledResolution getScaledResolution() {
        return this.scaledResolution;
    }

    public ListScaledResolutionEvent(ScaledResolution scaledResolution, List<AbstractMod> list) {
        this.scaledResolution = scaledResolution;
        this.abstractModList = list;
    }
}

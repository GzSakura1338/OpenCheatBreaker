package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.mod.AbstractHudMod;
import com.cheatbreaker.client.ui.AnchorPoint;
import com.cheatbreaker.client.ui.ScreenCorner;

public class DragHandle {
    public ModsScreen modsScreen;
    public AbstractHudMod abstractHudMod;
    public float originX;
    public float originY;
    public float originWidth;
    public float originHeight;
    public int mouseX;
    public int mouseY;
    public ScreenCorner screenCorner;
    public float originScale;
    public AnchorPoint anchorPoint;

    public DragHandle(ModsScreen modsScreen, AbstractHudMod abstractHudMod, ScreenCorner screenCorner, int n, int n2) {
        this.modsScreen = modsScreen;
        this.abstractHudMod = abstractHudMod;
        this.originX = abstractHudMod.getTranslationX();
        this.originY = abstractHudMod.getTranslationY();
        this.originWidth = abstractHudMod.hudWidth * abstractHudMod.getHudScale();
        this.originHeight = abstractHudMod.hudHeight * abstractHudMod.getHudScale();
        this.mouseX = n;
        this.mouseY = n2;
        this.screenCorner = screenCorner;
        this.originScale = (Float)abstractHudMod.scale.getValue();
        this.anchorPoint = abstractHudMod.getAnchorPoint();
    }
}

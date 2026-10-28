package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.ui.AnchorPoint;
import java.util.ArrayList;
import java.util.List;

public class HudEditorState {
    public ModsScreen modsScreen;
    public List<AbstractMod> mods;
    public List<AnchorPoint> anchorPoints;
    public List<Float> translationsX;
    public List<Float> translationsY;
    public List<Object> hudScales;

    public HudEditorState(ModsScreen modsScreen, List<HudPlacement> list) {
        this.modsScreen = modsScreen;
        ArrayList list2 = new ArrayList();
        ArrayList list3 = new ArrayList();
        ArrayList list4 = new ArrayList();
        ArrayList list5 = new ArrayList();
        ArrayList list6 = new ArrayList();

        for (HudPlacement hudPlacement : list) {
            if (hudPlacement.abstractMod.getAnchorPoint() != null) {
                list2.add(hudPlacement.abstractMod);
                list3.add(hudPlacement.abstractMod.getAnchorPoint());
                list4.add(hudPlacement.abstractMod.getTranslationX());
                list5.add(hudPlacement.abstractMod.getTranslationY());
                list6.add(hudPlacement.abstractMod.getHudScale());
            }
        }

        this.mods = list2;
        this.anchorPoints = list3;
        this.translationsX = list4;
        this.translationsY = list5;
        this.hudScales = list6;
    }
}

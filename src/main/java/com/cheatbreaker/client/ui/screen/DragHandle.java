package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.mod.AbstractHudMod;
import com.cheatbreaker.client.ui.AnchorPoint;
import com.cheatbreaker.client.ui.ScreenCorner;

public class DragHandle {
   public ModsScreen modsScreen;
   public AbstractHudMod abstractHudMod;
   public float floatValue;
   public float floatValue2;
   public float floatValue3;
   public float floatValue4;
   public int intValue;
   public int intValue2;
   public ScreenCorner screenCorner;
   public float floatValue5;
   public AnchorPoint anchorPoint;

   public DragHandle(ModsScreen modsScreen, AbstractHudMod abstractHudMod, ScreenCorner screenCorner, int n, int n2) {
      this.modsScreen = modsScreen;
      this.abstractHudMod = abstractHudMod;
      this.floatValue = abstractHudMod.getTranslationX();
      this.floatValue2 = abstractHudMod.getTranslationY();
      this.floatValue3 = abstractHudMod.hudWidth * abstractHudMod.getHudScale();
      this.floatValue4 = abstractHudMod.hudHeight * abstractHudMod.getHudScale();
      this.intValue = n;
      this.intValue2 = n2;
      this.screenCorner = screenCorner;
      this.floatValue5 = (Float)abstractHudMod.scale.getValue();
      this.anchorPoint = abstractHudMod.getAnchorPoint();
   }
}

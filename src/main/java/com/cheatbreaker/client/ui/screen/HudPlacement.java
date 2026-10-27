package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.mod.AbstractMod;

public class HudPlacement {
   public AbstractMod abstractMod;
   public float x;
   public float y;

   public HudPlacement(AbstractMod abstractMod, float f, float f2) {
      this.abstractMod = abstractMod;
      this.x = f;
      this.y = f2;
   }
}

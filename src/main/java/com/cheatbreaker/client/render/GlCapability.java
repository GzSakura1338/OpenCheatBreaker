package com.cheatbreaker.client.render;

import org.lwjgl.opengl.GL11;

public class GlCapability {
   public boolean flag = false;
   public int intValue;

   public void run() {
      this.process(false);
   }

   public void run2() {
      this.process(true);
   }

   public GlCapability(int n) {
      this.intValue = n;
   }

   public void process(boolean flag) {
      if (flag != this.flag) {
         this.flag = flag;
         if (flag) {
            GL11.glEnable(this.intValue);
         } else {
            GL11.glDisable(this.intValue);
         }
      }
   }
}

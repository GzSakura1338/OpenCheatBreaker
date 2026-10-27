package com.cheatbreaker.client.minimap;

public class RadarContext {
   public int scale = 0;
   public int centerX = 0;
   public int centerY = 0;
   public int zoom = 0;

   public void set(int n, int n2, int n3, int n4) {
      this.scale = n;
      this.centerX = n2;
      this.centerY = n3;
      this.zoom = n4;
   }
}

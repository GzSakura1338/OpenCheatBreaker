package net.minecraft.world;

public enum EnumSkyBlock {
   SKY(15),
   BLOCK(0);

   public int defaultLightValue;

   EnumSkyBlock(int n) {
      this.defaultLightValue = n;
   }
}

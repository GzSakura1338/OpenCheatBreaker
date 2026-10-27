package net.minecraft.world;

public class ColorizerGrass {
   public static int[] grassBuffer = new int[65536];

   public static void setGrassBiomeColorizer(int[] ints) {
      grassBuffer = ints;
   }

   public static int getGrassColor(double d, double d2) {
      d2 *= d;
      int n = (int)((1.0 - d) * 255.0);
      int n2 = (int)((1.0 - d2) * 255.0);
      int n3 = n2 << 8 | n;
      return n3 > grassBuffer.length ? -65281 : grassBuffer[n3];
   }
}

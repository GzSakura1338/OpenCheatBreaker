package net.minecraft.world;

public class ColorizerFoliage {
   public static int[] foliageBuffer = new int[65536];

   public static void setFoliageBiomeColorizer(int[] ints) {
      foliageBuffer = ints;
   }

   public static int getFoliageColor(double d, double d2) {
      d2 *= d;
      int n = (int)((1.0 - d) * 255.0);
      int n2 = (int)((1.0 - d2) * 255.0);
      return foliageBuffer[n2 << 8 | n];
   }

   public static int getFoliageColorPine() {
      return 6396257;
   }

   public static int getFoliageColorBirch() {
      return 8431445;
   }

   public static int getFoliageColorBasic() {
      return 4764952;
   }
}

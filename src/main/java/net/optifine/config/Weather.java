package net.optifine.config;

import net.minecraft.world.World;

public enum Weather {
   CLEAR,
   RAIN,
   THUNDER;

   public static Weather getWeather(World world, float f) {
      float thunderStrength = world.getThunderStrength(f);
      if (thunderStrength > 0.5F) {
         return THUNDER;
      }

      float rainStrength = world.getRainStrength(f);
      return rainStrength > 0.5F ? RAIN : CLEAR;
   }
}

package net.minecraft.potion;

import net.minecraft.util.ResourceLocation;

public class PotionHealth extends Potion {
   public PotionHealth(int potionID, ResourceLocation location, boolean badEffect, int potionColor) {
      super(potionID, location, badEffect, potionColor);
   }

   @Override
   public boolean isInstant() {
      return true;
   }

   @Override
   public boolean isReady(int n, int n2) {
      return n >= 1;
   }
}

package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentOxygen extends Enchantment {
   public EnchantmentOxygen(int enchID, ResourceLocation resourceLocation, int n) {
      super(enchID, resourceLocation, n, EnumEnchantmentType.ARMOR_HEAD);
      this.setName("oxygen");
   }

   @Override
   public int getMinEnchantability(int enchantmentLevel) {
      return 10 * enchantmentLevel;
   }

   @Override
   public int getMaxEnchantability(int enchantmentLevel) {
      return this.getMinEnchantability(enchantmentLevel) + 30;
   }

   @Override
   public int getMaxLevel() {
      return 3;
   }
}

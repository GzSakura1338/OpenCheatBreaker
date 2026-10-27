package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentKnockback extends Enchantment {
   public EnchantmentKnockback(int n, ResourceLocation resourceLocation, int n2) {
      super(n, resourceLocation, n2, EnumEnchantmentType.WEAPON);
      this.setName("knockback");
   }

   @Override
   public int getMinEnchantability(int enchantmentLevel) {
      return 5 + 20 * (enchantmentLevel - 1);
   }

   @Override
   public int getMaxEnchantability(int enchantmentLevel) {
      return super.getMinEnchantability(enchantmentLevel) + 50;
   }

   @Override
   public int getMaxLevel() {
      return 2;
   }
}

package net.minecraft.enchantment;

import java.util.Random;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantmentDurability extends Enchantment {
   public EnchantmentDurability(int enchID, ResourceLocation enchName, int enchWeight) {
      super(enchID, enchName, enchWeight, EnumEnchantmentType.BREAKABLE);
      this.setName("durability");
   }

   @Override
   public int getMinEnchantability(int enchantmentLevel) {
      return 5 + (enchantmentLevel - 1) * 8;
   }

   @Override
   public int getMaxEnchantability(int enchantmentLevel) {
      return super.getMinEnchantability(enchantmentLevel) + 50;
   }

   @Override
   public int getMaxLevel() {
      return 3;
   }

   @Override
   public boolean canApply(ItemStack stack) {
      return stack.isItemStackDamageable() ? true : super.canApply(stack);
   }

   public static boolean negateDamage(ItemStack itemStack, int n, Random random) {
      return itemStack.getItem() instanceof ItemArmor && random.nextFloat() < 0.6F ? false : random.nextInt(n + 1) > 0;
   }
}

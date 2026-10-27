package net.minecraft.enchantment;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantmentUntouching extends Enchantment {
   public EnchantmentUntouching(int n, ResourceLocation resourceLocation, int n2) {
      super(n, resourceLocation, n2, EnumEnchantmentType.DIGGER);
      this.setName("untouching");
   }

   @Override
   public int getMinEnchantability(int enchantmentLevel) {
      return 15;
   }

   @Override
   public int getMaxEnchantability(int enchantmentLevel) {
      return super.getMinEnchantability(enchantmentLevel) + 50;
   }

   @Override
   public int getMaxLevel() {
      return 1;
   }

   @Override
   public boolean canApplyTogether(Enchantment ench) {
      return super.canApplyTogether(ench) && ench.effectId != fortune.effectId;
   }

   @Override
   public boolean canApply(ItemStack stack) {
      return stack.getItem() == Items.shears ? true : super.canApply(stack);
   }
}

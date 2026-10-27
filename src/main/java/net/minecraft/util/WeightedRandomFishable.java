package net.minecraft.util;

import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

public class WeightedRandomFishable extends WeightedRandom.Item {
   public ItemStack returnStack;
   public float maxDamagePercent;
   public boolean enchantable;

   public WeightedRandomFishable(ItemStack returnStackIn, int itemWeightIn) {
      super(itemWeightIn);
      this.returnStack = returnStackIn;
   }

   public ItemStack getItemStack(Random random) {
      ItemStack itemStack = this.returnStack.copy();
      if (this.maxDamagePercent > 0.0F) {
         int n = (int)(this.maxDamagePercent * this.returnStack.getMaxDamage());
         int n2 = itemStack.getMaxDamage() - random.nextInt(random.nextInt(n) + 1);
         if (n2 > n) {
            n2 = n;
         }

         if (n2 < 1) {
            n2 = 1;
         }

         itemStack.setItemDamage(n2);
      }

      if (this.enchantable) {
         EnchantmentHelper.addRandomEnchantment(random, itemStack, 30);
      }

      return itemStack;
   }

   public WeightedRandomFishable setMaxDamagePercent(float maxDamagePercentIn) {
      this.maxDamagePercent = maxDamagePercentIn;
      return this;
   }

   public WeightedRandomFishable setEnchantable() {
      this.enchantable = true;
      return this;
   }
}

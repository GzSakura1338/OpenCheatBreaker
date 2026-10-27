package net.minecraft.enchantment;

import net.minecraft.util.WeightedRandom.Item;

public class EnchantmentData extends Item {
   public Enchantment enchantmentobj;
   public int enchantmentLevel;

   public EnchantmentData(Enchantment enchantmentObj, int enchLevel) {
      super(enchantmentObj.getWeight());
      this.enchantmentobj = enchantmentObj;
      this.enchantmentLevel = enchLevel;
   }
}

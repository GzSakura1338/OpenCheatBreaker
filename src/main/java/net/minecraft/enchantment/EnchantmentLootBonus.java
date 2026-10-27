package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentLootBonus extends Enchantment {
   public EnchantmentLootBonus(int n, ResourceLocation resourceLocation, int n2, EnumEnchantmentType enumEnchantmentType) {
      super(n, resourceLocation, n2, enumEnchantmentType);
      if (enumEnchantmentType == EnumEnchantmentType.DIGGER) {
         this.setName("lootBonusDigger");
      } else if (enumEnchantmentType == EnumEnchantmentType.FISHING_ROD) {
         this.setName("lootBonusFishing");
      } else {
         this.setName("lootBonus");
      }
   }

   @Override
   public int getMinEnchantability(int enchantmentLevel) {
      return 15 + (enchantmentLevel - 1) * 9;
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
   public boolean canApplyTogether(Enchantment ench) {
      return super.canApplyTogether(ench) && ench.effectId != silkTouch.effectId;
   }
}

package net.optifine.config;

import net.minecraft.enchantment.Enchantment;

public class ParserEnchantmentId implements IParserInt {
   @Override
   public int parse(String str, int n) {
      Enchantment enchantmentByLocation = Enchantment.getEnchantmentByLocation(str);
      return enchantmentByLocation == null ? n : enchantmentByLocation.effectId;
   }
}

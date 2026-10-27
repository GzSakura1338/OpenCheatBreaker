package net.minecraft.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class EnchantmentProtection extends Enchantment {
   public static String[] protectionName = new String[]{"all", "fire", "fall", "explosion", "projectile"};
   public static int[] baseEnchantability = new int[]{1, 10, 5, 5, 3};
   public static int[] levelEnchantability = new int[]{11, 8, 6, 8, 6};
   public static int[] thresholdEnchantability = new int[]{20, 12, 10, 12, 15};
   public int protectionType;

   public EnchantmentProtection(int n, ResourceLocation resourceLocation, int n2, int n3) {
      super(n, resourceLocation, n2, EnumEnchantmentType.ARMOR);
      this.protectionType = n3;
      if (n3 == 2) {
         this.type = EnumEnchantmentType.ARMOR_FEET;
      }
   }

   @Override
   public int getMinEnchantability(int enchantmentLevel) {
      return baseEnchantability[this.protectionType] + (enchantmentLevel - 1) * levelEnchantability[this.protectionType];
   }

   @Override
   public int getMaxEnchantability(int enchantmentLevel) {
      return this.getMinEnchantability(enchantmentLevel) + thresholdEnchantability[this.protectionType];
   }

   @Override
   public int getMaxLevel() {
      return 4;
   }

   @Override
   public int calcModifierDamage(int level, DamageSource source) {
      if (source.canHarmInCreative()) {
         return 0;
      }

      float f = (6 + level * level) / 3.0F;
      return this.protectionType == 0
         ? MathHelper.floor_float(f * 0.75F)
         : (
            this.protectionType == 1 && source.isFireDamage()
               ? MathHelper.floor_float(f * 1.25F)
               : (
                  this.protectionType == 2 && source == DamageSource.fall
                     ? MathHelper.floor_float(f * 2.5F)
                     : (
                        this.protectionType == 3 && source.isExplosion()
                           ? MathHelper.floor_float(f * 1.5F)
                           : (this.protectionType == 4 && source.isProjectile() ? MathHelper.floor_float(f * 1.5F) : 0)
                     )
               )
         );
   }

   @Override
   public String getName() {
      return "enchantment.protect." + protectionName[this.protectionType];
   }

   @Override
   public boolean canApplyTogether(Enchantment ench) {
      if (!(ench instanceof EnchantmentProtection)) {
         return super.canApplyTogether(ench);
      }

      EnchantmentProtection ench2 = (EnchantmentProtection)ench;
      return ench2.protectionType == this.protectionType ? false : this.protectionType == 2 || ench2.protectionType == 2;
   }

   public static int getFireTimeForEntity(Entity entity, int n) {
      int maxEnchantmentLevel = EnchantmentHelper.getMaxEnchantmentLevel(Enchantment.fireProtection.effectId, entity.getInventory());
      if (maxEnchantmentLevel > 0) {
         n -= MathHelper.floor_float((float)n * maxEnchantmentLevel * 0.15F);
      }

      return n;
   }

   public static double computeDouble(Entity entity, double d) {
      int maxEnchantmentLevel = EnchantmentHelper.getMaxEnchantmentLevel(Enchantment.blastProtection.effectId, entity.getInventory());
      if (maxEnchantmentLevel > 0) {
         d -= MathHelper.floor_double(d * (maxEnchantmentLevel * 0.15F));
      }

      return d;
   }
}

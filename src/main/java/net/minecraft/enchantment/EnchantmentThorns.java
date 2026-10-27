package net.minecraft.enchantment;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

public class EnchantmentThorns extends Enchantment {
   public EnchantmentThorns(int n, ResourceLocation resourceLocation, int n2) {
      super(n, resourceLocation, n2, EnumEnchantmentType.ARMOR_TORSO);
      this.setName("thorns");
   }

   @Override
   public int getMinEnchantability(int enchantmentLevel) {
      return 10 + 20 * (enchantmentLevel - 1);
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
      return stack.getItem() instanceof ItemArmor ? true : super.canApply(stack);
   }

   @Override
   public void onUserHurt(EntityLivingBase user, Entity attacker, int level) {
      Random rNG = user.getRNG();
      ItemStack enchantedItem = EnchantmentHelper.getEnchantedItem(Enchantment.thorns, user);
      if (shouldDamageAttacker(level, rNG)) {
         if (attacker != null) {
            attacker.attackEntityFrom(DamageSource.causeThornsDamage(user), getDamageAmount(level, rNG));
            attacker.playSound("damage.thorns", 0.5F, 1.0F);
         }

         if (enchantedItem != null) {
            enchantedItem.damageItem(3, user);
         }
      } else if (enchantedItem != null) {
         enchantedItem.damageItem(1, user);
      }
   }

   public static boolean shouldDamageAttacker(int n, Random random) {
      return n <= 0 ? false : random.nextFloat() < 0.15F * n;
   }

   public static int getDamageAmount(int n, Random random) {
      return n > 10 ? n - 10 : 1 + random.nextInt(4);
   }
}

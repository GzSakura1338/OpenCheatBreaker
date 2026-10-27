package net.minecraft.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class EnchantmentDamage extends Enchantment {
   public static String[] protectionName = new String[]{"all", "undead", "arthropods"};
   public static int[] baseEnchantability = new int[]{1, 5, 5};
   public static int[] levelEnchantability = new int[]{11, 8, 8};
   public static int[] thresholdEnchantability = new int[]{20, 20, 20};
   public int damageType;

   public EnchantmentDamage(int enchID, ResourceLocation enchName, int enchWeight, int classification) {
      super(enchID, enchName, enchWeight, EnumEnchantmentType.WEAPON);
      this.damageType = classification;
   }

   @Override
   public int getMinEnchantability(int enchantmentLevel) {
      return baseEnchantability[this.damageType] + (enchantmentLevel - 1) * levelEnchantability[this.damageType];
   }

   @Override
   public int getMaxEnchantability(int enchantmentLevel) {
      return this.getMinEnchantability(enchantmentLevel) + thresholdEnchantability[this.damageType];
   }

   @Override
   public int getMaxLevel() {
      return 5;
   }

   @Override
   public float calcDamageByCreature(int level, EnumCreatureAttribute creatureType) {
      return this.damageType == 0
         ? level * 1.25F
         : (
            this.damageType == 1 && creatureType == EnumCreatureAttribute.UNDEAD
               ? level * 2.5F
               : (this.damageType == 2 && creatureType == EnumCreatureAttribute.ARTHROPOD ? level * 2.5F : 0.0F)
         );
   }

   @Override
   public String getName() {
      return "enchantment.damage." + protectionName[this.damageType];
   }

   @Override
   public boolean canApplyTogether(Enchantment ench) {
      return !(ench instanceof EnchantmentDamage);
   }

   @Override
   public boolean canApply(ItemStack stack) {
      return stack.getItem() instanceof ItemAxe ? true : super.canApply(stack);
   }

   @Override
   public void onEntityDamaged(EntityLivingBase user, Entity target, int level) {
      if (target instanceof EntityLivingBase) {
         EntityLivingBase target2 = (EntityLivingBase)target;
         if (this.damageType == 2 && target2.getCreatureAttribute() == EnumCreatureAttribute.ARTHROPOD) {
            int n = 20 + user.getRNG().nextInt(10 * level);
            target2.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, n, 3));
         }
      }
   }
}

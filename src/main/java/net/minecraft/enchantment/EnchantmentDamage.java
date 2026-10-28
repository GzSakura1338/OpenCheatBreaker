package net.minecraft.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class EnchantmentDamage extends Enchantment {
    public static String[] protectionName = new String[]{"all", "undead", "arthropods"};
    public static int[] baseEnchantability = new int[]{1, 5, 5};
    public static int[] levelEnchantability = new int[]{11, 8, 8};
    public static int[] thresholdEnchantability = new int[]{20, 20, 20};
    public int damageType;

    public EnchantmentDamage(int n, int n2, int n3) {
        super(n, n2, EnumEnchantmentType.weapon);
        this.damageType = n3;
    }

    @Override
    public int getMinEnchantability(int n) {
        return baseEnchantability[this.damageType] + (n - 1) * levelEnchantability[this.damageType];
    }

    @Override
    public int getMaxEnchantability(int n) {
        return this.getMinEnchantability(n) + thresholdEnchantability[this.damageType];
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public float computeFloat(int n, EnumCreatureAttribute enumCreatureAttribute) {
        return this.damageType == 0
            ? n * 1.25F
            : (
                this.damageType == 1 && enumCreatureAttribute == EnumCreatureAttribute.UNDEAD
                    ? n * 2.5F
                    : (this.damageType == 2 && enumCreatureAttribute == EnumCreatureAttribute.ARTHROPOD ? n * 2.5F : 0.0F)
            );
    }

    @Override
    public String getName() {
        return "enchantment.damage." + protectionName[this.damageType];
    }

    @Override
    public boolean canApplyTogether(Enchantment enchantment) {
        return !(enchantment instanceof EnchantmentDamage);
    }

    @Override
    public boolean canApply(ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemAxe || super.canApply(itemStack);
    }

    @Override
    public void handleEntityLivingBase2(EntityLivingBase entityLivingBase, Entity entity, int n) {
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase2 = (EntityLivingBase)entity;
            if (this.damageType == 2 && entityLivingBase2.getCreatureAttribute() == EnumCreatureAttribute.ARTHROPOD) {
                int n2 = 20 + entityLivingBase.getRNG().nextInt(10 * n);
                entityLivingBase2.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, n2, 3));
            }
        }
    }
}

package net.minecraft.enchantment;

import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;

public abstract class Enchantment {
    public static Enchantment[] enchantmentsList = new Enchantment[256];
    public static Enchantment protection = new EnchantmentProtection(0, 10, 0);
    public static Enchantment fireProtection = new EnchantmentProtection(1, 5, 1);
    public static Enchantment featherFalling = new EnchantmentProtection(2, 5, 2);
    public static Enchantment blastProtection = new EnchantmentProtection(3, 2, 3);
    public static Enchantment projectileProtection = new EnchantmentProtection(4, 5, 4);
    public static Enchantment respiration = new EnchantmentOxygen(5, 2);
    public static Enchantment aquaAffinity = new EnchantmentWaterWorker(6, 2);
    public static Enchantment thorns = new EnchantmentThorns(7, 1);
    public static Enchantment sharpness = new EnchantmentDamage(16, 10, 0);
    public static Enchantment smite = new EnchantmentDamage(17, 5, 1);
    public static Enchantment baneOfArthropods = new EnchantmentDamage(18, 5, 2);
    public static Enchantment knockback = new EnchantmentKnockback(19, 5);
    public static Enchantment fireAspect = new EnchantmentFireAspect(20, 2);
    public static Enchantment looting = new EnchantmentLootBonus(21, 2, EnumEnchantmentType.weapon);
    public static Enchantment efficiency = new EnchantmentDigging(32, 10);
    public static Enchantment silkTouch = new EnchantmentUntouching(33, 1);
    public static Enchantment unbreaking = new EnchantmentDurability(34, 5);
    public static Enchantment fortune = new EnchantmentLootBonus(35, 2, EnumEnchantmentType.digger);
    public static Enchantment power = new EnchantmentArrowDamage(48, 10);
    public static Enchantment punch = new EnchantmentArrowKnockback(49, 2);
    public static Enchantment flame = new EnchantmentArrowFire(50, 2);
    public static Enchantment infinity = new EnchantmentArrowInfinite(51, 1);
    public static Enchantment luckOfTheSea = new EnchantmentLootBonus(61, 2, EnumEnchantmentType.fishing_rod);
    public static Enchantment lure = new EnchantmentFishingSpeed(62, 2, EnumEnchantmentType.fishing_rod);
    public static Enchantment[] enchantmentsBookList;
    public int effectId;
    public int weight;
    public EnumEnchantmentType type;
    public String name;

    public Enchantment(int n, int n2, EnumEnchantmentType enumEnchantmentType) {
        this.effectId = n;
        this.weight = n2;
        this.type = enumEnchantmentType;
        if (enchantmentsList[n] != null) {
            throw new IllegalArgumentException("Duplicate enchantment id!");
        }

        enchantmentsList[n] = this;
    }

    public int getWeight() {
        return this.weight;
    }

    public int getMinLevel() {
        return 1;
    }

    public int getMaxLevel() {
        return 1;
    }

    public int getMinEnchantability(int n) {
        return 1 + n * 10;
    }

    public int getMaxEnchantability(int n) {
        return this.getMinEnchantability(n) + 5;
    }

    public int calcModifierDamage(int n, DamageSource damageSource) {
        return 0;
    }

    public float computeFloat(int n, EnumCreatureAttribute enumCreatureAttribute) {
        return 0.0F;
    }

    public boolean canApplyTogether(Enchantment enchantment) {
        return this != enchantment;
    }

    public Enchantment setName(String str) {
        this.name = str;
        return this;
    }

    public String getName() {
        return "enchantment." + this.name;
    }

    public String getTranslatedName(int n) {
        String translateToLocal = StatCollector.translateToLocal(this.getName());
        return translateToLocal + " " + StatCollector.translateToLocal("enchantment.level." + n);
    }

    public boolean canApply(ItemStack itemStack) {
        return this.type.canEnchantItem(itemStack.getItem());
    }

    public void handleEntityLivingBase2(EntityLivingBase entityLivingBase, Entity entity, int n) {
    }

    public void handleEntityLivingBase(EntityLivingBase entityLivingBase, Entity entity, int n) {
    }

    static {
        ArrayList list = new ArrayList();

        for (Enchantment enchantment : enchantmentsList) {
            if (enchantment != null) {
                list.add(enchantment);
            }
        }

        enchantmentsBookList = (Enchantment[])list.toArray(new Enchantment[0]);
    }
}

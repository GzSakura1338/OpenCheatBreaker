package net.minecraft.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;

public class EnchantmentProtection extends Enchantment {
    public static String[] protectionName = new String[]{"all", "fire", "fall", "explosion", "projectile"};
    public static int[] baseEnchantability = new int[]{1, 10, 5, 5, 3};
    public static int[] levelEnchantability = new int[]{11, 8, 6, 8, 6};
    public static int[] thresholdEnchantability = new int[]{20, 12, 10, 12, 15};
    public int protectionType;

    public EnchantmentProtection(int n, int n2, int n3) {
        super(n, n2, EnumEnchantmentType.armor);
        this.protectionType = n3;
        if (n3 == 2) {
            this.type = EnumEnchantmentType.armor_feet;
        }
    }

    @Override
    public int getMinEnchantability(int n) {
        return baseEnchantability[this.protectionType] + (n - 1) * levelEnchantability[this.protectionType];
    }

    @Override
    public int getMaxEnchantability(int n) {
        return this.getMinEnchantability(n) + thresholdEnchantability[this.protectionType];
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public int calcModifierDamage(int n, DamageSource damageSource) {
        if (damageSource.canHarmInCreative()) {
            return 0;
        }

        float f = (6 + n * n) / 3.0F;
        return this.protectionType == 0
            ? MathHelper.floor_float(f * 0.75F)
            : (
                this.protectionType == 1 && damageSource.isFireDamage()
                    ? MathHelper.floor_float(f * 1.25F)
                    : (
                        this.protectionType == 2 && damageSource == DamageSource.fall
                            ? MathHelper.floor_float(f * 2.5F)
                            : (
                                this.protectionType == 3 && damageSource.isExplosion()
                                    ? MathHelper.floor_float(f * 1.5F)
                                    : (this.protectionType == 4 && damageSource.isProjectile() ? MathHelper.floor_float(f * 1.5F) : 0)
                            )
                    )
            );
    }

    @Override
    public String getName() {
        return "enchantment.protect." + protectionName[this.protectionType];
    }

    @Override
    public boolean canApplyTogether(Enchantment enchantment) {
        if (!(enchantment instanceof EnchantmentProtection)) {
            return super.canApplyTogether(enchantment);
        }

        EnchantmentProtection enchantmentProtection = (EnchantmentProtection)enchantment;
        return enchantmentProtection.protectionType != this.protectionType && (this.protectionType == 2 || enchantmentProtection.protectionType == 2);
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

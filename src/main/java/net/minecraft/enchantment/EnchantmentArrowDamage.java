package net.minecraft.enchantment;

public class EnchantmentArrowDamage extends Enchantment {
    public EnchantmentArrowDamage(int n, int n2) {
        super(n, n2, EnumEnchantmentType.bow);
        this.setName("arrowDamage");
    }

    @Override
    public int getMinEnchantability(int n) {
        return 1 + (n - 1) * 10;
    }

    @Override
    public int getMaxEnchantability(int n) {
        return this.getMinEnchantability(n) + 15;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }
}

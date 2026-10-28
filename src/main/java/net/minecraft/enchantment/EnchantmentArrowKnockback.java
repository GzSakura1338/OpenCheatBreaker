package net.minecraft.enchantment;

public class EnchantmentArrowKnockback extends Enchantment {
    public EnchantmentArrowKnockback(int n, int n2) {
        super(n, n2, EnumEnchantmentType.bow);
        this.setName("arrowKnockback");
    }

    @Override
    public int getMinEnchantability(int n) {
        return 12 + (n - 1) * 20;
    }

    @Override
    public int getMaxEnchantability(int n) {
        return this.getMinEnchantability(n) + 25;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }
}

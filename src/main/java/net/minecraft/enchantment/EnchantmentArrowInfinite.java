package net.minecraft.enchantment;

public class EnchantmentArrowInfinite extends Enchantment {
    public EnchantmentArrowInfinite(int n, int n2) {
        super(n, n2, EnumEnchantmentType.bow);
        this.setName("arrowInfinite");
    }

    @Override
    public int getMinEnchantability(int n) {
        return 20;
    }

    @Override
    public int getMaxEnchantability(int n) {
        return 50;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }
}

package net.minecraft.enchantment;

public class EnchantmentArrowFire extends Enchantment {
    public EnchantmentArrowFire(int n, int n2) {
        super(n, n2, EnumEnchantmentType.bow);
        this.setName("arrowFire");
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

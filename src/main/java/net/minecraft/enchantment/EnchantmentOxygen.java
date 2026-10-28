package net.minecraft.enchantment;

public class EnchantmentOxygen extends Enchantment {
    public EnchantmentOxygen(int n, int n2) {
        super(n, n2, EnumEnchantmentType.armor_head);
        this.setName("oxygen");
    }

    @Override
    public int getMinEnchantability(int n) {
        return 10 * n;
    }

    @Override
    public int getMaxEnchantability(int n) {
        return this.getMinEnchantability(n) + 30;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}

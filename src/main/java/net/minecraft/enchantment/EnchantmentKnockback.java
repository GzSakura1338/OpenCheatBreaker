package net.minecraft.enchantment;

public class EnchantmentKnockback extends Enchantment {
    public EnchantmentKnockback(int n, int n2) {
        super(n, n2, EnumEnchantmentType.weapon);
        this.setName("knockback");
    }

    @Override
    public int getMinEnchantability(int n) {
        return 5 + 20 * (n - 1);
    }

    @Override
    public int getMaxEnchantability(int n) {
        return super.getMinEnchantability(n) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }
}

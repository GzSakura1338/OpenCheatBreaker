package net.minecraft.enchantment;

public class EnchantmentFireAspect extends Enchantment {
    public EnchantmentFireAspect(int n, int n2) {
        super(n, n2, EnumEnchantmentType.weapon);
        this.setName("fire");
    }

    @Override
    public int getMinEnchantability(int n) {
        return 10 + 20 * (n - 1);
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
